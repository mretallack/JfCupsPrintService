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

import com.jonbanjo.cups.CupsPrinter;
import com.jonbanjo.cups.operations.AuthInfo;
import com.jonbanjo.cups.operations.cups.CupsGetPPDOperation;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Iterator;

public class CupsPpd{
    
    CupsPpdRec ppdRec;
    AuthInfo auth;
    String serviceResolution = "";
    
    public CupsPpd(AuthInfo auth){
        ppdRec = new CupsPpdRec();
        this.auth = auth;
        createStdList();
    }
    
    public String getServiceResolution(){
        return serviceResolution;
    }
    
    public void setServiceResolution(String resolution){
        this.serviceResolution = resolution;
    }
    
    public CupsPpdRec getPpdRec(){
        return ppdRec;
    }
    
    public void setPpdRec(CupsPpdRec rec){
        this.ppdRec = rec;
    }
    
    private void createStdList(){
        ppdRec.stdList = new PpdUiList();
        PpdSectionList groupList = new PpdSectionList();
        groupList.name = "Standard";
        groupList.text = "Standard";
        ppdRec.stdList.add(groupList);
        addOrientation(groupList);
        addCopies(groupList);
        addPageRanges(groupList);
        addPageSides(groupList);
        addFitToPage(groupList);
     }
    
    public String getCupsStdString(){
        return getCupsString(ppdRec.stdList);
    }
    
    public String getCupsExtraString(){
        return getCupsString(ppdRec.extraList);
    }
    
    private String getCupsString(PpdUiList uiList){
        if (uiList == null){
            return "";
        }
        String cupsString = "";
        boolean isNext = false;
        for (PpdSectionList group: uiList){
            for (PpdItemList section: group){
                if (section.defaultValue.equals(section.savedValue)){
                    continue;
                }
                if (isNext)
                    cupsString = cupsString + "#";
                else
                    isNext = true;
                cupsString = cupsString + section.name + ":" +
                        section.commandType.toString() + ":" +
                        section.savedValue;
            }
        }
        return cupsString;
    }
    
    public void createPpdRec(CupsPrinter printer, byte[] md5) throws UnsupportedEncodingException, IOException, Exception{
        URL printerUrl = printer.getPrinterUrl();
        CupsGetPPDOperation getPpd = new CupsGetPPDOperation();
        String ppdString = getPpd.getPPDFile(printerUrl, auth);
        createPpdRec(ppdString, md5);
    }
    
    public void createPpdRec(String ppdString, byte[] md5) throws IOException{
        ppdRec.isUpdated = false;
        byte[] mdBytes = {0};
        if (md5 == null){
            md5 = mdBytes;
        }
        try {
             MessageDigest md = MessageDigest.getInstance("MD5");
             mdBytes = md.digest(ppdString.getBytes());
             if (Arrays.equals(mdBytes, md5)){
                ppdRec.ppdMd5 = mdBytes;
                return;
            }
        }catch(Exception e){
            System.out.println(e.toString());
        }
        
        ppdRec.ppdMd5 = mdBytes;
        createStdList();
        ppdRec.extraList = new PpdUiList();
        ppdRec.ppdServiceInfo = new PpdServiceInfo();
        PpdSectionList currentGroup = openGroup("General", "General");
        PpdItemList currentSection = null;
        String sectionName = "";
        
        BufferedReader reader = new BufferedReader(new StringReader(ppdString));
        String line;
        while ( (line =reader.readLine()) != null){
            line = line.trim();
            line = line.replace('\t' , ' ');
            if (!line.startsWith("*")){
                continue;
            }
            if (line.startsWith("*%")){
                continue;
            }
            //System.out.println(line);
            int cmdPos = line.indexOf(':');
            if (cmdPos < 2){
                continue;
            }
            //String[] cmdVal = line.split(":");
            //if (cmdVal.length < 2)
            //    continue;
            String cmdVal0 = line.substring(0, cmdPos);
            String cmdVal1 = line.substring(cmdPos+1);
            String value = cmdVal1.trim();
           
            int commandpos = cmdVal0.indexOf(" ");
            String command;
            String option;
            //if (cmdVal0.contains("Resolution")) {
            //    System.out.println();
            //}
            if (commandpos > 0){
               command = cmdVal0.substring(0, commandpos);
               option = cmdVal0.substring(commandpos).trim();
            }
            else {
               command = cmdVal0.trim();
               option = "";
            }
            command = command.substring(1);
            if (command.length()<2)
                continue;

            if (command.equals("OpenGroup")){
                OptionPair pr = OptionPair.getOptionPair(value);
                currentGroup = openGroup(pr.option, pr.text);
            }
            else if (command.equals("CloseGroup")){
                closeGroup();
            }
            else if (command.equals("OpenUI")){
                OptionPair pr = OptionPair.getOptionPair(option.substring(1));
                currentSection = openSection(pr.option, pr.text, currentGroup);
                currentSection.commandType = CupsType.KEYWORD;
                sectionName = pr.option;
            }
            else if (command.equals("CloseUI")){
                currentSection = null;
                sectionName = "";
            }
           else if (command.equals("PaperDimension")){
                if (value.equals("")){
                    continue;
                }
                
                //if (cmdVal.length < 2){
                //   continue;
                //}
                
                ppdRec.ppdServiceInfo.addPaperDimension(option, cmdVal1);
            }
            else {
                if (currentSection == null)
                    continue;
                if (command.startsWith("Default")){
                    command = command.substring(7);
                    if (sectionName.equals(command)){
                        currentSection.defaultValue = value;
                        currentSection.savedValue = value;
                    }
                    continue;
                }
                if (command.equals("Resolution")){
                    ppdRec.ppdServiceInfo.addResolution(option);
                }
                addItem(currentSection, option);
                    
            }
        }
        
        //remove empty groups and ensure a default exists
        Iterator<PpdSectionList> it = ppdRec.extraList.iterator();
        while (it.hasNext()){
        	PpdSectionList section = it.next();
        	if (section.size() == 0){
        		it.remove();
        	}
            else {
                for (PpdItemList items: section){
                	if (items.defaultValue.equals("")){
                        items.defaultValue = "default";
                        items.savedValue = "default";
                        addItem(items, "default/default");
                    }
                	else {
                		boolean found = false;
                		for (PpdItem item: items){
                			if (item.value.equals(items.defaultValue)){
                				found = true;
                				break;
                			}
                		}
                		if (!found){
                			items.add(0, new PpdItem(items, items.defaultValue, items.defaultValue));
                		}
                	}
                    if (items.name.equals("Resolution")){
                        ppdRec.ppdServiceInfo.setDefaultResolution(items.defaultValue);
                    }
                    else if (items.name.equals("PageSize")){
                        ppdRec.ppdServiceInfo.setDefaultPaperDimension(items.defaultValue);
                    }
                }
            }
        }
        
        //if (ppdRec.ppdServiceInfo.getResolutions().isEmpty()){
        //}

        //move "general" section to std list
        it = ppdRec.extraList.iterator();
        while (it.hasNext()){
            PpdSectionList section = it.next();
            if (section.name.equals("General")){
                ppdRec.stdList.add(section);
                it.remove();
                break;
            }
        }
        ppdRec.isUpdated = true;
    }
    
    private void addItem(PpdItemList section, String data){
        int pos = data.indexOf('/');
        if (pos < 1){
            return;
        }
        section.add(new PpdItem(section, data.substring(0, pos), data.substring(pos+1)));
    }
    
    private PpdSectionList openGroup(String groupName, String groupText){
        
        for (PpdSectionList gpList: ppdRec.extraList){
            if (gpList.name.equals(groupName))
                return gpList;
        }
        PpdSectionList groupList = new PpdSectionList();
        groupList.name = groupName;
        groupList.text = groupText;
        ppdRec.extraList.add(groupList);
        return groupList;
    }
    
    private PpdSectionList closeGroup(){
        for (PpdSectionList gpList:ppdRec.extraList){
            if (gpList.name.equals("General"))
                return gpList;
        }
        return null;
    }
    
    private PpdItemList openSection(String sectionName, String sectionText, PpdSectionList groupList){
        
        for (PpdItemList secList: groupList){
            if (secList.name.equals("section"))
                return secList;
        }
        PpdItemList sectionList = new PpdItemList();
            sectionList.name = sectionName;
            sectionList.text = sectionText;
            groupList.add(sectionList);
        return sectionList;
    }
    private void addOrientation(PpdSectionList groupList){
        PpdItemList sectionList = new PpdItemList();
        sectionList.name = "orientation-requested";
        sectionList.text = "Orientation";
        sectionList.commandType = CupsType.ENUM;
        sectionList.defaultValue="3";
        sectionList.savedValue = "3";
        sectionList.add(new PpdItem(sectionList, "3", "Portrait"));
        sectionList.add(new PpdItem(sectionList, "4", "Landscape"));
        sectionList.add(new PpdItem(sectionList, "5", "Reverse Portrait"));
        sectionList.add(new PpdItem(sectionList, "6", "Reverse Landscape"));
        groupList.add(sectionList);
    }
        
    private void addCopies(PpdSectionList groupList){
        PpdItemList sectionList = new PpdItemList();
        sectionList.name = "copies";
        sectionList.text = "Copies";
        sectionList.commandType = CupsType.INTEGER;
        sectionList.defaultValue = "1";
        sectionList.savedValue = "1";
        groupList.add(sectionList);
    }
        
    private void addPageRanges(PpdSectionList groupList){
        PpdItemList sectionList = new PpdItemList();
        sectionList.name = "page-ranges";
        sectionList.text = "Page Ranges";
        sectionList.commandType = CupsType.SETOFRANGEOFINTEGER;
        sectionList.defaultValue = "";
        sectionList.savedValue = "";
        groupList.add(sectionList);
    }
        
    private void addPageSides(PpdSectionList groupList){
        PpdItemList sectionList = new PpdItemList();
        sectionList.name = "sides";
        sectionList.text = "Page Sides";
        sectionList.commandType = CupsType.KEYWORD;
        sectionList.defaultValue = "one-sided";
        sectionList.savedValue = "one-sided";
        sectionList.add(new PpdItem(sectionList, "one-sided", "One Sided"));
        sectionList.add(new PpdItem(sectionList, "two-sided-long-edge", "Long Edge"));
        sectionList.add(new PpdItem(sectionList, "two-sided-short-edge", "Short Edge"));
        groupList.add(sectionList);
    }
        
    private void addFitToPage(PpdSectionList groupList){
        PpdItemList sectionList = new PpdItemList();
        sectionList.name = "fit-to-page";
        sectionList.text = "Fit To Page";
        sectionList.commandType = CupsType.BOOLEAN;
        sectionList.defaultValue = "false";
        sectionList.savedValue = "false";
        groupList.add(sectionList);
    }
}
