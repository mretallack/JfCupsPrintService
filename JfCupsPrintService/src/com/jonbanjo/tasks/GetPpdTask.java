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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.jonbanjo.cups.CupsPrinter;
import com.jonbanjo.cups.ppd.CupsPpd;

public class GetPpdTask implements Runnable{

	private CupsPrinter printer;
	private Exception exception;
	private byte[] md5;
	private CountDownLatch latch;
	private GetPpdListener taskListener;
	private CupsPpd cupsPpd;

	public GetPpdTask(CupsPrinter printer, CupsPpd cupsPpd, byte[] md5){
		this.printer = printer;
		this.cupsPpd = cupsPpd;
		this.md5 = md5;
	}
	
	public void setPpdTaskListener(GetPpdListener listener){
		this.taskListener = listener;
	}
	

	@Override
	public void run() {
		try {
			cupsPpd.createPpdRec(printer, md5);
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
	
	public void execute(final int priority){
		
		Runnable runner = new Runnable(){

			@Override
			public void run() {
				Thread t = new Thread(this);
				t.setPriority(priority);
				t.start();
				latch = new CountDownLatch(1);
				try{
					latch.await(7000, TimeUnit.MILLISECONDS);
				}
				catch (Exception e){
					exception = e;
					System.err.println(e.toString());
				}
				taskListener.onGetPpdTaskDone(exception);
			}
		};
		runner.run();
	}
	
	public void get(int priority){
		Thread t = new Thread(this);
		t.setPriority(priority);
		t.start();
		latch = new CountDownLatch(1);
		try{
			latch.await(7000, TimeUnit.MILLISECONDS);
		}
		catch (Exception e){
			exception = e;
			System.err.println(e.toString());
		}
	}
}
