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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;

public class HostScanTask extends AsyncTask<Void, Integer, PrinterResult>
 implements ProgressUpdater{

IPTester services;
Context context;
PrinterUpdater printerUpdater;
ProgressDialog pd;
boolean stopped = false;
String username = "";
String password = "";

public HostScanTask(Context context, PrinterUpdater updater, String username, String password){
	this.context = context;
	this.printerUpdater = updater;
	if (username != null){
		this.username = username;
	}
	if (password != null){
		this.password = password;
	}
}

@Override
protected PrinterResult doInBackground(Void... arg0) {
	services = new IPTester(this);
	String ip = null;
	try {
		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
			NetworkInterface intf = en.nextElement();
			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
				InetAddress inetAddress = enumIpAddr.nextElement();
				if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
					ip = inetAddress.getHostAddress();
				}
			}
		}
	} catch (Exception e) {
		System.out.println(e.toString());
	}
	if (ip == null){
		return null;
	}
	return services.getPrinters(ip, 24, 631, username, password);
}

@Override
protected void onPreExecute(){
	pd = new ProgressDialog(context);
	pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	pd.setIndeterminate(false);
	pd.setCanceledOnTouchOutside(false);
	pd.setMax(100);
	pd.setTitle("Scanning Hosts");
	pd.setOnDismissListener(new OnDismissListener(){
	
		@Override
		public void onDismiss(DialogInterface dialog) {
			doStop();
		}			
	});
	pd.show();
}

protected void onProgressUpdate(Integer... progress) {
	pd.setProgress(progress[0]);
}

@Override
protected void onPostExecute(PrinterResult result){
	services = null;
	if (pd != null)
		pd.dismiss();
	if (!stopped)
		printerUpdater.getDetectedPrinter(result);
}

@Override
public void DoUpdate(int value) {
	this.publishProgress(value);
}

public void doStop(){
	stopped = true;
	if (!(services == null)){
		services.stop();
	}
}

}

