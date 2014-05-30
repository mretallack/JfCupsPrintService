package com.jonbanjo.cups.operations.cups;

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

import com.jonbanjo.cups.operations.AuthInfo;
import com.jonbanjo.cups.operations.IppHeader;
import com.jonbanjo.cups.operations.IppOperation;
import com.jonbanjo.cups.operations.OperationResult;
import com.jonbanjo.vppserver.schema.ippclient.Attribute;
import com.jonbanjo.vppserver.schema.ippclient.AttributeGroup;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class CupsGetPPDOperation extends IppOperation {

    public CupsGetPPDOperation() {
        super();
    }
        
    @Override
    public void setOperation(){
        operationID = 0x400F;
        bufferSize = 8192;
    }
    
    

  public String getPPDFile(URL printerUrl, AuthInfo auth) throws UnsupportedEncodingException, IOException, Exception{
    
    OperationResult result = request(printerUrl, auth);
    
    
    //If printer is external,
    //cups responds with the correct url to retrive the ppd.
    String urlStr = null;
    for (AttributeGroup group : result.getIppResult().getAttributeGroupList()){
        if (group.getTagName().equals("operation-attributes-tag")){
            for (Attribute attr : group.getAttribute()) {
                if (attr.getName().equals("printer-uri")){
                    urlStr = (attr.getAttributeValue().get(0).getValue());
                    break;
                }
            }
        }
    }
    if (urlStr != null){
        if (urlStr.startsWith("ipps://")){
            urlStr = urlStr.replace("ipps://", "https://");
        }
        else{
            urlStr = urlStr.replace("ipp://", "http://");
        }
        result = request(new URL(urlStr), null);
    }
    
    String status = result.getHttpStatusResult();
        if (!(status.contains("200"))){
            throw new Exception(status);
        }
    
    String buf = new String(result.getIppResult().getBuf());
    try {
        buf = buf.substring(buf.indexOf("*"));
    }
    catch (Exception e){
        System.out.println("Ppd Buffer is empty");
    }
    return buf;
  }
  
   @Override
   protected void setAttributes() throws UnsupportedEncodingException{
       header = IppHeader.getUriTag(header, "printer-uri", url);
   }
  

}
