package com.bluetouch.fragment;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluetouch.BlueTooth.DeviceScanActivity;
import com.bluetouch.R;
import com.bluetouch.util.Contact;
import com.bluetouch.util.SharedPrefsUtils;

/**
 * Created by User on 2016-04-12.
 */
public class BlueMianTwoFragment extends Fragment implements View.OnClickListener {  //블루터치 기능선택 화면 첫번쨰 Fragment
    private ViewGroup issuelayout;
    private TextView light;
    private TextView consent;
    private TextView doorlock;
    private TextView valve;
    public static final String LIGHT = "조명";
    public static final String CONSENT = "콘센트";
    public static final String VALVE = "벨브";
    public static final String DOORLOCK = "도어락";


    private RelativeLayout toplinear;
    private LinearLayout deletelinear;
    private TextView redlinear;
    private TextView blueinear;
    private TextView ornagelinear;
    private TextView redlinear_text;
    private TextView blueinear_text;
    private TextView ornagelinear_text;
    private String strid;
    private int realposition;
    private String address;
    private ImageView logo_image;
    private Animation alphaAni;
    private TextView Item_delete_one;
    private TextView Item_delete_two;
    private TextView Item_delete_three;


    // long click을 위한 변수들
    private boolean mHasPerformedLongPress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strid = getArguments().getString("strid"); //로그인 아이디 얻어오기
        realposition = getArguments().getInt("realposition");
        address = SharedPrefsUtils.getStringPreference(getContext(), "A" + (realposition + 1));
        Log.e("position", String.valueOf(address));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ITEM_DELETE");   //인텐트 필터 등록
        getContext().registerReceiver(mRecevier, intentFilter);  //브로드캐스트 리시버 등록

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  //해당뷰 선언 및 정의 와 클릭이벤트 설정
        issuelayout = (ViewGroup) inflater.inflate(R.layout.activity_bluemain_fragment_two, container, false);
        light = (TextView) issuelayout.findViewById(R.id.light);
        light.setTag(LIGHT);
        consent = (TextView) issuelayout.findViewById(R.id.consent);
        consent.setTag(CONSENT);
        doorlock = (TextView) issuelayout.findViewById(R.id.doorlock);
        doorlock.setTag(DOORLOCK);
        valve = (TextView) issuelayout.findViewById(R.id.valve);
        valve.setTag(VALVE);

        toplinear = (RelativeLayout) issuelayout.findViewById(R.id.toplinear);
        deletelinear = (LinearLayout) issuelayout.findViewById(R.id.deletelinear);
        redlinear = (TextView) issuelayout.findViewById(R.id.redlinear);
        redlinear_text = (TextView) issuelayout.findViewById(R.id.redlinear_text);
        blueinear = (TextView) issuelayout.findViewById(R.id.blueinear);
        blueinear_text = (TextView) issuelayout.findViewById(R.id.blueinear_text);
        ornagelinear = (TextView) issuelayout.findViewById(R.id.ornagelinear);
        ornagelinear_text = (TextView) issuelayout.findViewById(R.id.ornagelinear_text);

        Item_delete_one = (TextView) issuelayout.findViewById(R.id.Item_delete_one);
        Item_delete_two = (TextView) issuelayout.findViewById(R.id.Item_delete_two);
        Item_delete_three = (TextView) issuelayout.findViewById(R.id.Item_delete_three);
        Item_delete_one.setOnClickListener(this);
        Item_delete_two.setOnClickListener(this);
        Item_delete_three.setOnClickListener(this);

        logo_image = (ImageView) issuelayout.findViewById(R.id.logo_image);
        alphaAni = AnimationUtils.loadAnimation(getContext(), R.anim.alpha);
        logo_image.startAnimation(alphaAni);

        light.setOnLongClickListener(new LongClickListener());
        consent.setOnLongClickListener(new LongClickListener());
        doorlock.setOnLongClickListener(new LongClickListener());
        valve.setOnLongClickListener(new LongClickListener());

        issuelayout.findViewById(R.id.toplinear).setOnDragListener(
                new DragListener());
        issuelayout.findViewById(R.id.bottomlinear).setOnDragListener(
                new DragListener());
        issuelayout.findViewById(R.id.deletelinear).setOnDragListener(
                new DragListener());
        issuelayout.findViewById(R.id.redlinear).setOnDragListener(
                new DragListener());
        issuelayout.findViewById(R.id.blueinear).setOnDragListener(
                new DragListener());
        issuelayout.findViewById(R.id.ornagelinear).setOnDragListener(
                new DragListener());

        getBackGround();
        ItemDeleteView();

        return issuelayout;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mRecevier);   //브로드캐스트리시버 종료
    }

    public static Fragment newInstacne(int position, String strid, int realposition) {   //인텐트에서 정보 받아오기
        BlueMianTwoFragment fragment = new BlueMianTwoFragment();
        Bundle args = new Bundle();
        args.putString("strid", strid);
        args.putInt("realposition", realposition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Item_delete_one:  //첫번째 기능 삭제시
                redlinear.setBackground(null);
                redlinear_text.setText("");
                String getTag_red = SharedPrefsUtils.getStringPreference(getContext(), address + "red");
                if (getTag_red.equals(BlueMianTwoFragment.LIGHT)) {
                    BeaconModeOFF(BlueMianTwoFragment.LIGHT);
                    SharedPrefsUtils.setStringPreference(getContext(), "light_address", null);
                    Intent Delete_intent = new Intent("delete_light");
                    getContext().sendBroadcast(Delete_intent);
                } else if (getTag_red.equals(BlueMianTwoFragment.CONSENT)) {
                    BeaconModeOFF(BlueMianTwoFragment.CONSENT);
                    SharedPrefsUtils.setStringPreference(getContext(), "consent_address", null);
                    Intent Delete_intent = new Intent("delete_consent");
                    getContext().sendBroadcast(Delete_intent);
                } else if (getTag_red.equals(BlueMianTwoFragment.VALVE)) {
                    BeaconModeOFF(BlueMianTwoFragment.VALVE);
                    SharedPrefsUtils.setStringPreference(getContext(), "valve_address", null);
                    Intent Delete_intent = new Intent("delete_valve");
                    getContext().sendBroadcast(Delete_intent);
                } else if (getTag_red.equals(BlueMianTwoFragment.DOORLOCK)) {
                    BeaconModeOFF(BlueMianTwoFragment.DOORLOCK);
                    SharedPrefsUtils.setStringPreference(getContext(), "doorlock_address", null);
                    Intent Delete_intent = new Intent("delete_doorlock");
                    getContext().sendBroadcast(Delete_intent);
                }
                SharedPrefsUtils.setStringPreference(getContext(), address + "red", null);
                Intent Item_Delete_red = new Intent("ITEM_DELETE");
                Item_Delete_red.putExtra("ITEM_DELETE", "red");
                getContext().sendBroadcast(Item_Delete_red);
                break;
            case R.id.Item_delete_two:      //두번째 기능 삭제시
                blueinear.setBackground(null);
                blueinear_text.setText("");
                String getTag_blue = SharedPrefsUtils.getStringPreference(getContext(), address + "blue");
                if (getTag_blue.equals(BlueMianTwoFragment.LIGHT)) {
                    BeaconModeOFF(BlueMianTwoFragment.LIGHT);
                    SharedPrefsUtils.setStringPreference(getContext(), "light_address", null);
                    Intent Delete_intent = new Intent("delete_light");
                    getContext().sendBroadcast(Delete_intent);
                } else if (getTag_blue.equals(BlueMianTwoFragment.CONSENT)) {
                    BeaconModeOFF(BlueMianTwoFragment.CONSENT);
                    SharedPrefsUtils.setStringPreference(getContext(), "consent_address", null);
                    Intent Delete_intent = new Intent("delete_consent");
                    getContext().sendBroadcast(Delete_intent);
                } else if (getTag_blue.equals(BlueMianTwoFragment.VALVE)) {
                    BeaconModeOFF(BlueMianTwoFragment.VALVE);
                    SharedPrefsUtils.setStringPreference(getContext(), "valve_address", null);
                    Intent Delete_intent = new Intent("delete_valve");
                    getContext().sendBroadcast(Delete_intent);
                } else if (getTag_blue.equals(BlueMianTwoFragment.DOORLOCK)) {
                    BeaconModeOFF(BlueMianTwoFragment.DOORLOCK);
                    SharedPrefsUtils.setStringPreference(getContext(), "doorlock_address", null);
                    Intent Delete_intent = new Intent("delete_doorlock");
                    getContext().sendBroadcast(Delete_intent);
                }
                SharedPrefsUtils.setStringPreference(getContext(), address + "blue", null);
                Intent Item_Delete_blue = new Intent("ITEM_DELETE");
                Item_Delete_blue.putExtra("ITEM_DELETE", "blue");
                getContext().sendBroadcast(Item_Delete_blue);
                break;
            case R.id.Item_delete_three:            //세번째 기능 삭제시
                ornagelinear.setBackground(null);
                ornagelinear_text.setText("");
                String getTag_orange = SharedPrefsUtils.getStringPreference(getContext(), address + "orange");
                if (getTag_orange.equals(BlueMianTwoFragment.LIGHT)) {
                    BeaconModeOFF(BlueMianTwoFragment.LIGHT);
                    SharedPrefsUtils.setStringPreference(getContext(), "light_address", null);
                    Intent Delete_intent = new Intent("delete_light");
                    getContext().sendBroadcast(Delete_intent);
                } else if (getTag_orange.equals(BlueMianTwoFragment.CONSENT)) {
                    BeaconModeOFF(BlueMianTwoFragment.CONSENT);
                    SharedPrefsUtils.setStringPreference(getContext(), "consent_address", null);
                    Intent Delete_intent = new Intent("delete_consent");
                    getContext().sendBroadcast(Delete_intent);
                } else if (getTag_orange.equals(BlueMianTwoFragment.VALVE)) {
                    BeaconModeOFF(BlueMianTwoFragment.VALVE);
                    SharedPrefsUtils.setStringPreference(getContext(), "valve_address", null);
                    Intent Delete_intent = new Intent("delete_valve");
                    getContext().sendBroadcast(Delete_intent);
                } else if (getTag_orange.equals(BlueMianTwoFragment.DOORLOCK)) {
                    BeaconModeOFF(BlueMianTwoFragment.DOORLOCK);
                    SharedPrefsUtils.setStringPreference(getContext(), "doorlock_address", null);
                    Intent Delete_intent = new Intent("delete_doorlock");
                    getContext().sendBroadcast(Delete_intent);
                }
                SharedPrefsUtils.setStringPreference(getContext(), address + "orange", null);
                Intent Item_Delete_orange = new Intent("ITEM_DELETE");
                Item_Delete_orange.putExtra("ITEM_DELETE", "orange");
                getContext().sendBroadcast(Item_Delete_orange);
                break;
        }

    }

    private final class LongClickListener implements                        //롱 클릭 리스너 클래스
            View.OnLongClickListener {

        public boolean onLongClick(View view) {
            // 태그 생성
            ClipData.Item item = new ClipData.Item(
                    (CharSequence) view.getTag());

            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData data = new ClipData(view.getTag().toString(),
                    mimeTypes, item);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                    view);

            view.startDrag(data, // data to be dragged
                    shadowBuilder, // drag shadow
                    view, // 드래그 드랍할  Viiew
                    0 // 필요없은 플래그
            );
            view.setVisibility(View.INVISIBLE);
            return true;
        }
    }

    class DragListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {

            // 이벤트 시작
            switch (event.getAction()) {

                // 이미지를 드래그 시작될때
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.d("DragClickListener", "ACTION_DRAG_STARTED");
                    break;

                // 드래그한 이미지를 옮길려는 지역으로 들어왔을때
                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.d("DragClickListener", "ACTION_DRAG_ENTERED");
                    // 이미지가 들어왔다는 것을 알려주기 위해 배경이미지 변경
                    break;

                // 드래그한 이미지가 영역을 빠져 나갈때
                case DragEvent.ACTION_DRAG_EXITED:
                    Log.d("DragClickListener", "ACTION_DRAG_EXITED");
                    break;

                // 이미지를 드래그해서 드랍시켰을때
                case DragEvent.ACTION_DROP:
                    Log.d("DragClickListener", "ACTION_DROP");
                    if (v == redlinear) {
                        View view = (View) event.getLocalState();
                        view.setVisibility(View.VISIBLE);
                        String getTag = String.valueOf(view.getTag());
                        SharedPrefsUtils.setStringPreference(getContext(), address + "red", getTag);
                        setBackGround(redlinear, getTag, redlinear_text);
                        Intent Item_Delete_red = new Intent("ITEM_DELETE");
                        Item_Delete_red.putExtra("ITEM_DELETE", "red");
                        getContext().sendBroadcast(Item_Delete_red);
                        if (getTag.equals(LIGHT) | getTag.equals(CONSENT) | getTag.equals(VALVE) | getTag.equals(DOORLOCK)) {
                            final Intent intent = new Intent(getContext(), DeviceScanActivity.class);
                            intent.putExtra("Title", getTag);
                            intent.putExtra("Setting_flag", getTag);
                            startActivity(intent);
                        }
                    } else if (v == blueinear) {      //블루 리니어 레이아웃의 경우
                        View view = (View) event.getLocalState();
                        view.setVisibility(View.VISIBLE);
                        String getTag = String.valueOf(view.getTag());
                        SharedPrefsUtils.setStringPreference(getContext(), address + "blue", getTag);
                        setBackGround(blueinear, getTag, blueinear_text);
                        Intent Item_Delete_blue = new Intent("ITEM_DELETE");
                        Item_Delete_blue.putExtra("ITEM_DELETE", "blue");
                        getContext().sendBroadcast(Item_Delete_blue);
                        if (getTag.equals(LIGHT) | getTag.equals(CONSENT) | getTag.equals(VALVE) | getTag.equals(DOORLOCK)) {
                            final Intent intent = new Intent(getContext(), DeviceScanActivity.class);
                            intent.putExtra("Title", getTag);
                            intent.putExtra("Setting_flag", getTag);
                            startActivity(intent);
                        }

                    } else if (v == ornagelinear) {      //오렌지 리니어 레이아웃의 경우
                        View view = (View) event.getLocalState();
                        view.setVisibility(View.VISIBLE);
                        String getTag = String.valueOf(view.getTag());
                        SharedPrefsUtils.setStringPreference(getContext(), address + "orange", getTag);
                        setBackGround(ornagelinear, getTag, ornagelinear_text);
                        Intent Item_Delete_orange = new Intent("ITEM_DELETE");
                        Item_Delete_orange.putExtra("ITEM_DELETE", "orange");
                        getContext().sendBroadcast(Item_Delete_orange);
                        if (getTag.equals(LIGHT) | getTag.equals(CONSENT) | getTag.equals(VALVE) | getTag.equals(DOORLOCK)) {
                            final Intent intent = new Intent(getContext(), DeviceScanActivity.class);
                            intent.putExtra("Title", getTag);
                            intent.putExtra("Setting_flag", getTag);
                            startActivity(intent);
                        }

                    } else if (v == deletelinear) {
                        View view = (View) event.getLocalState();
                        ViewGroup viewgroup = (ViewGroup) view
                                .getParent();
                        viewgroup.removeView(view);
                        LinearLayout containView = (LinearLayout) v;
                        containView.addView(view);
                        view.setVisibility(View.VISIBLE);
                    } else if (v == toplinear) {
                        View view = (View) event.getLocalState();
                        view.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "이 지역으로 이동시킬수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                    View view = (View) event.getLocalState();
                    view.setVisibility(View.VISIBLE);
                    Log.d("DragClickListener", "ACTION_DRAG_ENDED");

                default:
                    break;
            }
            return true;
        }
    }

    private void setBackGround(TextView TextView, String getTag, TextView TextView2) {      //백그라운드 설정 함수
        TextView2.setText(getTag);
        TextView.setGravity(Gravity.CENTER);
        if (getTag.equals(BlueMainOneFragment.MESSAGE)) {
            TextView.setBackground(getResources().getDrawable(R.drawable.mail));
        } else if (getTag.equals(BlueMainOneFragment.CALL)) {
            TextView.setBackground(getResources().getDrawable(R.drawable.telephone));
        } else if (getTag.equals(BlueMainOneFragment.NAVIGATION)) {
            TextView.setBackground(getResources().getDrawable(R.drawable.gps_2));
        } else if (getTag.equals(BlueMainOneFragment.CAMERA)) {
            TextView.setBackground(getResources().getDrawable(R.drawable.technology_1));
        } else if (getTag.equals(BlueMainOneFragment.FAKECALL)) {
            TextView.setBackground(getResources().getDrawable(R.drawable.fake_call_image));
        } else if (getTag.equals(BlueMainOneFragment.WEATHER)) {
            TextView.setBackground(getResources().getDrawable(R.drawable.sun));
        } else if (getTag.equals(BlueMainOneFragment.ALARM)) {
            TextView.setBackground(getResources().getDrawable(R.drawable.clock));
        } else if (getTag.equals(BlueMianTwoFragment.LIGHT)) {
            TextView.setBackground(getResources().getDrawable(R.drawable.bulb));
        } else if (getTag.equals(BlueMianTwoFragment.CONSENT)) {
            TextView.setBackground(getResources().getDrawable(R.drawable.consent_on));
        } else if (getTag.equals(BlueMianTwoFragment.DOORLOCK)) {
            TextView.setBackground(getResources().getDrawable(R.drawable.doorlock_open));
        } else if (getTag.equals(BlueMianTwoFragment.VALVE)) {
            TextView.setBackground(getResources().getDrawable(R.drawable.valve_image));
        } else {
            TextView.setBackground(getResources().getDrawable(R.drawable.ic_launcher));
        }
        TextView.setTextColor(Color.parseColor("#FFFFFF"));
    }

    private void setCreateBackGround(String color, String getTag) {   //백그라운드 설정 함수
        if (color.equals(address + "red")) {
            setBackGround(redlinear, getTag, redlinear_text);
        } else if (color.equals(address + "blue")) {
            setBackGround(blueinear, getTag, blueinear_text);
        } else if (color.equals(address + "orange")) {
            setBackGround(ornagelinear, getTag, ornagelinear_text);
        }
    }

    private void getBackGround() {  //백그라운드 가져오는 함수
        String redTag = SharedPrefsUtils.getStringPreference(getContext(), address + "red");
        String blueTag = SharedPrefsUtils.getStringPreference(getContext(), address + "blue");
        String orangeTag = SharedPrefsUtils.getStringPreference(getContext(), address + "orange");

        if (redTag != null) {
            setCreateBackGround(address + "red", redTag);
        }
        if (blueTag != null) {
            setCreateBackGround(address + "blue", blueTag);
        }
        if (orangeTag != null) {
            setCreateBackGround(address + "orange", orangeTag);
        }
    }

    private void ItemDeleteView() {   //기능 삭제 뷰 정의 함수
        if (SharedPrefsUtils.getStringPreference(getContext(), address + "red") == null) {
            Item_delete_one.setVisibility(View.GONE);
        } else {
            Item_delete_one.setVisibility(View.VISIBLE);
        }
        if (SharedPrefsUtils.getStringPreference(getContext(), address + "blue") == null) {
            Item_delete_two.setVisibility(View.GONE);
        } else {
            Item_delete_two.setVisibility(View.VISIBLE);
        }
        if (SharedPrefsUtils.getStringPreference(getContext(), address + "orange") == null) {
            Item_delete_three.setVisibility(View.GONE);
        } else {
            Item_delete_three.setVisibility(View.VISIBLE);
        }
    }

    private BroadcastReceiver mRecevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {  //브로드캐스트리시버 함수
            if (intent.getAction().equals("ITEM_DELETE")) {
                String data = intent.getStringExtra("ITEM_DELETE");
                if (data.equals("red")) {
                    if (SharedPrefsUtils.getStringPreference(getContext(), address + "red") == null) {
                        Item_delete_one.setVisibility(View.GONE);
                    } else {
                        Item_delete_one.setVisibility(View.VISIBLE);
                    }
                } else if (data.equals("blue")) {
                    if (SharedPrefsUtils.getStringPreference(getContext(), address + "blue") == null) {
                        Item_delete_two.setVisibility(View.GONE);
                    } else {
                        Item_delete_two.setVisibility(View.VISIBLE);
                    }
                } else if (data.equals("orange")) {
                    if (SharedPrefsUtils.getStringPreference(getContext(), address + "orange") == null) {
                        Item_delete_three.setVisibility(View.GONE);
                    } else {
                        Item_delete_three.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    };

    private void BeaconModeOFF(String tag) {  //비콘모드 OFF 정의 함수
        if (tag.equals(BlueMianTwoFragment.LIGHT)) {
            SharedPrefsUtils.setStringPreference(getContext(), "Light_Becon", "OFF");
            Intent Light_Data_intent = new Intent("LIGHT_DATA");
            Light_Data_intent.putExtra("Becon_flag", "OFF");
            getContext().sendBroadcast(Light_Data_intent);
        } else if (tag.equals(BlueMianTwoFragment.CONSENT)) {
            SharedPrefsUtils.setStringPreference(getContext(), Contact.Consent_Becon, Contact.OFF);

            Intent Consent_Data_intent = new Intent(Contact.CONSENT_DATA);
            Consent_Data_intent.putExtra("Consent_Becon_flag", Contact.OFF);
            getContext().sendBroadcast(Consent_Data_intent);
        } else if (tag.equals(BlueMianTwoFragment.VALVE)) {
            SharedPrefsUtils.setStringPreference(getContext(), Contact.Valve_Becon, Contact.OFF);

            Intent Consent_Data_intent = new Intent(Contact.VALVE_DATA);
            Consent_Data_intent.putExtra("Valve_Becon_flag", Contact.OFF);
            getContext().sendBroadcast(Consent_Data_intent);
        } else if (tag.equals(BlueMianTwoFragment.DOORLOCK)) {
            SharedPrefsUtils.setStringPreference(getContext(), Contact.Doorlock_Becon, Contact.OFF);

            Intent Consent_Data_intent = new Intent(Contact.DOORLOCK_DATA);
            Consent_Data_intent.putExtra("DoorLock_Becon_flag", Contact.OFF);
            getContext().sendBroadcast(Consent_Data_intent);
        }

    }
}