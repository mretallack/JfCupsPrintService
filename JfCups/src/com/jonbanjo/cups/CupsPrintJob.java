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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

public class CupsPrintJob {
    private InputStream document;
    private String jobName;
    private Map<String, String> attributes;

    public CupsPrintJob(byte[] document, String jobName) {
        this.document = new ByteArrayInputStream(document);
        this.jobName = jobName;
    }

    public CupsPrintJob(InputStream document, String jobName) {
        this.document = document;
        this.jobName =  jobName;    }


    public Map<String, String> getAttributes() {
        return attributes;
    }

    public InputStream getDocument() {
        return document;
    }

    public void setAttributes(Map<String, String> printJobAttributes) {
        this.attributes = printJobAttributes;
    }

    public String getJobName() {
        return jobName;
    }
    
}
