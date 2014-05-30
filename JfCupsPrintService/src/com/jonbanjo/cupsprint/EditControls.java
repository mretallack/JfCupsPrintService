package com.jonbanjo.cupsprint;

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

public class EditControls {
	
	public final static ArrayList<Pair> orientationOpts;
	public final static ArrayList<String>protocols;
	public final static ArrayList<String>showInOpts;
	
	static{
		orientationOpts = new ArrayList<Pair>();
		orientationOpts.add(new Pair("3", "Portrait"));
		orientationOpts.add(new Pair("4", "Landscape"));
		orientationOpts.add(new Pair("5", "Reverse Portrait"));
		orientationOpts.add(new Pair("6", "Reverse Landscape"));
		
		protocols = new ArrayList<String>();
		protocols.add("http");
		protocols.add("https");
		
		showInOpts = new ArrayList<String>();
		showInOpts.add("Both");
		showInOpts.add("Shares");
		showInOpts.add("Print Service");
	}

}
