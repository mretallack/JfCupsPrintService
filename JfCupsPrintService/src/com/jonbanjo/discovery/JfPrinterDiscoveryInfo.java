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

public class JfPrinterDiscoveryInfo {
	
	private String nickname;
	private String queue;
	private int status;
	private boolean isStatic = false;
	private boolean isDynamic = false;
	

	public JfPrinterDiscoveryInfo(String nickname, String queue){
		this.nickname = nickname;
		this.queue = queue;
	}
	
	public String getNickname(){
		return nickname;
	}
	
	public String getQueue(){
		return queue;
	}
	
	public int getStatus(){
		return status;
	}
	
	public void setStatus(int status){
		this.status = status;
	}
	
	public void setStatic(){
		isStatic = true;
	}
	
	public void setDynamic(){
		isDynamic = true;
	}
	
	public boolean setRemoveStatic(){
		isStatic = false;
		if (!isDynamic){
			return true;
		}
		return false;
	}
	
	public boolean setRemoveDynamic(){
		isDynamic = false;
		if (!isStatic){
			return true;
		}
		return false;
	}
	
}
