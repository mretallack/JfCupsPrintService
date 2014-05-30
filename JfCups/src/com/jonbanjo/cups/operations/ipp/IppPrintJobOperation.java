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

import com.jonbanjo.cups.CupsPrintJob;
import com.jonbanjo.cups.PrintRequestResult;
import com.jonbanjo.cups.operations.AuthInfo;
import com.jonbanjo.cups.operations.IppHeader;
import com.jonbanjo.cups.operations.IppOperation;
import com.jonbanjo.cups.operations.OperationResult;
import com.jonbanjo.vppserver.ippclient.IppTag;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class IppPrintJobOperation extends IppOperation{
    
    private CupsPrintJob printJob;
    private String userName;
    
    public IppPrintJobOperation() {
        super();
    }
    
    @Override
    public void setOperation(){
        operationID = 0x0002;
        bufferSize = 8192;
    }
    
    public PrintRequestResult print(URL printerUrl, String userName, CupsPrintJob printJob, AuthInfo auth) throws Exception{
        this.userName = userName;
        this.printJob = printJob;
        OperationResult result = request(printerUrl, printJob.getDocument(), auth);
    
        return new PrintRequestResult(result);
    }
    
    @Override
    protected void setAttributes() throws UnsupportedEncodingException{
        header = IppHeader.getUriTag(header, "printer-uri", url);
        header = IppHeader.getNameWithoutLanguage(header, "requesting-user-name", userName);
        if (printJob.getJobName() != null){
            header = IppHeader.getNameWithoutLanguage(header, "job-name", printJob.getJobName());
        }
        if (printJob.getAttributes() == null){
            return;
        }
        String attrs = printJob.getAttributes().get("job-attributes");
        if (attrs != null){
            String[] attributeBlocks = attrs.split("#");
            getJobAttributes(attributeBlocks);
        }
    }
    
    private void getJobAttributes(String[] attributeBlocks)
      throws UnsupportedEncodingException {
      
        if (attributeBlocks == null) {
            return;
        }

        header = IppTag.getJobAttributesTag(header);

        int l = attributeBlocks.length;
        for (int i = 0; i < l; i++) {
            String[] attr = attributeBlocks[i].split(":");
            if ((attr == null) || (attr.length != 3)) {
                System.err.println("Invalid attribute block:" + attributeBlocks[i]);
                continue;
            }
            
            String name = attr[0];
            String tagName = attr[1];
            String value = attr[2];

            if (tagName.equals("boolean")) {
                header = IppHeader.getBoolean(header, name, value);
            } else if (tagName.equals("integer")) {
                header = IppHeader.getInteger(header, name, value);
            } else if (tagName.equals("rangeOfInteger")) {
                header = IppHeader.getRangeOfInteger(header, name, value);
            } else if (tagName.equals("setOfRangeOfInteger")) {
                header = IppHeader.getSetOfRangeOfInteger(header, name, value);
            } else if (tagName.equals("keyword")) {
                    header = IppHeader.getKeyword(header, name, value);
            } else if (tagName.equals("name")) {
                    header = IppHeader.getNameWithoutLanguage(header, name, value);
            } else if (tagName.equals("enum")) {
                    header = IppHeader.getEnum(header, name, Integer.parseInt(value));
            } else if (tagName.equals("resolution")) {
                header = IppHeader.getResolution(header, name, value);
            }
            else {
                System.err.println("Unknown attribute block:" + attributeBlocks[i]);
            }
        }
    }
}
