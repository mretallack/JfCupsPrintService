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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jonbanjo.cups.operations.AuthInfo;
import com.jonbanjo.cups.ppd.CupsPpd;
import com.jonbanjo.cups.ppd.CupsPpdRec;
import com.jonbanjo.cups.ppd.PpdServiceInfo;
import com.jonbanjo.cups.ppd.PpdServiceInfo.Dimension;
import com.jonbanjo.cupsprint.CupsPrintApp;
import com.jonbanjo.cupsprint.PrintQueueConfig;
import com.jonbanjo.cupsprint.PrintQueueIniHandler;
import com.jonbanjo.discovery.JfPrinterDiscoveryInfo;
import com.jonbanjo.discovery.JfPrinterDiscoveryListener;
import com.jonbanjo.tasks.GetServicePpdListener;
import com.jonbanjo.tasks.GetServicePpdTask;

import android.os.Handler;
import android.print.PrintAttributes;
import android.print.PrinterCapabilitiesInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrinterDiscoverySession;
import android.widget.Toast;



public class JfPrinterDiscoverySession extends PrinterDiscoverySession 
		implements JfPrinterDiscoveryListener, GetServicePpdListener{

	private JfPrintService printService;
	
	public JfPrinterDiscoverySession(JfPrintService jfPrintService){
		this.printService = jfPrintService;
	}
	
	@Override
	public void onDestroy(){
	}
	
	@Override
	public void onStartPrinterDiscovery(List<PrinterId> arg0) {
		Map<String, JfPrinterDiscoveryInfo> printerMap = CupsPrintApp.getPrinterDiscovery().addDiscoveryListener(this);
		Iterator<String> it = printerMap.keySet().iterator();
		List<PrinterInfo> printers = new ArrayList<PrinterInfo>();
		while (it.hasNext()){
			JfPrinterDiscoveryInfo info = printerMap.get(it.next());
			PrinterInfo printerInfo = createPrinterInfo(info);
			if (printerInfo != null){
				printers.add(printerInfo);
			}
		}
		addPrinters(printers);
		ArrayList<PrinterId>printerIds = new ArrayList<PrinterId>();
		for (PrinterInfo printerInfo : this.getPrinters()){
			JfPrinterDiscoveryInfo info = printerMap.get(printerInfo.getName());
			if (info == null){
				JfPrintService.capabilities.remove(printerInfo.getName());
				printerIds.add(printerInfo.getId());
			}
		}
		this.removePrinters(printerIds);
		
	
	 }

	@Override
	public void onStopPrinterDiscovery() {
		CupsPrintApp.getPrinterDiscovery().removeDiscoveryListener(this);
	}
	
	@Override
	public void onStartPrinterStateTracking(PrinterId printerId) {
		byte[] md5 = null;
		String nickName = printerId.getLocalId();
		CupsPpd savedPpd = JfPrintService.capabilities.get(nickName);
		if (savedPpd == null){
			AuthInfo auth = null;
			savedPpd = new CupsPpd(auth);
			JfPrintService.capabilities.put(nickName, savedPpd);
		}
		else{
			md5 = savedPpd.getPpdRec().getPpdMd5();
		}
		
		PrintQueueIniHandler ini = new PrintQueueIniHandler(CupsPrintApp.getContext());
		PrintQueueConfig config = ini.getPrinter(nickName);
		if (config != null){
			AuthInfo auth = null;
			if (!(config.getPassword().equals(""))){
				auth = new AuthInfo(config.getUserName(), config.getPassword());
			}
			GetServicePpdTask task = new GetServicePpdTask(config, auth, md5);
			task.setPpdTaskListener(this);
			task.get(true, Thread.NORM_PRIORITY);
		}
	
	}


	@Override
	public void onStopPrinterStateTracking(PrinterId arg0) {
	}

	@Override
	public void onValidatePrinters(List<PrinterId> arg0) {
	}

	@Override
	public void onPrinterAdded(final JfPrinterDiscoveryInfo info) {
		Handler handler = new Handler(CupsPrintApp.getContext().getMainLooper());
		Runnable runnable = new Runnable(){

			@Override
			public void run() {
				onPrinterAddedMainThread(info);
			}
		};
		handler.post(runnable);
	}
	
	
	public void onPrinterAddedMainThread(JfPrinterDiscoveryInfo info){
		List<PrinterInfo> printers = new ArrayList<PrinterInfo>();
		PrinterInfo printerInfo = createPrinterInfo(info);
		if (printerInfo != null){
			printers.add(printerInfo);
			this.addPrinters(printers);;
		}
		
	}

	@Override
	public void onPrinterRemoved(final JfPrinterDiscoveryInfo info) {
		Handler handler = new Handler(CupsPrintApp.getContext().getMainLooper());
		Runnable runnable = new Runnable(){

			@Override
			public void run() {
				onPrinterRemovedMainThread(info);
			}
		};
		handler.post(runnable);
	
	}
	
	private void onPrinterRemovedMainThread(JfPrinterDiscoveryInfo info){
		List<PrinterId> ids = new ArrayList<PrinterId>();
		PrinterId id = printService.generatePrinterId(info.getNickname());
		ids.add(id);
		this.removePrinters(ids);
		JfPrintService.capabilities.remove(id.getLocalId());
	
	}
	
	private PrinterInfo createPrinterInfo(JfPrinterDiscoveryInfo info){
		PrinterId id = printService.generatePrinterId(info.getNickname());
		PrinterInfo.Builder builder = new PrinterInfo.Builder(id, info.getNickname(), PrinterInfo.STATUS_IDLE);
		try{
			return builder.build();
		}catch (Exception e){
			System.err.println(e.toString());
			return null;
		}
	}

	
	@Override
	public void onGetServicePpdTaskDone(CupsPpd cupsPpd, PrintQueueConfig config, Exception exception) {
		final String nicknameId = config.getNickname();
		if (exception != null){
			JfPrintService.capabilities.remove(nicknameId);
			//Toast.makeText(this.printService, exception.toString(), Toast.LENGTH_LONG).show();
			return;
		}
		CupsPpdRec ppdRec = cupsPpd.getPpdRec();
		//cupsPpd.setServiceResolution(config.getResolution());
		if (ppdRec.getIsUpdated()){
			JfPrintService.capabilities.put(nicknameId, cupsPpd);
		}
		else{
			cupsPpd = JfPrintService.capabilities.get(nicknameId);
			if (cupsPpd != null){
				cupsPpd.setServiceResolution(config.getResolution());
			}
		}
		Handler handler = new Handler(CupsPrintApp.getContext().getMainLooper());
		Runnable runnable = new Runnable(){

			@Override
			public void run() {
				setPrinterCapabilities(nicknameId);
			}
		};
		handler.post(runnable);

	
	}
	
	private void setPrinterCapabilities(String nickname){
		
		CupsPpd cupsPpd = JfPrintService.capabilities.get(nickname);
		if (cupsPpd == null){
			return;
		}

		PpdServiceInfo serviceInfo = null; 
		try {
			serviceInfo = cupsPpd.getPpdRec().getPpdServiceInfo();
		}catch (Exception e){
			System.err.println(e.toString());
		}
		if (serviceInfo == null){
			return;
		}
		
		PrinterId id = printService.generatePrinterId(nickname);
		PrinterInfo.Builder infoBuilder =
				new PrinterInfo.Builder(id, nickname, PrinterInfo.STATUS_IDLE);
		PrinterCapabilitiesInfo.Builder capBuilder = new PrinterCapabilitiesInfo.Builder(id);
		
		Map<String, PpdServiceInfo.Dimension> mediaSizes = serviceInfo.getPaperDimensions();
		String defaultVal = serviceInfo.getDefaultPaperDimension();
		for (Map.Entry<String, PpdServiceInfo.Dimension> entry : mediaSizes.entrySet()) {
			Dimension dim = entry.getValue();
			String key = entry.getKey();
			boolean isDefault;
			if (key.equals(defaultVal)){
				isDefault = true;
			}
			else {
				isDefault = false;
			}
			
			capBuilder.addMediaSize(
					new PrintAttributes.MediaSize(entry.getKey(), dim.getText(),
								  dim.getWidth(), dim.getHeight()) , isDefault);
		}
		
		//capBuilder.addMediaSize(new PrintAttributes.MediaSize("ISO_A4", "ISO_A4", 210, 297), true);
		//capBuilder.addMediaSize(MediaSize.ISO_A4, true);
		//String defaultVal;
		//PrintAttributes.MediaSize builtIn = PrintAttributes.MediaSize.ISO_A4;
		//PrintAttributes.MediaSize custom = new PrintAttributes.MediaSize("Letter", "Letter", 612, 792);
		//String s = builtIn.getLabel(CupsPrintApp.getContext().getPackageManager());
		
		Map<String, PpdServiceInfo.Dimension> resolutions = serviceInfo.getResolutions();
		boolean ppdDefault = cupsPpd.getServiceResolution().equals("");

		defaultVal = serviceInfo.getDefaultResolution();
		for (Map.Entry<String, PpdServiceInfo.Dimension> entry : resolutions.entrySet()) {
			Dimension dim = entry.getValue();
			String key = entry.getKey();
			boolean isDefault;
			if (ppdDefault && key.equals(defaultVal)){
				isDefault = true;
			}
			else {
				isDefault = false;
			}
			capBuilder.addResolution(
					new PrintAttributes.Resolution(
							entry.getKey(), dim.getText(), dim.getWidth(), dim.getHeight()),
					isDefault);
			
		}
		if (!ppdDefault) {
			String res = cupsPpd.getServiceResolution();
			String[] dpis = res.split("x");
			int x = 360; int y = 360;
			try {
				x = Integer.parseInt(dpis[0]);
				y = Integer.parseInt(dpis[1]);
			}catch (Exception e){}
			capBuilder.addResolution(new PrintAttributes.Resolution("App default", "App default", x, y), true);	
		}
		
		//capBuilder.addResolution(new PrintAttributes.Resolution("4x4", "5x5", 360, 360), true);
		/*
		 * 
		 */

		capBuilder.setColorModes(PrintAttributes.COLOR_MODE_COLOR + PrintAttributes.COLOR_MODE_MONOCHROME, 
				PrintAttributes.COLOR_MODE_COLOR);
		capBuilder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);
		PrinterCapabilitiesInfo caps = null;
		PrinterInfo printInfo = null;
		try {
			caps = capBuilder.build();
			infoBuilder.setCapabilities(caps);
			printInfo = infoBuilder.build();
		}
		catch (Exception e){
			Toast.makeText(this.printService, e.toString(), Toast.LENGTH_LONG).show();
 			System.err.println(e.toString());
			return;
		}
		List<PrinterInfo> infos = new ArrayList<PrinterInfo>();
		infos.add(printInfo);
		try {
			this.addPrinters(infos);
		} catch (Exception e){
			Toast.makeText(this.printService, e.toString(), Toast.LENGTH_LONG).show();
			System.err.println(e.toString());
		}
		
	}
	
}
