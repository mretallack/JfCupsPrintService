package com.jonbanjo.discovery;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jonbanjo.cupsprint.CupsPrintApp;
import com.jonbanjo.cupsprint.PrintQueueIniHandler;
import com.jonbanjo.cupsprint.PrintQueueConfig;

public class JfPrinterDiscovery{
	
	private Map<String, JfPrinterDiscoveryInfo> printerInfos;
	private List<JfPrinterDiscoveryListener> printerDiscoveryListeners;
	
	public JfPrinterDiscovery(){
		printerInfos = Collections.synchronizedMap(new HashMap<String, JfPrinterDiscoveryInfo>());
		printerDiscoveryListeners = new ArrayList<JfPrinterDiscoveryListener>();
	}
	
    public Map<String, JfPrinterDiscoveryInfo> addDiscoveryListener(JfPrinterDiscoveryListener listener){
		printerDiscoveryListeners.add(listener);
		return getPrinters();
	}
	
	public void removeDiscoveryListener(JfPrinterDiscoveryListener listener){
		printerDiscoveryListeners.remove(listener);
	}
	
	
	public Map<String, JfPrinterDiscoveryInfo>getPrinters(){
		return new HashMap<String, JfPrinterDiscoveryInfo>(printerInfos);
	}
	
	public JfPrinterDiscoveryInfo getPrinterInfo(String queue){
		
		return printerInfos.get(queue);
	}
	
	private void readStaticConfig(){
		
		PrintQueueIniHandler ini = new PrintQueueIniHandler(CupsPrintApp.getContext());
		ArrayList<String> iniPrintersArray = ini.getServiceConfigs();
		
		synchronized(printerInfos){
			Iterator<String> it = printerInfos.keySet().iterator();
			while (it.hasNext()){
				String key = it.next();
				PrintQueueConfig test = ini.getPrinter(key);
				if (test == null){
					JfPrinterDiscoveryInfo info = printerInfos.get(key);
					if (info != null){
						if (info.setRemoveStatic()){
							it.remove();
							notifyRemovePrinter(info);
						}
					}
				}
				else {
					if (!test.showInPrintService()){
						JfPrinterDiscoveryInfo info = printerInfos.get(key);
						if (info != null){
							if (info.setRemoveStatic()){
								it.remove();
								notifyRemovePrinter(info);
							}
						}
					}
				}
			}
		}
		
		for(String nickname : iniPrintersArray){
			JfPrinterDiscoveryInfo jfInfo = printerInfos.get(nickname);
			if (jfInfo == null){
				PrintQueueConfig config = ini.getPrinter(nickname);
				if (config != null){
					JfPrinterDiscoveryInfo newInfo = new JfPrinterDiscoveryInfo(nickname,config.getPrintQueue());
					newInfo.setStatic();
					printerInfos.put(nickname, newInfo);
					notifyAddPrinter(newInfo);
				}
			}
			else {
				jfInfo.setStatic();
			}
		}
	}
	
	private void notifyAddPrinter(JfPrinterDiscoveryInfo info){
		Iterator<JfPrinterDiscoveryListener> it = printerDiscoveryListeners.iterator();
		while (it.hasNext()){
			JfPrinterDiscoveryListener listener = it.next();
			try{
				listener.onPrinterAdded(info);
			}catch (Exception e){
				it.remove();
				System.err.println(e.toString());
			}
		}
	}
	
	public void notifyRemovePrinter(JfPrinterDiscoveryInfo info){
		Iterator<JfPrinterDiscoveryListener> it = printerDiscoveryListeners.iterator();
		while (it.hasNext()){
			JfPrinterDiscoveryListener listener = it.next();
			try{
				listener.onPrinterRemoved(info);
			}catch (Exception e){
				it.remove();
				System.err.println(e.toString());
			}
		}
	}
	
	public void updateStaticConfig(){
		new Thread(new staticUpdater()).start();
	}

	public class staticUpdater implements Runnable{

		@Override
		public void run() {
			readStaticConfig();
		}
	}

}	
