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

public class PrintQueueConfig {

	String nickname;
	String protocol;
	String host;
	String port;
	String queue;
	String userName;
	String password;
	String orientation;
	boolean imageFitToPage;
	boolean noOptions;
	boolean isDefault;
	String showIn;
	String extensions;
	String resolution;
	
	
	public PrintQueueConfig(String nickname, String protocol, String host, String port, String queue){
		this.nickname = nickname;
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.queue = queue;
	}
	
	public String getNickname(){
		return nickname;
	}
	
	public String getClient(){
		return protocol + "://" + host + ":" + port;
	}
	
	public String getQueuePath(){
		return "/printers/" + queue;
	}
	
	public String getPrintQueue(){
		return protocol + "://" + host + ":" + port + "/printers/" + queue;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public String getPassword(){
		return password;
	}
	
	public String getResolution(){
		return resolution;
	}
	
	public boolean showInPrintService(){
		return (!(showIn.equals("Shares")));
	}
}
