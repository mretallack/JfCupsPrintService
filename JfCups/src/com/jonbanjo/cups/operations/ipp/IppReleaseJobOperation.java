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

import com.jonbanjo.cups.PrintRequestResult;
import com.jonbanjo.cups.operations.AuthInfo;
import com.jonbanjo.cups.operations.IppHeader;
import com.jonbanjo.cups.operations.IppOperation;
import com.jonbanjo.cups.operations.OperationResult;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class IppReleaseJobOperation extends IppOperation {

    private String userName;
    
    public IppReleaseJobOperation() {
        super();
    }
 
    @Override
    protected void setOperation() {
        operationID = 0x000D;
        bufferSize = 8192;
    }

    public PrintRequestResult releaseJob(URL clientUrl, AuthInfo auth, String userName, int jobID) throws IOException, UnsupportedEncodingException{
        
        this.userName = userName;
        url = new URL(clientUrl.toString() + "/jobs/" + Integer.toString(jobID));
    
        OperationResult result = request(url, auth);

        return new PrintRequestResult(result);

    }
    @Override
    protected void setAttributes() throws UnsupportedEncodingException{
    
        header = IppHeader.getUriTag(header, "job-uri", url);
        header = IppHeader.getNameWithoutLanguage(header, "requesting-user-name", userName);
    }
    
}