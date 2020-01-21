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

import com.bluetouch.util.RingBuffer;
import com.bluetouch.util.SharedPrefsUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BluetoothLeService_Light extends Service {
    private Context context = this;

    private final static String TAG = BluetoothLeService_Light.class.getSimpleName();

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
    private TimerTask mTask;
    private Timer mTimer;
    private Timer Data_Timer;
    private double testDistance = 0;
    private double sumDistance = 0;
    private double averDistance = 0;
    private double TheadHoldDistance = 0;
    private int TxPower = -59;
    private double getrssi;
    private int count = 0;
    private String address;

    public void onSerialReceived_two(int data, String address) {

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
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED_LIGHT";
    private final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED_LIGHT";
    private final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED_LIGHT";
    private final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE_LIGHT";
    private final static String EXTRA_DATA_LIGHT =
            "com.example.bluetooth.le.EXTRA_DATA_LIGHT";
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            getrssi = (double) rssi;
        }


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

              /*  try {
                    address = SharedPrefsUtils.getStringPreference(getApplicationContext(),"light_address");
                    final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    mBluetoothGatt = device.connectGatt(getApplicationContext(), false, mGattCallback);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {

                }*/
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
        System.out.println("BluetoothLeService_Light broadcastUpdate");
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            intent.putExtra(EXTRA_DATA_LIGHT, new String(data) +
                    stringBuilder.toString().substring(1, 2));

            intent.putExtra("address", address);   //address
            intent.putExtra("data", Integer.parseInt(stringBuilder.toString().substring(1, 2)));  //data
            sendBroadcast(intent);
        }
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService_Light getService() {
            return BluetoothLeService_Light.this;
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
        System.out.println("BluetoothLeService_Light initialize" + mBluetoothManager);
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

    public void writeCharacteristic2(BluetoothGattCharacteristic characteristic) {
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
        Log.e("BluetoothLeService_Ligh", "BluetoothLeService_Light");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("LIGHT_DATA");
        registerReceiver(mRecevier, intentFilter);
        try {
            initBluetooth();
            if (mBluetoothManager == null) {
                mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            }
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            try {
                address = SharedPrefsUtils.getStringPreference(getApplicationContext(),"light_address");
                final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                mBluetoothGatt = device.connectGatt(getApplicationContext(), true, mGattCallback);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {

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
        try {
            if (SharedPrefsUtils.getStringPreference(getApplicationContext(), "Light_Becon").equals("ON")) {
                BeconMode();
            } else {
                if (Data_Timer != null) {
                    Data_Timer.cancel();
                    Data_Timer = null;
                }
            }
        } catch (NullPointerException e) {

        }

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

            if (BluetoothLeService_Light.ACTION_GATT_CONNECTED.equals(action)) {
            } else if (BluetoothLeService_Light.ACTION_GATT_DISCONNECTED.equals(action)) {
            } else if (BluetoothLeService_Light.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                try {
                    for (BluetoothGattService gattService : getSupportedGattServices()) {
                        System.out.println("ACTION_GATT_SERVICES_DISCOVERED  " +
                                gattService.getUuid().toString());
                    }
                    getGattServices(getSupportedGattServices());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else if (BluetoothLeService_Light.ACTION_DATA_AVAILABLE.equals(action)) {
                realAddress = intent.getExtras().getString("address");
                subData = intent.getExtras().getInt("data");
                if (mSCharacteristic == mModelNumberCharacteristic) {
                    try {
                        if (intent.getStringExtra(BluetoothLeService_Light.EXTRA_DATA_LIGHT).toUpperCase().startsWith("DF BLUNO")) {
                            setCharacteristicNotification(mSCharacteristic, false);

                            mSCharacteristic = mCommandCharacteristic;
                            mSCharacteristic.setValue(mPassword);
                            writeCharacteristic2(mSCharacteristic);
                            mSCharacteristic.setValue(mBaudrateBuffer);
                            writeCharacteristic2(mSCharacteristic);
                            mSCharacteristic = mSerialPortCharacteristic;
                            setCharacteristicNotification(mSCharacteristic, true);

                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } else if (mSCharacteristic == mSerialPortCharacteristic) {
                    // onSerialReceived(subData,realAddress);
                }

                onSerialReceived_two(subData, realAddress);
            }
            else if (action.equals("delete_light")){
                if (Data_Timer != null) {
                    Data_Timer.cancel();
                    Data_Timer = null;
                }
                disconnect();
                close();
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService_Light.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService_Light.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService_Light.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService_Light.ACTION_DATA_AVAILABLE);
        intentFilter.addAction("delete_light");
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
            mGattCharacteristics.add(charas);
        }

        if (mModelNumberCharacteristic == null || mSerialPortCharacteristic == null || mCommandCharacteristic == null) {
        } else {
            mSCharacteristic = mModelNumberCharacteristic;
            setCharacteristicNotification(mSCharacteristic, true);
            readCharacteristic(mSCharacteristic);
        }

    }

    private BroadcastReceiver mRecevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("LIGHT_DATA")) {
                int data = intent.getIntExtra("data_light", 0);
                String flag = intent.getStringExtra("Becon_flag");
                Log.e("LIGHT_DATA", String.valueOf(data));
                try {
                    if (flag.equals("ON")) {
                        BeconMode();
                    } else {
                        if (Data_Timer != null) {
                            Data_Timer.cancel();
                            Data_Timer = null;
                        }
                    }
                } catch (NullPointerException e) {

                }

                if (data == 1) {
                    try {
                        mSCharacteristic.setValue(String.valueOf(data));
                        mSCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                        mBluetoothGatt.writeCharacteristic(mSCharacteristic);
                    } catch (NullPointerException e) {

                    }
                } else if (data == 2) {
                    try {
                        mSCharacteristic.setValue(String.valueOf(data));
                        mSCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                        mBluetoothGatt.writeCharacteristic(mSCharacteristic);
                    } catch (NullPointerException e) {

                    }
                } else if (data == 3) {
                    try {
                        mSCharacteristic.setValue(String.valueOf(data));
                        mSCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                        mBluetoothGatt.writeCharacteristic(mSCharacteristic);
                    } catch (NullPointerException e) {

                    }
                } else if (data == 4) {
                    try {
                        mSCharacteristic.setValue(String.valueOf(data));
                        mSCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                        mBluetoothGatt.writeCharacteristic(mSCharacteristic);
                    } catch (NullPointerException e) {

                    }
                } else if (data == 5) {
                    try {
                        mSCharacteristic.setValue(String.valueOf(data));
                        mSCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                        mBluetoothGatt.writeCharacteristic(mSCharacteristic);
                    } catch (NullPointerException e) {

                    }
                }
            }
        }
    };


    protected static double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0;
        }
        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }

    private void BeconMode() {
        mTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    mBluetoothGatt.readRemoteRssi();
                } catch (NullPointerException e) {

                }
                testDistance = calculateAccuracy(TxPower, getrssi);
                if (testDistance != 0) {
                    count++;
                    sumDistance += testDistance;
                    if (count >= 20) {
                        averDistance = sumDistance / count + 1;
                        count = 0;
                        sumDistance = 0;
                    }
                }
            }
        };

        mTimer = new Timer();
        mTimer.schedule(mTask, 0, 100);

        TimerTask Data_task = new TimerTask() {
            @Override
            public void run() {
                if (TheadHoldDistance == 0) {
                    if (averDistance / 4 <= 8) {
                        TheadHoldDistance = averDistance / 4;
                    }
                } else {
                    if (TheadHoldDistance < averDistance / 4) {
                        double filter = averDistance / 4 - TheadHoldDistance;
                        if (filter >= 1) {
                            TheadHoldDistance = TheadHoldDistance + 0.5;
                        } else {
                            TheadHoldDistance = averDistance / 4;
                        }

                    } else {
                        double filter = TheadHoldDistance - averDistance / 4;
                        if (filter >= 1) {
                            TheadHoldDistance = TheadHoldDistance - 0.5;
                        } else {
                            TheadHoldDistance = averDistance / 4;
                        }
                    }

                    if (TheadHoldDistance != 0) {
                        Log.e("distance_light", String.valueOf(TheadHoldDistance));
                    }
                    if ((int) (TheadHoldDistance) <= 3) {
                        Intent Light_Data_intent = new Intent("LIGHT_DATA");
                        Light_Data_intent.putExtra("data_light", 4);
                        sendBroadcast(Light_Data_intent);
                    } else {
                        Intent Light_Data_intent = new Intent("LIGHT_DATA");
                        Light_Data_intent.putExtra("data_light", 5);
                        sendBroadcast(Light_Data_intent);
                    }
                }

                if (TheadHoldDistance >= 8) {
                    if (averDistance / 4 <= 7) {
                        TheadHoldDistance = averDistance / 4;
                    }
                }
            }
        };
        Data_Timer = new Timer();
        Data_Timer.schedule(Data_task, 0, 3000);
    }
}
