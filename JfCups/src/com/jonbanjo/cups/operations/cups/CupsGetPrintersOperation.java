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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class CupsGetPrintersOperation extends IppOperation{

    String userName;
    String requestedAttributes;
    
    public CupsGetPrintersOperation() {
        super();
    }
    
    @Override
    public void setOperation(){
        operationID = 0x4002;
        bufferSize = 8192;
    }

    public OperationResult getPrinters(URL url, String userName, AuthInfo auth, String requestedAttributes) throws UnsupportedEncodingException, IOException{
        this.userName = userName;
        this.requestedAttributes = requestedAttributes;
        return request(new URL(url.toString() + "/printers/"), auth);
    }


   @Override
   protected void setAttributes()throws UnsupportedEncodingException{
   
       header = IppHeader.getUriTag(header, "printer-uri", url);
       if (userName != null){
            header = IppHeader.getNameWithoutLanguage(header, "requesting-user-name", userName);
       }
       header = IppHeader.getKeyword(header, "requested-attributes", requestedAttributes);
    }
}
