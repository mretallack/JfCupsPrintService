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

public class PpdItem{
    
    String value;
    String text;
    PpdItemList parent;
    
    public PpdItem(PpdItemList parent, String value, String text){
        this.parent = parent;
        this.value = value;
        this.text = text;
    }
    
    public String getText(){
        return text;
    }
    
    public String getValue(){
        return value;
    }
    
    @Override
    public String toString(){
        switch (parent.commandType){
            case KEYWORD:
                if (parent.defaultValue.equals(value))
                    //return ("*" + text + "/" + value);
                    return ("*" + text);
                else
                    //return text + "/" + value;
                    return text;
            case ENUM:
                if (parent.defaultValue.equals(value))
                    return ("*" + text);
                else
                    return text;
                
            default:
                return text;
        }
    }
}
