package com.bluetouch.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.bluetouch.function.OneFunction;
import com.bluetouch.util.Contact;
import com.bluetouch.util.RingBuffer;
import com.bluetouch.util.SharedPrefsUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class BluetoothLeService extends Service {
    private Context context = this;

    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    public int mConnectionState = STATE_DISCONNECTED;


    private static final int WRITE_NEW_CHARACTERISTIC = -1;
    private static final int MAX_CHARACTERISTIC_LENGTH = 17;
    private boolean mIsWritingCharacteristic = false;
    private static BluetoothAdapter myBluetoothAdapter;
    private static BluetoothGattCharacteristic mSCharacteristic, mModelNumberCharacteristic, mSerialPortCharacteristic, mCommandCharacteristic;
    public static final String SerialPortUUID = "0000dfb1-0000-1000-8000-00805f9b34fb";
    public static final String CommandUUID = "0000dfb2-0000-1000-8000-00805f9b34fb";
    public static final String ModelNumberStringUUID = "00002a24-0000-1000-8000-00805f9b34fb";
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private int mBaudrate = 115200;
    private String mPassword = "AT+PASSWOR=DFRobot\r\n";
    private String mBaudrateBuffer = "AT+CURRUART=" + mBaudrate + "\r\n";
    private String realAddress;
    private int subData;

    @Override
    public void onCreate() {
        //super.onCreate();
        Log.e("mGattUpdateReceiver", "Service_start");
        Intent intent1 = new Intent("Service_start");
        sendBroadcast(intent1);

    }


    public void onSerialReceived(int data, String address) { //BLE 데이터 받는 함수
        System.out.println("rece");
        System.out.println(data);
        String Activity_Flag = SharedPrefsUtils.getStringPreference(getApplicationContext(), "Activity_flag"); //Sharedpreference를 이용하여 현재 액티비티 정보를 가져오기 

        if (Activity_Flag == null) { //액티비티 정보가 null 이면
            if (data == 1) { //BLE data가 1이면
                String redTag = SharedPrefsUtils.getStringPreference(getApplicationContext(), address + "red"); // 블루터치 기능선택 액티비티에 첫번째칸에있는 TAG 가져오기
                System.out.println(redTag);
                OneFunction oneFunction = new OneFunction();
                oneFunction.SetFuntion(redTag, this, data);   //OneFunction에 SetFuntion함수에 태그와 데이터 전달
            } else if (data == 2) {
                String blueTag = SharedPrefsUtils.getStringPreference(getApplicationContext(), address + "blue");  // 블루터치 기능선택 액티비티에 두번째 칸에있는 TAG 가져오기
                System.out.println(blueTag);
                OneFunction oneFunction = new OneFunction();
                oneFunction.SetFuntion(blueTag, this, data);  //OneFunction에 SetFuntion함수에 태그와 데이터 전달
            } else if (data == 3) {
                String orangeTag = SharedPrefsUtils.getStringPreference(getApplicationContext(), address + "orange");  // 블루터치 기능선택 액티비티에 세번째 칸에있는 TAG 가져오기
                {
                    System.out.println(orangeTag);
                    OneFunction oneFunction = new OneFunction();
                    oneFunction.SetFuntion(orangeTag, this, data);  //OneFunction에 SetFuntion함수에 태그와 데이터 전달
                }
            }
        } else {
            if (Activity_Flag.equals("CAMERA")) {   //액티비티 정보가 카메라이면
                Log.e("CAMERA_DATA_SEVICE", String.valueOf(data));
                Intent Camer_Data_intent = new Intent("CAMERA_DATA");
                Camer_Data_intent.putExtra("data", data);  //CAMERA_DATA 인텐트필터에 데이터 전달
                sendBroadcast(Camer_Data_intent);
            } else if (Activity_Flag.equals("LIGHT")) {  //액티비티 정보가  조명이면
                Log.e("LIGHT_DATA_SEVICE", String.valueOf(data));
                Intent Light_Data_intent = new Intent("LIGHT_DATA");
                Light_Data_intent.putExtra("data_light", data);  //LIGHT_DATA 인텐트필터에 데이터 전달
                sendBroadcast(Light_Data_intent);
            } else if (Activity_Flag.equals("CONSENT")) {  //액티비티 정보가  콘센트이면
                Log.e("CONSENT_DATA_SEVICE", String.valueOf(data));
                Intent Consent_Data_intent = new Intent(Contact.CONSENT_DATA);
                Consent_Data_intent.putExtra(Contact.data_consent, data);   //CONSENT_DATA 인텐트필터에 데이터 전달
                sendBroadcast(Consent_Data_intent);
            } else if (Activity_Flag.equals("VALVE")) {   //액티비티 정보가 벨브이면
                Log.e("VALVE_DATA_SEVICE", String.valueOf(data));
                Intent Consent_Data_intent = new Intent(Contact.VALVE_DATA);    
                Consent_Data_intent.putExtra(Contact.data_valve, data);     //VALVE_DATA 인텐트필터에 데이터 전달
                sendBroadcast(Consent_Data_intent);
            } else if (Activity_Flag.equals("DOORLOCK")) {  //액티비티 정보가 도어락이면
                Log.e("DOORLOCK_DATA_SEVICE", String.valueOf(data));
                Intent Consent_Data_intent = new Intent(Contact.DOORLOCK_DATA);
                Consent_Data_intent.putExtra(Contact.data_doorlock, data);   //DOORLOCK_DATA 인텐트필터에 데이터 전달
                sendBroadcast(Consent_Data_intent);
            } else if (Activity_Flag.equals("FAKECALL")) {  //액티비티 정보가 페이크콜이면
                Log.e("FAKE_DATA_SEVICE", String.valueOf(data));
                Intent FAKE_Data_intent = new Intent("FAKE_DATA");
                FAKE_Data_intent.putExtra("data_fake", data);       //FAKE_DATA 인텐트필터에 데이터 전달
                sendBroadcast(FAKE_Data_intent);
            } else if (Activity_Flag.equals("WEATHER")) {  //액티비티 정보가 날씨이면
                Log.e("WEATHER_DATA_SEVICE", String.valueOf(data));
                Intent WEATHER_Data_intent = new Intent("WEATHER_DATA");
                WEATHER_Data_intent.putExtra("data_weather", data);     //WEATHER_DATA 인텐트필터에 데이터 전달
                sendBroadcast(WEATHER_Data_intent);
            } else if (Activity_Flag.equals("ALARM")) {  //액티비티 정보가 알람이면
                Log.e("ALARM_DATA_SEVICE", String.valueOf(data));
                Intent ALARM_Data_intent = new Intent("ALARM_DATA");
                ALARM_Data_intent.putExtra("data_alarm", data);     //ALARM_DATA 인텐트필터에 데이터 전달
                sendBroadcast(ALARM_Data_intent);
            } else if (Activity_Flag.equals("ALARMVIEW")) {  //액티비티 정보가 알람뷰이면
                Log.e("ALARM_DATA_SEVICE", String.valueOf(data));
                Intent ALARMVIEW_Data_intent = new Intent("ALARMVIEW_DATA");
                ALARMVIEW_Data_intent.putExtra("data_alarmview", data);     //ALARMVIEW_DATA 인텐트필터에 데이터 전달
                sendBroadcast(ALARMVIEW_Data_intent);
            }
        }
    }

    private class BluetoothGattCharacteristicHelper {
        BluetoothGattCharacteristic mCharacteristic;
        String mCharacteristicValue;

        BluetoothGattCharacteristicHelper(BluetoothGattCharacteristic characteristic, String characteristicValue) {
            mCharacteristic = characteristic;
            mCharacteristicValue = characteristicValue;
        }
    }

    //ring buffer
    private RingBuffer<BluetoothGattCharacteristicHelper> mCharacteristicRingBuffer = new RingBuffer<BluetoothGattCharacteristicHelper>(8);

    private final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    private final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    private final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    private final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    private final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            System.out.println("BluetoothGattCallback----onConnectionStateChange" + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                if (mBluetoothGatt.discoverServices()) {
                    Log.i(TAG, "Attempting to start service discovery:");

                } else {
                    Log.i(TAG, "Attempting to start service discovery:not success");

                }


            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            System.out.println("onServicesDiscovered " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e("writeCharacteristic", "writeCharacteristic");
            //this block should be synchronized to prevent the function overloading
            synchronized (this) {
                //CharacteristicWrite success
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    System.out.println("onCharacteristicWrite success:" + new String(characteristic.getValue()));
                    Log.e("test", gatt.getDevice().getName() + gatt.getDevice().getAddress());
                    if (mCharacteristicRingBuffer.isEmpty()) {
                        mIsWritingCharacteristic = false;
                    } else {
                        BluetoothGattCharacteristicHelper bluetoothGattCharacteristicHelper = mCharacteristicRingBuffer.next();
                        if (bluetoothGattCharacteristicHelper.mCharacteristicValue.length() > MAX_CHARACTERISTIC_LENGTH) {
                            try {
                                bluetoothGattCharacteristicHelper.mCharacteristic.setValue(bluetoothGattCharacteristicHelper.mCharacteristicValue.substring(0, MAX_CHARACTERISTIC_LENGTH).getBytes("ISO-8859-1"));

                            } catch (UnsupportedEncodingException e) {
                                throw new IllegalStateException(e);
                            }


                            if (mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicHelper.mCharacteristic)) {
                                System.out.println("writeCharacteristic init " + new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue()) + ":success");
                            } else {
                                System.out.println("writeCharacteristic init " + new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue()) + ":failure");
                            }
                            bluetoothGattCharacteristicHelper.mCharacteristicValue = bluetoothGattCharacteristicHelper.mCharacteristicValue.substring(MAX_CHARACTERISTIC_LENGTH);
                        } else {
                            try {
                                bluetoothGattCharacteristicHelper.mCharacteristic.setValue(bluetoothGattCharacteristicHelper.mCharacteristicValue.getBytes("ISO-8859-1"));
                            } catch (UnsupportedEncodingException e) {
                                throw new IllegalStateException(e);
                            }

                            if (mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicHelper.mCharacteristic)) {
                                System.out.println("writeCharacteristic init " + new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue()) + ":success");
                            } else {
                                System.out.println("writeCharacteristic init " + new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue()) + ":failure");
                            }
                            bluetoothGattCharacteristicHelper.mCharacteristicValue = "";

                            mCharacteristicRingBuffer.pop();
                        }
                    }
                } else if (status == WRITE_NEW_CHARACTERISTIC) {
                    if ((!mCharacteristicRingBuffer.isEmpty()) && mIsWritingCharacteristic == false) {
                        BluetoothGattCharacteristicHelper bluetoothGattCharacteristicHelper = mCharacteristicRingBuffer.next();
                        if (bluetoothGattCharacteristicHelper.mCharacteristicValue.length() > MAX_CHARACTERISTIC_LENGTH) {

                            try {
                                bluetoothGattCharacteristicHelper.mCharacteristic.setValue(bluetoothGattCharacteristicHelper.mCharacteristicValue.substring(0, MAX_CHARACTERISTIC_LENGTH).getBytes("ISO-8859-1"));
                            } catch (UnsupportedEncodingException e) {
                                throw new IllegalStateException(e);
                            }

                            if (mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicHelper.mCharacteristic)) {
                                System.out.println("writeCharacteristic init " + new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue()) + ":success");
                            } else {
                                System.out.println("writeCharacteristic init " + new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue()) + ":failure");
                            }
                            bluetoothGattCharacteristicHelper.mCharacteristicValue = bluetoothGattCharacteristicHelper.mCharacteristicValue.substring(MAX_CHARACTERISTIC_LENGTH);
                        } else {
                            try {
                                bluetoothGattCharacteristicHelper.mCharacteristic.setValue(bluetoothGattCharacteristicHelper.mCharacteristicValue.getBytes("ISO-8859-1"));
                            } catch (UnsupportedEncodingException e) {
                                throw new IllegalStateException(e);
                            }


                            if (mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicHelper.mCharacteristic)) {
                                System.out.println("writeCharacteristic init " + new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue()) + ":success");

                            } else {
                                System.out.println("writeCharacteristic init " + new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue()) + ":failure");
                            }
                            bluetoothGattCharacteristicHelper.mCharacteristicValue = "";

                            mCharacteristicRingBuffer.pop();
                        }
                    }

                    mIsWritingCharacteristic = true;

                    if (mCharacteristicRingBuffer.isFull()) {
                        mCharacteristicRingBuffer.clear();
                        mIsWritingCharacteristic = false;
                    }
                } else {
                    mCharacteristicRingBuffer.clear();
                    System.out.println("onCharacteristicWrite fail:" + new String(characteristic.getValue()));
                    System.out.println(status);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                System.out.println("onCharacteristicRead  " + characteristic.getUuid().toString());
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic, gatt.getDevice().getAddress());
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor characteristic,
                                      int status) {
            System.out.println("onDescriptorWrite  " + characteristic.getUuid().toString() + " " + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            System.out.println("onCharacteristicChanged  " + new String(characteristic.getValue()));
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic, gatt.getDevice().getAddress());
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic, String address) {
        final Intent intent = new Intent(action);
        System.out.println("BluetoothLeService broadcastUpdate");
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            intent.putExtra(EXTRA_DATA, new String(data) +
                    stringBuilder.toString().substring(1, 2));
            intent.putExtra("address", address);   //해당 BLE address 
            intent.putExtra("data", Integer.parseInt(stringBuilder.toString().substring(1, 2)));  //해당 BLE data 
            sendBroadcast(intent);
        }
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;

    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);

    }

    private final IBinder mBinder = new LocalBinder();

    public boolean initialize() {
        System.out.println("BluetoothLeService initialize" + mBluetoothManager);
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    private void disconnect() {
        System.out.println("BluetoothLeService disconnect");
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    private void close() {
        System.out.println("BluetoothLeService close");
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        try {
            mBluetoothGatt.readCharacteristic(characteristic);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        String writeCharacteristicString;
        try {
            writeCharacteristicString = new String(characteristic.getValue(), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        System.out.println("allwriteCharacteristicString:" + writeCharacteristicString);

        mCharacteristicRingBuffer.push(new BluetoothGattCharacteristicHelper(characteristic, writeCharacteristicString));
        System.out.println("mCharacteristicRingBufferlength:" + mCharacteristicRingBuffer.size());
        mGattCallback.onCharacteristicWrite(mBluetoothGatt, characteristic, WRITE_NEW_CHARACTERISTIC);

    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        try {
            mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {  //서비스 시작
        try {


            Log.e("BluetoothLeService", "BluetoothLeService");

            initBluetooth();  //스마트폰 블루투스 켜는 함수
            if (mBluetoothManager == null) {
                mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            }
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            try {
                String add = SharedPrefsUtils.getStringPreference(context, "A" + 1);  //첫번째 블루터치 어드레스 가져오기
                if (add != null) {
                    final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(add);  //가져온 어드레스로 디바이스 정의
                    mBluetoothGatt = device.connectGatt(getApplicationContext(), true, mGattCallback);  //해당 디바이스 gatt연결
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("size" + mBluetoothGatt.getConnectedDevices().size());
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        return super.onStartCommand(intent, flags, startId);
    }

    private void initBluetooth() {                                                    //블루투스를 강제로 켜기 위한 함수.
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (myBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth", Toast.LENGTH_LONG).show();
        } else {
// 장치가 블루투스를 지원하는 경우
            if (myBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON ||           // 블루투스가 켜져있는 상태거나 켜는중일경우
                    myBluetoothAdapter.getState() == myBluetoothAdapter.STATE_ON) {
            } else {
                myBluetoothAdapter.enable();                                                       //아닐경우에는 강제로 블루투스를 킨다.
            }
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @SuppressLint("DefaultLocale")
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            System.out.println("mGattUpdateReceiver->onReceive->action=" + action);

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                for (BluetoothGattService gattService : getSupportedGattServices()) {
                    System.out.println("ACTION_GATT_SERVICES_DISCOVERED  " +
                            gattService.getUuid().toString());
                }
                getGattServices(getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {  //브로드캐스트리시버 액션이 ACTION_DATA_AVAILABLE 이면 
                realAddress = intent.getExtras().getString("address"); 
                subData = intent.getExtras().getInt("data");
                if (mSCharacteristic == mModelNumberCharacteristic) {
                    try {
                        if (intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toUpperCase().startsWith("DF BLUNO")) {
                            setCharacteristicNotification(mSCharacteristic, false);
                            mSCharacteristic = mCommandCharacteristic;
                            mSCharacteristic.setValue(mPassword);
                            writeCharacteristic(mSCharacteristic);
                            mSCharacteristic.setValue(mBaudrateBuffer);
                            writeCharacteristic(mSCharacteristic);
                            mSCharacteristic = mSerialPortCharacteristic;
                            setCharacteristicNotification(mSCharacteristic, true);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } else if (mSCharacteristic == mSerialPortCharacteristic) {
                }
                onSerialReceived(subData, realAddress);
            } else if (action.equals("delete_one")) {
                disconnect();
                close();
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {  //브로드캐스트리시버 인텐트 필터
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction("delete_one");
        return intentFilter;
    }


    private void getGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        mModelNumberCharacteristic = null;
        mSerialPortCharacteristic = null;
        mCommandCharacteristic = null;
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            System.out.println("displayGattServices + uuid=" + uuid);

            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            // try {
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                uuid = gattCharacteristic.getUuid().toString();
                if (uuid.equals(ModelNumberStringUUID)) {
                    mModelNumberCharacteristic = gattCharacteristic;
                    System.out.println("mModelNumberCharacteristic  " + mModelNumberCharacteristic.getUuid().toString());
                } else if (uuid.equals(SerialPortUUID)) {
                    mSerialPortCharacteristic = gattCharacteristic;
                    System.out.println("mSerialPortCharacteristic  " + mSerialPortCharacteristic.getUuid().toString());
//                    updateConnectionState(R.string.comm_establish);
                } else if (uuid.equals(CommandUUID)) {
                    mCommandCharacteristic = gattCharacteristic;
                    System.out.println("mSerialPortCharacteristic  " + mSerialPortCharacteristic.getUuid().toString());
//                    updateConnectionState(R.string.comm_establish);
                }
            }
            //   }catch ()
            mGattCharacteristics.add(charas);
        }

        if (mModelNumberCharacteristic == null || mSerialPortCharacteristic == null || mCommandCharacteristic == null) {
        } else {
            mSCharacteristic = mModelNumberCharacteristic;
            setCharacteristicNotification(mSCharacteristic, true);
            readCharacteristic(mSCharacteristic);
        }

    }

}
