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

import com.jonbanjo.vppserver.schema.ippclient.Attribute;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;


public abstract class IppOperation {
  
    protected short operationID = -1; 
    protected short bufferSize = 8192;
    protected ByteBuffer header;
    protected URL url;
    
    public IppOperation(){
    }
    
    protected abstract void setOperation();
    
    protected abstract void setAttributes()throws UnsupportedEncodingException;
    
    protected final OperationResult request(URL url, AuthInfo auth) throws UnsupportedEncodingException, IOException{
        return request(url, null, auth);
    }
    protected final OperationResult request(URL url, InputStream documentStream, AuthInfo auth) throws UnsupportedEncodingException, IOException{
        if (auth == null){
            auth = new AuthInfo();
        }
        setOperation();
        this.url = url;
        header = IppHeader.getIppHeader(bufferSize, operationID);
        setAttributes();
        header = IppHeader.close(header);
        return HttpPoster.sendRequest(url, header, documentStream, auth);
    }
    
    protected String getAttributeValue(Attribute attr) {
        return attr.getAttributeValue().get(0).getValue();
    }
  
    
}
