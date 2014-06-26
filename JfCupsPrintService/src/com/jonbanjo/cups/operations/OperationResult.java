package com.jonbanjo.cups.operations;

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

import com.jonbanjo.vppserver.ippclient.IppResult;

public class OperationResult {
    
    private IppResult ippResult;
    private String httpResult;
    private AuthInfo authInfo;
    
    
    public void setIppResult(IppResult result){
        ippResult = result;
    }
    
    public IppResult getIppResult(){
        return ippResult;
    }
    
    public void setHttResult(String result){
        httpResult = result;
    }
    
    public String getHttpStatusResult(){
        return httpResult;
    }
    
    public AuthInfo getAuthInfo(){
        return authInfo;
    }
    
    public void setAuthInfo(AuthInfo auth){
        authInfo = auth;
    }
    
}
