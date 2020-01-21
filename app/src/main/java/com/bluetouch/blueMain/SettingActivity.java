package com.bluetouch.blueMain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluetouch.R;
import com.bluetouch.join.LoginActivity;
import com.bluetouch.setting.CallSettingActivity;
import com.bluetouch.setting.MessageSettingActivity;
import com.bluetouch.setting.NavigationActivity;
import com.bluetouch.util.SharedPrefsUtils;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2016-04-07.
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener, OnMenuItemClickListener, OnMenuItemLongClickListener {
    private String strid;
    private RelativeLayout call_setting;
    private RelativeLayout message_setting;
    private RelativeLayout navigation_setting;


    private ContextMenuDialogFragment mMenuDialogFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Intent intent = getIntent();
        strid = intent.getExtras().getString("strid");


        call_setting = (RelativeLayout) findViewById(R.id.call_setting);
        message_setting = (RelativeLayout) findViewById(R.id.message_setting);
        navigation_setting = (RelativeLayout) findViewById(R.id.navigation_setting);

        call_setting.setOnClickListener(this);
        message_setting.setOnClickListener(this);
        navigation_setting.setOnClickListener(this);

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
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.call_setting:
                Intent callsettingIntent = new Intent(getApplicationContext(), CallSettingActivity.class);  //CallSettingActivity로 이동
                startActivity(callsettingIntent);
                break;
            case R.id.message_setting:
                Intent messagesettingIntent = new Intent(getApplicationContext(), MessageSettingActivity.class);  //MessageSettingActivity 이동
                startActivity(messagesettingIntent);
                break;
            case R.id.navigation_setting:
                Intent navigationttingIntent = new Intent(getApplicationContext(), NavigationActivity.class);  //NavigationActivity 이동
                startActivity(navigationttingIntent);
                break;


        }

    }

    private void initToolbar() {  //툴바 정의 함수
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(null);
        mToolBarTextView.setText("Blue Touch");
    }

    private void initMenuFragment() {   //툴바 메뉴 정의 함수
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
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
    public void onMenuItemClick(View clickedView, int position) {
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

        } else if (position == 4) {
            Intent iotIntent = new Intent(getApplicationContext(), IotActivity.class);            //IotActivity 화면으로 이동
            iotIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            iotIntent.putExtra("strid", strid);
            startActivity(iotIntent);
        } else if (position == 5) {
            SharedPrefsUtils.setStringPreference(getApplicationContext(), "autoLogin", "off");
            Intent intentlogout = new Intent(getApplicationContext(), LoginActivity.class);            //LoginActivity 화면으로 이동
            intentlogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intentlogout);
        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {

    }
}
