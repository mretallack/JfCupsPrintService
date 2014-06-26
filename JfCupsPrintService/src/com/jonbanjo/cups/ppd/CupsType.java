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

public enum CupsType {
    KEYWORD("keyword"), ENUM("enum"), BOOLEAN("boolean"), 
    INTEGER("integer"), SETOFRANGEOFINTEGER("setOfRangeOfInteger");
   
   private final String stringValue;
   
   private CupsType(final String s) { 
       stringValue = s; 
   }
   
   public String toString() {
       return stringValue;
   }
}
