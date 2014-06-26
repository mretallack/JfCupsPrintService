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
import com.jonbanjo.cups.WhichJobsEnum;
import com.jonbanjo.cups.operations.AuthInfo;
import com.jonbanjo.cups.operations.IppHeader;
import com.jonbanjo.cups.operations.OperationResult;
import com.jonbanjo.vppserver.schema.ippclient.AttributeGroup;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class IppGetJobsOperation extends BaseJobAttributes{

    private String userName;
    private boolean myJobs;
    private WhichJobsEnum whichJobs;
    
    public IppGetJobsOperation() {
        super();
    }
    
    @Override
    protected void setOperation() {
        operationID = 0x000a;
        bufferSize = 8192;
    }

  public List<CupsPrintJobAttributes> getPrintJobs(URL printerUrl, AuthInfo auth, String userName, 
          WhichJobsEnum whichJobs, boolean myJobs) throws IOException, Exception{
      
        this.userName = userName;
        this.whichJobs = whichJobs;
        this.myJobs = myJobs;
        
        List<CupsPrintJobAttributes> jobs = new ArrayList<CupsPrintJobAttributes>();
        CupsPrintJobAttributes jobAttributes;
      
        OperationResult result = request(printerUrl, auth);
        String status = result.getHttpStatusResult();
        if (!(status.contains("200"))){
            throw new Exception(status);
        }
        for (AttributeGroup group : result.getIppResult().getAttributeGroupList()) {
            if ("job-attributes-tag".equals(group.getTagName())) {
            jobAttributes = new CupsPrintJobAttributes();
            setJobAttributes(jobAttributes, group);
            jobs.add(jobAttributes);
        }
    }

    return jobs;
  }
    @Override
    protected void setAttributes() throws UnsupportedEncodingException{

        header = IppHeader.getUriTag(header, "printer-uri", url);
        header = IppHeader.getNameWithoutLanguage(header, "requesting-user-name", userName);
        header = IppHeader.getKeyword(header, "requested-attributes",
            "page-ranges print-quality sides time-at-creation job-uri job-id job-state job-printer-uri job-name job-originating-user-name");

        header = IppHeader.getKeyword(header,"which-jobs", whichJobs.getValue());
        header = IppHeader.getBoolean(header, "my-jobs", myJobs);
    }
    
}
