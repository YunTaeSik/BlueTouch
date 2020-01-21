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

public class BluetoothLeService_two extends Service {
    private Context context = this;

    private final static String TAG = BluetoothLeService_two.class.getSimpleName();

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
        Log.e("mGattUpdateReceiver","Service_start");
        Intent intent1 =new Intent("Service_start");
        sendBroadcast(intent1);

    }



    public void onSerialReceived(int data, String address) {
        System.out.println("rece");
        System.out.println(data);
        String Activity_Flag = SharedPrefsUtils.getStringPreference(getApplicationContext(), "Activity_flag");

        if (Activity_Flag == null) {
            if (data == 1) {
                String redTag = SharedPrefsUtils.getStringPreference(getApplicationContext(), address + "red");
                System.out.println(redTag);
                OneFunction oneFunction = new OneFunction();
                oneFunction.SetFuntion(redTag, this, data);
            } else if (data == 2) {
                String blueTag = SharedPrefsUtils.getStringPreference(getApplicationContext(), address + "blue");
                System.out.println(blueTag);
                OneFunction oneFunction = new OneFunction();
                oneFunction.SetFuntion(blueTag, this, data);
            } else if (data == 3) {
                String orangeTag = SharedPrefsUtils.getStringPreference(getApplicationContext(), address + "orange");
                {
                    System.out.println(orangeTag);
                    OneFunction oneFunction = new OneFunction();
                    oneFunction.SetFuntion(orangeTag, this, data);
                }
            }
        } else {
            if (Activity_Flag.equals("CAMERA")) {
                Log.e("CAMERA_DATA_SEVICE", String.valueOf(data));
                Intent Camer_Data_intent = new Intent("CAMERA_DATA");
                Camer_Data_intent.putExtra("data", data);
                sendBroadcast(Camer_Data_intent);
            } else if (Activity_Flag.equals("LIGHT")) {
                Log.e("LIGHT_DATA_SEVICE", String.valueOf(data));
                Intent Light_Data_intent = new Intent("LIGHT_DATA");
                Light_Data_intent.putExtra("data_light", data);
                sendBroadcast(Light_Data_intent);
            } else if (Activity_Flag.equals("CONSENT")) {
                Log.e("CONSENT_DATA_SEVICE", String.valueOf(data));
                Intent Consent_Data_intent = new Intent(Contact.CONSENT_DATA);
                Consent_Data_intent.putExtra(Contact.data_consent, data);
                sendBroadcast(Consent_Data_intent);
            }else if (Activity_Flag.equals("VALVE")) {
                Log.e("VALVE_DATA_SEVICE", String.valueOf(data));
                Intent Consent_Data_intent = new Intent(Contact.VALVE_DATA);
                Consent_Data_intent.putExtra(Contact.data_valve, data);
                sendBroadcast(Consent_Data_intent);
            }else if (Activity_Flag.equals("DOORLOCK")) {
                Log.e("DOORLOCK_DATA_SEVICE", String.valueOf(data));
                Intent Consent_Data_intent = new Intent(Contact.DOORLOCK_DATA);
                Consent_Data_intent.putExtra(Contact.data_doorlock, data);
                sendBroadcast(Consent_Data_intent);
            }
            else if (Activity_Flag.equals("FAKECALL")) {
                Log.e("FAKE_DATA_SEVICE", String.valueOf(data));
                Intent FAKE_Data_intent = new Intent("FAKE_DATA");
                FAKE_Data_intent.putExtra("data_fake", data);
                sendBroadcast(FAKE_Data_intent);
            } else if (Activity_Flag.equals("WEATHER")) {
                Log.e("WEATHER_DATA_SEVICE", String.valueOf(data));
                Intent WEATHER_Data_intent = new Intent("WEATHER_DATA");
                WEATHER_Data_intent.putExtra("data_weather", data);
                sendBroadcast(WEATHER_Data_intent);
            }else if (Activity_Flag.equals("ALARM")) {
                Log.e("ALARM_DATA_SEVICE", String.valueOf(data));
                Intent ALARM_Data_intent = new Intent("ALARM_DATA");
                ALARM_Data_intent.putExtra("data_alarm", data);
                sendBroadcast(ALARM_Data_intent);
            }else if (Activity_Flag.equals("ALARMVIEW")) {
                Log.e("ALARM_DATA_SEVICE", String.valueOf(data));
                Intent ALARMVIEW_Data_intent = new Intent("ALARMVIEW_DATA");
                ALARMVIEW_Data_intent.putExtra("data_alarmview", data);
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
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED_TWO";
    private final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED_TWO";
    private final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED_TWO";
    private final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE_TWO";
    private final static String EXTRA_DATA_TWO =
            "com.example.bluetooth.le.EXTRA_DATA_TWO";
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
        System.out.println("BluetoothLeService_two broadcastUpdate");
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            intent.putExtra(EXTRA_DATA_TWO, new String(data) +
                    stringBuilder.toString().substring(1, 2));
            intent.putExtra("address", address);   //address
            intent.putExtra("data", Integer.parseInt(stringBuilder.toString().substring(1, 2)));  //data
            sendBroadcast(intent);
        }
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService_two getService() {
            return BluetoothLeService_two.this;
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
        System.out.println("BluetoothLeService_two initialize" + mBluetoothManager);
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
        System.out.println("BluetoothLeService_two disconnect");
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    private void close() {
        System.out.println("BluetoothLeService_two close");
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            SharedPrefsUtils.setStringPreference(getApplicationContext(), "Service_flag", "on");
            Intent Light_Data_intent = new Intent("Service_finish");
            Light_Data_intent.putExtra("finish", 1);
            getApplicationContext().sendBroadcast(Light_Data_intent);

            Log.e("BluetoothLeService_two", "BluetoothLeService_two");

            initBluetooth();
            if (mBluetoothManager == null) {
                mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            }
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            try {
                String add = SharedPrefsUtils.getStringPreference(context, "A" + 2);
                if (add != null) {
                    final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(add);
                    mBluetoothGatt = device.connectGatt(getApplicationContext(), true, mGattCallback);
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
            Intent intent1 =new Intent("Service_finish");
            intent1.putExtra("finish",1);
            sendBroadcast(intent1);

            final String action = intent.getAction();
            System.out.println("mGattUpdateReceiver->onReceive->action=" + action);

            if (BluetoothLeService_two.ACTION_GATT_CONNECTED.equals(action)) {

            } else if (BluetoothLeService_two.ACTION_GATT_DISCONNECTED.equals(action)) {

            } else if (BluetoothLeService_two.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                for (BluetoothGattService gattService : getSupportedGattServices()) {
                    System.out.println("ACTION_GATT_SERVICES_DISCOVERED  " +
                            gattService.getUuid().toString());
                }
                getGattServices(getSupportedGattServices());
            } else if (BluetoothLeService_two.ACTION_DATA_AVAILABLE.equals(action)) {
                realAddress = intent.getExtras().getString("address");
                subData = intent.getExtras().getInt("data");
                if (mSCharacteristic == mModelNumberCharacteristic) {
                    try {
                        if (intent.getStringExtra(BluetoothLeService_two.EXTRA_DATA_TWO).toUpperCase().startsWith("DF BLUNO")) {
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
            }
            else if (action.equals("delete_two")){
                disconnect();
                close();
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService_two.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService_two.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService_two.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService_two.ACTION_DATA_AVAILABLE);
        intentFilter.addAction("delete_two");
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
