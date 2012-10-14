package com.damgeek.countdownwidget;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class MyWidgetProvider extends AppWidgetProvider {
	
	//private static final String ACTION_CLICK = "ACTION_CLICK";
	private PendingIntent service = null;
		
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
        
        final Calendar TIME = Calendar.getInstance();  
        TIME.set(Calendar.MINUTE, 0);  
        TIME.set(Calendar.SECOND, 0);  
        TIME.set(Calendar.MILLISECOND, 0);  
  
        final Intent i = new Intent(context, UpdateWidgetService.class);  
  
        if (service == null)  
        {  
            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);  
        }  
  
        m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 1000 * 60, service);  
		
        // Build the intent to call the service
//		Intent intent = new Intent(context.getApplicationContext(), MyWidgetProvider.class);
//		
//		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		
		// Update the widgets via the service
//		context.startService(intent);
		
        		
		// Get all ids
		ComponentName thisWidget = new ComponentName(context,
				MyWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		
		for (int widgetId : allWidgetIds) {
			EventInfo eventInfo = CountdownWidgetActivity.getEventInfoByWidgetId(context, widgetId);
	        updateAppWidget(context, appWidgetManager, widgetId, eventInfo);
		}
		
	}
	
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId, EventInfo eventInfo) {
        // Getting the string this way allows the string to be localized.  The format
        // string is filled in using java.util.Formatter-style format strings.
        // CharSequence text = CountdownWidgetActivity.loadEventPref(context, appWidgetId);

        // Construct the RemoteViews object.  It takes the package name (in our case, it's our
        // package, but it needs this because on the other side it's the widget host inflating
        // the layout from our package).
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        int imageSrcId = context.getResources().getIdentifier(eventInfo.logo,"drawable",context.getPackageName());
        views.setImageViewResource(R.id.imageView1, imageSrcId);
        
        
        //long numOfDays = UpdateWidgetService.daysLeft(eventInfo.eventDate);
		//views.setTextViewText(R.id.update, String.valueOf(numOfDays)+ " Days");
        views.setTextViewText(R.id.update, UpdateWidgetService.getCountDownText(eventInfo.eventDate));
		
        // Tell the widget manager
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

  
    @Override
    public void onDisabled(Context context) {  
        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
        m.cancel(service);  
    }  
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            CountdownWidgetActivity.deleteEventPref(context, appWidgetIds[i]);
        }
    }




}
