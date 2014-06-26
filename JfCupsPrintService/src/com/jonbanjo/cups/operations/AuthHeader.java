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

import org.apache.http.HttpRequest;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.DigestScheme;

public class AuthHeader {
    
    static void makeAuthHeader(HttpRequest request, AuthInfo auth){
      
        if (auth.reason == AuthInfo.AUTH_NOT_SUPPORTED){
            return;
        }
      
        if (auth.username.equals("") || (auth.password.equals(""))){
            auth.reason = AuthInfo.AUTH_REQUIRED;
            return;
        }
      
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(auth.username, auth.password);
        if (auth.getType().equals("Basic")){
            BasicScheme basicScheme = new BasicScheme();
            try {
                auth.setAuthHeader(basicScheme.authenticate(creds, request));
            }catch (Exception e){
                System.err.println(e.toString());
                auth.reason = AuthInfo.AUTH_BAD;
            }
        }
        else if (auth.getType().equals("Digest")){
            try {
                DigestScheme digestScheme = new DigestScheme();
                digestScheme.processChallenge(auth.getHttpHeader());
                auth.setAuthHeader(digestScheme.authenticate(creds, request));
                String test0 = auth.getHttpHeader().getValue();
                String test1 = auth.getAuthHeader().getValue();
                System.out.println();
            }catch (Exception e){
              System.err.println(e.toString());
              auth.reason = AuthInfo.AUTH_BAD;
            }
        }
        else {
          auth.reason = AuthInfo.AUTH_NOT_SUPPORTED;
        }
    }
}
