package com.bluetouch.blueMain;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bluetouch.R;
import com.bluetouch.adapter.BlueMainAdapter;
import com.bluetouch.dialog.DeleteDailog;
import com.bluetouch.fragment.BlueMainOneFragment;
import com.bluetouch.fragment.BlueMianTwoFragment;
import com.bluetouch.function.BackPressCloseHandler;
import com.bluetouch.join.LoginActivity;
import com.bluetouch.util.DeleteTouch;
import com.bluetouch.util.SharedPrefsUtils;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


/**
 * Created by User on 2016-03-02.
 */
public class BlueMainActivity extends AppCompatActivity implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener, View.OnClickListener, OnMenuItemClickListener, OnMenuItemLongClickListener {

    private TabHost mTabHost;
    private ViewPager mViewPager;
    private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabInfo>();
    private BlueMainAdapter mPagerAdapter;
    private ImageView back_btn;
    private String strid;
    private int position;
    private Button delete_btn;
    private Button setting_btn;
    private BackPressCloseHandler backPressCloseHandler;
    private String address;

    private ContextMenuDialogFragment mMenuDialogFragment;
    private FragmentManager fragmentManager;


    private class TabInfo {
        private String tag;
        private Class<?> clss;
        private Bundle args;
        private Fragment fragment;

        TabInfo(String tag, Class<?> clazz, Bundle args) {
            this.tag = tag;
            this.clss = clazz;
            this.args = args;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_main);

        Intent intent = getIntent();
        strid = intent.getExtras().getString("strid"); //strid 가져오기
        position = intent.getExtras().getInt("position");//position 가져오기
        address = SharedPrefsUtils.getStringPreference(getApplicationContext(), "A" + (position + 1)); //해당 포지션 ble address 가져오기
        initialiseTabHost(savedInstanceState);
        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
        intialiseViewPager();  //뷰페이지 정의 함수

        back_btn = (ImageView) findViewById(R.id.back_btn);
        delete_btn = (Button) findViewById(R.id.delete_btn);
        setting_btn = (Button) findViewById(R.id.setting_btn);

        back_btn.setOnClickListener(this);
        delete_btn.setOnClickListener(this);
        setting_btn.setOnClickListener(this);

        backPressCloseHandler = new BackPressCloseHandler(this);  

        fragmentManager = getSupportFragmentManager();
        initToolbar();  //툴바 정의 함수
        initMenuFragment(); //툴바 메뉴 정의 함수


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
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    @Override
    public void onTabChanged(String tabId) {
        TabInfo newTab = this.mapTabInfo.get(tabId);
        int pos = this.mTabHost.getCurrentTab();
        mViewPager.setCurrentItem(pos);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        this.mTabHost.setCurrentTab(position);
        this.mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void intialiseViewPager() {  // 뷰페이지 정의함수 

        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this,
                BlueMainOneFragment.class.getName()));
        fragments.add(Fragment.instantiate(this,
                BlueMianTwoFragment.class.getName()));


        this.mPagerAdapter = new BlueMainAdapter(
                getSupportFragmentManager(), fragments, strid, position);
        this.mViewPager = (ViewPager) findViewById(R.id.blueviewpager);
        mViewPager.setAdapter(mPagerAdapter);
        this.mViewPager.setOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(3);
    }

    private void initialiseTabHost(Bundle args) {  //탭 정의 함수

        mTabHost = (TabHost) findViewById(android.R.id.tabhost);


        setupTab(new TextView(this), "App 기능");    //첫번쨰 탭 네임
        setupTab(new TextView(this), "I.O.T 제품");   //두번째 탭 네임

        mTabHost.setOnTabChangedListener(this);
    }

    private void setupTab(final View view, final String tag) {

        View tabview = createTabView(mTabHost.getContext(), tag);
        TabHost.TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview)
                .setContent(new TabHost.TabContentFactory() {
                    public View createTabContent(String tag) {
                        return view;
                    }
                });
        mTabHost.setup();
        mTabHost.addTab(setContent);
    }

    private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.tabwidget_tabs, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text);
        return view;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", mTabHost.getCurrentTabTag());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) { 
            case R.id.back_btn:   //백버튼 클릭시  touchChoice 이동
                Intent back_intent = new Intent(this, touchChoice.class);
                back_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                back_intent.putExtra("strid", strid);
                startActivity(back_intent);
                break;
            case R.id.delete_btn:   //삭제 버튼 클릭시  DeleteDailog 이동
                if (SharedPrefsUtils.getStringPreference(getApplicationContext(), address + "red") == null &&   SharedPrefsUtils.getStringPreference(getApplicationContext(), address + "blue") == null && SharedPrefsUtils.getStringPreference(getApplicationContext(), address + "orange") == null) {
                    Intent intent = new Intent(this, DeleteDailog.class);
                    startActivityForResult(intent, 0);
                } else {
                    Toast.makeText(getApplicationContext(), "모든 아이템을 삭제후 블루터치 삭제가 가능합니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.setting_btn:  //세팅 버튼 클릭시 SettingActivity이동
                Intent settingIntent = new Intent(this, SettingActivity.class);
                settingIntent.putExtra("strid", strid);
                startActivity(settingIntent);
                break;

        }
    }

    private void delete_Tag() { //블루터치에 기능 태그 삭제 함수

        SharedPrefsUtils.setStringPreference(getApplicationContext(), address + "red", null);  //첫번쨰 기능 삭제
        SharedPrefsUtils.setStringPreference(getApplicationContext(), address + "blue", null);  //두번째 기능 삭제
        SharedPrefsUtils.setStringPreference(getApplicationContext(), address + "orange", null);  //세번쨰 기능 삭제

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  //startActivityForResult 실행시킨 액티비티의 데이터 받는 함수
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case 10:  //데이터가 10이면 해당 블루터치 삭제 
                int count = SharedPrefsUtils.getIntegerPreference(getApplicationContext(), strid + "C", 0);
                SharedPrefsUtils.setIntegerPreference(getApplicationContext(), strid + "C", count - 1);
                SharedPrefsUtils.setIntegerPreference(getApplicationContext(), "AC", count - 1);
                DeleteTouch touch = new DeleteTouch();
                touch.deleteTouch(getApplicationContext(), position, strid);  //블루터치 삭제

                Intent intenttouch_choice_delete = new Intent(this, touchChoice.class);
                intenttouch_choice_delete.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intenttouch_choice_delete.putExtra("strid", strid);
                finish();
                startActivity(intenttouch_choice_delete);

                delete_Tag();

                if (position == 0) {  //포지션에 따라 해당 인텐트 필터에 정보 전달 
                    Intent Delete_intent = new Intent("delete_one");  
                    getApplicationContext().sendBroadcast(Delete_intent);
                } else if (position == 1) {
                    Intent Delete_intent = new Intent("delete_two");
                    getApplicationContext().sendBroadcast(Delete_intent);
                } else if (position == 2) {
                    Intent Delete_intent = new Intent("delete_three");
                    getApplicationContext().sendBroadcast(Delete_intent);
                }
                Toast.makeText(getApplicationContext(), "블루터치가 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                break;
            case 11:
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

    private void initMenuFragment() {  //툴바 메뉴 함수
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {  //해당 메뉴 클릭 함수
        if (position == 1) {
            Intent intenttouch_choice = new Intent(this, touchChoice.class);            //touchChoice 화면으로 이동
            intenttouch_choice.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intenttouch_choice.putExtra("strid", strid);
            finish();
            startActivity(intenttouch_choice);
        } else if (position == 2) {
        } else if (position == 3) {
            Intent intentsetting = new Intent(getApplicationContext(), SettingActivity.class);            //SettingActivity 화면으로 이동
            intentsetting.putExtra("strid", strid);
            startActivity(intentsetting);
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

    private List<MenuObject> getMenuObjects() { //메뉴 리스트 정의
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
    public void onMenuItemLongClick(View clickedView, int position) {

    }
}
