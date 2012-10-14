package com.damgeek.countdownwidget;

import java.util.Calendar;
import java.util.Date;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

// obsoleted

public class UpdateWidgetService extends Service {

	@Override  
	public void onCreate()  
	{  
		super.onCreate();  
	}  


	@Override
	public int onStartCommand(Intent intent, int flags, int startId){  	
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
				.getApplicationContext());

		ComponentName thisWidget = new ComponentName(getApplicationContext(),
				MyWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		// ImageView logoImage;

		for (int widgetId : allWidgetIds) {

			RemoteViews remoteViews = new RemoteViews(this
					.getApplicationContext().getPackageName(),
					R.layout.widget_layout);

			Calendar eventDate = CountdownWidgetActivity.getEventInfoByWidgetId(this.getApplicationContext(), widgetId).eventDate;
			String strCountDown = getCountDownText(eventDate);
			remoteViews.setTextViewText(R.id.update, strCountDown);


			//remoteViews.setTextViewText(R.id.eventName, text);


			// Push update for this widget to the home screen
			AppWidgetManager manager = AppWidgetManager.getInstance(this); 
			manager.updateAppWidget(widgetId, remoteViews);
		}

		return super.onStartCommand(intent, flags, startId);  

	}

	public static String getCountDownText(Calendar eventDate){
		String strCountDown = "Go!";
		
		long numOfHours = hoursLeft(eventDate);
		
		if (numOfHours == -1)
		{
			strCountDown = "Go!";
			
		}
		else {
			double dblHours = numOfHours;
			long numOfDays = (long) Math.ceil(dblHours / 24);
				if (numOfDays > 1)
					strCountDown = String.valueOf(numOfDays)+ " Days";
				else
					if (numOfHours > 1)
						strCountDown = String.valueOf(numOfHours)+ " Hours";
					else
						strCountDown = String.valueOf(numOfHours)+ " Hour";	
		}
		
		return strCountDown;
	}
	
	private static long hoursLeft(Calendar calEvent) {
		Calendar calToday = Calendar.getInstance();
		double dblHours = 0;
		
		if (calToday.after(calEvent))
			return -1;
		
		long milliToday = calToday.getTimeInMillis();
		long milliEvent = calEvent.getTimeInMillis();
		Long milliDiff = milliEvent - milliToday;
		double dblMilliDiff = milliDiff.doubleValue();
		
		dblHours = (dblMilliDiff) / 1000 / 60 / 60;
		
		return (long) Math.ceil(dblHours);
	}
	
	// below to be removed
	private static Calendar getDatePart(Date date){
		Calendar cal = Calendar.getInstance();       // get calendar instance
		cal.setTime(date);      
		cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
		cal.set(Calendar.MINUTE, 0);                 // set minute in hour
		cal.set(Calendar.SECOND, 0);                 // set second in minute
		cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second

		return cal;                                  // return the date part
	}

	// below function to be removed
	private static long daysBetween(Calendar startDate, Calendar endDate) {
		Calendar date = (Calendar) startDate.clone();
		long daysBetween = 0;
		while (date.before(endDate)) {
			date.add(Calendar.DAY_OF_MONTH, 1);
			daysBetween++;
		}
		return daysBetween;
	}

	// below function to be removed
	private static long daysLeft(Calendar endDate) {
		Calendar today = Calendar.getInstance();
		return daysBetween(today, endDate);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}