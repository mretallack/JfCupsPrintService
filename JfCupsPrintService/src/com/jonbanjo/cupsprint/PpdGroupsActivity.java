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

import com.jonbanjo.cups.ppd.PpdSectionList;
import com.jonbanjo.cupsprintservice.R;
import com.jonbanjo.printservice.ServicePrintJobActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PpdGroupsActivity extends Activity {

	private String op;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ppd_groups);
		final ListView groupListView = (ListView) findViewById(R.id.groupListView);
		groupListView.setClickable(true);
		Intent intent = getIntent();
		op = intent.getStringExtra("op");
		if (op == null){
			op = "";
		}
		ArrayAdapter<PpdSectionList> aa;
		if (op.equals("service")){
			aa = new ArrayAdapter<PpdSectionList>(this, 
					android.R.layout.simple_list_item_1, ServicePrintJobActivity.getPpd().getPpdRec().getExtraList());
		}
		else {
			aa = new ArrayAdapter<PpdSectionList>(this, 
					android.R.layout.simple_list_item_1, PrintJobActivity.getPpd().getPpdRec().getExtraList());
		}

		groupListView.setAdapter(aa);
		
		groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				doSection(position);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.aboutmenu, menu);
		return true;
	}

	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case R.id.about:
	    		Intent intent = new Intent(this, AboutActivity.class);
	    		intent.putExtra("printer", "");
	    		startActivity(intent);
	    		break;
	    }
	    return super.onContextItemSelected(item);
	 }

	private void doSection(int index){
		Intent intent = new Intent(this, PpdSectionsActivity.class);
		intent.putExtra("section", index);
		intent.putExtra("op", op);
		startActivity(intent);
	}
}
