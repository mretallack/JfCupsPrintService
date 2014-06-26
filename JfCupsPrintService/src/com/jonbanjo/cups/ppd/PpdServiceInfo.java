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

import java.util.LinkedHashMap;
import java.util.Map;

public class PpdServiceInfo{
    
    private static double POINTS_TO_MILS = 13.8888889;
    
    public class Dimension{
        private String text;
        private int width;
        private int height;
        
        public Dimension(String text, int width, int height){
            //ppd dimensions are in points. Android requires mils (thousandth of inch)
            this.text = text;
            this.width = (int) (width * POINTS_TO_MILS);
            this.height = (int) (height * POINTS_TO_MILS);
        }
        
        public String getText(){
            return text;
        }
        
        public int getWidth(){
            return width;
        }
        
        public int getHeight(){
            return height;
        }
    }
    
    byte [] ppdMD5;
    boolean mD5Match = false;
    private String defaultPaperDimension = "";
    private String defaultResolution = "";
    private Map<String, Dimension> paperDimensions;
    private Map<String, Dimension> resolutions;

    public PpdServiceInfo(){
        paperDimensions = new LinkedHashMap();
        resolutions = new LinkedHashMap();
    }
    
    public boolean getMD5Match(){
        return mD5Match;
    }
    
    public byte[] getPpdMd5(){
        return ppdMD5;
    }
    
    public String getDefaultPaperDimension(){
        return defaultPaperDimension;
    }
    
    public Map<String, Dimension> getPaperDimensions(){
        return paperDimensions;
    }

   public String getDefaultResolution(){
        return defaultResolution;
    }
    
    public Map<String, Dimension> getResolutions(){
        return resolutions;
    }
    
    protected void setDefaultPaperDimension(String name){
        defaultPaperDimension = name.trim();
    }
    
    protected void addPaperDimension(String name, String value){
        
        String[] names = name.split("/");
        String cupsValue = names[0].trim();
        String text;
        if (names.length == 1){
            text = cupsValue;
        }
        else{
            text = names[1].trim();
        }
        value = value.trim().replace("\"", "");
        String[] dims = value.split(" ");
        if (dims.length <2){
            return;
        }
        int width = getInt(dims[0]);
        int height = getInt(dims[1]);
        paperDimensions.put(cupsValue, new Dimension(text, width, height));
    
    }
    
    protected void setDefaultResolution(String value){
        this.defaultResolution = value.trim();
    }
    
    protected void addResolution(String value){
        String[] parts = value.split("/");
        String cupsValue = parts[0].trim();
        String key;
        if (parts.length == 1){
            key = cupsValue;
        }
        else{
            key = parts[1].trim();
        }
        String[] dims = cupsValue.split("x");
        int width;
        int height;
        width = getInt(dims[0]);
        if (dims.length == 1){
           height = getInt(dims[0]);
        }
        else{
            height = getInt(dims[1]);
        }
        resolutions.put(cupsValue, new Dimension(key, width, height));
    }
    
    private int getInt(String value){
        value = value.trim();
        String tmp = "";
        for (char c: value.toCharArray()){
            if (Character.isDigit(c)){
                tmp = tmp + c;
            }
            else{
                break;
            }
        }
        int ret = 0;
        try {
            ret =  Integer.parseInt(tmp);
        }catch (Exception e){}
        return ret;
    }
}
