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

import com.jonbanjo.cups.JobStateEnum;
import com.jonbanjo.cups.CupsPrintJobAttributes;
import com.jonbanjo.cups.operations.IppOperation;
import com.jonbanjo.vppserver.schema.ippclient.Attribute;
import com.jonbanjo.vppserver.schema.ippclient.AttributeGroup;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

public abstract class BaseJobAttributes extends IppOperation{
    
    public BaseJobAttributes(){
        super();
    }
    
    protected void setJobAttributes(CupsPrintJobAttributes job, AttributeGroup group) throws IOException{
        
        for (Attribute attr : group.getAttribute()) {
           if (attr.getAttributeValue() != null && !attr.getAttributeValue().isEmpty()) {
            String attValue = getAttributeValue(attr);
            if ("job-uri".equals(attr.getName())) {
                job.setJobURL(new URL(attValue.replace("ipp://", url.getProtocol() + "://")));
            } else if ("job-id".equals(attr.getName())) {
              job.setJobID(Integer.parseInt(attValue));
            } else if ("job-state".equals(attr.getName())) {
              //System.out.println("job-state "+ attValue);
              job.setJobState(JobStateEnum.fromString(attValue));
            } else if ("job-printer-uri".equals(attr.getName())) {
              job.setPrinterURL(new URL(attValue.replace("ipp://", url.getProtocol() + "://")));
            } else if ("job-name".equals(attr.getName())) {
              job.setJobName(attValue);
            } else if ("job-originating-user-name".equals(attr.getName())) {
              job.setUserName(attValue);
            } else if ("job-k-octets".equals(attr.getName())){
              job.setSize(Integer.parseInt(attValue));
            } else if ("time-at-creation".equals(attr.getName())){
              job.setJobCreateTime(new Date(1000*Long.parseLong(attValue)));
            } else if ("time-at-completed".equals(attr.getName())){
              job.setJobCompleteTime(new Date(1000*Long.parseLong(attValue)));
            } else if ("job-media-sheets-completed".equals(attr.getName())){
              job.setPagesPrinted(Integer.parseInt(attValue));
            }
          }
        }
    }
}
