package com.jonbanjo.detect;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Merger {

    public void merge(List<PrinterRec> httpRecs,List<PrinterRec>httpsRecs){
    	List<PrinterRec> tmpRecs = new ArrayList<PrinterRec>();
    	for (PrinterRec httpRec: httpRecs){
    		boolean match = false;
    		for (PrinterRec httpsRec: httpsRecs){
    			if (httpRec.getQueue().equals(httpsRec.getQueue()) &&
    					httpRec.getHost().equals(httpsRec.getHost()) &&
    					httpRec.getPort() == httpsRec.getPort()){
    				match = true;
    				break;
    			}
    		}
    		if (!match){
    			tmpRecs.add(httpRec);
    		}
    	}
    	for (PrinterRec rec: tmpRecs){
    		httpsRecs.add(rec);
    	}
    	Collections.sort(httpsRecs);
    }
}
