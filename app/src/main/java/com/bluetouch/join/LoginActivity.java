package com.bluetouch.join;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.bluetouch.R;
import com.bluetouch.blueMain.touchChoice;
import com.bluetouch.util.SharedPrefsUtils;

/**
 * Created by User on 2016-02-26.
 */
public class LoginActivity extends Activity implements View.OnClickListener {  //로그인 액티비티

    private Button btnjoin, btnlogin;
    private CheckBox autocheck;
    private EditText login_id, login_password;
    private Context context = this;
    private String strid;
    private String realstrid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnjoin = (Button) findViewById(R.id.join);
        btnlogin = (Button) findViewById(R.id.login);
        autocheck = (CheckBox) findViewById(R.id.autocheck);
        login_id = (EditText) findViewById(R.id.login_id);
        login_password = (EditText) findViewById(R.id.login_password);

        btnjoin.setText("회원 가입");
        btnlogin.setText("로그인");

        String lastId = SharedPrefsUtils.getStringPreference(context, "lastid");
        if (lastId != null) {
            login_id.setText(lastId);
        }

        autocheck.setOnClickListener(this);
        btnjoin.setOnClickListener(this);
        btnlogin.setOnClickListener(this);

        if (autocheck.isChecked()) {
            //눌렸을때
            //SharedPrefsUtils.setIntegerPreference(context, "auto_checked", 1);
            SharedPrefsUtils.setStringPreference(context, "isChecked", "on");

        } else {
            //안눌렸을때
            //SharedPrefsUtils.setIntegerPreference(context, "auto_checked", 0);
            SharedPrefsUtils.setStringPreference(context, "isChecked", "off");

        }
        autoLoginChecked(lastId);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void autoLoginChecked(String lastId) {
        if (SharedPrefsUtils.getStringPreference(context, "autoLogin") != null) {
            if (SharedPrefsUtils.getStringPreference(context, "autoLogin").equals("on")) {
                //  realstrid = SharedPrefsUtils.getStringPreference(context, strid);
                Intent intentjoin = new Intent(getApplicationContext(), touchChoice.class);
                intentjoin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentjoin.putExtra("strid", lastId);
                finish();
                startActivity(intentjoin);
            }
        }
    }

    private void LoginCheckd() {  //자동로그인 체크 정의함수
        strid = login_id.getText().toString();        //적어둔 string을 이용해서 적혀있는 아이디 불러옴
        String getStringPreference = login_password.getText().toString();// 적어둔 string을 이용해서 비번 불러옴

        if (strid == null) {
            Toast.makeText(LoginActivity.this, "존재하지 않는 아이디 입니다.", Toast.LENGTH_SHORT).show();
        } else {
            realstrid = SharedPrefsUtils.getStringPreference(context, strid);
            if (realstrid != null) {
                if (realstrid.equals(strid)) {
                    String realPassword = SharedPrefsUtils.getStringPreference(context, strid + "P");
                    if (getStringPreference.equals(realPassword)) {
                        Intent intentjoin = new Intent(getApplicationContext(), touchChoice.class);
                        intentjoin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//세팅 화면으로 이동
                        SharedPrefsUtils.setStringPreference(context, "lastid", strid);                     //접속했던 아이디 기억

                        if (SharedPrefsUtils.getStringPreference(context, "isChecked").equals("on")) {
                            SharedPrefsUtils.setStringPreference(context, "autoLogin", "on");
                        } else {
                            SharedPrefsUtils.setStringPreference(context, "autoLogin", "off");
                        }
                        finish();
                        intentjoin.putExtra("strid", realstrid);
                        startActivity(intentjoin);
                    } else {
                        Toast.makeText(LoginActivity.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(LoginActivity.this, "존재하지 않는 아이디 입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                LoginCheckd();
                break;
            case R.id.join:
                Intent intentlogin = new Intent(getApplicationContext(), JoinActivity.class);
                intentlogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentlogin);
                break;
            case R.id.autocheck:
                if (autocheck.isChecked()) {
                    //눌렸을때
                    //SharedPrefsUtils.setIntegerPreference(context, "auto_checked", 1);
                    SharedPrefsUtils.setStringPreference(context, "isChecked", "on");

                } else {
                    //안눌렸을때
                    //SharedPrefsUtils.setIntegerPreference(context, "auto_checked", 0);
                    SharedPrefsUtils.setStringPreference(context, "isChecked", "off");

                }
                break;
        }
    }

}
