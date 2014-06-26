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

import org.apache.http.Header;

public class AuthInfo{
    
    public final static int AUTH_OK = 0;
    public final static int AUTH_REQUESTED = 1;
    public final static int AUTH_REQUIRED = 2;
    public final static int AUTH_NOT_SUPPORTED = -1;
    public final static int AUTH_BAD = -2;

    String username = "";
    String password = "";
    int    reason = AUTH_OK;

    private Header httpHeader = null;
    private Header authHeader = null;
    private String type = "";
    
    public AuthInfo(){
    }
    
    public AuthInfo(String username, String password){
        this.username = username;
        this.password = password;
        this.type = "Basic";
        this.reason = AUTH_REQUESTED;
    }
    
    public void setUserPass(String username, String password){
        this.username = username;
        this.password = password;
        this.reason = AUTH_REQUESTED;
    }
    
    public Header getHttpHeader(){
        return httpHeader;
    }
    
    public Header getAuthHeader(){
        return authHeader;
    }
    
    public String getType(){
        return type;
    }
    
    void setHttpHeader(Header header){
      this.httpHeader = header;
      type = header.getValue().split(" ")[0];

      if ((type.equals("Basic"))  || (type.equals("Digest"))){
          reason = AUTH_REQUIRED;
      }
      else{
          reason = AUTH_NOT_SUPPORTED;
      }
    }
    
    protected void setAuthHeader(Header header){
        this.authHeader = header;
        reason = AUTH_OK;
    }
    
    
    public int getReason(){
        return reason;
    }
}
