package com.bluetouch.BlueTooth;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluetouch.R;
import com.bluetouch.dialog.BeaconSettingDailog;
import com.bluetouch.util.SharedPrefsUtils;

import java.util.ArrayList;

public class DeviceScanActivity extends ListActivity { //ble 스캔하는 액티비티
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private LinearLayout device_layout;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private String Title;
    private String getTag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();

        Intent intent = getIntent();
        Title = intent.getExtras().getString("Title");
        getTag = intent.getStringExtra("Setting_flag");  //tag 및 Title 정보 받기

        setTitle(Title);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        try {
            mLeDeviceListAdapter.clear();
            scanLeDevice(true);
        } catch (NullPointerException e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {  //취소되거나 끄면 종료
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false); //스캔 종료
        mLeDeviceListAdapter.clear();
    }

    private void scanLeDevice(final boolean enable) {  //스캔하는 함수
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        //invalidateOptionsMenu();
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                try {
                    if (Title.equals("블루터치")) {
                        if (device.getName().equals("Bluetouch")) {
                            mLeDevices.add(device);
                        }
                    } else if (Title.equals("조명")) {
                        if (device.getName().equals("Light")) {
                            mLeDevices.add(device);
                        }
                    } else if (Title.equals("콘센트")) {
                        if (device.getName().equals("Consent")) {
                            mLeDevices.add(device);
                        }
                    } else if (Title.equals("벨브")) {
                        if (device.getName().equals("Valve")) {
                            mLeDevices.add(device);
                        }
                    } else if (Title.equals("도어락")) {
                        if (device.getName().equals("Doorlock")) {
                            mLeDevices.add(device);
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.

            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);

                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.device_layout = (LinearLayout) view.findViewById(R.id.device_layout);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
                viewHolder.deviceAddress.setText(device.getAddress());
            }
            viewHolder.device_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Title.equals("블루터치")) {
                        SharedPrefsUtils.setStringPreference(getApplicationContext(), "fake_address", device.getAddress());

                        Intent Consent_Data_intent = new Intent("Touch_Data");
                        Consent_Data_intent.putExtra("data", 1);
                        sendBroadcast(Consent_Data_intent);
                        finish();
                    } else if (Title.equals("조명")) {
                        SharedPrefsUtils.setStringPreference(getApplicationContext(), "light_address", device.getAddress());
                        finish();
                        Intent Beacon_Setting_Dialog = new Intent(getApplicationContext(), BeaconSettingDailog.class);
                        Beacon_Setting_Dialog.putExtra("Setting_flag", getTag);
                        startActivity(Beacon_Setting_Dialog);
                    } else if (Title.equals("콘센트")) {
                        SharedPrefsUtils.setStringPreference(getApplicationContext(), "consent_address", device.getAddress());
                        finish();
                        Intent Beacon_Setting_Dialog = new Intent(getApplicationContext(), BeaconSettingDailog.class);
                        Beacon_Setting_Dialog.putExtra("Setting_flag", getTag);
                        startActivity(Beacon_Setting_Dialog);
                    } else if (Title.equals("벨브")) {
                        SharedPrefsUtils.setStringPreference(getApplicationContext(), "valve_address", device.getAddress());
                        finish();
                        Intent Beacon_Setting_Dialog = new Intent(getApplicationContext(), BeaconSettingDailog.class);
                        Beacon_Setting_Dialog.putExtra("Setting_flag", getTag);
                        startActivity(Beacon_Setting_Dialog);
                    } else if (Title.equals("도어락")) {
                        SharedPrefsUtils.setStringPreference(getApplicationContext(), "doorlock_address", device.getAddress());
                        finish();
                        Intent Beacon_Setting_Dialog = new Intent(getApplicationContext(), BeaconSettingDailog.class);
                        Beacon_Setting_Dialog.putExtra("Setting_flag", getTag);
                        startActivity(Beacon_Setting_Dialog);
                    }
                }
            });

            return view;
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLeDeviceListAdapter.addDevice(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        LinearLayout device_layout;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) { //다이얼로그 외부 터치시 꺼지는현상 방지
        return false;
    }
}