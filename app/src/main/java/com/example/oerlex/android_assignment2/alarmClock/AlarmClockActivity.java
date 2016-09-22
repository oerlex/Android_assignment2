package com.example.oerlex.android_assignment2.alarmClock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import com.example.oerlex.android_assignment2.R;

public class AlarmClockActivity extends AppCompatActivity {

    AlarmManager alarmManager;
    private PendingIntent notifyIntent;
    private TimePicker alarmTimePicker;
    private TextView timeView;
    //TODO
    private AppCompatActivity alarmClockActivity;
    DateFormat dateFormatHm;
    public static ToggleButton alarmToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_clock);
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        alarmTimePicker.setIs24HourView(true);
        timeView = (TextView) findViewById(R.id.alarmText);
        alarmToggle = (ToggleButton) findViewById(R.id.alarmToggle);
        dateFormatHm =  new java.text.SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
    }

    //if the toggle button (Alarm on/off) gets toggled either the alarm manager and the pending are getting initialized or canceled
    public void onToggleClicked(View view) {
        if (((ToggleButton) view).isChecked()) {
            alarmClockActivity = this;
            new Thread(sleepThread).start();

            Date d =  setAlarm();

            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmSetActivity.class);
            notifyIntent = PendingIntent.getActivity(this, 1, intent, 0);

            am.set(AlarmManager.RTC_WAKEUP, d.getTime(), notifyIntent);

            timeView.setText(dateFormatHm.format(d));

        } else {
            alarmManager.cancel(notifyIntent);
            timeView.setText("canceled");
            Log.d("MyActivity", "Alarm Off");
        }
    }

    //Sleeping thread for updating the clock every 5 second
    private Runnable sleepThread = new Runnable() {
        DateFormat dateFormatHm = new java.text.SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        public void run() {
            while (true) {
                try {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            timeView.setText(dateFormatHm.format(Calendar.getInstance().getTime()));
                        }
                    });
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //Convertes the picked hour and minute into a date object that can be displayed
    private Date setAlarm(){
        int hour = alarmTimePicker.getCurrentHour();
        int minute = alarmTimePicker.getCurrentMinute();

        Calendar c = GregorianCalendar.getInstance();

        if(c.get(Calendar.HOUR_OF_DAY) > hour){
            c.add(Calendar.DATE,1);
        }
        c.set(Calendar.HOUR_OF_DAY,hour);
        c.set(Calendar.MINUTE,minute);

        return c.getTime();
    }
}
