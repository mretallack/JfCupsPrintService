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

import com.jonbanjo.cups.CupsClient;
import com.jonbanjo.cups.CupsPrinter;
import com.jonbanjo.cups.operations.AuthInfo;

import android.os.AsyncTask;


public class GetPrinterTask extends AsyncTask<Void, Void, Void>{

	protected CupsClient client;
	protected String queue;
	protected boolean extended;
	protected Exception exception;
	protected GetPrinterListener listener;
	protected CupsPrinter printer;
	protected AuthInfo auth;
	
	public GetPrinterTask(CupsClient client, AuthInfo auth, String queue, boolean extended){
		super();
		this.client = client;
		this.auth = auth;
		this.queue = queue;
		this.extended = extended;
	}
	
	public void setListener(GetPrinterListener listener){
		this.listener = listener;
	}
	
	public CupsPrinter getPrinter(){
		return printer;
	}
	
	public Exception getException(){
		return exception;
	}
	
	@Override
	protected Void doInBackground(Void... params){
		try {
			this.printer = client.getPrinter(queue, auth, extended);
		}
		catch (Exception e){
			exception = e;
			System.err.println(e.toString());
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void v){
		if (!this.isCancelled()){
			listener.onGetPrinterTaskDone(printer, exception);
		}
	}
}
