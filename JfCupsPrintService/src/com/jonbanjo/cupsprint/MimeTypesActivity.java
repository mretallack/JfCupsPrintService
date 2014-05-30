package com.jonbanjo.cupsprint;

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

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.jonbanjo.cups.CupsClient;
import com.jonbanjo.cups.CupsPrinter;
import com.jonbanjo.cups.operations.AuthInfo;
import com.jonbanjo.cupsprintservice.R;
import com.jonbanjo.tasks.GetPrinterListener;
import com.jonbanjo.tasks.GetPrinterTask;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class MimeTypesActivity extends Activity implements GetPrinterListener {

	PrintQueueConfig printConfig;
	GetPrinterTask task;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mime_types);
		Intent intent = getIntent();
		String sPrinter = intent.getStringExtra("printer");
		PrintQueueIniHandler ini = new PrintQueueIniHandler(getBaseContext());
	    printConfig = ini.getPrinter(sPrinter);
	    if (printConfig == null){
			Util.showToast(this, "Config for " + sPrinter + " not found");
			finish();
	    	return;
		}
		CupsClient client;
	    try {
	    	client = new CupsClient(Util.getClientURL(printConfig));
	    }
	    catch (Exception e){
	    	Util.showToast(this, e.getMessage());
	    	finish();
	    	return;
	    }
	    AuthInfo auth = null;
	    if (!(printConfig.getPassword().equals(""))){
	    	auth = new AuthInfo(printConfig.getUserName(), printConfig.getPassword());
	    }
	    task = new GetPrinterTask(client, auth, Util.getQueue(printConfig),true);
	    task.setListener(this);
	    try {
	    	task.execute().get(5000, TimeUnit.MILLISECONDS);
	    }
	    catch (Exception e){
	    	Util.showToast(this, e.toString());
	    	finish();
	    	return;
	    }
	    Exception exception = task.getException();
	    
	    if (exception != null){
	    	Util.showToast(this, exception.getMessage());
	    	finish();
	    	return;
	    }
	    
	    CupsPrinter printer = task.getPrinter();
	    if (printer == null){
	    	Util.showToast(this, printConfig.nickname + " not found");
	    	finish();
	    	return;
	    }
	    
	    ArrayList<String> mimeTypes = printer.getSupportedMimeTypes();
	    if (mimeTypes.size() == 0){
	    	Util.showToast(this, "Unable to get mime types for " + printConfig.nickname);
	    	finish();
	    	return;
	    }
	    
		TextView mimeList = (TextView) findViewById(R.id.mimeList);
		String S = printConfig.nickname + "\n\n"; 
	    for(String type: mimeTypes){
	    	S = S + type + "\n";
	    }
		mimeList.setText(S);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.aboutmenu, menu);
		return true;
	}


	@Override
	public void onGetPrinterTaskDone(CupsPrinter printer, Exception exception) {
	}

}
