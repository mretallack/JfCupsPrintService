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

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.validator.routines.UrlValidator;

import android.app.Activity;
import android.widget.Toast;

public class Util {
	
	public static URL getURL(String urlStr) throws MalformedURLException{
		
		UrlValidator urlValidator = new UrlValidator();
		if (urlValidator.isValid(urlStr)){
			try {
				return new URL(urlStr);
			}catch (Exception e){}
		}
		throw new MalformedURLException("Invalid URL\n" + urlStr);
	}
	
	public static String getQueue(PrintQueueConfig printConfig){
		return "/printers/" + printConfig.queue;
	}
	
	public static String getClientUrlStr(PrintQueueConfig printConfig){
		return printConfig.protocol + "://" + 
				printConfig.host + ":" + 
				printConfig.port;
	}
	
	public static String getPrinterUrlStr(PrintQueueConfig printConfig){
		return getClientUrlStr(printConfig) +
				"/printers/" + printConfig.queue;
	}
	
	public static URL getClientURL(PrintQueueConfig printConfig) throws MalformedURLException{
		String urlStr = getClientUrlStr(printConfig);
		return getURL(urlStr);
	}
	
	public static URL getPrinterURL(PrintQueueConfig printConfig) throws MalformedURLException{
		String urlStr = getPrinterUrlStr(printConfig);
		return getURL(urlStr);
	}
	
	
	public static void showToast(final Activity activity, final String toast)
	{
	    activity.runOnUiThread(new Runnable() {
	        public void run()
	        {
	            Toast.makeText(activity, toast, Toast.LENGTH_LONG).show();
	        }
	    });
	}
}
