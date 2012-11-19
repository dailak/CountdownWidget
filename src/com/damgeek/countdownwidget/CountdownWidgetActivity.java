package com.damgeek.countdownwidget;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import com.damgeek.countdownwidget.events.EventInfo;
import com.damgeek.countdownwidget.events.EventPrefManager;

public class CountdownWidgetActivity extends ListActivity
implements LoaderManager.LoaderCallbacks<Cursor>{
	/** Called when the activity is first created. */

	List<String> raceTypes = new ArrayList<String>();
    
	SimpleCursorAdapter mAdapter;
	// These are the Contacts rows that we will retrieve
	static final String[] PROJECTION = new String[] {ContactsContract.Data._ID,
		ContactsContract.Data.DISPLAY_NAME};
	// This is the select criteria
	static final String SELECTION = "((" + 
			ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
			ContactsContract.Data.DISPLAY_NAME + " != '' ))";
	private static final String TAG = "CountdownWidgetActivity"; 

	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	EditText mAppWidgetPrefix;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set the result to CANCELED.  This will cause the widget host to cancel
		// out of the widget placement if they press the back button.
		setResult(RESULT_CANCELED);

		// Set the view layout resource to use.
		setContentView(R.layout.main);

		// Create a progress bar to display while the list loads
		ProgressBar progressBar = new ProgressBar(this);
		progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		progressBar.setIndeterminate(true);
		getListView().setEmptyView(progressBar);

		// Must add the progress bar to the root of the layout
		ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
		root.addView(progressBar);

		raceTypes.add("Full Ironman");
		raceTypes.add("Ironman 70.3");
		

		
/*		events.add(new EventInfo(1,"70.3 Sri Lanka","isrilanka70", new GregorianCalendar(2013, 1, 19, 7, 0, 0) ));
		events.add(new EventInfo(2,"70.3 Singapore","isingapore70",new GregorianCalendar(2013, 2, 18, 7, 0, 0)));
		events.add(new EventInfo(3,"70.3 New Orleans","ineworleans70",new GregorianCalendar(2013, 3, 22, 7, 0, 0)));
		events.add(new EventInfo(4,"Canada","icanada",new GregorianCalendar(2013, 7, 26, 7, 0, 0)));
		events.add(new EventInfo(5,"70.3 Busselton","ibusselton70",new GregorianCalendar(2013, 4, 5, 7, 0, 0)));
		events.add(new EventInfo(6,"St. George Utah","istgeorge",new GregorianCalendar(2013, 4, 5, 7, 0, 0)));
		events.add(new EventInfo(7,"Australia Port Macquarie","iaustraliaportmacquarie",new GregorianCalendar(2013, 4, 6, 7, 0, 0)));
		events.add(new EventInfo(8,"70.3 St. Croix","istcroix70",new GregorianCalendar(2013, 4, 6, 7, 0, 0)));
		events.add(new EventInfo(9,"70.3 Mallorca","imallorca70",new GregorianCalendar(2013, 4, 12, 7, 0, 0)));
		events.add(new EventInfo(10,"Texas","itexas",new GregorianCalendar(2013, 4, 19, 7, 0, 0)));*/

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, raceTypes);
		setListAdapter(adapter);
		
		// Find the widget id from the intent. 
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		// If they gave us an intent without the widget id, just bail.
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}

	}


	
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		return new CursorLoader(this, ContactsContract.Data.CONTENT_URI,
				PROJECTION, SELECTION, null, null);
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in.  (The framework will take care of closing the
		// old cursor once we return.)
		mAdapter.swapCursor(data);
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed.  We need to make sure we are no
		// longer using it.
		mAdapter.swapCursor(null);
	}

	@Override 
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Push widget update to surface with newly set prefix
		final Context context = CountdownWidgetActivity.this;

		if (getListAdapter().getItem(position).getClass() == String.class) {
			// Race Type Selected
			String raceType = (String) getListAdapter().getItem(position);
			List<EventInfo> events = EventPrefManager.getEvents(this, raceType);
			if (events == null || events.size() == 0)
				Log.d(TAG, "event list is null after selecting an event, raceType:"+raceType);
			ArrayAdapter<EventInfo> adapter = new ArrayAdapter<EventInfo>(this,
					android.R.layout.simple_list_item_1, events);
			setListAdapter(adapter);
		} else {
			// Race selected - create widget
	        EventInfo event = (EventInfo) getListAdapter().getItem(position);
	        EventPrefManager.saveEventPref(context, mAppWidgetId, event.id);
			

			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			MyWidgetProvider.updateAppWidget(context, appWidgetManager,
			                mAppWidgetId, event);

			// Make sure we pass back the original appWidgetId
			Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
			setResult(RESULT_OK, resultValue);
			finish();
		}
	}


}