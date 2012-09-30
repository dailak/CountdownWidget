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
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

public class CountdownWidgetActivity extends ListActivity
implements LoaderManager.LoaderCallbacks<Cursor>{
	/** Called when the activity is first created. */

    private static final String PREFS_NAME
    = "com.damgeek.countdownwidget.MyWidgetProvider";
    private static final String PREF_EVENTID_KEY = "eventid_";
    
    static List<EventInfo> events = new ArrayList<EventInfo>();
	
	SimpleCursorAdapter mAdapter;
	// These are the Contacts rows that we will retrieve
	static final String[] PROJECTION = new String[] {ContactsContract.Data._ID,
		ContactsContract.Data.DISPLAY_NAME};
	// This is the select criteria
	static final String SELECTION = "((" + 
			ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
			ContactsContract.Data.DISPLAY_NAME + " != '' ))";

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

		// For the cursor adapter, specify which columns go into which views
		//String[] fromColumns = {ContactsContract.Data.DISPLAY_NAME};
		//int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1

		// Create an empty adapter we will use to display the loaded data.
		// We pass null for the cursor, then update it in onLoadFinished()
		/*mAdapter = new SimpleCursorAdapter(this, 
                android.R.layout.simple_list_item_1, null,
                fromColumns, toViews, 0);*/
		
		//Calendar theDate = new GregorianCalendar(TimeZone.getTimeZone("PST"));
		EventParser parser = new EventParser(CountdownWidgetActivity.this);
		events = parser.parse();
		
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
		
		ArrayAdapter<EventInfo> adapter = new ArrayAdapter<EventInfo>(this,
				android.R.layout.simple_list_item_1, events);
		setListAdapter(adapter);
		//setListAdapter(mAdapter);

		// Prepare the loader.  Either re-connect with an existing one,
		// or start a new one.
		//getLoaderManager().initLoader(0, null, this);

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

		//mAppWidgetPrefix.setText(loadTitlePref(CountdownWidgetActivity.this, mAppWidgetId));

	}

    // Write the prefix to the SharedPreferences object for this widget
    static void saveEventPref(Context context, int appWidgetId, int id) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_EVENTID_KEY + appWidgetId, id);
        prefs.commit();
    }
	
    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static int loadEventPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int eventId = prefs.getInt(PREF_EVENTID_KEY + appWidgetId, 1);
        return eventId;
    }
    
    static void deleteEventPref(Context context, int appWidgetId) {
    	SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_EVENTID_KEY + appWidgetId); 
        prefs.commit();
    }
    
    static EventInfo getEventInfoByWidgetId(Context context, int appWidgetId) {
    	return getEventInfoByEventId(loadEventPref(context, appWidgetId));
    }
    
    static EventInfo getEventInfoByEventId(int eventId) {
    	return (events.get(eventId-1));
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

        EventInfo event = (EventInfo) getListAdapter().getItem(position);
        saveEventPref(context, mAppWidgetId, event.id);
		

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