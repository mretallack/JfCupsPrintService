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

/*Based on ch.ethz.vppserver.ippclient.IppConverter.Java 
Copyright (C) 2008 ITS of ETH Zurich, Switzerland, Sarah Windler Burri
*/

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The IppConverter class provides useful transformation methods.
 */
public class IppConverter {

	private static final String DEFAULT_CHARSET = "utf-8";
	private static final int DATE_AND_TIME_LENGTH = 11;
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SZ";

	public static String getTranslatedString(String str)
	throws CharacterCodingException {
		return getTranslatedString(str,null);
	}

	public static String getTranslatedString(String str, String charsetValue)
	throws CharacterCodingException {
		if (charsetValue == null) {
			charsetValue = DEFAULT_CHARSET;
		}
		Charset charset = Charset.forName(charsetValue);
		CharsetDecoder decoder = charset.newDecoder();
		CharsetEncoder encoder = charset.newEncoder();
		decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
		encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
		// Convert a string to charsetValue bytes in a ByteBuffer
		// The new ByteBuffer is ready to be read.
		ByteBuffer buf = encoder.encode(CharBuffer.wrap(str));
		// Convert charsetValue bytes in a ByteBuffer to a character ByteBuffer
		// and then to a string. The new ByteBuffer is ready to be read.
		CharBuffer cbuf = decoder.decode(buf);
		return cbuf.toString();
	}

	public static boolean isAlive(String ipAddress) throws IOException {
		return InetAddress.getByName(ipAddress).isReachable(2000);
	}

	/**
	 * See rfc2910, IPP boolean is defined as SIGNED-BYTE,
	 * where 0x00 is 'false' and 0x01'true'.
	 * @param b byte
	 * @return String representation of boolean: i.e. true, false
	 */
	public static String toBoolean(byte b) {
		return (b == 0) ? "false" : "true";
	}
	
	/**
	 * To default encoding ('utf-8')
	 */
	public static byte[] toBytes(String str) throws UnsupportedEncodingException {
		return toBytes(str,null);
	}

	public static byte[] toBytes(String str, String encoding) 
	throws UnsupportedEncodingException {
		if (encoding == null) { encoding = DEFAULT_CHARSET; }
		return str.getBytes(encoding);
	}

	/**
	 * See rfc2579 for DateAndTime byte length and explanation of byte fields, 
	 * whereas IPP datetime must have a length of eleven bytes.
	 * @param dst byte array
	 * @return String representation of xsd:dateTime
	 */
	public static String toDateTime(byte[] dst) {	
		StringBuffer sb = new StringBuffer();
		short year = toShort(dst[0],dst[1]);
		sb.append(year).append("-");
		byte month = dst[2];
		sb.append(month).append("-");
		byte day = dst[3];
		sb.append(day).append("T");
		byte hours = dst[4];
		sb.append(hours).append(":");
		byte min = dst[5];
		sb.append(min).append(":");
		byte sec = dst[6];
		sb.append(sec).append(".");
		byte decSec = dst[7];
		sb.append(decSec);
		
		int b = dst[8];
		int ival = ((int)b) & 0xff;
		char c = (char) ival;
		sb.append(c);
	
		hours = dst[9];
		sb.append(hours).append(":");
		min = dst[10];
		sb.append(min);
		return sb.toString();
	}
	
	public static String toHex(byte b)  {
		String st = Integer.toHexString(b);
		return (st.length() == 1) ? "0" + st : st;
	}
	
	/**
	 * @return String with Marker '0x' ahead
	 */
	public static String toHexWithMarker(byte b)  {
		StringBuffer sb = new StringBuffer();
		sb.append("0x").append(toHex(b));
		return sb.toString();
	}
	
	/**
	 * 
	 * @param a high byte
	 * @param b low byte
	 * @return short value
	 */
	public static short toShort(byte a, byte b)  {
		return (short)(((a & 0x00ff) << 8) + (b & 0x00ff));
	}

	public static String toString(byte[] dst) throws UnsupportedEncodingException {
		return toString(dst, DEFAULT_CHARSET);
	}

	public static String toString(byte[] dst, String charset) 
	throws UnsupportedEncodingException {
		return new String(dst, charset);
	}

	/**
	 * @return String representation of xsd:datetime 
	 */
	public static String toString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(date);
	} 
}