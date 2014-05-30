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

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.jonbanjo.discovery.JfPrinterDiscovery;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

public class CupsPrintApp extends Application{
    
	private static CupsPrintApp instance;
	private static JfPrinterDiscovery printerDiscovery;
	private static SecretKey secretKey;
	private static final String PREF_FILE = "userData";
	private static final String USER_KEY = "userKey";


    public static CupsPrintApp getInstance() {
        return instance;
    }

    public static JfPrinterDiscovery getPrinterDiscovery(){
    	return printerDiscovery;
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }
    
    public static SecretKey getSecretKey(){
    	return secretKey;
    }
    

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        setSecretKey(this.getApplicationContext());
    	printerDiscovery = new JfPrinterDiscovery();
    	printerDiscovery.updateStaticConfig();
    }

    private void setSecretKey(Context context){
	    SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
		String encoded = sharedPreferences.getString(USER_KEY, null);
		if (encoded != null){
			secretKey = new SecretKeySpec(Base64.decode(encoded, Base64.DEFAULT), "AES");
			return;
		}

		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		    keyGenerator.init(128);
		    secretKey = keyGenerator.generateKey();
		}catch (Exception e){
			System.err.println(e.toString());
			secretKey = null;
			return;
		}
	    encoded = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
	    Editor editor = sharedPreferences.edit();
	    editor.putString(USER_KEY, encoded);
	    editor.commit();
	}
}
