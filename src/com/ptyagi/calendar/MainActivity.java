package com.ptyagi.calendar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

	private Button createCalAction = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		createCalAction = (Button) findViewById(R.id.button1);
		createCalAction.setOnClickListener(this);
		listCalendars();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button1:
			createCalendar(this);
			break;

		default:
			break;
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	private String ACCOUNT_NAME = "priyanka";
	private String CALENDAR_NAME = "days";
	private MyCalendar m_calendars[];
	
	private ContentValues buildNewCalContentValues() {
		final ContentValues cv = new ContentValues();
		cv.put(Calendars.ACCOUNT_NAME, ACCOUNT_NAME);
		cv.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
		cv.put(Calendars.NAME, CALENDAR_NAME);
		cv.put(Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_NAME);
		cv.put(Calendars.CALENDAR_COLOR, 0xEA8561);
		//user can only read the calendar
		cv.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_READ);
		cv.put(Calendars.OWNER_ACCOUNT, ACCOUNT_NAME);
		cv.put(Calendars.VISIBLE, 1);
		cv.put(Calendars.SYNC_EVENTS, 1);
		return cv;
	}
	
	/**The main/basic URI for the android calendars table*/
	private static final Uri CAL_URI = CalendarContract.Calendars.CONTENT_URI;
	/**Builds the Uri for your Calendar in android database (as a Sync Adapter)*/
	private Uri buildCalUri() {
		return CAL_URI
		.buildUpon()
		.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "false")
		.appendQueryParameter(Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
		.appendQueryParameter(Calendars.ACCOUNT_TYPE,
		CalendarContract.ACCOUNT_TYPE_LOCAL)
		.build();
	}
	
	/**Create and insert new calendar into android database
	* @param ctx The context (e.g. activity)
	*/
	public void createCalendar(Context ctx) {
		ContentResolver cr = ctx.getContentResolver();
		final ContentValues cv = buildNewCalContentValues();
		Uri calUri = buildCalUri();
		//insert the calendar into the database
		cr.insert(calUri, cv);
	}
	
	private void listCalendars() {
    	String[] l_projection = new String[]{"_id", "calendar_displayName"};
    	Uri l_calendars;
    	if (Build.VERSION.SDK_INT >= 8) {
    		l_calendars = Uri.parse("content://com.android.calendar/calendars");
    	} else {
    		l_calendars = Uri.parse("content://calendar/calendars");
    	}
    	Cursor l_managedCursor = this.managedQuery(l_calendars, l_projection, null, null, null);	//all calendars
    	//Cursor l_managedCursor = this.managedQuery(l_calendars, l_projection, "selected=1", null, null);   //active calendars
    	if (l_managedCursor.moveToFirst()) {
    		m_calendars = new MyCalendar[l_managedCursor.getCount()];
    		String l_calName;
    		String l_calId;
    		int l_cnt = 0;
    		int l_nameCol = l_managedCursor.getColumnIndex(l_projection[1]);
    		int l_idCol = l_managedCursor.getColumnIndex(l_projection[0]);
    		do {
    			l_calName = l_managedCursor.getString(l_nameCol);
    			l_calId = l_managedCursor.getString(l_idCol);
    			m_calendars[l_cnt] = new MyCalendar(l_calName, l_calId);
    			++l_cnt;
    		} while (l_managedCursor.moveToNext());
    	}
	}
}

class MyCalendar {
	public String name;
	public String id;
	public MyCalendar(String _name, String _id) {
		name = _name;
		id = _id;
	}
	@Override
	public String toString() {
		return name;
	}
}
