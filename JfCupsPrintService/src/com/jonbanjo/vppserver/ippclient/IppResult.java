package com.jonbanjo.vppserver.ippclient;

/*
JfCups
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

/*Based on ch.ethz.vppserver.ippclient.IppResult.Java 
Copyright (C) 2008 ITS of ETH Zurich, Switzerland, Sarah Windler Burri
*/

import java.util.List;

import com.jonbanjo.vppserver.schema.ippclient.AttributeGroup;
import java.util.Map;

public class IppResult {
  String _ippStatusResponse = null;
  List<AttributeGroup> _attributeGroupList = null;
  byte buf[];

  public IppResult() {
  }

  /**
   * 
   * @return
   */
  public String getIppStatusResponse() {
    return _ippStatusResponse;
  }

  /**
   * 
   * @param statusResponse
   */
  public void setIppStatusResponse(String statusResponse) {
    _ippStatusResponse = statusResponse;
  }

  /**
   * 
   * @return
   */
  public List<AttributeGroup> getAttributeGroupList() {
    return _attributeGroupList;
  }

  /**
   * 
   * @param group
   */
  public void setAttributeGroupList(List<AttributeGroup> group) {
    _attributeGroupList = group;
  }
  
  public byte[] getBuf(){
	  return buf;
  }
  
  public void setBuf(byte[] buffer){
	  this.buf = buffer;
  }
}
