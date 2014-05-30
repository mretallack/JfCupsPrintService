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

import java.util.List;
import java.util.Locale;

import com.jonbanjo.cups.CupsClient;
import com.jonbanjo.cups.CupsPrinter;
import com.jonbanjo.cups.operations.AuthInfo;
import com.jonbanjo.cupsprintservice.R;
import com.jonbanjo.detect.HostScanTask;
import com.jonbanjo.detect.MdnsScanTask;
import com.jonbanjo.detect.PrinterRec;
import com.jonbanjo.detect.PrinterResult;
import com.jonbanjo.detect.PrinterUpdater;
import com.jonbanjo.tasks.GetPrinterListener;
import com.jonbanjo.tasks.GetPrinterTask;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;


public class PrinterAddEditActivity extends Activity implements PrinterUpdater, GetPrinterListener{

	Spinner  protocol;
	EditText nickname;
	EditText host;
	EditText port;
	EditText queue;
	EditText userName;
	EditText password;
	EditText extensions;
	EditText resolution;
	Spinner orientation;
	CheckBox fitToPage;
	CheckBox fitPlot;
	CheckBox noOptions;
	CheckBox isDefault;
	Spinner showIn;
	String oldPrinter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_printer_add_edit);
		Intent intent = getIntent();
		
		oldPrinter = intent.getStringExtra("printer");
		nickname = (EditText) findViewById(R.id.editNickname);
		protocol = (Spinner) findViewById(R.id.editProtocol);
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, 
 				android.R.layout.simple_spinner_item, EditControls.protocols);
		protocol.setAdapter(aa);
		host = (EditText) findViewById(R.id.editHost);
		port = (EditText) findViewById(R.id.editPort);
		queue = (EditText) findViewById(R.id.editQueue);
		userName = (EditText) findViewById(R.id.editUserName);
		password = (EditText) findViewById(R.id.editPassword);
		orientation = (Spinner) findViewById(R.id.editOrientation);
		extensions = (EditText) findViewById(R.id.editExtensions);
		resolution = (EditText) findViewById(R.id.editResolution);
		ArrayAdapter<Pair> aa1 = new ArrayAdapter<Pair>(this, 
	 				android.R.layout.simple_spinner_item, EditControls.orientationOpts);
	 	orientation.setAdapter(aa1);

		fitToPage = (CheckBox) findViewById(R.id.editFitToPage);
		noOptions = (CheckBox) findViewById(R.id.editNoOptions);
		isDefault = (CheckBox) findViewById(R.id.editIsDefault);
		showIn = (Spinner) findViewById(R.id.editShowIn);
		ArrayAdapter<String> aa2 = new ArrayAdapter<String>(this, 
 				android.R.layout.simple_spinner_item, EditControls.showInOpts);
		showIn.setAdapter(aa2);
		if (!oldPrinter.contentEquals("")){
		     PrintQueueIniHandler ini = new PrintQueueIniHandler(getBaseContext());
		     PrintQueueConfig conf = ini.getPrinter(oldPrinter);
		     if (conf != null){
		    	 int size = EditControls.protocols.size();
		    	 int pos = 0;
		 		 for (pos=0; pos<size; pos++){
		 			 String test = EditControls.protocols.get(pos);
		 			 if (test.equals(conf.protocol)){
		 				 protocol.setSelection(pos);
		 				 break;
		 			 }
		 		 }
		 		 nickname.setText(conf.nickname);
		    	 host.setText(conf.host);
		    	 port.setText(conf.port);
		    	 queue.setText(conf.queue);
		    	 userName.setText(conf.userName);
		    	 password.setText(conf.password);
		    	 size = EditControls.orientationOpts.size();
		    	 pos = 0;
		 		 for (pos=0; pos<size; pos++){
		 			 Pair opt = EditControls.orientationOpts.get(pos);
		 			 if (opt.option.equals(conf.orientation)){
		 				 orientation.setSelection(pos);
		 				 break;
		 			 }
		 		 }
		 		 fitToPage.setChecked(conf.imageFitToPage);
		 		 noOptions.setChecked(conf.noOptions);
		 		 isDefault.setChecked(conf.isDefault);
		    	 size = EditControls.showInOpts.size();
		 		 for (pos=0; pos<size; pos++){
		 			 String test = EditControls.showInOpts.get(pos);
		 			 if (test.equals(conf.showIn)){
		 				 showIn.setSelection(pos);
		 				 break;
		 			 }
		 		 }

		 		 extensions.setText(conf.extensions);
		 		 resolution.setText(conf.resolution);
		     }
		}
		if (oldPrinter.equals("")){
			port.setText("631");
			fitToPage.setChecked(true);
		}
			
	}

	  
   @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scanmenu, menu);
		getMenuInflater().inflate(R.menu.certificatemenu, menu);
		getMenuInflater().inflate(R.menu.aboutmenu, menu);
		return true;
	}

	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case R.id.about:
	    		Intent intent = new Intent(this, AboutActivity.class);
	    		intent.putExtra("printer", "");
	    		startActivity(intent);
	    		break;
	    	case R.id.certificates:
	    		intent = new Intent(this, CertificateActivity.class);
	    		intent.putExtra("host", host.getText().toString());
	    		intent.putExtra("port", port.getText().toString());
	    		startActivity(intent);
	    		break;
	    	case R.id.scanhost:
	    	    String user = userName.getText().toString();
	    	    if (user.equals("")){
	    	    	user = "anonymous";
	    	    }
	    	    String passwd = password.getText().toString();
	    		new HostScanTask(this, this, user, passwd).execute();
	    		break;
	    	case R.id.scanmdns:
	    		new MdnsScanTask(this, this).execute();
	    		break;
	    }
	    return super.onContextItemSelected(item);
	 }

	private void alert(String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		       .setTitle("error");
		AlertDialog dialog = builder.create();	
		dialog.show();
	}
	
	
	private boolean checkEmpty(String fieldName, String value){
		if (value.equals("")){
			alert(fieldName + " missing");
			return false;
		}
		return true;
	}
	
	private boolean checkInt(String fieldName, String value){
		
		try {
			@SuppressWarnings("unused")
			int test = Integer.parseInt(value);
			return true;
		}
		catch (Exception e){
			alert(fieldName + " must be an integer");
			return false;
		}
	}
	
	private boolean checkResolution(String fieldName, String resolution){
		if (resolution.equals("")){
			return true;
		}
		String[] dpis = resolution.split("x");
		if (dpis.length != 2){
			alert(fieldName + " must be empty\nOr in the format <integer>x<integer> ");
			return false;
		}
		try {
			@SuppressWarnings("unused")
			int x = Integer.parseInt(dpis[0]);
			x = Integer.parseInt(dpis[1]);
		}catch (Exception e){
			alert(fieldName + " must be empty\nOr in the format <integer>x<integer> ");
			return false;
		}
		return true;
	}
	
	private boolean checkExists(String name, PrintQueueIniHandler ini){
		
		if (oldPrinter.equals(name))
			return false;
		if (!ini.printerExists(name))
			return false;
		
		alert("Duplicate nickname: " + name);
		return true;
				
	}
	
	private void showResult(String title, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		       .setTitle(title);
		AlertDialog dialog = builder.create();	
		try {
			dialog.show();
		}catch (Exception e){}
	}
	
	public void testPrinter(View view){
		PrintQueueConfig printConfig = new PrintQueueConfig(
				nickname.getText().toString(),
				(String)protocol.getSelectedItem(),
					host.getText().toString(),
					port.getText().toString(),
					queue.getText().toString());
		
	    CupsClient client;
	    try {
	    	client = new CupsClient(Util.getClientURL(printConfig));
	    }
	    catch (Exception e){
	    	showResult("Failed", e.getMessage());
	    	return;
	    }
   
	    String user = userName.getText().toString();
	    if (user.equals("")){
	    	user = "anonymous";
	    }
	    client.setUserName(user);
	    String passwd = password.getText().toString();
	    AuthInfo auth = null;
	    if (!(passwd.equals(""))){
	    	auth = new AuthInfo(user,passwd);
	    }
	    
	    GetPrinterTask task = new GetPrinterTask(client, auth, Util.getQueue(printConfig), false);
	    task.setListener(this);
	    task.execute();
	}
	
	
	@Override
	public void onGetPrinterTaskDone(CupsPrinter printer, Exception exception) {
	    
		if (exception != null){
	    	showResult("Failed", exception.getMessage());
	    	return;
	    }
	    
	    if (printer == null){
	    	showResult("Failed", "Printer not found");
	    	return;
	    }
	    
	    String result = "Name: " + printer.getName() +
				"\nDescription: " + printer.getDescription() +
				"\nMake: " + printer.getMake() +
				"\nLocation: " + printer.getLocation();
			    
		showResult("Success", result);
	}
	
	public void savePrinter(View view) {
	     final PrintQueueIniHandler ini = new PrintQueueIniHandler(getBaseContext());
	     String sNickname = nickname.getText().toString().trim();
	     if (!checkEmpty("Nickname", sNickname)){
	    	 nickname.requestFocus();
	    	 return;
	     }
	     if (checkExists(sNickname, ini)){
	    	 nickname.requestFocus();
	    	 return;
	     }
	     String sHost = host.getText().toString().trim();
	     if (!checkEmpty("Host", sHost)){
	    	 host.requestFocus();
	    	 return;
	     }
	     String sPort = port.getText().toString().trim();
	     if (!checkEmpty("Port", sPort)){
	    	 port.requestFocus();
	    	 return;
	     }
	     if (!checkInt("Port", sPort)){
	    	 port.requestFocus();
	    	 return;
	     }
	     String sQueue = queue.getText().toString().trim();
	     if (!checkEmpty("Queue", sQueue)){
	    	 queue.requestFocus();
	    	 return;
	     }
	     String sUserName = userName.getText().toString().trim();
	     if (sUserName.equals("")){
	    	 sUserName = "anonymous";
	     }
	     String sProtocol = (String) protocol.getSelectedItem();
	     String sPassword = password.getText().toString().trim();
	     String sResolution = resolution.getText().toString().trim().toLowerCase(Locale.US);
	     if (!checkResolution("Resolution", sResolution)){
	    	 resolution.requestFocus();
	    	 return;
	     }
	     String sShowIn = (String) showIn.getSelectedItem();
	     final PrintQueueConfig conf = new PrintQueueConfig(sNickname, sProtocol, sHost, sPort, sQueue);
	     if (checkExists(conf.getPrintQueue(), ini)){
	    	 host.requestFocus();
	    	 return;
	     }
	     conf.userName = sUserName;
	     conf.password = sPassword;
	     Pair opt = (Pair) orientation.getSelectedItem();
	     conf.orientation = opt.option;
	     conf.extensions = extensions.getText().toString().trim();
	     conf.imageFitToPage = fitToPage.isChecked();
	     conf.noOptions = noOptions.isChecked();
	     conf.isDefault = isDefault.isChecked();
	     conf.resolution = sResolution;
	     conf.showIn = sShowIn;
	     if ((conf.protocol.equals("http")) && (!(conf.password.equals("")))){
	         AlertDialog.Builder builder = new AlertDialog.Builder(this);
	         builder.setTitle("Warning: Using password with http protocol")
	         		.setMessage("This will result in both your username and password being sent over the network as plain text. "
	         				+ "Using the https protocol is reccomended for authentication.")
	                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int id) {
	                    	doSave(ini, conf);
	                    }
	                })
	                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int id) {
	                    }
	                });
	         builder.create().show();
	    	 return;
	     }
	     doSave(ini, conf);
	}
	     
	public void doSave(PrintQueueIniHandler ini, PrintQueueConfig conf){
	     ini.addPrinter(conf, oldPrinter);
	     CupsPrintApp.getPrinterDiscovery().updateStaticConfig();
		 Intent intent = new Intent(this, PrinterMainActivity.class);
	     startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	}
	
	public void getDetectedPrinter(PrinterResult results){
			
			List<String> errors = results.getErrors();
			final List<PrinterRec> printers = results.getPrinters();
			if (errors.size() > 0){
				String errorMessage = "";
				for (String error: errors){
					errorMessage = errorMessage + error + "\n";
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Scan messages");
				builder.setMessage(errorMessage);
				builder.setOnCancelListener(new OnCancelListener() {
				    public void onCancel(final DialogInterface dialog) {
				    	chooseDetectedPrinter(printers);
				    }
				});
				AlertDialog dialog = builder.create();
				dialog.show();
			}
			else {
				chooseDetectedPrinter(printers);
			}
		}
			
	public void chooseDetectedPrinter(List<PrinterRec> printers){
			if (printers.size() < 1){
				showResult("", "No printers found");
				return;
			}
			final ArrayAdapter<PrinterRec> aa = new ArrayAdapter<PrinterRec>(
					this, android.R.layout.simple_list_item_1,printers); 
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Select printer");
			builder.setAdapter(aa, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					PrinterRec printer = aa.getItem(which);
					int size = protocol.getCount();
					int i;
					for (i=0; i<size; i++){
						if (protocol.getItemAtPosition(i).equals(printer.getProtocol())){
							protocol.setSelection(i);
							break;
						}
					}
					nickname.setText(printer.getNickname());
					protocol.setSelection(i);
					host.setText(printer.getHost());
					port.setText(String.valueOf(printer.getPort()));
					queue.setText(printer.getQueue());					
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		
}
