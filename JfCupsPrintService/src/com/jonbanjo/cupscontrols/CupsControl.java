package com.jonbanjo.cupscontrols;

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

import android.view.View;

public abstract class CupsControl <T extends View> {

	public static int TextSize = 14;
	public static float TextScale = 0.8f;
	protected T control;

	public CupsControl(T control){
		this.control = control;
	}

	public abstract boolean validate();
	
	public abstract void update();

}
