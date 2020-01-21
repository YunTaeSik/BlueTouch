package com.bluetouch.function;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bluetouch.R;
import com.bluetouch.util.SharedPrefsUtils;

import java.util.Calendar;

/**
 * Created by User on 2016-05-23.
 */
public class AlarmViewActivity extends Activity implements View.OnClickListener {  //알람이 울리는 뷰 액티비티
    private MediaPlayer mPlayer = null;
    private String hour;
    private String minute;
    private TextView time_text;
    private Button ok_btn;
    private Button restart;
    private AlarmManager mManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activirt_alarm_view);
        time_text = (TextView) findViewById(R.id.time_text);
        ok_btn = (Button) findViewById(R.id.ok_btn);
        restart = (Button) findViewById(R.id.restart);

        ok_btn.setOnClickListener(this);
        restart.setOnClickListener(this);

        mManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        try {
            Intent intent = getIntent();
            hour = intent.getStringExtra("hour");
            minute = intent.getStringExtra("minute");
            time_text.setText(hour + " : " + minute);
            mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.morning);
            if (mPlayer != null) {
                mPlayer.start();
                mPlayer.setLooping(true);
            }
        } catch (IllegalStateException e) {
        } catch (NullPointerException e) {

        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ALARMVIEW_DATA");
        registerReceiver(mRecevier, intentFilter);
        SharedPrefsUtils.setStringPreference(getApplicationContext(), "Activity_flag", "ALARMVIEW");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.stop();
        }
        unregisterReceiver(mRecevier);
        SharedPrefsUtils.setStringPreference(getApplicationContext(), "Activity_flag", null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn:
                mPlayer.stop();
                finish();
                break;
            case R.id.restart:
                restart();
                break;
        }
    }

    private void restart() {  //3분후 다시 울리게하는 함수
        mPlayer.stop();
        finish();
        Calendar today = Calendar.getInstance();
        today.add(Calendar.MINUTE, 3);
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE), Integer.valueOf(hour), today.get(Calendar.MINUTE));
        Log.e("time", String.valueOf(hour) + ":" + String.valueOf(today.get(Calendar.MINUTE)));
        Log.i("HelloAlarmActivity", today.getTime().toString());
        Intent intent = new Intent(this, AlarmViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("hour", hour);
        bundle.putString("minute", minute);
        intent.putExtras(bundle);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mManager.set(AlarmManager.RTC_WAKEUP, today.getTimeInMillis(), pIntent);
    }

    private BroadcastReceiver mRecevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("ALARMVIEW_DATA")) {
                int data = intent.getIntExtra("data_alarmview", 0);
                Log.e("ALARMVIEW_DATA", String.valueOf(data));
                if (data == 1) {
                    restart();
                } else if (data == 2) {
                } else if (data == 3) {
                    mPlayer.stop();
                    finish();
                }
            }
        }
    };
}
