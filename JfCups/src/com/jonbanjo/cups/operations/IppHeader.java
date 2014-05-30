package com.jonbanjo.cups.operations;

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

import com.jonbanjo.vppserver.ippclient.IppTag;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;

public class IppHeader {
    
    public static ByteBuffer getIppHeader(short bufferSize, short operationID) throws UnsupportedEncodingException {

        ByteBuffer ippBuf = ByteBuffer.allocateDirect(bufferSize);
        return IppTag.getOperation(ippBuf, operationID);
    }
    
    public static ByteBuffer getUriTag(ByteBuffer ippBuf, String name, URL value) throws UnsupportedEncodingException{
         return IppTag.getUri(ippBuf, name, stripPortNumber(value));
    }
    
    public static ByteBuffer getNameWithoutLanguage(ByteBuffer ippBuf, String name, String value) throws UnsupportedEncodingException{
         return IppTag.getNameWithoutLanguage(ippBuf, name, value);
    }
    
    public static ByteBuffer getInteger(ByteBuffer ippBuf, String name, String value) throws UnsupportedEncodingException {
        return IppTag.getInteger(ippBuf, name, Integer.parseInt(value));
    }
    
    public static ByteBuffer getKeyword(ByteBuffer ippBuf, String name, String value) throws UnsupportedEncodingException{
        String[] sta = value.split(" ");
        if (sta != null) {
            ippBuf = IppTag.getKeyword(ippBuf, name, sta[0]);
            int l = sta.length;
            for (int i = 1; i < l; i++) {
                ippBuf = IppTag.getKeyword(ippBuf, null, sta[i]);
            }
        }
    return ippBuf;
    }

    public static ByteBuffer getBoolean(ByteBuffer ippBuf, String name, String value) throws UnsupportedEncodingException{
        return IppTag.getBoolean(ippBuf, name, Boolean.parseBoolean(value));
    }
    
    public static ByteBuffer getBoolean(ByteBuffer ippBuf, String name, boolean value) throws UnsupportedEncodingException{
       return IppTag.getBoolean(ippBuf, name, value);
    }
    
    public static ByteBuffer getRangeOfInteger(ByteBuffer ippBuf, String name, String value) throws UnsupportedEncodingException{
        String[] range = value.split("-");
        int low = Integer.parseInt(range[0]);
        int high = Integer.parseInt(range[1]);
        return IppTag.getRangeOfInteger(ippBuf, name, low, high);
    }
    
    public static ByteBuffer getSetOfRangeOfInteger(ByteBuffer ippBuf, String name, String value) throws UnsupportedEncodingException{
        String ranges[] = value.split(",");
        for (String range : ranges) {
            range = range.trim();
            String[] values = range.split("-");
            int value1 = Integer.parseInt(values[0]);
            int value2 = value1;
            // two values provided?
            if (values.length == 2) {
                value2 = Integer.parseInt(values[1]);
            }
            // first attribute value needs name, additional values need to get the "null" name
            ippBuf = IppTag.getRangeOfInteger(ippBuf, name, value1, value2);
            name = null;
        }
        return ippBuf;
    }
    
    public static ByteBuffer getEnum(ByteBuffer ippBuf, String name, int value) throws UnsupportedEncodingException{
        return IppTag.getEnum(ippBuf, name, value);
    }
    
    public static ByteBuffer getResolution(ByteBuffer ippBuf, String name, String value) throws UnsupportedEncodingException{
        String[] resolution = value.split(",");
        int value1 = Integer.parseInt(resolution[0]);
        int value2 = Integer.parseInt(resolution[1]);
        byte value3 = Byte.valueOf(resolution[2]);
        return IppTag.getResolution(ippBuf, name, value1, value2, value3);
    }
    
    public static ByteBuffer close(ByteBuffer ippBuf){
        ippBuf = IppTag.getEnd(ippBuf);
        ippBuf.flip();
        return ippBuf;
    }

    protected static String stripPortNumber(URL url){
        return url.getProtocol() + "://" + url.getHost() + url.getPath();
    }

}
