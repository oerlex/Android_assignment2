package com.example.oerlex.android_assignment2.alarmClock;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.example.oerlex.android_assignment2.R;

public class AlarmSetActivity extends AppCompatActivity {

    //When the alarm goes of this intent is opened. It is basically just a textview that can be clicked what results in this intent being closed
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Ringtone r;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

        if (AlarmClockActivity.alarmToggle != null) {
            AlarmClockActivity.alarmToggle.setChecked(false);
        }

        TextView textView = (TextView) findViewById(R.id.alarmText);
        if (textView != null) {
            textView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    r.stop();
                    finish();
                }
            });
        }
    }
}
