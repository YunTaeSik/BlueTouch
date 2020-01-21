package com.bluetouch.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluetouch.R;
import com.bluetouch.fragment.BlueMainOneFragment;
import com.bluetouch.util.SharedPrefsUtils;

/**
 * Created by Owner on 2016-05-01.
 */
public class SettingDailog extends Activity implements View.OnClickListener { //APP 기능 드래그앤드롭 후 뜨는 다이얼로그 액티비티
    private TextView setting_information;
    private Button dialog_setting_ok;
    private TextView text_one;
    private TextView text_two;
    private EditText edit_one;
    private EditText edit_two;
    private LinearLayout linear_two;

    private String getTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_setting_dailog);
        setFinishOnTouchOutside(false);

        setting_information = (TextView) findViewById(R.id.setting_information);
        dialog_setting_ok = (Button) findViewById(R.id.dialog_setting_ok);
        text_one = (TextView) findViewById(R.id.text_one);
        text_two = (TextView) findViewById(R.id.text_two);
        edit_one = (EditText) findViewById(R.id.edit_one);
        edit_two = (EditText) findViewById(R.id.edit_two);
        linear_two = (LinearLayout) findViewById(R.id.linear_two);

        dialog_setting_ok.setOnClickListener(this);

        Intent intent = getIntent();
        getTag = intent.getStringExtra("Setting_flag");  //태그 받아오기

        if (getTag.equals(BlueMainOneFragment.CALL)) {  //태그가  "전화"일때
            setting_information.setText("블루 터치를 이용하여 전화 기능을 이용할 번호를 입력하여 주세요.");
            linear_two.setVisibility(View.GONE);
            text_one.setText("번호 입력");
            if (SharedPrefsUtils.getStringPreference(getApplicationContext(), "call_number") != null) {
                edit_one.setText(SharedPrefsUtils.getStringPreference(getApplicationContext(), "call_number"));
            }
        } else if (getTag.equals(BlueMainOneFragment.MESSAGE)) {  //태그가  "메세지"일때
            setting_information.setText("블루 터치를 이용하여 메세지 기능을 이용할 번호와 내용을 입력하여 주세요.");
            text_one.setText("번호 입력");
            text_two.setText("내용 입력");
            if (SharedPrefsUtils.getStringPreference(getApplication(), "message_number") != null) {
                edit_one.setText(SharedPrefsUtils.getStringPreference(getApplication(), "message_number"));
            }

            if (SharedPrefsUtils.getStringPreference(getApplication(), "message_string") != null) {
                edit_two.setText(SharedPrefsUtils.getStringPreference(getApplication(), "message_string"));
            }

        } else if (getTag.equals(BlueMainOneFragment.NAVIGATION)) {  //태그가  "네비게이션"일때
            setting_information.setText("블루 터치를 이용하여 네비게이션 기능을 이용할 자주가는 출발지와 도착지을 입력하여 주세요.");
            text_one.setText("출발지");
            text_two.setText("목적지");
            if (SharedPrefsUtils.getStringPreference(getApplicationContext(), "navigation_start") != null) {
                edit_one.setText(SharedPrefsUtils.getStringPreference(getApplicationContext(), "navigation_start"));
            }
            if (SharedPrefsUtils.getStringPreference(getApplicationContext(), "navigation_finish") != null) {
                edit_two.setText(SharedPrefsUtils.getStringPreference(getApplicationContext(), "navigation_finish"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_setting_ok: //해당 정보 저장
                if (getTag.equals(BlueMainOneFragment.CALL)) {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), "call_number", edit_one.getText().toString());
                } else if (getTag.equals(BlueMainOneFragment.MESSAGE)) {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), "message_number", edit_one.getText().toString());
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), "message_string", edit_two.getText().toString());
                } else if (getTag.equals(BlueMainOneFragment.NAVIGATION)) {
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), "navigation_start", edit_one.getText().toString());
                    SharedPrefsUtils.setStringPreference(getApplicationContext(), "navigation_finish", edit_two.getText().toString());
                }
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
