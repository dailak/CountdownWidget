package com.damgeek.countdownwidget.events;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damgeek.countdownwidget.utils.JSONParser;

import android.app.Activity;
import android.content.Context;

public class EventParser {
	// url to make request
	//private static String url = "http://www.dailak.com/ironmancountdown/races_list.json";
	private Context mContext;
	
	// JSON Node names
	private static final String TAG_RACES = "races";
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_DATE = "date";
	private static final String TAG_UTCOFFSET = "utcoffset";
	private static final String TAG_BACKGROUND = "background";
	 
	// races JSONArray
	JSONArray races = null;
	
	public EventParser(Context context) {
		mContext = context;
	}
	
	public ArrayList<EventInfo> parse(int raceListId) {
		ArrayList<EventInfo> events = new ArrayList<EventInfo>();
		EventInfo ev;
		// Creating JSON Parser instance
		JSONParser jParser = new JSONParser();
		 
		// getting JSON string from URL
		//JSONObject json = jParser.getJSONFromUrl(url);
		JSONObject json = jParser.getJSONFromFile(raceListId, mContext.getResources());
		//JSONObject json = new JSONObject();
		
		try {
		    // Getting Array of Contacts
		    races = json.getJSONArray(TAG_RACES);
		    JSONObject c;
		    DateFormat formatter = new SimpleDateFormat("MM/dd/yy");
		    Date date;
		    // looping through All Contacts
		    for(int i = 0; i < races.length(); i++){
		        c = races.getJSONObject(i);
		        ev = new EventInfo();
		        ev.id = Integer.parseInt(c.getString(TAG_ID));
		        ev.name = c.getString(TAG_NAME);
		        date = formatter.parse(c.getString(TAG_DATE)); 
		        ev.eventDate = GregorianCalendar.getInstance();
		        ev.eventDate.setTime(date);
		        // Storing each json item in variable
		        ev.offset = Integer.parseInt(c.getString(TAG_UTCOFFSET));
		        ev.logo = c.getString(TAG_BACKGROUND);
		        events.add(ev);
		    }
		} catch (JSONException e) {
		    e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    return events;
	}
}
