package com.jonbanjo.vppserver.ippclient;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

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

/*Based on ch.ethz.vppserver.ippclient.IppTag.Java 
Copyright (C) 2008 ITS of ETH Zurich, Switzerland, Sarah Windler Burri
*/

public class IppTag {
    private static final byte MAJOR_VERSION = 0x01;
    private static final byte MINOR_VERSION = 0x01;

    private static final String ATTRIBUTES_CHARSET = "attributes-charset";
    private static final String ATTRIBUTES_NATURAL_LANGUAGE = "attributes-natural-language";

    private static final String ATTRIBUTES_CHARSET_VALUE = "utf-8";
    private static final String ATTRIBUTES_NATURAL_LANGUAGE_VALUE = "en-us";
    private static final short ATTRIBUTES_INTEGER_VALUE_LENGTH = 0x0004;
    private static final short ATTRIBUTES_RANGE_OF_INT_VALUE_LENGTH = 0x0008;
    private static final short ATTRIBUTES_BOOLEAN_VALUE_LENGTH = 0x0001;
    private static final short ATTRIBUTES_RESOLUTION_VALUE_LENGTH = 0x0009;
    private static final byte ATTRIBUTES_BOOLEAN_FALSE_VALUE = 0x00;
    private static final byte ATTRIBUTES_BOOLEAN_TRUE_VALUE = 0x01;

    public static final byte OPERATION_ATTRIBUTES_TAG = 0x01;
    public static final byte JOB_ATTRIBUTES_TAG = 0x02;
    public static final byte END_OF_ATTRIBUTES_TAG = 0x03;
    public static final byte PRINTER_ATTRIBUTES_TAG = 0x04;
    public static final byte UNSUPPORTED_ATTRIBUTES_TAG = 0x05;
    public static final byte SUBSCRIPTION_ATTRIBUTES_TAG = 0x06;
    public static final byte EVENT_NOTIFICATION_ATTRIBUTES_TAG = 0x07;
    public static final byte NO_VALUE_TAG = 0x13; 
    public static final byte INTEGER_TAG = 0x21;
    public static final byte BOOLEAN_TAG = 0x22;
    public static final byte ENUM_TAG = 0x23;
    public static final byte OCTET_STRING_TAG = 0x30;
    public static final byte DATETIME_TAG = 0x31;
    public static final byte RESOLUTION_TAG = 0x32;
    public static final byte RANGE_OF_INTEGER_TAG = 0x33;
    public static final byte TEXT_WITH_LANGUAGE_TAG = 0x35;
    public static final byte NAME_WITH_LANGUAGE_TAG = 0x36;
    public static final byte TEXT_WITHOUT_LANGUAGE_TAG = 0x41;
    public static final byte NAME_WITHOUT_LANGUAGE_TAG = 0x42;
    public static final byte KEYWORD_TAG = 0x44;
    public static final byte URI_TAG = 0x45;
    public static final byte URI_SCHEME_TAG = 0x46;
    public static final byte CHARSET_TAG = 0x47;
    public static final byte NATURAL_LANGUAGE_TAG = 0x48;
    public static final byte MIME_MEDIA_TYPE_TAG = 0x49;

    private static final int MAX_NAME_WITHOUT_LANGUAGE =  255;
    private static final int MAX_NAME_WITH_LANGUAGE = 1023;
    private static final int MAX_TEXT = 1023;
    
    private static final short NULL_LENGTH = 0;
	private static int requestId = 0;
	
    public static ByteBuffer getBoolean(ByteBuffer ippBuf) 
    throws UnsupportedEncodingException {
    	return getBoolean(ippBuf,null);
    } 
    
    public static ByteBuffer getBoolean(ByteBuffer ippBuf,String attributeName) 
    throws UnsupportedEncodingException {
        ippBuf.put(BOOLEAN_TAG);
        if (attributeName != null) {
        	byte[] b = IppConverter.toBytes(attributeName);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
        }
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        
        ippBuf.putShort(NULL_LENGTH);
        return ippBuf;
    }
    
    public static ByteBuffer getBoolean(ByteBuffer ippBuf,String attributeName,boolean value) 
    throws UnsupportedEncodingException {
        ippBuf.put(BOOLEAN_TAG);
        if (attributeName != null) {
        	byte[] b = IppConverter.toBytes(attributeName);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
        }
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        
        ippBuf.putShort(ATTRIBUTES_BOOLEAN_VALUE_LENGTH);
        if (value == true) {
        	ippBuf.put(ATTRIBUTES_BOOLEAN_TRUE_VALUE);
        }
        else {
        	ippBuf.put(ATTRIBUTES_BOOLEAN_FALSE_VALUE);
        }
        return ippBuf;
    }

    public static ByteBuffer getCharset(ByteBuffer ippBuf) 
    throws UnsupportedEncodingException {
    	return getCharset(ippBuf,null,null);
    }
    
    public static ByteBuffer getCharset(ByteBuffer ippBuf,String attributeName) 
    throws UnsupportedEncodingException {
    	return getCharset(ippBuf,attributeName,null);
    }
    
    public static ByteBuffer getCharset(ByteBuffer ippBuf,String attributeName,String value) 
    throws UnsupportedEncodingException {
    	return getUsAscii(ippBuf,CHARSET_TAG,attributeName,value);
    }
    
    public static ByteBuffer getEmptyAttribute(ByteBuffer ippBuf,String attributeName)
    throws UnsupportedEncodingException {
    	ippBuf.put(NO_VALUE_TAG);
    	if (attributeName != null) {
    		byte[] b = IppConverter.toBytes(attributeName);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
    	}
    	else {
    		ippBuf.putShort(NULL_LENGTH);
    	}
    	ippBuf.putShort(NULL_LENGTH);
    	return ippBuf;
    }
 
    public static ByteBuffer getEnd(ByteBuffer ippBuf) {
    	ippBuf.put(END_OF_ATTRIBUTES_TAG);
    	return ippBuf;
    }
    
    public static ByteBuffer getEnum(ByteBuffer ippBuf) 
    throws UnsupportedEncodingException {
    	return getEnum(ippBuf,null);
    }

    public static ByteBuffer getEnum(ByteBuffer ippBuf,String attributeName) 
    throws UnsupportedEncodingException {
        ippBuf.put(ENUM_TAG);
        if (attributeName != null) {
        	byte[] b = IppConverter.toBytes(attributeName);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
        }
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        ippBuf.putShort(NULL_LENGTH);
        return ippBuf;
    }
    
    public static ByteBuffer getEnum(ByteBuffer ippBuf,String attributeName,int value) 
    throws UnsupportedEncodingException {
        ippBuf.put(ENUM_TAG);
        if (attributeName != null) {
        	byte[] b = IppConverter.toBytes(attributeName);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
        }
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        ippBuf.putShort(ATTRIBUTES_INTEGER_VALUE_LENGTH);
        ippBuf.putInt(value);
        return ippBuf;
    }

    public static ByteBuffer getEventNotificationAttributesTag(ByteBuffer ippBuf) {
    	ippBuf.put(EVENT_NOTIFICATION_ATTRIBUTES_TAG);
    	return ippBuf;
    }

    public static ByteBuffer getInteger(ByteBuffer ippBuf) 
    throws UnsupportedEncodingException{
    	return getInteger(ippBuf,null);
    }

    public static ByteBuffer getInteger(ByteBuffer ippBuf,String attributeName) 
    throws UnsupportedEncodingException{
        ippBuf.put(INTEGER_TAG);
        if (attributeName != null) {
        	byte[] b = IppConverter.toBytes(attributeName);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
        }
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        
        ippBuf.putShort(NULL_LENGTH);
        
        return ippBuf;
    }
 
    public static ByteBuffer getInteger(ByteBuffer ippBuf,String attributeName,int value) 
    throws UnsupportedEncodingException{
        ippBuf.put(INTEGER_TAG);
        if (attributeName != null) {
        	byte[] b = IppConverter.toBytes(attributeName);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
        }
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        
        ippBuf.putShort(ATTRIBUTES_INTEGER_VALUE_LENGTH);
        ippBuf.putInt(value);
        return ippBuf;
    }

    public static ByteBuffer getJobAttributesTag(ByteBuffer ippBuf) {
    	ippBuf.put(JOB_ATTRIBUTES_TAG);
    	return ippBuf;
    }

    public static ByteBuffer getKeyword(ByteBuffer ippBuf) 
    throws UnsupportedEncodingException {
    	return getKeyword(ippBuf,null,null);
    }

    public static ByteBuffer getKeyword(ByteBuffer ippBuf,String attributeName) 
    throws UnsupportedEncodingException {
    	return getKeyword(ippBuf,attributeName,null);
    }

    public static ByteBuffer getKeyword(ByteBuffer ippBuf,String attributeName,String value) 
    throws UnsupportedEncodingException {
    	return getUsAscii(ippBuf,KEYWORD_TAG,attributeName,value);
    }
    
    public static ByteBuffer getMimeMediaType(ByteBuffer ippBuf) 
    throws UnsupportedEncodingException {
    	return getMimeMediaType(ippBuf,null,null);
    }

    public static ByteBuffer getMimeMediaType(ByteBuffer ippBuf,String attributeName) 
    throws UnsupportedEncodingException {
    	return getMimeMediaType(ippBuf,attributeName,null);
    }

    public static ByteBuffer getMimeMediaType(ByteBuffer ippBuf,String attributeName,String value) 
    throws UnsupportedEncodingException {
    	return getUsAscii(ippBuf,MIME_MEDIA_TYPE_TAG,attributeName,value);
    }
 
    public static ByteBuffer getNameWithLanguage(ByteBuffer ippBuf,
    		String attributeName,String value) 
    throws UnsupportedEncodingException {
    	return getNameWithLanguage(ippBuf,attributeName,value,null,null);
    }

    public static ByteBuffer getNameWithLanguage(ByteBuffer ippBuf,
    		String attributeName,String value,String language) 
    throws UnsupportedEncodingException {
    	return getNameWithLanguage(ippBuf,attributeName,value,language,null);
    }
 
    public static ByteBuffer getNameWithLanguage(ByteBuffer ippBuf,
    		String attributeName,String value,String language,String charset) 
    throws UnsupportedEncodingException {
    	if (value.length() > MAX_NAME_WITH_LANGUAGE) {
    		value = value.substring(0,MAX_NAME_WITH_LANGUAGE - 1);
    	}
    	
    	if (language == null) {
    		language = ATTRIBUTES_NATURAL_LANGUAGE_VALUE;
    	}  	
    	if (charset == null) {
    		charset = ATTRIBUTES_CHARSET_VALUE;
    	}
    	
        ippBuf.put(NAME_WITH_LANGUAGE_TAG);
        
        if (attributeName != null) {
        	byte[] b = IppConverter.toBytes(attributeName);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
        }
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        byte[] b1 = IppConverter.toBytes(language);
    	ippBuf.putShort((short)b1.length);
        ippBuf.put(b1);
     
        if (value != null) {
        	byte[] b2 = IppConverter.toBytes(value,charset);
        	ippBuf.putShort((short)b2.length);
            ippBuf.put(b2);
        }
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        return ippBuf;
    }
 
    public static ByteBuffer getNameWithoutLanguage(ByteBuffer ippBuf,
    		String attributeName,String value) throws UnsupportedEncodingException {
    	return getNameWithoutLanguage(ippBuf,attributeName,value,null);
    }

    public static ByteBuffer getNameWithoutLanguage(ByteBuffer ippBuf,String attributeName,String value,String charset) 
    throws UnsupportedEncodingException {
    	if (value.length() > MAX_NAME_WITHOUT_LANGUAGE) {
    		value = value.substring(0,MAX_NAME_WITHOUT_LANGUAGE - 1);
    	}
    	if (charset == null) {
    		charset = ATTRIBUTES_CHARSET_VALUE;
    	}

        ippBuf.put(NAME_WITHOUT_LANGUAGE_TAG);
        
        if (attributeName != null) {
        	byte[] b = IppConverter.toBytes(attributeName);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
        }
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        
        if (value != null) {
        	byte[] b = IppConverter.toBytes(value,charset);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
        }
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        return ippBuf;
    }
 
    public static ByteBuffer getNaturalLanguage(ByteBuffer ippBuf) 
    throws UnsupportedEncodingException {
    	return getNaturalLanguage(ippBuf,null,null);
    }

    public static ByteBuffer getNaturalLanguage(ByteBuffer ippBuf,String attributeName) 
    throws UnsupportedEncodingException {
    	return getNaturalLanguage(ippBuf,attributeName,null);
    }

    public static ByteBuffer getNaturalLanguage(ByteBuffer ippBuf,String attributeName,String value) 
    throws UnsupportedEncodingException {
    	return getUsAscii(ippBuf,NATURAL_LANGUAGE_TAG,attributeName,value);
    }

    public static ByteBuffer getOctetString(ByteBuffer ippBuf) 
    throws UnsupportedEncodingException {
    	return getOctetString(ippBuf,null,null);
    }

    public static ByteBuffer getOctetString(ByteBuffer ippBuf,String attributeName) 
    throws UnsupportedEncodingException {
    	return getOctetString(ippBuf,attributeName,null);
    }

    public static ByteBuffer getOctetString(ByteBuffer ippBuf,String attributeName,String value) 
    throws UnsupportedEncodingException {
    	return getUsAscii(ippBuf,OCTET_STRING_TAG,attributeName,value);
    }
    
    public static ByteBuffer getOperation(ByteBuffer ippBuf,short operation) 
    throws UnsupportedEncodingException {
    	return getOperation(ippBuf,operation,null,null);
    }

    public static ByteBuffer getOperation(ByteBuffer ippBuf,short operation,String charset,
                    String naturalLanguage) throws UnsupportedEncodingException {
        if (charset == null) {
        	charset = ATTRIBUTES_CHARSET_VALUE;
        }
        if (naturalLanguage == null) {
        	naturalLanguage = ATTRIBUTES_NATURAL_LANGUAGE_VALUE;
        }
        ippBuf.put(MAJOR_VERSION);
        ippBuf.put(MINOR_VERSION);
        ippBuf.putShort(operation); 
        ippBuf.putInt(++requestId);
        ippBuf.put(OPERATION_ATTRIBUTES_TAG);

        ippBuf = getCharset(ippBuf,ATTRIBUTES_CHARSET,charset);
        ippBuf = getNaturalLanguage(ippBuf,ATTRIBUTES_NATURAL_LANGUAGE,naturalLanguage);
        return ippBuf;
    }
 
    public static ByteBuffer getOperationAttributesTag(ByteBuffer ippBuf) {
    	ippBuf.put(OPERATION_ATTRIBUTES_TAG);
    	return ippBuf;
    }

    public static ByteBuffer getPrinterAttributesTag(ByteBuffer ippBuf) {
    	ippBuf.put(PRINTER_ATTRIBUTES_TAG);
    	return ippBuf;
    }

    public static ByteBuffer getRangeOfInteger(ByteBuffer ippBuf) 
    throws UnsupportedEncodingException {
    	return getRangeOfInteger(ippBuf,null);
    }

    public static ByteBuffer getRangeOfInteger(ByteBuffer ippBuf,String attributeName) 
    throws UnsupportedEncodingException {
        ippBuf.put(RANGE_OF_INTEGER_TAG);
        if (attributeName != null) {
        	byte[] b = IppConverter.toBytes(attributeName);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
        }
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        ippBuf.putShort(NULL_LENGTH);
        return ippBuf;
    }

    public static ByteBuffer getRangeOfInteger(ByteBuffer ippBuf,String attributeName,
    		int value1,int value2) throws UnsupportedEncodingException {
        ippBuf.put(RANGE_OF_INTEGER_TAG);
        if (attributeName != null) {
        	byte[] b = IppConverter.toBytes(attributeName);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
        }
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        ippBuf.putShort(ATTRIBUTES_RANGE_OF_INT_VALUE_LENGTH);
        ippBuf.putInt(value1);
        ippBuf.putInt(value2);
        return ippBuf;
    }
 
    public static ByteBuffer getResolution(ByteBuffer ippBuf) 
    throws UnsupportedEncodingException {
    	return getResolution(ippBuf,null);
    }

    public static ByteBuffer getResolution(ByteBuffer ippBuf,String attributeName) 
    throws UnsupportedEncodingException {
        ippBuf.put(RESOLUTION_TAG);
        if (attributeName != null) {
        	byte[] b = IppConverter.toBytes(attributeName);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
        }       
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        ippBuf.putShort(NULL_LENGTH);
        return ippBuf;
    }
    
    public static ByteBuffer getResolution(ByteBuffer ippBuf,String attributeName,
    		int value1,int value2,byte value3) throws UnsupportedEncodingException {
        ippBuf.put(RESOLUTION_TAG);
        if (attributeName != null) {
        	byte[] b = IppConverter.toBytes(attributeName);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
        }     
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        ippBuf.putShort(ATTRIBUTES_RESOLUTION_VALUE_LENGTH);
        ippBuf.putInt(value1);
        ippBuf.putInt(value2);
        ippBuf.put(value3);
        return ippBuf;
    }

    public static ByteBuffer getSubscriptionAttributesTag(ByteBuffer ippBuf) {
    	ippBuf.put(SUBSCRIPTION_ATTRIBUTES_TAG);
    	return ippBuf;
    }

    public static ByteBuffer getTextWithLanguage(ByteBuffer ippBuf,
    		String attributeName,String value) 
    throws UnsupportedEncodingException {
    	return getTextWithLanguage(ippBuf,attributeName,value,null,null);
    }

    public static ByteBuffer getTextWithLanguage(ByteBuffer ippBuf,
    		String attributeName,String value,String language) 
    throws UnsupportedEncodingException {
    	return getTextWithLanguage(ippBuf,attributeName,value,language,null);
    }

    public static ByteBuffer getTextWithLanguage(ByteBuffer ippBuf,
    		String attributeName,String value,String language,String charset) 
    throws UnsupportedEncodingException {
    	if (value.length() > MAX_TEXT) {
    		value = value.substring(0,MAX_TEXT - 1);
    	}
    	
    	if (language == null) {
    		language = ATTRIBUTES_NATURAL_LANGUAGE_VALUE;
    	}
    	if (charset == null) {
    		charset = ATTRIBUTES_CHARSET_VALUE;
    	}
    	
        ippBuf.put(TEXT_WITH_LANGUAGE_TAG);
        
        if (attributeName != null) {
        	byte[] b = IppConverter.toBytes(attributeName);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
        }
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        
        byte[] b1 = IppConverter.toBytes(language);
    	ippBuf.putShort((short)b1.length);
        ippBuf.put(b1);
      
        if (value != null) {
        	byte[] b2 = IppConverter.toBytes(value,charset);
        	ippBuf.putShort((short)b2.length);
            ippBuf.put(b2);
        }
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        return ippBuf;
    }

    public static ByteBuffer getTextWithoutLanguage(ByteBuffer ippBuf,
    		String attributeName,String value) 
    throws UnsupportedEncodingException {
    	return getTextWithoutLanguage(ippBuf,attributeName,value,null);
    }
    
    public static ByteBuffer getTextWithoutLanguage(ByteBuffer ippBuf,
    		String attributeName,String value,String charset) 
    throws UnsupportedEncodingException {
    	if (value.length() > MAX_TEXT) {
    		value = value.substring(0,MAX_TEXT - 1);
    	}
    	
    	if (charset == null) {
    		charset = ATTRIBUTES_CHARSET_VALUE;
    	}
    	
        ippBuf.put(TEXT_WITHOUT_LANGUAGE_TAG);
        
        if (attributeName != null) {
        	byte[] b = IppConverter.toBytes(attributeName);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
        }
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        
        if (value != null) {
        	byte[] b = IppConverter.toBytes(value,charset);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
        }
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        return ippBuf;
    }

    public static ByteBuffer getUnsupportedAttributesTag(ByteBuffer ippBuf) {
    	ippBuf.put(UNSUPPORTED_ATTRIBUTES_TAG);
    	return ippBuf;
    }

    public static ByteBuffer getUri(ByteBuffer ippBuf) 
    throws UnsupportedEncodingException {
    	return getUri(ippBuf,null,null);
    }
    
    public static ByteBuffer getUri(ByteBuffer ippBuf,String attributeName) 
    throws UnsupportedEncodingException {
    	return getUri(ippBuf,attributeName,null);
    }
 
    public static ByteBuffer getUri(ByteBuffer ippBuf,String attributeName,String value) 
    throws UnsupportedEncodingException {
    	return getUsAscii(ippBuf,URI_TAG,attributeName,value);
    }

    public static ByteBuffer getUriScheme(ByteBuffer ippBuf) 
    throws UnsupportedEncodingException {
    	return getUriScheme(ippBuf,null,null);
    }

    public static ByteBuffer getUriScheme(ByteBuffer ippBuf,String attributeName) 
    throws UnsupportedEncodingException {
    	return getUriScheme(ippBuf,attributeName,null);
    }
 
    public static ByteBuffer getUriScheme(ByteBuffer ippBuf,String attributeName,String value) 
    throws UnsupportedEncodingException {
    	return getUsAscii(ippBuf,URI_SCHEME_TAG,attributeName,value);
    }

    private static ByteBuffer getUsAscii(ByteBuffer ippBuf,byte tag,String attributeName,String value) 
    throws UnsupportedEncodingException {
        ippBuf.put(tag);
        if (attributeName != null) {
        	byte[] b = IppConverter.toBytes(attributeName);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
        }
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        
        if (value != null) {
        	byte[] b = IppConverter.toBytes(value);
        	ippBuf.putShort((short)b.length);
            ippBuf.put(b);
        }
        else {
        	ippBuf.putShort(NULL_LENGTH);
        }
        return ippBuf;
    }

    public static ByteBuffer getVersion(String version,ByteBuffer ippBuf) {
		if (version == null) {
			ippBuf.put(MAJOR_VERSION);
        	ippBuf.put(MINOR_VERSION);
			return ippBuf;
		}
		
		String[] sta = version.split("\\.");
		if (sta == null) {
			return ippBuf;
		}

		ippBuf.put(Byte.parseByte(sta[0]));
		ippBuf.put(Byte.parseByte(sta[1]));
		return ippBuf;
	}
}