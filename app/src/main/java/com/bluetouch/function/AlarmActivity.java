package com.bluetouch.function;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bluetouch.R;
import com.bluetouch.util.SharedPrefsUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by User on 2016-05-23.
 */
public class AlarmActivity extends AppCompatActivity /*implements TimePicker.OnTimeChangedListener*/ {

    private AlarmManager mManager;
    private GregorianCalendar mCalendar;
    private GregorianCalendar reser_mCalendar;
    private TimePicker mTime;

    private Button ok_btn;
    private Button cancel_btn;

    private NotificationManager mNotification;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //통지 매니저를 취득
        mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mCalendar = new GregorianCalendar();
        Log.i("HelloAlarmActivity", mCalendar.getTime().toString());
        setContentView(R.layout.activity_alarm);

        mTime = (TimePicker) findViewById(R.id.time_picker);
        mTime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        mTime.setIs24HourView(true);

        ok_btn = (Button) findViewById(R.id.ok_btn);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm();
                finish();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAlarm();
                finish();
                Toast.makeText(getApplicationContext(), "알람이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ALARM_DATA");
        registerReceiver(mRecevier, intentFilter);
    }

    //알람의 설정
    private void setAlarm() {
        reser_mCalendar = new GregorianCalendar();
        mCalendar = new GregorianCalendar();
        reser_mCalendar.set(reser_mCalendar.get(Calendar.YEAR), reser_mCalendar.get(Calendar.MONTH), reser_mCalendar.get(Calendar.DATE), mTime.getCurrentHour(), mTime.getCurrentMinute());
        if (reser_mCalendar.getTimeInMillis() > mCalendar.getTimeInMillis() && reser_mCalendar.getTimeInMillis() != mCalendar.getTimeInMillis()) {
            mManager.set(AlarmManager.RTC_WAKEUP, reser_mCalendar.getTimeInMillis(), pendingIntent());
        } else {
            Calendar today = Calendar.getInstance();
            today.add(Calendar.DATE, 1);
            today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE), mTime.getCurrentHour(), mTime.getCurrentMinute());
            mManager.set(AlarmManager.RTC_WAKEUP, today.getTimeInMillis(), pendingIntent());
        }

    }

    //알람의 해제
    private void resetAlarm() {
        mManager.cancel(pendingIntent());
    }

    //알람의 설정 시각에 발생하는 인텐트 작성
    private PendingIntent pendingIntent() {
        Intent intent = new Intent(this, AlarmViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("hour", String.valueOf(mTime.getCurrentHour()));
        bundle.putString("minute", String.valueOf(mTime.getCurrentMinute()));
        intent.putExtras(bundle);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pIntent;


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRecevier);
        SharedPrefsUtils.setStringPreference(getApplicationContext(), "Activity_flag", null);
    }

    private BroadcastReceiver mRecevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("ALARM_DATA")) {
                int data = intent.getIntExtra("data_alarm", 0);
                Log.e("ALARM_DATA", String.valueOf(data));
                if (data == 1) {
                    //mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
                    if (mTime.getCurrentMinute() == 59) {
                        mTime.setCurrentMinute(mTime.getCurrentMinute() + 1);
                        mTime.setCurrentHour(mTime.getCurrentHour() + 1);
                    }else{
                        mTime.setCurrentMinute(mTime.getCurrentMinute() + 1);
                    }
                } else if (data == 2) {
                    mTime.setCurrentHour(mTime.getCurrentHour() + 1);
                } else if (data == 3) {
                    setAlarm();
                    finish();
                }
            }
        }
    };
}