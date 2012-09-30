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

			// the following should be changed to load the right image

			// remoteViews.setImageViewResource(R.id.imageView1, R.drawable.icanada);

			// calculate number of days left
			Calendar theDate = CountdownWidgetActivity.getEventInfoByWidgetId(this.getApplicationContext(), widgetId).eventDate; 

			long numOfDays = daysLeft(theDate);
			// Set the text
			remoteViews.setTextViewText(R.id.update,
					String.valueOf(numOfDays)+ " Days");


			//remoteViews.setTextViewText(R.id.eventName, text);


			// Push update for this widget to the home screen
			AppWidgetManager manager = AppWidgetManager.getInstance(this); 
			manager.updateAppWidget(widgetId, remoteViews);
		}

		return super.onStartCommand(intent, flags, startId);  

	}

	public static Calendar getDatePart(Date date){
		Calendar cal = Calendar.getInstance();       // get calendar instance
		cal.setTime(date);      
		cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
		cal.set(Calendar.MINUTE, 0);                 // set minute in hour
		cal.set(Calendar.SECOND, 0);                 // set second in minute
		cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second

		return cal;                                  // return the date part
	}

	/**
	 * This method also assumes endDate >= startDate
	 **/
	/*	public static long daysBetween(Date startDate, Date endDate) {
		Calendar sDate = getDatePart(startDate);
		Calendar eDate = getDatePart(endDate);

		long daysBetween = 0;
		while (sDate.before(eDate)) {
			sDate.add(Calendar.DAY_OF_MONTH, 1);
			daysBetween++;
		}
		return daysBetween;
	}*/

	public static long daysBetween(Calendar startDate, Calendar endDate) {
		Calendar date = (Calendar) startDate.clone();
		long daysBetween = 0;
		while (date.before(endDate)) {
			date.add(Calendar.DAY_OF_MONTH, 1);
			daysBetween++;
		}
		return daysBetween;
	}


	public static long daysLeft(Calendar endDate) {
		Calendar today = Calendar.getInstance();
		return daysBetween(today, endDate);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}