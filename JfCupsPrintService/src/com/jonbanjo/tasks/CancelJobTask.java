package com.jonbanjo.tasks;

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

import com.jonbanjo.cups.CupsClient;
import com.jonbanjo.cups.PrintRequestResult;
import com.jonbanjo.cups.operations.AuthInfo;
import com.jonbanjo.cupsprint.Util;

import android.app.Activity;
import android.os.AsyncTask;

public class CancelJobTask extends AsyncTask<Void, Void, Void>{

	public enum Operation{
		CANCEL, HOLD, RELEASE
	}
	
	private Activity activity;
	private CupsClient client;
	private AuthInfo auth;
	private int jobId;
	private Operation op;
	private PrintRequestResult result;
	
	public CancelJobTask(Activity activity, CupsClient client, AuthInfo auth, Operation op, int jobId){
		super();
		this.activity = activity;
		this.client = client;
		this.auth = auth;
		this.op = op;
		this.jobId = jobId;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			switch (op){
			case CANCEL:
				result = client.cancelJob(jobId, auth);
				break;
			case HOLD:
				result = client.holdJob(jobId, auth);
				break;
			case RELEASE:
				result = client.releaseJob(jobId, auth);
				break;
			}
			Util.showToast(activity, result.getResultDescription());
		}catch (Exception e){
			Util.showToast(activity, e.toString());
		}
		return null;
	}

}
