package com.jonbanjo.vppserver.ippclient;

/*
JfCups
Copyright (C) 2014 Jon Freeman

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/*Based on ch.ethz.vppserver.ippclient.IppResponse.Java 
Copyright (C) 2008 ITS of ETH Zurich, Switzerland, Sarah Windler Burri
*/

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jonbanjo.vppserver.schema.ippclient.Attribute;
import com.jonbanjo.vppserver.schema.ippclient.AttributeGroup;
import com.jonbanjo.vppserver.schema.ippclient.AttributeValue;
import java.io.UnsupportedEncodingException;

public class IppResponse {
  private final static String CRLF = "\r\n";

  private static final int BYTEBUFFER_CAPACITY = 8192;
  // Saved response of printer
  private AttributeGroup _attributeGroupResult = null;
  private Attribute _attributeResult = null;
  private List <AttributeGroup> _result = null;

  // read IPP response in global buffer
  ByteBuffer _buf = null;

  public IppResponse() {
    _result = new ArrayList<AttributeGroup>();
    _buf = ByteBuffer.allocate(BYTEBUFFER_CAPACITY);
  }


  /**
   * 
   * @param channel
   * @return
   * @throws IOException
   */
  public IppResult getResponse(ByteBuffer buffer) throws IOException {

    _buf.clear();

    _attributeGroupResult = null;
    _attributeResult = null;
    _result.clear();

    IppResult result = new IppResult();
    result.setBuf(buffer.array());
    boolean ippHeaderResponse = false;

    // be careful: HTTP and IPP could be transmitted in different set of
    // buffers.
    // see RFC2910, http://www.ietf.org/rfc/rfc2910, page 19
    // read IPP header
    if ((!ippHeaderResponse) && (buffer.hasRemaining())) {
      _buf = buffer;
      result.setIppStatusResponse(getIPPHeader());
      ippHeaderResponse = true;
    }

    _buf = buffer;
    // read attribute group list with attributes
    getAttributeGroupList();

    closeAttributeGroup();
    result.setAttributeGroupList(_result);
    return result;
  }

  /**
   * 
   * @return
   */
  private String getIPPHeader() {
    StringBuffer sb = new StringBuffer();
    sb.append("Major Version:" + IppConverter.toHexWithMarker(_buf.get()));
    sb.append(" Minor Version:" + IppConverter.toHexWithMarker(_buf.get()));

    String statusCode = IppConverter.toHexWithMarker(_buf.get()) + IppConverter.toHex(_buf.get());
    String statusMessage = IppLists.statusCodeMap.get(statusCode);
    if (statusMessage == null){
        statusMessage = "unknown";
    }
    sb.append(" Request Id:" + _buf.getInt() + "\n");
    sb.append("Status Code:" + statusCode + "(" + statusMessage + ")");

    if (sb.length() != 0) {
      return sb.toString();
    }
    return null;
  }

  /**
   * <p>
   * <strong>Note:</strong> Global variables <code>_attributeGroupResult</code>,
   * <code>_attributeResult</code>, <code>_result</code> are filled by local
   * 'tag' methods.<br />
   * Decision for this programming solution is based on the structure of IPP tag
   * sequences to clarify the attribute structure with its values.
   * </p>
   * 
   * @return list of attributes group
   */
  private List<AttributeGroup> getAttributeGroupList() throws UnsupportedEncodingException {
    while (_buf.hasRemaining()) {

      byte tag = _buf.get();
      switch (tag) {
      case 0x00:
        setAttributeGroup(tag); // reserved
        continue;
      case 0x01:
        setAttributeGroup(tag); // operation-attributes
        continue;
      case 0x02:
        setAttributeGroup(tag); // job-attributes
        continue;
      case 0x03:
        return _result; // end-attributes
      case 0x04:
        setAttributeGroup(tag); // printer-attributes
        continue;
      case 0x05:
        setAttributeGroup(tag); // unsupported-attributes
        continue;
      case 0x06:
        setAttributeGroup(tag); // subscription-attributes
        continue;
      case 0x07:
        setAttributeGroup(tag); // event-notification-attributes
        continue;
      case 0x13:
        setNoValueAttribute(tag); // no-value
        continue;
      case 0x21:
        setIntegerAttribute(tag); // integer
        continue;
      case 0x22:
        setBooleanAttribute(tag); // boolean
        continue;
      case 0x23:
        setEnumAttribute(tag); // enumeration
        continue;
      case 0x30:
        setTextAttribute(tag); // octetString;
        continue;
      case 0x31:
        setDateTimeAttribute(tag);// datetime
        continue;
      case 0x32:
        setResolutionAttribute(tag);// resolution
        continue;
      case 0x33:
        setRangeOfIntegerAttribute(tag);// rangeOfInteger
        continue;
      case 0x35:
        setTextWithLanguageAttribute(tag); // textWithLanguage
        continue;
      case 0x36:
        setNameWithLanguageAttribute(tag); // nameWithLanguage
        continue;
      case 0x41:
        setTextAttribute(tag); // textWithoutLanguage
        continue;
      case 0x42:
        setTextAttribute(tag); // nameWithoutLanguage
        continue;
      case 0x44:
        setTextAttribute(tag); // keyword
        continue;
      case 0x45:
        setTextAttribute(tag); // uri
        continue;
      case 0x46:
        setTextAttribute(tag); // uriScheme
        continue;
      case 0x47:
        setTextAttribute(tag); // charset
        continue;
      case 0x48:
        setTextAttribute(tag); // naturalLanguage
        continue;
      case 0x49:
        setTextAttribute(tag); // mimeMediaType
        continue;
      default:
        return _result; // not defined
      }
    }
    return null;
  }

  /**
   * 
   * @param tag
   */
  private void setAttributeGroup(byte tag) {
    if (_attributeGroupResult != null) {
      if (_attributeResult != null) {
        _attributeGroupResult.getAttribute().add(_attributeResult);
      }
      _result.add(_attributeGroupResult);
    }
    _attributeResult = null;

    _attributeGroupResult = new AttributeGroup();
    _attributeGroupResult.setTagName(getTagName(IppConverter.toHexWithMarker(tag)));
  }

  /**
	 * 
	 */
  private void closeAttributeGroup() {
    if (_attributeGroupResult != null) {
      if (_attributeResult != null) {
        _attributeGroupResult.getAttribute().add(_attributeResult);
      }
      _result.add(_attributeGroupResult);
    }
    _attributeResult = null;
    _attributeGroupResult = null;
  }

  /**
   * 
   * @param tag
   */
  private void setTextAttribute(byte tag) throws UnsupportedEncodingException {
    short length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      setAttributeName(length);
    }

    // set attribute value
    if (!_buf.hasRemaining()) {
      return;
    }
    length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      byte[] dst = new byte[length];
      _buf.get(dst);
      String value = IppConverter.toString(dst);
      String hex = IppConverter.toHexWithMarker(tag);
      AttributeValue attrValue = new AttributeValue();
      attrValue.setTag(hex);
      String tagName = getTagName(hex);
      attrValue.setTagName(tagName);
      attrValue.setValue(value);
      _attributeResult.getAttributeValue().add(attrValue);
    }

  }

  /**
   * TODO: natural-language not considered in reporting
   * 
   * @param tag
   */
  private void setTextWithLanguageAttribute(byte tag) throws UnsupportedEncodingException {
    short length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      setAttributeName(length);
    }

    // set natural-language and attribute value
    if (!_buf.hasRemaining()) {
      return;
    }

    // set tag, tag name, natural-language
    length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      byte[] dst = new byte[length];
      _buf.get(dst);
      String value = IppConverter.toString(dst);
      String hex = IppConverter.toHexWithMarker(tag);
      AttributeValue attrValue = new AttributeValue();
      attrValue.setTag(hex);
      String tagName = getTagName(hex);
      attrValue.setTagName(tagName);
      attrValue.setValue(value);
      _attributeResult.getAttributeValue().add(attrValue);

      // set value
      length = _buf.getShort();
      if ((length != 0) && (_buf.remaining() >= length)) {
        dst = new byte[length];
        _buf.get(dst);
        value = IppConverter.toString(dst);
        attrValue = new AttributeValue();
        attrValue.setValue(value);
        _attributeResult.getAttributeValue().add(attrValue);
      }
    }
  }

  /**
   * TODO: natural-language not considered in reporting
   * 
   * @param tag
   */
  private void setNameWithLanguageAttribute(byte tag) throws UnsupportedEncodingException {
    short length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      setAttributeName(length);
    }

    // set natural-language and attribute value
    if (!_buf.hasRemaining()) {
      return;
    }

    // set tag, tag name, natural-language
    length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      byte[] dst = new byte[length];
      _buf.get(dst);
      String value = IppConverter.toString(dst);
      String hex = IppConverter.toHexWithMarker(tag);
      AttributeValue attrValue = new AttributeValue();
      attrValue.setTag(hex);
      String tagName = getTagName(hex);
      attrValue.setTagName(tagName);
      attrValue.setValue(value);
      _attributeResult.getAttributeValue().add(attrValue);

      // set value
      length = _buf.getShort();
      if ((length != 0) && (_buf.remaining() >= length)) {
        dst = new byte[length];
        _buf.get(dst);
        value = IppConverter.toString(dst);
        attrValue = new AttributeValue();
        attrValue.setValue(value);
        _attributeResult.getAttributeValue().add(attrValue);
      }
    }
  }

  /**
   * 
   * @param tag
   */
  private void setBooleanAttribute(byte tag) throws UnsupportedEncodingException {
    short length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      setAttributeName(length);
    }

    // set attribute value
    if (!_buf.hasRemaining()) {
      return;
    }
    length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      byte value = _buf.get();
      String hex = IppConverter.toHexWithMarker(tag);
      AttributeValue attrValue = new AttributeValue();
      attrValue.setTag(hex);
      String tagName = getTagName(hex);
      attrValue.setTagName(tagName);
      attrValue.setValue(IppConverter.toBoolean(value));
      _attributeResult.getAttributeValue().add(attrValue);
    }
  }

  /**
   * 
   * @param tag
   */
  private void setDateTimeAttribute(byte tag) throws UnsupportedEncodingException {
    short length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      setAttributeName(length);
    }

    // set attribute value
    if (!_buf.hasRemaining()) {
      return;
    }
    length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      byte[] dst = new byte[length];
      _buf.get(dst, 0, length);
      String value = IppConverter.toDateTime(dst);
      String hex = IppConverter.toHexWithMarker(tag);
      AttributeValue attrValue = new AttributeValue();
      attrValue.setTag(hex);
      String tagName = getTagName(hex);
      attrValue.setTagName(tagName);
      attrValue.setValue(value);
      _attributeResult.getAttributeValue().add(attrValue);
    }
  }

  /**
   * 
   * @param tag
   */
  private void setIntegerAttribute(byte tag) throws UnsupportedEncodingException {
    short length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      setAttributeName(length);
    }
    // set attribute value
    if (!_buf.hasRemaining()) {
      return;
    }
    length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      int value = _buf.getInt();
      String hex = IppConverter.toHexWithMarker(tag);
      AttributeValue attrValue = new AttributeValue();
      attrValue.setTag(hex);
      String tagName = getTagName(hex);
      attrValue.setTagName(tagName);
      attrValue.setValue(Integer.toString(value));
      _attributeResult.getAttributeValue().add(attrValue);
    }
  }

  /**
   * 
   * @param tag
   */
  private void setNoValueAttribute(byte tag) throws UnsupportedEncodingException {
    short length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      setAttributeName(length);
    }
  }

  /**
   * 
   * @param tag
   */
  private void setRangeOfIntegerAttribute(byte tag) throws UnsupportedEncodingException {
    short length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      setAttributeName(length);
    }
    // set attribute value
    if (!_buf.hasRemaining()) {
      return;
    }
    length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      int value1 = _buf.getInt();
      int value2 = _buf.getInt();
      String hex = IppConverter.toHexWithMarker(tag);
      AttributeValue attrValue = new AttributeValue();
      attrValue.setTag(hex);
      String tagName = getTagName(hex);
      attrValue.setTagName(tagName);
      attrValue.setValue(Integer.toString(value1) + "," + Integer.toString(value2));
      _attributeResult.getAttributeValue().add(attrValue);
    }
  }

  /**
   * 
   * @param tag
   */
  private void setResolutionAttribute(byte tag) throws UnsupportedEncodingException {
    short length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      setAttributeName(length);
    }

    // set attribute value
    if (!_buf.hasRemaining()) {
      return;
    }
    length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      int value1 = _buf.getInt();
      int value2 = _buf.getInt();
      byte value3 = _buf.get();
      String hex = IppConverter.toHexWithMarker(tag);
      AttributeValue attrValue = new AttributeValue();
      attrValue.setTag(hex);
      String tagName = getTagName(hex);
      attrValue.setTagName(tagName);
      attrValue.setValue(Integer.toString(value1) + "," + Integer.toString(value2) + "," + Integer.toString(value3));
      _attributeResult.getAttributeValue().add(attrValue);
    }
  }

  /**
   * 
   * @param tag
   */
  private void setEnumAttribute(byte tag) throws UnsupportedEncodingException {
    short length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      setAttributeName(length);
    }

    // set attribute value
    if (!_buf.hasRemaining()) {
      return;
    }

    length = _buf.getShort();
    if ((length != 0) && (_buf.remaining() >= length)) {
      String hex = IppConverter.toHexWithMarker(tag);
      AttributeValue attrValue = new AttributeValue();
      attrValue.setTag(hex);
      String tagName = getTagName(hex);
      attrValue.setTagName(tagName);

      int value = _buf.getInt();
      if (_attributeResult != null) {
        String enumName = getEnumName(value, _attributeResult.getName());
        attrValue.setValue(enumName);
      } else {
        _attributeResult = new Attribute();
        _attributeResult.setName("no attribute name given:");
        attrValue.setValue(Integer.toString(value));
      }

      _attributeResult.getAttributeValue().add(attrValue);
    }
  }

  /**
   * 
   * @param length
   */
  private void setAttributeName(short length) throws UnsupportedEncodingException {
    if ((length == 0) || (_buf.remaining() < length)) {
      return;
    }
    byte[] dst = new byte[length];
    _buf.get(dst);
    String name = IppConverter.toString(dst);
    if (_attributeResult != null) {
      _attributeGroupResult.getAttribute().add(_attributeResult);
    }
    _attributeResult = new Attribute();
    _attributeResult.setName(name.toString());
  }

  /**
   * 
   * @param tag
   * @return
   */
  private String getTagName(String tag) {
    if (tag == null) {
      System.err.println("IppResponse.getTagName(): no tag given");
      return null;
    }
    int l = IppLists.tagList.size();
    for (int i = 0; i < l; i++) {
      if (tag.equals(IppLists.tagList.get(i).getValue())) {
        return IppLists.tagList.get(i).getName();
      }
    }
    return "no name found for tag:" + tag;
  }

  /**
   * 
   * @param value
   * @nameOfAttribute
   * @return
   */
  private String getEnumName(int value, String nameOfAttribute) {
    if (nameOfAttribute == null) 
        return "Null attribute requested";
    EnumItemMap itemMap = IppLists.enumMap.get(nameOfAttribute);
    if (itemMap == null)
        return "Attribute " + nameOfAttribute + "not found";
    String attrValue = itemMap.get(value).name;
    if (attrValue == null)
        return "Value " + value + " for attribute " + nameOfAttribute + " not found";
    return attrValue;
  }
}
