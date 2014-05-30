package com.jonbanjo.printservice;

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

import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jonbanjo.cups.CupsClient;
import com.jonbanjo.cups.CupsPrintJob;
import com.jonbanjo.cups.PrintRequestResult;
import com.jonbanjo.cups.operations.AuthInfo;
import com.jonbanjo.cups.ppd.CupsPpd;
import com.jonbanjo.cupsprint.CupsPrintApp;
import com.jonbanjo.cupsprint.PrintQueueConfig;
import com.jonbanjo.cupsprint.PrintQueueIniHandler;
import com.jonbanjo.tasks.PrintTask;
import com.jonbanjo.tasks.PrintTaskListener;

import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintAttributes.MediaSize;
import android.print.PrintJobId;
import android.print.PrintJobInfo;
import android.printservice.PrintDocument;
import android.printservice.PrintJob;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.widget.Toast;


public class JfPrintService extends PrintService implements PrintTaskListener{
	
	static ConcurrentHashMap<String, CupsPpd> capabilities;
	static CupsPpd jobPpd;
	static PrintJobId ppdJobId;

	@Override
	public void onCreate(){
		super.onCreate();
		capabilities = new ConcurrentHashMap<String, CupsPpd>();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	
	@Override
	protected PrinterDiscoverySession onCreatePrinterDiscoverySession() {
		return new JfPrinterDiscoverySession(this);
	}

	@Override
	protected void onPrintJobQueued(PrintJob job) {
		//job.start();
		String cupsString = "";
		String advString = job.getAdvancedStringOption("CupsString");
		if (advString == null){
			advString = "";
		}
		if (advString.equals("")){
			advString = "fit-to-page:boolean:true";
		}
		cupsString = addAttribute(cupsString, advString);
		PrintJobInfo jobInfo = job.getInfo();
		int copies = jobInfo.getCopies();
		cupsString = addAttribute(cupsString, "copies:integer:" + String.valueOf(copies));
		PageRange[] ranges = jobInfo.getPages();
		String rangeStr = "";
		for (PageRange range: ranges){
			int start = range.getStart() + 1;
			if (start < 1){
				start = 1;
			}
			int end = range.getEnd() + 1;
			if (end > 65535 || end < 1){
				end = 65535;
			}
			String rangeTmp;
			String from = String.valueOf(start);
			String to   = String.valueOf(end);
			if (from.equals(to)){
				rangeTmp = from;
			}
			else {
				rangeTmp = from + "-" + to;
			}
			if (rangeStr.equals("")){
				rangeStr = rangeTmp;
			}
			else {
				rangeStr = rangeStr + "," + rangeTmp;
			}
			
		}
		//rangeStr = "1-4";
		if (rangeStr.equals("1-65535")){
			rangeStr = "";
		}
		if (!(rangeStr.equals(""))){
			cupsString = addAttribute(cupsString, "page-ranges:setOfRangeOfInteger:" + rangeStr);
		}
		PrintAttributes attributes = jobInfo.getAttributes();
		MediaSize mediaSize = attributes.getMediaSize();
		String tmp = mediaSize.getId();
		cupsString = addAttribute(cupsString, "PageSize:keyword:"+ tmp);
		if (mediaSize.isPortrait()){
			cupsString = addAttribute(cupsString, "orientation-requested:enum:3");
		}
		else {
			cupsString = addAttribute(cupsString, "orientation-requested:enum:4");
		}
		//Resolution resolution = attributes.getResolution();
		//int colorModel = attributes.getColorMode();
		Map<String, String>cupsAttributes = null;
		if (!(cupsString.equals(""))){
			cupsAttributes = new HashMap<String, String>();
			cupsAttributes.put("job-attributes", cupsString);
		}
		PrintDocument document = job.getDocument();
/*
		FileInputStream infile = new ParcelFileDescriptor.AutoCloseInputStream(document.getData());
		try {
			File file = new File(Environment.getExternalStoragePublicDirectory(
			            Environment.DIRECTORY_PICTURES), document.getInfo().getName());
			FileOutputStream outfile = new FileOutputStream(file);
			byte[] buffer = new byte[4096];
			int offset = 0;
			int bytesRead;
			while ((bytesRead = infile.read(buffer))>0){
				outfile.write(buffer, 0, bytesRead);
				offset = offset + bytesRead;
			}
		    outfile.close();
		      			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (true){
			return;
		}*/
		FileInputStream file = new ParcelFileDescriptor.AutoCloseInputStream(document.getData());
		String fileName = document.getInfo().getName();
	    CupsPrintJob cupsPrintJob = new CupsPrintJob(file, fileName);
	    if (attributes != null){
	    	cupsPrintJob.setAttributes(cupsAttributes);
	    }
	    String nickname = jobInfo.getPrinterId().getLocalId();
		PrintQueueIniHandler ini = new PrintQueueIniHandler(CupsPrintApp.getContext());
		PrintQueueConfig config = ini.getPrinter(nickname);
		if (config == null){
			job.fail("Printer Config not found");
			return;
		}
	    URL clientURL;
	    try {
	    	clientURL = new URL(config.getClient());
	    }catch (Exception e){
	    	System.err.println(e.toString());
	    	job.fail("Invalid print queue: " + config.getClient());
	    	return;
	    }
	    CupsClient cupsClient = new CupsClient(clientURL);
	    cupsClient.setUserName(config.getUserName());
	    AuthInfo auth = null;
	    if (!(config.getPassword().equals(""))){
	    	auth = new AuthInfo(config.getUserName(), config.getPassword());
	    }
	    PrintTask printTask = new PrintTask(cupsClient, config.getQueuePath());
		printTask.setJob(cupsPrintJob, auth);
		printTask.setServicePrintJob(job);
		printTask.setListener(this);
		job.start();
		printTask.execute();
	}
	
	private String addAttribute(String cupsString, String attribute){
		
		if (attribute == null){
			return cupsString;
		}
	
		if (attribute.equals("")){
			return cupsString;
		}
		
		if (cupsString.equals("")){
			return attribute;
		}
		return cupsString + "#" + attribute;
	}

	@Override
	protected void onRequestCancelPrintJob(PrintJob job) {
	}
	

	//@Override
	public void onPrintTaskDone(PrintTask task) {
		
		Exception exception = task.getException();
		PrintJob servicePrintJob = task.getServicePrintJob();
		String jobname = servicePrintJob.getDocument().getInfo().getName();
		String errmsg = "JfCupsPrint " + jobname + ":\n";
		
		if (exception != null){
			Toast.makeText(this, errmsg + exception.toString(), Toast.LENGTH_LONG).show();
			servicePrintJob.cancel();
			return;
		}
		
		PrintRequestResult result = task.getResult();
		if (result == null){
			Toast.makeText(this, errmsg + "Print job returned null", Toast.LENGTH_LONG).show();
			servicePrintJob.cancel();
	    	return;
	    }
		
		Toast.makeText(this, errmsg + result.getResultDescription(), Toast.LENGTH_LONG).show();
		servicePrintJob.complete();
	}

	
}
