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

import com.jonbanjo.cupsprintservice.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		TextView tv = (TextView) findViewById(R.id.abouttext);
		PackageInfo pInfo;
		String version = "";
		
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = pInfo.versionName;
		} catch (NameNotFoundException e) {
		}
		String html = "<h1>JfCupsPrintService " + version + "</h1>";
		
		html = html + "<p>Copyright &copy; Jon Freeman 2014</p>";

		html = html + 
"<p>This software uses ini4j, jmdns and libraries from the Apache Commons Project. These are " +
"licenced under the Apache Licence. </p>";

html = html + 
"<p>JfCupsPrintService is released under the GNU General Public Licence version 3. " + 
"Further details may be found at " +
"<a href=\"http://mobd.jonbanjo.com/jfcupsprintservice/licence.php\">http://mobd.jonbanjo.com/jfcupsprintservice/licence.php</a>";

		tv.setText(Html.fromHtml(html));
		tv.setMovementMethod(LinkMovementMethod.getInstance());	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
