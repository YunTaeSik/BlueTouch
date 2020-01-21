package com.bluetouch.blueMain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluetouch.R;
import com.bluetouch.join.LoginActivity;
import com.bluetouch.util.Contact;
import com.bluetouch.util.SharedPrefsUtils;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2016-05-22.
 */
public class IotActivity extends AppCompatActivity implements OnMenuItemClickListener {
    private TextView light_condition;
    private ImageView light_image;
    private TextView consent_condition;
    private ImageView consent_image;
    private TextView valve_condition;
    private ImageView valve_image;
    private TextView doorlock_condition;
    private ImageView doorlock_image;

    private int lightdata;
    private int consentdata;
    private int valvedata;
    private int doorlockdata;
    private String strid;

    private static String ON = "ON";
    private static String OFF = "OFF";
    private static String DISCONNECT = "disconnect";

    private ContextMenuDialogFragment mMenuDialogFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("LIGHT_DATA");
        intentFilter.addAction(Contact.CONSENT_DATA);
        intentFilter.addAction(Contact.VALVE_DATA);
        intentFilter.addAction(Contact.DOORLOCK_DATA);
        registerReceiver(mRecevier, intentFilter);

        light_condition = (TextView) findViewById(R.id.light_condition);
        light_image = (ImageView) findViewById(R.id.light_image);
        consent_condition = (TextView) findViewById(R.id.consent_condition);
        consent_image = (ImageView) findViewById(R.id.consent_image);
        valve_condition = (TextView) findViewById(R.id.valve_condition);
        valve_image = (ImageView) findViewById(R.id.valve_image);
        doorlock_condition = (TextView) findViewById(R.id.doorlock_condition);
        doorlock_image = (ImageView) findViewById(R.id.doorlock_image);


        setView();
        fragmentManager = getSupportFragmentManager();
        initToolbar();
        initMenuFragment();
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRecevier); //브로트캐스트 리시버 삭제
    }

    private void setView() {
        Intent intent = getIntent();
        strid = intent.getStringExtra("strid");

        if (SharedPrefsUtils.getStringPreference(getApplicationContext(), "light_address") == null) {
            setCondition(light_condition, DISCONNECT);
        }
        if (SharedPrefsUtils.getStringPreference(getApplicationContext(), "consent_address") == null) {
            setCondition(consent_condition, DISCONNECT);
        }
        if (SharedPrefsUtils.getStringPreference(getApplicationContext(), "valve_address") == null) {
            setCondition(valve_condition, DISCONNECT);
        }
        if (SharedPrefsUtils.getStringPreference(getApplicationContext(), "doorlock_address") == null) {
            setCondition(doorlock_condition, DISCONNECT);
        }
    }

    private void setCondition(TextView textView, String condition) { //해당 view 그리는 함수
        if (condition.equals(ON)) { // 해당 기능이 연결이 되어있고 비콘 모드가 ON 이면
            textView.setBackground(getResources().getDrawable(R.drawable.iot_btn_background));
            textView.setTextColor(Color.parseColor("#161d75"));
            textView.setText(ON);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        } else if (condition.equals(OFF)) {  // 해당 기능이 연결이 되어있고 비콘 모드가 OFF 이면
            textView.setBackground(getResources().getDrawable(R.drawable.iot_btn_background_off));
            textView.setTextColor(Color.parseColor("#ffffff"));
            textView.setText(OFF);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        } else if (condition.equals(DISCONNECT)) {      // 해당 기능 연동이 안되어있을때
            textView.setBackground(getResources().getDrawable(R.drawable.iot_btn_background_null));
            textView.setTextColor(Color.parseColor("#888888"));
            textView.setText("연동 안됨");
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        }

    }

    private BroadcastReceiver mRecevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {   //브로드캐스트 리시버 정의
            if (intent.getAction().equals("LIGHT_DATA")) {
                int lightdata = intent.getIntExtra("data_light", 0);
                if (SharedPrefsUtils.getStringPreference(getApplicationContext(), "light_address") == null) {
                    setCondition(light_condition, DISCONNECT);
                } else {
                    if (lightdata == 4) {
                        setCondition(light_condition, ON);
                        light_image.setBackground(getResources().getDrawable(R.drawable.bulb));
                    } else {
                        setCondition(light_condition, OFF);
                        light_image.setBackground(getResources().getDrawable(R.drawable.bulb_off));
                    }

                }
            }
            if (intent.getAction().equals(Contact.CONSENT_DATA)) {
                int consentdata = intent.getIntExtra(Contact.data_consent, 0);
                if (SharedPrefsUtils.getStringPreference(getApplicationContext(), "consent_address") == null) {
                    setCondition(consent_condition, DISCONNECT);
                } else {
                    if (consentdata == 4) {
                        setCondition(consent_condition, OFF);
                        consent_image.setBackground(getResources().getDrawable(R.drawable.consent_off));
                    } else {
                        setCondition(consent_condition, ON);
                        consent_image.setBackground(getResources().getDrawable(R.drawable.consent_on));
                    }
                }
            }
            if (intent.getAction().equals(Contact.VALVE_DATA)) {
                int valvedata = intent.getIntExtra(Contact.data_valve, 0);
                if (SharedPrefsUtils.getStringPreference(getApplicationContext(), "valve_address") == null) {
                    setCondition(valve_condition, DISCONNECT);
                } else {
                    if (valvedata == 4) {
                        setCondition(valve_condition, ON);
                        valve_image.setVisibility(View.GONE);
                    } else {
                        setCondition(valve_condition, OFF);
                        valve_image.setVisibility(View.VISIBLE);
                    }
                }
            }
            if (intent.getAction().equals(Contact.DOORLOCK_DATA)) {
                int doorlockdata = intent.getIntExtra(Contact.data_doorlock, 0);
                if (SharedPrefsUtils.getStringPreference(getApplicationContext(), "doorlock_address") == null) {
                    setCondition(doorlock_condition, DISCONNECT);
                } else {
                    if (doorlockdata == 1) {
                        setCondition(doorlock_condition, ON);
                        doorlock_image.setBackground(getResources().getDrawable(R.drawable.doorlock_open));
                    } else {
                        setCondition(doorlock_condition, OFF);
                        doorlock_image.setBackground(getResources().getDrawable(R.drawable.doorlock_close));
                    }

                }
            }
        }
    };

    private void initToolbar() {   //툴바 정의 함수
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(null);
        mToolBarTextView.setText("Blue Touch");
    }

    private void initMenuFragment() {  //툴바 메뉴 정의 함수
        MenuParams menuParams = new MenuParams(); 
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
    }

    private List<MenuObject> getMenuObjects() {  //툴바 메뉴 리스트 정의
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

    @Override
    public void onMenuItemClick(View clickedView, int position) {   //메뉴 클릭 함수 
        if (position == 1) {
            Intent intenttouch_choice = new Intent(this, touchChoice.class);            //touchChoice 화면으로 이동
            intenttouch_choice.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intenttouch_choice.putExtra("strid", strid);
            finish();
            startActivity(intenttouch_choice);
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
        } else if (position == 5) {
            SharedPrefsUtils.setStringPreference(getApplicationContext(), "autoLogin", "off");
            Intent intentlogout = new Intent(getApplicationContext(), LoginActivity.class);            //LoginActivity 화면으로 이동
            intentlogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intentlogout);
        }
    }
}
