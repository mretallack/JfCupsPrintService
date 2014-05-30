package com.jonbanjo.cups.operations.ipp;

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
import java.util.Map;

public class IppGetPrinterAttributesOperation extends IppOperation{

    private String userName;
    private Map<String, String> map;
    
    @Override
    protected void setOperation() {
        operationID = 0x000b;
        bufferSize = 8192;
    }

    public OperationResult getPrinterAttributes(URL url, String userName, AuthInfo auth, Map <String, String>map) throws UnsupportedEncodingException, IOException{
        this.userName = userName;
        this.map = map;
        return request(url, auth);
    }
    
    @Override
    protected void setAttributes() throws UnsupportedEncodingException{
            header = IppHeader.getUriTag(header, "printer-uri", url);
        
        if (userName != null)
            header = IppHeader.getNameWithoutLanguage(header, "requesting-user-name", userName);

        if (map == null) {
            header = IppHeader.getKeyword(header, "requested-attributes", "all");
            return;
        }

        
        if (map.get("requested-attributes") != null) {
            header = IppHeader.getKeyword(header, "requested-attributes", map.get("requested-attributes"));
        }

        if (map.get("document-format") != null){
            header = IppHeader.getNameWithoutLanguage(header, "document-format", map.get("document-format"));
        }
    }
    
}
