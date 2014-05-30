package com.jonbanjo.tasks;

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

import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.jonbanjo.cups.CupsPrinter;
import com.jonbanjo.cups.operations.AuthInfo;
import com.jonbanjo.cups.ppd.CupsPpd;
import com.jonbanjo.cupsprint.PrintQueueConfig;

public class GetServicePpdTask implements Runnable{

	private PrintQueueConfig config;
	private byte[] md5;
	private CountDownLatch latch;
	private GetServicePpdListener taskListener;
	private CupsPpd cupsPpd;
	private Exception exception;
	private AuthInfo auth;

	public GetServicePpdTask(PrintQueueConfig config, AuthInfo auth, byte[] md5){
		this.config = config;
		this.auth = auth;
		this.md5 = md5;
	}
	
	public void setPpdTaskListener(GetServicePpdListener listener){
		this.taskListener = listener;
	}
	

	@Override
	public void run() {
		try {
			CupsPrinter printer = new CupsPrinter(new URL(config.getPrintQueue()));
			cupsPpd = new CupsPpd(auth);
			cupsPpd.createPpdRec(printer, md5);
			cupsPpd.setServiceResolution(config.getResolution());
			latch.countDown();
		}
		catch (Exception e){
			exception = e;
			System.err.println(e.toString());
		}
	}
	
	public Exception getException(){
		return exception;
	}
	
	public void get(boolean async, int priority){
		Thread t = new Thread(this);
		t.setPriority(priority);
		t.start();
		latch = new CountDownLatch(1);
		if (async){
			try{
				latch.await(7000, TimeUnit.MILLISECONDS);
			}
			catch (Exception e){
				exception = e;
				System.err.println(e.toString());
			}
		taskListener.onGetServicePpdTaskDone(cupsPpd, config, exception);
		}
	}
}
