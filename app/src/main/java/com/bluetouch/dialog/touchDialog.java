package com.bluetouch.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bluetouch.R;
import com.bluetouch.adapter.GridViewAdapter;
import com.bluetouch.blueMain.touchChoice;

/**
 * Created by User on 2016-03-09.
 */
public class touchDialog extends Activity implements View.OnClickListener {  //블루터치 네임 설정하는 다이얼로그 액티비티
    private EditText dialog_edit_text;
    private Button dialog_btn_cancle;
    private Button dialog_btn_ok;
    public static String touch_name_text;
    private int OK = 5;
    private int CANCLE = 6;
    private GridViewAdapter gridViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_dialog);
        dialog_edit_text = (EditText) findViewById(R.id.dialog_edit_text);
        dialog_btn_cancle = (Button) findViewById(R.id.dialog_btn_cancle);
        dialog_btn_ok = (Button) findViewById(R.id.dialog_btn_ok);

        dialog_btn_cancle.setOnClickListener(this);
        dialog_btn_ok.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_btn_ok:
                    touch_name_text = dialog_edit_text.getText().toString();
                    if (dialog_edit_text.getText().length() > 0) {
                        Intent i = new Intent(touchDialog.this, touchChoice.class);
                        i.putExtra("touch_name", touch_name_text);
                        setResult(OK, i);
                        finish();
                    } else { //블루터치의 이름이 없을때
                        Toast.makeText(getApplicationContext(), "블루터치의 이름을 설정해주세요", Toast.LENGTH_SHORT).show();
                    }
                break;
            case R.id.dialog_btn_cancle:
                setResult(CANCLE);
                finish();
                break;
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) { //다이얼로그 외부 터치시 꺼지는현상 방지
        return false;
    }
}
