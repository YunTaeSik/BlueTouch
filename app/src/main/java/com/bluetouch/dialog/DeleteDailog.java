package com.bluetouch.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.bluetouch.R;

/**
 * Created by Owner on 2016-04-16.
 */
public class DeleteDailog extends Activity implements View.OnClickListener {  //삭제 다이얼로그
    private Button dialog_delete_ok;
    private Button dialog_delete_cancle;
    private int DELETE_OK = 10;
    private int DELETE_CANCLE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_delete_dialog);

        dialog_delete_ok = (Button) findViewById(R.id.dialog_delete_ok);
        dialog_delete_cancle = (Button) findViewById(R.id.dialog_delete_cancle);
        dialog_delete_ok.setOnClickListener(this);
        dialog_delete_cancle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_delete_ok:
                setResult(DELETE_OK);
                finish();
                break;
            case R.id.dialog_delete_cancle:
                setResult(DELETE_CANCLE);
                finish();
                break;

        }

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) { //다이얼로그 외부 터치시 꺼지는현상 방지
        return false;
    }

}
