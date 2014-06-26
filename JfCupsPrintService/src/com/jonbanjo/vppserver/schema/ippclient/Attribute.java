package com.jonbanjo.vppserver.schema.ippclient;

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

/*Based on ch.ethz.vppserver.schema.ippclient.Attribute.Java 
Copyright (C) 2008 ITS of ETH Zurich, Switzerland, Sarah Windler Burri
*/

import java.util.ArrayList;
import java.util.List;

public class Attribute {

  protected List<AttributeValue> attributeValue;
  protected String name;
  protected String description;

  public List<AttributeValue> getAttributeValue() {
    if (attributeValue == null) {
      attributeValue = new ArrayList<AttributeValue>();
    }
    return this.attributeValue;
  }

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String value) {
    this.description = value;
  }

}
