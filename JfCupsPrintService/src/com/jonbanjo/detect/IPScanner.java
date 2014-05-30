package com.jonbanjo.detect;

/*
JfCupsPrintService
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

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.List;

import com.jonbanjo.cups.CupsClient;
import com.jonbanjo.cups.CupsPrinter;
import com.jonbanjo.cups.operations.AuthInfo;

public class IPScanner implements Runnable{
    String ipbase;
    int port = 631;
    String username;
    String password;
    
    public IPScanner(String ipbase, int port, String username, String password){
        this.ipbase = ipbase;
        this.port = port;
        this.username = username;
        this.password = password;
    }
    
    @Override
    public void run() {
        IPTester.portTesters.incrementAndGet();
        Socket s = null;
        try {
            int ipnum;
            String ip = "";
            ipnum=IPTester.portIps.take();
            while (ipnum!= -1){
                try {
                    int hi = (ipnum & 0xFF00) >> 8;
                    int lo = ipnum & 0xFF;
                    ip = ipbase + "." + Integer.toString(hi) + "." + Integer.toString(lo);
                    s = new Socket();
                    s.connect(new InetSocketAddress(ip, port),IPTester.TIMEOUT);
                    s.close();
                    //System.out.println(ip + " open");
                    try {
                    	AuthInfo auth = null;
                    	if (!(password.equals(""))){
                    		auth = new AuthInfo(username, password);
                    	}
                    	CupsClient cupsClient = new CupsClient(
                            new URL("http://" + ip + ":" + port), "");
                    	cupsClient.setUserName(username);
                    	List<CupsPrinter> pList = cupsClient.listPrinters(auth);
                    	for (CupsPrinter p: pList){
                    		PrinterRec rec = new PrinterRec(
                                p.getDescription(),
                                "http",
                                ip,
                                port,
                                p.getName()
                                );
                    		IPTester.httpResults.printerRecs.add(rec);
                    		//System.out.println(p.getName());
                    	}
                    }catch (Exception e){}
                    try {
                    	CupsClient cupsClient = new CupsClient(
                            new URL("https://" + ip + ":" + port), "");
                    	cupsClient.setUserName(username);
                    	AuthInfo auth = null;
                    	if (!(password.equals(""))){
                    		auth = new AuthInfo(username, password);
                    	}
                    	List<CupsPrinter> pList = cupsClient.listPrinters(auth);
                    	for (CupsPrinter p: pList){
                    		PrinterRec rec = new PrinterRec(
                                p.getDescription(),
                                "https",
                                ip,
                                port,
                                p.getName()
                                );
                        IPTester.httpsResults.printerRecs.add(rec);
                        //System.out.println(p.getName());
                    	}
                    }catch (Exception e){
                    	System.out.println(e.toString());
                    	if (e.getMessage().contains("No Certificate")){
                    		IPTester.httpsResults.errors.add("https://" + ip + ":" + port + ": No SSL cetificate\n");
                    	}
                    }
                }
                catch (Exception e){
                    //System.out.println(e.toString());
                    //System.out.println(ip + "closed");
                }
                IPTester.tested.incrementAndGet();
                ipnum=IPTester.portIps.take();
            }
        }
        catch (Exception e){
            System.out.println(e.toString());
         }
        finally {
            if (s != null){
                if (!s.isClosed()){
                    try{
                        s.close();
                    }
                    catch(Exception e){}
                }
            }              
        IPTester.portTesters.decrementAndGet();
        }
    }
}