package com.jonbanjo.cups.ppd;

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

import java.util.ArrayList;

public class PpdItemList extends ArrayList<PpdItem>{
    String name;
    String text;
    String defaultValue = "";
    String savedValue = "";
    CupsType commandType;
    
    
    public String getName(){
        return name;
    }
    
    public String getText(){
        return text;
    }
    
    public String getDefaultValue(){
        return defaultValue;
    }
    
    public void setDefaultValue(String value){
        defaultValue = value;
    }
    
    public String getSavedValue(){
        return savedValue;
    }
    
    public void setSavedValue(String value){
        savedValue = value;
    }
    
    public CupsType getCommandType(){
        return commandType;
    }
    
    @Override
    public String toString(){
        return name;
    }
    
    public PpdItemList deepClone(){
        PpdItemList itemList = new PpdItemList();
        itemList.commandType = commandType;
        itemList.defaultValue = defaultValue;
        itemList.name = name;
        itemList.savedValue = savedValue;
        itemList.text = text;
        for (PpdItem item: this){
            itemList.add(new PpdItem(itemList, item.value, item.text));
        }
        return itemList;
    }
}
