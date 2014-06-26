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

import com.jonbanjo.cups.CupsPrintJobAttributes;
import com.jonbanjo.cups.operations.IppHeader;
import com.jonbanjo.cups.operations.OperationResult;
import com.jonbanjo.vppserver.schema.ippclient.AttributeGroup;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class IppGetJobAttributesOperation extends BaseJobAttributes{

    private String userName;
    
    public IppGetJobAttributesOperation(){
        super();
    }
    
    @Override
    protected void setOperation() {
        operationID = 0x0009;
        bufferSize = 8192;
    }

  public CupsPrintJobAttributes getPrintJobAttributes(URL url, String userName, int jobId) throws UnsupportedEncodingException, IOException{
    
    this.userName = userName;
    CupsPrintJobAttributes job = new CupsPrintJobAttributes();

    OperationResult result = request(new URL(url.toString() + "/jobs/" + jobId), null);

    for (AttributeGroup group : result.getIppResult().getAttributeGroupList()) {
      if ("job-attributes-tag".equals(group.getTagName()) ||"unassigned".equals(group.getTagName())) {
          setJobAttributes(job, group);
      }
    }
    return job;
  }
    
    @Override
    protected void setAttributes() throws UnsupportedEncodingException{
        header = IppHeader.getUriTag(header, "job-uri", url);
        header = IppHeader.getKeyword(header, "requested-attributes", "all");
        header = IppHeader.getNameWithoutLanguage(header, "requesting-user-name", userName);
    }
    
}
