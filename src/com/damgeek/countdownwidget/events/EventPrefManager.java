package com.damgeek.countdownwidget.events;

import java.util.ArrayList;
import java.util.List;

import com.damgeek.countdownwidget.R;
import com.damgeek.countdownwidget.R.raw;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class EventPrefManager {
	
    private static final String PREFS_NAME
    = "com.damgeek.countdownwidget.MyWidgetProvider";
    private static final String PREF_EVENTID_KEY = "eventid_";
	private static final String TAG = "EventPrefManager";
    private static List<EventInfo> events_half = new ArrayList<EventInfo>();
    private static List<EventInfo> events_full = new ArrayList<EventInfo>();
    
    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public static int loadEventPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int eventId = prefs.getInt(PREF_EVENTID_KEY + appWidgetId, 1);
        return eventId;
    }
    
    public static void deleteEventPref(Context context, int appWidgetId) {
    	SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_EVENTID_KEY + appWidgetId); 
        prefs.commit();
    }
    
    // Write the prefix to the SharedPreferences object for this widget
    public static void saveEventPref(Context context, int appWidgetId, int id) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_EVENTID_KEY + appWidgetId, id);
        prefs.commit();
    }
    
    public static EventInfo getEventInfoByWidgetId(Context context, int appWidgetId) {
    	return getEventInfoByEventId(context, loadEventPref(context, appWidgetId));
    }
    
    public static EventInfo getEventInfoByEventId(Context context, int eventId) {
    	List<EventInfo> events;
    	if (eventId > 1000) {
    		events = getEvents(context, EventInfo.HALF_IRONMAN);
    		eventId -= 1000;
    	}  else
    		events = getEvents(context, EventInfo.FULL_IRONMAN);
    	if (events == null || events.size() == 0) {
    		Log.d(TAG, "events are still empty after reloading");
    		return null;
    	} else
    		return (events.get(eventId-1));
    }

    public static List<EventInfo> getEvents(Context context, String raceType) {
    	if (raceType.equalsIgnoreCase("Full Ironman")) {
    		if (events_full == null || events_full.size() == 0)
    			loadEvents(context, raceType);
    		return events_full;
    	} else {
    		if (events_half == null || events_half.size() == 0)
    			loadEvents(context, raceType);
    		return events_half;
    	}
    }
    
	private static void loadEvents(Context context, String raceType) {
		EventParser parser = new EventParser(context);
		if (raceType.equalsIgnoreCase("Full Ironman")) {
			events_full = parser.parse(R.raw.races_list_full);
		} else {
			events_half = parser.parse(R.raw.races_list_half);
		}
	}
	
	
}
