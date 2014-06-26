package com.jonbanjo.cups;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jonbanjo.vppserver.ippclient.IppResult;
import com.jonbanjo.vppserver.schema.ippclient.Attribute;
import com.jonbanjo.vppserver.schema.ippclient.AttributeGroup;
import com.jonbanjo.cups.operations.OperationResult;

public class PrintRequestResult {
    private int jobId;
    private String resultCode = "";
    private String resultDescription = "";
    private AuthInfo auth;

    public PrintRequestResult(OperationResult result) {
        auth = result.getAuthInfo();
        jobId = -1;
        
        if ((result == null) || isNullOrEmpty(result.getHttpStatusResult())) {
            return;
        }
        
        initializeFromHttpStatusResponse(result.getHttpStatusResult());
        
        try {
            int code = Integer.parseInt(resultCode);
            if ((code != 100) && (code != 200)){
                return;
            }
        }catch (Exception e){}
        
        if (result.getIppResult().getIppStatusResponse() != null) {
            initializeFromIppStatusResponse(result.getIppResult());
        }
        
        for (AttributeGroup group : result.getIppResult().getAttributeGroupList()) {
            if (group.getTagName().equals("job-attributes-tag")) {
                for (Attribute attr : group.getAttribute()) {
                    if (attr.getName().equals("job-id")) {
                        jobId = Integer.parseInt(attr.getAttributeValue().get(0).getValue());
                        break;
                    }
                }
                break;
            }
        }
    }

    private void initializeFromIppStatusResponse(IppResult ippResult) {
        Pattern p = Pattern.compile("Status Code:(0x\\p{XDigit}+)(.*)");
        Matcher m = p.matcher(ippResult.getIppStatusResponse());
        if (m.find()) {
        this.resultCode = m.group(1);
        this.resultDescription = m.group(2);
        }
    }

    private void initializeFromHttpStatusResponse(String result) {
        Pattern p = Pattern.compile("HTTP/1.\\d (\\d+) (.*)");
        Matcher m = p.matcher(result);
        if (m.find()) {
            this.resultCode = m.group(1);
            this.resultDescription = m.group(2);
        }
    }

    public AuthInfo getAuthInfo(){
        return auth;
    }
    
    private boolean isNullOrEmpty(String string) {
        return (string == null) || ("".equals(string.trim()));
    }

    public boolean isSuccessfulResult() {
        return (resultCode != null) && resultCode.startsWith("0x00");
    }

    public String getResultCode() {
        return resultCode;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public int getJobId() {
        return jobId;
    }

    protected void setJobId(int jobId) {
        this.jobId = jobId;
    }

}
