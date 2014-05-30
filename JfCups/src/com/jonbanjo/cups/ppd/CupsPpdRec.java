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

public class CupsPpdRec {
    
    byte[] ppdMd5;
    boolean isUpdated = false;
    PpdUiList stdList;
    PpdUiList extraList;
    PpdServiceInfo ppdServiceInfo;
    
    public CupsPpdRec(){
        byte[] md5 = {};
        ppdMd5 = md5;
    }
    
    public boolean getIsUpdated(){
        return isUpdated;
    }
    
    public byte[] getPpdMd5(){
        return ppdMd5;
    }
    
    public PpdUiList getStdList(){
        return stdList;
    }
    
    public PpdUiList getExtraList(){
        return extraList;
    }
    
    public PpdServiceInfo getPpdServiceInfo(){
        return ppdServiceInfo;
    }
    
    public CupsPpdRec deepCloneUILists(){
        CupsPpdRec rec = new CupsPpdRec();
        if (stdList != null){
            rec.stdList = new PpdUiList();
            for (PpdSectionList list: stdList){
                rec.stdList.add(list.deepClone());
            }
        }
        if (extraList != null){
            rec.extraList = new PpdUiList();
            for (PpdSectionList list: extraList){
                rec.extraList.add(list.deepClone());
            }
        }
        rec.ppdServiceInfo = new PpdServiceInfo();

        return rec;
    }
     
    
    
}
