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

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class IPTester {
    static final int MAX_PORT_TESTERS = 50;
    static final int TIMEOUT = 500;
    static AtomicInteger portTesters = new AtomicInteger(0);
    static AtomicInteger tested = new AtomicInteger(0);
    static LinkedBlockingQueue<Integer> portIps =
            new LinkedBlockingQueue<Integer>();
    static PrinterResult httpsResults;
    static PrinterResult httpResults;
    int numTesters = 0;
    String username;
    String password;
    
    ProgressUpdater updater;
    
    public IPTester(ProgressUpdater updater){
    	this.updater = updater;
    }
    
    //mask range 16 - 32
    public PrinterResult getPrinters(String ip, int mask, int port, String username, String password){
        tested.set(0);
        portTesters.set(0);
    	httpResults = new PrinterResult();
    	httpsResults = new PrinterResult();
        String[] ipparts = ip.split("\\.");
        this.username = username;
        this.password = password;
        int addr;
        int addresses;
        try {
            ip = ipparts[0] + "." + ipparts[1];
            int ipnum = (Integer.parseInt(ipparts[2]) * 256);
            ipnum = ipnum + Integer.parseInt(ipparts[3]) +1;
        
            addresses = 1 << (32 - mask);
            mask = 32 - mask;
            addr = (ipnum >> mask) << mask;
        }
        catch (Exception e){
            System.out.println(e.toString());
            return new PrinterResult();
        }

        numTesters = addresses / 10;
        if (numTesters > MAX_PORT_TESTERS){
            numTesters = MAX_PORT_TESTERS;
        }
        for (int i=0; i<numTesters; i++){
            new Thread(new IPScanner(ip, port, username, password)).start();
        }
        try {
            for (int i=addr; i<addr+addresses; i++){
                   portIps.put(i);
            }
        }
        catch (Exception e){
            
        }
        for (int i=0; i< numTesters; i++){
            try {
                portIps.put(-1);
            }catch (Exception e){
                System.out.println(e.toString());
                return new PrinterResult();
            }
        }
        doProgress(addresses);
        new Merger().merge(httpResults.printerRecs, httpsResults.printerRecs);
        return httpsResults; 
    }
    
    public void doProgress(int addresses){
        while (portTesters.get() >0){
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
            int done = tested.get();
                updater.DoUpdate((done * 100) / addresses);
        }
        
    }
    
    public void stop(){
    	portIps.clear();
    	for (int i=0; i <= (numTesters); i++){
    		try {
    			portIps.put(-1);
    		}
    		catch (Exception e){
    			System.out.println(e.toString());
    		}
    	}
    }

}
