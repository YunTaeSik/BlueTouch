package com.bluetouch.blueMain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluetouch.BlueTooth.DeviceScanActivity;
import com.bluetouch.R;
import com.bluetouch.adapter.GridViewAdapter;
import com.bluetouch.dialog.touchDialog;
import com.bluetouch.function.BackPressCloseHandler;
import com.bluetouch.join.LoginActivity;
import com.bluetouch.util.SharedPrefsUtils;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2016-02-26.
 */
public class touchChoice extends AppCompatActivity implements View.OnClickListener, OnMenuItemClickListener, OnMenuItemLongClickListener {


    private Context context = this;
    private ImageView back_btn;
    private ImageButton tapbtn;
    private GridViewAdapter gridViewAdapter;
    private GridView grid_view;
    private Button add_btn;
    private String strid;
    private BackPressCloseHandler backPressCloseHandler;
    private String count_2;
    private Button test_btn;   //noBluno test


    private ContextMenuDialogFragment mMenuDialogFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchchoice);
        back_btn = (ImageView) findViewById(R.id.back_btn);
        tapbtn = (ImageButton) findViewById(R.id.tap);
        grid_view = (GridView) findViewById(R.id.grid_view);
        add_btn = (Button) findViewById(R.id.add_btn);
        test_btn = (Button) findViewById(R.id.test_btn);

       /* test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent
                         = new Intent(getApplicationContext() , AlarmActivity.class);
                startActivity(intent);

            }
        });*/

        back_btn.setOnClickListener(this);
        tapbtn.setOnClickListener(this);
        add_btn.setOnClickListener(this);

        backPressCloseHandler = new BackPressCloseHandler(this);  //백버튼 정의 함수

        SharedPrefsUtils.setStringPreference(context, "Activity_flag", null);

        IntentFilter intentFilter = new IntentFilter();  //인텐트 필터 등록
        intentFilter.addAction("Service_finish");
        intentFilter.addAction("Touch_Data");
        intentFilter.addAction("Service_start");
        registerReceiver(mRecevier, intentFilter);  //브로드캐스트 리시버 등록

        fragmentManager = getSupportFragmentManager();
        initToolbar();   //툴바 정의함수
        initMenuFragment();  //툴바 메뉴 정의 함수

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRecevier);  //종료시 브로드캐스트리시버 삭제
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStrID();   //strid 정보 및 어댑터 정의 함수
        count_2 = String.valueOf(gridViewAdapter.getCount() + 1);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back_btn:
                SharedPrefsUtils.setStringPreference(context, "autoLogin", "off");
                Intent backIntent = new Intent(getApplicationContext(), LoginActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                backIntent.putExtra("strid", strid);
                startActivity(backIntent);
                break;
            case R.id.add_btn:
                final Intent intent = new Intent(getApplicationContext(), DeviceScanActivity.class);
                intent.putExtra("Title", "블루터치");
                startActivity(intent);
                break;
         /*   case R.id.light:
                final Intent lightintent = new Intent(context, LightActivity.class);
                context.startActivity(lightintent);
                break;
            case R.id.consent:
                final Intent consentintent = new Intent(context, ConsentActivity.class);
                context.startActivity(consentintent);
                break;
            case R.id.valve:
                final Intent valveintent = new Intent(context, ValveActivity.class);
                context.startActivity(valveintent);

                break;
            case R.id.doorlock:
                final Intent doorintent = new Intent(context, DoorlockActivity.class);
                context.startActivity(doorintent);

                break;*/
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {   //startActivityForResult 데이터 받는 함수
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 5: //데이터 ==resultCode 가 5면 작동
                if (gridViewAdapter.count >= 3) {
                    Toast.makeText(getApplicationContext(), "블루터치를 최대 3개까지 등록하실수 있습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    String touch_name = data.getStringExtra("touch_name");
                    gridViewAdapter.count = gridViewAdapter.count + 1;
                    SharedPrefsUtils.setIntegerPreference(context, strid + "C", gridViewAdapter.count);
                    String getCount = Integer.toString(gridViewAdapter.getCount());
                    SharedPrefsUtils.setStringPreference(context, strid + "T" + getCount, touch_name);
                    String address = SharedPrefsUtils.getStringPreference(context, "fake_address");
                    SharedPrefsUtils.setStringPreference(context, "A" + getCount, address);
                    SharedPrefsUtils.setIntegerPreference(context, "AC", gridViewAdapter.count);
                    gridViewAdapter.notifyDataSetChanged();
                    onResume();

                    Intent intent = new Intent();
                    intent.setAction("Touch_choice");
                    sendBroadcast(intent);
                }
                break;
            case 6:
                break;
        }
    }

    private void getStrID() {  //strid 정보 및 어댑터 정의 함수
        Intent intent = getIntent();
        strid = intent.getExtras().getString("strid");

        gridViewAdapter = new GridViewAdapter(this, strid);
        grid_view.setAdapter(gridViewAdapter);
    }

    private BroadcastReceiver mRecevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {  //브로드캐스트 리시버 등록
            if (intent.getAction().equals("Service_finish")) {
                int data = intent.getIntExtra("finish", 0);
                if (data == 1) {
                }
            }
            if (intent.getAction().equals("Touch_Data")) {
                int data = intent.getIntExtra("data", 0);
                if (data == 1) {

                    Intent touchdialog = new Intent(context, touchDialog.class);
                    startActivityForResult(touchdialog, 0);
                }
            }
            if (intent.getAction().equals("Service_start")) {
                Toast.makeText(getApplicationContext(), "서비스 준비 완료", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private List<MenuObject> getMenuObjects() {   //툴바 메뉴 리스트 정의 함수
        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject("닫기");
        close.setResource(R.drawable.cancel);
        MenuObject touchchoice = new MenuObject("터치선택");
        touchchoice.setResource(R.drawable.blue_toch_image_2);
        MenuObject function_choice = new MenuObject("기능선택");
        function_choice.setResource(R.drawable.function_choice);
        MenuObject setting = new MenuObject("설정");
        setting.setResource(R.drawable.setting_image);
        MenuObject iot = new MenuObject("I.O.T 상태");
        iot.setResource(R.drawable.smart_home);
        MenuObject logout = new MenuObject("로그아웃");
        logout.setResource(R.drawable.logout);


        menuObjects.add(close);
        menuObjects.add(touchchoice);
        menuObjects.add(function_choice);
        menuObjects.add(setting);
        menuObjects.add(iot);
        menuObjects.add(logout);

        return menuObjects;
    }

    private void initMenuFragment() {    //툴바 메뉴 정의 함수
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        if (position == 1) {
        } else if (position == 2) {
            Intent intentfeatures = new Intent(getApplicationContext(), BlueMainActivity.class);            //BlueMainActivity 화면으로 이동
            intentfeatures.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentfeatures.putExtra("strid", strid);
            finish();
            startActivity(intentfeatures);
        } else if (position == 3) {
            Intent intentsetting = new Intent(getApplicationContext(), SettingActivity.class);            //SettingActivity 화면으로 이동
            intentsetting.putExtra("strid", strid);
            startActivity(intentsetting);
        } else if (position == 4) {
            Intent iotIntent = new Intent(getApplicationContext(), IotActivity.class);            //IotActivity 화면으로 이동
            iotIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            iotIntent.putExtra("strid", strid);
            startActivity(iotIntent);
        }
        else if (position == 5) {
            SharedPrefsUtils.setStringPreference(context, "autoLogin", "off");
            Intent intentlogout = new Intent(getApplicationContext(), LoginActivity.class);            //LoginActivity 화면으로 이동
            intentlogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intentlogout);
        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {

    }

    private void initToolbar() {  //툴바 정의함수
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(null);
        mToolBarTextView.setText("Blue Touch");
    }
}




