package com.jonbanjo.cups;

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

import com.jonbanjo.cups.operations.AuthInfo;
import com.jonbanjo.cups.operations.OperationResult;
import com.jonbanjo.cups.operations.cups.CupsGetPrintersOperation;
import com.jonbanjo.cups.operations.ipp.IppCancelJobOperation;
import com.jonbanjo.cups.operations.ipp.IppGetJobAttributesOperation;
import com.jonbanjo.cups.operations.ipp.IppGetJobsOperation;
import com.jonbanjo.cups.operations.ipp.IppGetPrinterAttributesOperation;
import com.jonbanjo.cups.operations.ipp.IppHoldJobOperation;
import com.jonbanjo.cups.operations.ipp.IppPrintJobOperation;
import com.jonbanjo.cups.operations.ipp.IppReleaseJobOperation;
import com.jonbanjo.vppserver.schema.ippclient.Attribute;
import com.jonbanjo.vppserver.schema.ippclient.AttributeGroup;
import com.jonbanjo.vppserver.schema.ippclient.AttributeValue;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CupsClient {
   
  public static final int USER_AllOWED = 0;
  public static final int USER_DENIED = 1;
  public static final int USER_NOT_ALLOWED = 2;

  
  private URL url = null;
  private String userName = "anonymous";
  
  private static final String listAttrs = 
     "device-uri printer-name printer-info printer-location printer-make-and-model printer-uri-supported";
  private static final String stdAttrs = 
     "device-uri printer-name requesting-user-name-allowed requesting-user-name-denied printer-info printer-location printer-make-and-model printer-uri-supported";
          
  private static final String extAttrs = "document-format-supported";
  
  public CupsClient(URL url){
      this.url =url;
  }
  
    public CupsClient(URL url, String userName){
      this.url = url;
      this.userName = userName;
    }

    public URL getUrl(){
        return url;
    }
    
    public String getUserName(){
      return userName;
    }
  
    public void setUserName(String userName){
        this.userName = userName;
    }
    
    public List<CupsPrinter> listPrinters(AuthInfo auth) throws UnsupportedEncodingException, IOException, Exception{
        List<CupsPrinter> printers = new ArrayList<CupsPrinter>();
        OperationResult result = new CupsGetPrintersOperation().getPrinters(url, null, auth, listAttrs);
        for (AttributeGroup group : result.getIppResult().getAttributeGroupList()) {

            if (group.getTagName().equals("printer-attributes-tag")) {
                printers.add(setPrinter(url, group));
            }
        }
        return printers;
    }

    public CupsPrinter getPrinter(String queue, AuthInfo auth) throws UnsupportedEncodingException, IOException, Exception{
        return getPrinter(queue, auth, stdAttrs);
    }
  
    public CupsPrinter getPrinter(String queue, AuthInfo auth, boolean extended) throws UnsupportedEncodingException, IOException, Exception{
        if (extended)
            return getPrinter(queue, auth, stdAttrs + " " + extAttrs);
        else 
            return getPrinter(queue, auth, stdAttrs);
    }
  
    public List<CupsPrintJobAttributes> getJobs(String queue, AuthInfo auth, WhichJobsEnum whichJobs, boolean myJobs) throws IOException, Exception {    
        URL printerUrl = new URL(url.toString() + queue);
        return new IppGetJobsOperation().getPrintJobs(printerUrl, auth, userName, whichJobs, myJobs);
    }

    public CupsPrintJobAttributes getJobAttributes(int jobID) throws UnsupportedEncodingException, IOException{
        return new IppGetJobAttributesOperation().getPrintJobAttributes(url, userName, jobID);
    }


    public PrintRequestResult cancelJob(int jobID, AuthInfo auth) throws UnsupportedEncodingException, IOException{
        return new IppCancelJobOperation().cancelJob(url, auth, userName, jobID);
    }

    public PrintRequestResult holdJob(int jobID, AuthInfo auth) throws UnsupportedEncodingException, IOException{
        return new IppHoldJobOperation().holdJob(url, auth, userName, jobID);
    }

    public PrintRequestResult releaseJob(int jobID, AuthInfo auth) throws UnsupportedEncodingException, IOException{
        return new IppReleaseJobOperation().releaseJob(url, auth, userName, jobID);
    }

    public PrintRequestResult print(CupsPrinter printer, CupsPrintJob printJob, AuthInfo auth) throws Exception{
        return print(printer.getPrinterUrl(), printJob, auth);
    }
    
    public PrintRequestResult print(String queue, CupsPrintJob printJob, AuthInfo auth) throws Exception {
         URL printerUrl = new URL(url.toString() + queue);
         return print(printerUrl, printJob, auth);
    }
   
    private PrintRequestResult print(URL printerUrl, CupsPrintJob printJob, AuthInfo auth) throws Exception {
        IppPrintJobOperation command = new IppPrintJobOperation();
        return command.print(printerUrl, userName, printJob, auth);
    }
    
    private CupsPrinter getPrinter(String queue, AuthInfo auth, String attrs) throws UnsupportedEncodingException, IOException, Exception{
        URL printerUrl = new URL(url.toString() + queue);
        IppGetPrinterAttributesOperation op = new IppGetPrinterAttributesOperation();
        HashMap<String,String> map = new HashMap<String, String>();
        map.put("requested-attributes", attrs);
        OperationResult result = op.getPrinterAttributes(printerUrl, userName, auth, map);
        String status = result.getHttpStatusResult();
        if (!(status.contains("200"))){
            throw new Exception(status);
        }
        for (AttributeGroup group : result.getIppResult().getAttributeGroupList()) {
            if (group.getTagName().equals("printer-attributes-tag")) {
                return setPrinter(url, group);
            }
            else if (group.getTagName().equals("unsupported-attributes-tag")) {
                throw new Exception(this.url + " is not a CUPS server");
            }
        }
        return null;
    } 
    
    private CupsPrinter setPrinter(URL url, AttributeGroup group) throws IOException, Exception{
        URL    printerUri = null;
        String printerName = null;
        String printerLocation = null;
        String printerDescription = null;
        String printerMake = null;
        boolean allowed = true;
        boolean denied = false;
        boolean isCups = false;
        ArrayList<String>supportedMimeTypes = null;
        
        for (Attribute attr : group.getAttribute()) {
            String attrName = attr.getName();
            if (attrName.equals("printer-uri-supported")) {
                String tmp = attr.getAttributeValue().get(0).getValue().replace("ipp://", url.getProtocol() + "://");
                URL tmpUri = new URL(tmp);
                printerUri = new URL(url.toString() + tmpUri.getPath());
            }else if (attrName.equals("printer-name")) {
                printerName = attr.getAttributeValue().get(0).getValue();
            } else if (attrName.equals("printer-location")) {
                if (attr.getAttributeValue() != null && attr.getAttributeValue().size() > 0) {
                    printerLocation = attr.getAttributeValue().get(0).getValue();
                }
            } else if (attrName.equals("printer-info")) {
                if (attr.getAttributeValue() != null && attr.getAttributeValue().size() > 0) {
                printerDescription = attr.getAttributeValue().get(0).getValue();
                }
            } else if (attrName.equals("printer-make-and-model")) {
                if (attr.getAttributeValue() != null && attr.getAttributeValue().size() > 0) {
                printerMake = attr.getAttributeValue().get(0).getValue();
                }
            } else if (attrName.equals("requesting-user-name-allowed")) {
                if (attr.getAttributeValue() != null && attr.getAttributeValue().size() > 0) {
                allowed = getAllowed(attr.getAttributeValue());
                }
            } else if (attrName.equals("requesting-user-name-denied")) {
                if (attr.getAttributeValue() != null && attr.getAttributeValue().size() > 0) {
                denied = getDenied(attr.getAttributeValue());
                }
            } else if (attrName.equals("document-format-supported")) {
                if (attr.getAttributeValue() != null && attr.getAttributeValue().size() > 0) {
                    supportedMimeTypes = getSupportedMimeTypes(attr.getAttributeValue());
                }
            } else if (attrName.equals("device-uri")) {
                if (attr.getAttributeValue() != null){
                    isCups = true;
                }
            }

        }
        if (!isCups){
            throw new Exception(this.url + " is not a CUPS Server");
        }
        CupsPrinter printer = new CupsPrinter(printerUri, printerName, 
                printerDescription, printerLocation, printerMake);
        if (denied){
            printer.setAllowUser(USER_DENIED);
        }
        else {
            if (!allowed){
                printer.setAllowUser(USER_NOT_ALLOWED);
            }
        }
        if (supportedMimeTypes != null){
            printer.setSupportedMimeTypes(supportedMimeTypes);
        }
        return printer;
   }
    
    private boolean getAllowed(List<AttributeValue> list){
        if (list.isEmpty()){
            return true;
        }
        for (AttributeValue value: list){
            if (value.getValue().equals(userName)){
                return true;
            }
        }
        return false;
   }
   
   private boolean getDenied(List<AttributeValue> list){
        if (list.isEmpty()){
            return false;
        }
        for (AttributeValue value: list){
            if (value.getValue().equals(userName)){
                return true;
            }
        }
        return false;
   }
   
   private ArrayList<String> getSupportedMimeTypes(List<AttributeValue> list){
       ArrayList<String> types = new ArrayList<String>();
       for (AttributeValue av :list){
            if (av.getTagName().equals("mimeMediaType")){
                types.add(av.getValue());
            }
       }
       return types;
   }
}
