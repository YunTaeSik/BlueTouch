package com.bluetouch.join;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bluetouch.R;
import com.bluetouch.util.SharedPrefsUtils;

/**
 * Created by User on 2016-02-26.
 */
public class JoinActivity extends Activity {  //회원가입 액티비티
    Button btncancle, btnregister;
    EditText edtid, edtpassword;
    private Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        edtid = (EditText) findViewById(R.id.edtid);
        edtpassword = (EditText) findViewById(R.id.edtpassward);


        btncancle = (Button) findViewById(R.id.cancel);
        btncancle.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                finish();                                                                                  //현재 액티비티를 종료
                Intent intentlogin = new Intent(getApplicationContext(), LoginActivity.class);            //세팅 화면으로 이동
                startActivity(intentlogin);                                                                //엑티비티 없애고 로그인 액티비티 이동
            }
        });

        btnregister = (Button) findViewById(R.id.register);
        btnregister.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                String strid = edtid.getText().toString();
                String strpassword = edtpassword.getText().toString();

                if (strid.length() < 6 || strpassword.length() < 6) {      //등록시 아이디나 패스워드가 지정된 값보다 작을경우.
                    Toast.makeText(JoinActivity.this, "아이디는 6자 이상 패스워드는 6자 이상 입력하십시오.", Toast.LENGTH_SHORT).show();

                } else {                                                   //등록시 아이디나 패스워드가 지정된값을 만족할 경우
                    String IdentifyId = SharedPrefsUtils.getStringPreference(context, strid);   //저장한 아이디가 있는지를 확인
                    Log.e("TAG", ""+IdentifyId);//무슨 값이 들어갔는지를 확인

                    if (IdentifyId != null) {                         //아이디를 불러와서 동일한 아이디가 있을경우 저장 안됨
                        Toast.makeText(JoinActivity.this, "이미 등록된 아이디입니다.", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "ID already existed"+IdentifyId);

                    } else {

                        SharedPrefsUtils.setStringPreference(context,strid ,strid);//sharedpref 입력
                        SharedPrefsUtils.setStringPreference(context,strid+"P",strpassword);//sharedpref 입력
                        Toast.makeText(JoinActivity.this, "아이디 생성 완료", Toast.LENGTH_SHORT).show();
                        Intent intentlogin = new Intent(getApplicationContext(), LoginActivity.class);            //세팅 화면으로 이동
                        intentlogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intentlogin);                                                                //엑티비티 없애고 로그인 액티비티 이동
                        Log.e("TAG", "ID don't existed" + IdentifyId);

                    }
                }
                Log.e("TAG", "ddddd");
                Log.e("TAG", strid);
                Log.e("TAG", strpassword);
            }
        });

    }
}
