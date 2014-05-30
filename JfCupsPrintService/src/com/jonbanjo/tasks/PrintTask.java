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

import android.os.AsyncTask;
import android.printservice.PrintJob;

import com.jonbanjo.cups.CupsClient;
import com.jonbanjo.cups.CupsPrintJob;
import com.jonbanjo.cups.PrintRequestResult;
import com.jonbanjo.cups.operations.AuthInfo;

public class PrintTask extends AsyncTask<Void, Void, Void>{
	
	private CupsClient client;
	private String queue;
	private CupsPrintJob cupsPrintJob;
	private AuthInfo auth;
	private Exception exception;
	private PrintRequestResult result;
	private PrintTaskListener listener;	
	private PrintJob servicePrintJob;
	
	
	public PrintTask(CupsClient client, String queue){
		super();
		this.client = client;
		this.queue = queue;
	}
	
	public void setListener(PrintTaskListener listener){
		this.listener = listener;
	}
	
	public void setJob(CupsPrintJob job, AuthInfo auth){
		this.cupsPrintJob = job;
		this.auth = auth;
        System.setProperty("java.net.preferIPv4Stack" , "true"); 
	}
	
	public void setServicePrintJob(PrintJob servicePrintJob){
		this.servicePrintJob = servicePrintJob;
	}

	public android.printservice.PrintJob getServicePrintJob(){
		return this.servicePrintJob;
	}
	
	public Exception getException(){
		return exception;
	}
	
	public PrintRequestResult getResult(){
		return result;
	}
	
	public CupsPrintJob getCupsPrintJob(){
		return cupsPrintJob;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			result = client.print(queue, cupsPrintJob, auth);
		}catch (Exception e){
			this.exception = e;
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void v){
			listener.onPrintTaskDone(this);
	}
}
