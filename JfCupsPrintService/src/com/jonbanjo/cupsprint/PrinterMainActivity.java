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

import com.jonbanjo.cupsprintservice.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PrinterMainActivity extends Activity {

	ListView printersListView;
	ArrayList<String> printersArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_printer_main);
		printersListView=(ListView) findViewById(R.id.printersListView);
		printersListView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View view,
	                int position, long id) {
	        	setOperation(position);
	        	}
			});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.addprintermenu, menu);
		getMenuInflater().inflate(R.menu.certificatemenu, menu);
		getMenuInflater().inflate(R.menu.aboutmenu, menu);
		return true;
	}
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case R.id.addprinter:
	    		addPrinter();
	    		break;
	    	case R.id.about:
	    		Intent intent = new Intent(this, AboutActivity.class);
	    		intent.putExtra("printer", "");
	    		startActivity(intent);
	    		break;
	    	case R.id.certificates:
	    		intent = new Intent(this, CertificateActivity.class);
	    		startActivity(intent);
	    		break;
	    }
	    return super.onContextItemSelected(item);
	 }
	
	@Override
	public void onStart(){
		super.onStart();
		PrintQueueIniHandler ini = new PrintQueueIniHandler(getBaseContext());
		printersArray = ini.getPrintQueueConfigs();
		if (printersArray.size() == 0){
			new AlertDialog.Builder(this)
			.setTitle("")
			.setMessage("No printers are configured. Add new printer?")
			.setIcon(android.R.drawable.ic_input_add)
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

			    public void onClick(DialogInterface dialog, int whichButton) {
			    	addPrinter();
			    }})
			 .setNegativeButton(android.R.string.no, null).show();	
			
		}
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, 
			android.R.layout.simple_list_item_1, printersArray);
		printersListView.setAdapter(aa);
	}
	
	private void setOperation(final int index) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    String[] items = {"Edit", "Delete", "Jobs", "Mime Types"}; 
	    final String nickname = printersArray.get(index);
	    builder.setTitle(nickname)
	           .setItems(items, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   doOperation(nickname, which);
	           }
	    });
	    AlertDialog dialog = builder.create();
	    dialog.show();
	}
	
	private void doOperation(final String nickname, final int op){
		
		if (op == 0){
			Intent intent = new Intent(this, PrinterAddEditActivity.class);
			intent.putExtra("printer", nickname);
			startActivity(intent);
		}
		else if (op == 1){
			
			new AlertDialog.Builder(this)
			.setTitle("Confim")
			.setMessage("Delete " + nickname + "?")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

			    public void onClick(DialogInterface dialog, int whichButton) {
			    	doDelete(nickname);
			    }})
			 .setNegativeButton(android.R.string.no, null).show();	
		}
		else if (op == 2){
			Intent intent = new Intent(this, JobListActivity.class);
			intent.putExtra("printer", nickname);
			startActivity(intent);
		}
		else if (op == 3){
			Intent intent = new Intent(this, MimeTypesActivity.class);
			intent.putExtra("printer", nickname);
			startActivity(intent);
			
		}
	}

	private void doDelete(String printer){
		System.out.println("delete called");
		PrintQueueIniHandler ini = new PrintQueueIniHandler(getBaseContext());
		ini.removePrinter(printer);
		printersArray = ini.getPrintQueueConfigs();
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1, printersArray);
		printersListView.setAdapter(aa);
		CupsPrintApp.getPrinterDiscovery().updateStaticConfig();
	}
	
	public void addPrinter(){
		Intent intent = new Intent(this, PrinterAddEditActivity.class);
		intent.putExtra("printer", "");
		startActivity(intent);
	}
	
}
