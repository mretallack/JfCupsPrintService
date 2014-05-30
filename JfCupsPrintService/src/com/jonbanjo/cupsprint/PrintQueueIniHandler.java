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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.crypto.Cipher;

import org.ini4j.Ini;

import android.content.Context;
import android.util.Base64;

public class PrintQueueIniHandler extends Ini{

	private static final long serialVersionUID = 1L;
	private static final String defaultPrinter = "jfcupsprintdefault"; 

	public PrintQueueIniHandler(Context context){
    	super();
    	try {
    		String filePath = context.getFilesDir().getPath().toString() + "/printers.conf";
    		File file = new File(filePath);
    		file.createNewFile();
    		setFile(file);
    		load();
    	}
    	catch (Exception e){
    		System.out.println(e.toString());
        return;
    	}
    }
	
	public String getDefaultPrinter(){
		return getString(defaultPrinter, "default");
	}
	
	public void setDefaultPrinter(String printer){
		put (defaultPrinter, "default", printer);
		try {
			this.store();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}

	public boolean printerExists(String name){
		Section section = this.get(name);
		return !(section == null);
	}
	
	public void removePrinter(String printer){
		Section section = this.get(printer);
		if (section != null){
			remove(section);
			String currDefault = getDefaultPrinter();
			if (currDefault.equals(printer))
				setDefaultPrinter("");
			try {
				store();
			} catch (IOException e) {
				System.out.println(e.toString());
			}
		}
		
	}
	public void addPrinter(PrintQueueConfig config, String oldConfig){
		Section section = this.get(oldConfig);
		if (section != null){
			remove(section);
		}
		String nickname = config.nickname;
		add(nickname);
		put(nickname, "host", config.host);
		put(nickname, "protocol", config.protocol);
		put(nickname, "port", config.port);
		put(nickname, "queue", config.queue);
		put(nickname, "username", config.userName);
		put(nickname, "password", encrypt(config.password));
		put(nickname, "orientation", config.orientation);
		putBoolean(nickname, "fittopage", config.imageFitToPage);
		putBoolean(nickname, "nooptions", config.noOptions);
		put(nickname, "extensions", config.extensions);
		put(nickname, "resolution", config.resolution);
		put(nickname, "showin", config.showIn);
		if (config.isDefault)
			put (defaultPrinter, "default", config.nickname);
		else {
			String currDefault = getString(defaultPrinter, "default");
			if (currDefault != null){
				if (currDefault.equals(oldConfig))
					put(defaultPrinter, "default", "");
			}
		}
		try {
			this.store();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	
	public ArrayList<String> getPrintQueueConfigs(){
		
		ArrayList<String> printerList = new ArrayList<String>();
		for (String name: keySet()){
			if (!name.equals(defaultPrinter)){
				printerList.add(name);
			}
		}
		Collections.sort(printerList);
		return printerList;
		
	}
	
	public ArrayList<String> getShareConfigs(){
		
		ArrayList<String> printerList = new ArrayList<String>();
		for (String name: keySet()){
			if (!name.equals(defaultPrinter)){
				String showin = this.getString(name, "showin");
				if (!(showin.equals("Print Service"))){
					printerList.add(name);
				}
			}
		}
		Collections.sort(printerList);
		return printerList;
	}
	
	public ArrayList<String> getServiceConfigs(){
		
		ArrayList<String> printerList = new ArrayList<String>();
		for (String name: keySet()){
			if (!name.equals(defaultPrinter)){
				String showin = this.getString(name, "showin");
				if (!(showin.equals("Shares"))){
					printerList.add(name);
				}
			}
		}
		Collections.sort(printerList);
		return printerList;
	}
	
	public PrintQueueConfig getPrinter(String name){
		Section section = this.get(name);
		if (section == null)
			return null;
		String host = this.getString(name, "host");
		String protocol = this.getString(name, "protocol");
		String port = this.getString(name, "port");
		String queue = this.getString(name, "queue");
		PrintQueueConfig pqc = new PrintQueueConfig(name, protocol, host, port, queue);
		pqc.userName = this.getString(name, "username");
		pqc.password = decrypt(this.getString(name, "password"));
		pqc.orientation = this.getString(name, "orientation");
		pqc.imageFitToPage = this.getBoolean(name, "fittopage");
		pqc.noOptions = this.getBoolean(name, "nooptions");
		pqc.extensions = this.getString(name, "extensions");
		pqc.resolution = this.getString(name, "resolution");
		pqc.showIn = this.getString(name, "showin");
		String currDefault = this.getString(defaultPrinter, "default");
		pqc.isDefault = (pqc.nickname.equals(currDefault));
		return pqc;
	}
	
	public String getString(String section, String key){
		String val = this.get(section, key);
		if (val == null)
			return "";
		return val;
	}
	
	private Boolean getBoolean(String section, String key){
		String value = get(section, key);
		if (value == null)
			return false;
		return (value.equals("true"));
	}
	
	private void putBoolean(String section, String key, boolean value){
		if (value)
			put(section, key, "true");
		else
			put(section, key, "false");
			
	}
	
	private String encrypt(String data){
		if (data.equals("")){
			return "";
		}
		try {
			Cipher cipher = Cipher.getInstance("AES");
	        cipher.init(Cipher.ENCRYPT_MODE, CupsPrintApp.getSecretKey());
	        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
	        return new String(Base64.encode(encryptedBytes, Base64.DEFAULT));
		}catch (Exception e){
			System.err.println(e.toString());
			return "";
		}
		
	}
	
	private String decrypt(String data){
		if (data.equals("")){
			return "";
		}
		try {
			Cipher cipher = Cipher.getInstance("AES");
	        cipher.init(Cipher.DECRYPT_MODE, CupsPrintApp.getSecretKey());
	        byte[] decryptedBytes = cipher.doFinal(Base64.decode(data, Base64.DEFAULT));
	        return new String(decryptedBytes);
		}catch (Exception e){
			System.err.println(e.toString());
			return "";
		}
	}
	

}
