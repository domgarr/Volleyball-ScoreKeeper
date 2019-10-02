package com.domgarr.android.volleyballscorekeeper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CentralDeviceBleService {
    private static CentralDeviceBleService instance;

    private BluetoothGatt mGatt;
    private List<BluetoothGattService> services = new ArrayList<>();
    public static BluetoothDevice device;

    private BluetoothGattCharacteristic redScoreCharacteristic;
    private BluetoothGattCharacteristic blueScoreCharacteristic;

    private int connectionState = STATE_DISCONNECTED;
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    private Context context;

    private CentralDeviceBleService(){ }

    public static CentralDeviceBleService getInstance(){
        if(instance == null){
            instance = new CentralDeviceBleService();
        }
        return instance;
    }

    public void connectToGattService(Context context){
        this.context = context;
        device.connectGatt(context, true, gattCallback);
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(newState == BluetoothProfile.STATE_CONNECTED) {
                mGatt = gatt;
                connectionState = STATE_CONNECTED;
                gatt.discoverServices();
                MainActivity.vConnectToBluetoothImageView.setColorFilter(ContextCompat.getColor(context, R.color.blutooth_connected));
            }else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                connectionState = STATE_DISCONNECTED;
                MainActivity.vConnectToBluetoothImageView.setColorFilter(ContextCompat.getColor(context, R.color.blutooth_disconnected));

            }else if(newState == BluetoothProfile.STATE_CONNECTING){
                connectionState = STATE_CONNECTING;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(status == BluetoothGatt.GATT_SUCCESS){
                if(blueScoreCharacteristic != null || redScoreCharacteristic != null){
                    return;
                }

                services = gatt.getServices();

                printAllServiceUuid(gatt);

                Log.d("onServicesDiscovered", services.toString());

                Log.d("onServicesDiscovered:deviceName", device.getName());

                if(device.getName() == null){
                    Log.d("onServicesDiscovered:deviceName", "Device not found.");
                    return;
                }

                BluetoothGattService bgs1 = gatt.getService(ScoreBoardGattAttributes.scoreboard_services.get(device.getName()));
                Log.d("onServicesDiscovered", bgs1.getUuid().toString());

                Log.d("onServicesDiscovered", bgs1.getCharacteristics().size() + "");

                for (BluetoothGattCharacteristic c :  bgs1.getCharacteristics()) {
                    Log.d( "onServicesDiscovered", c.getUuid().toString());
                    if(c.getUuid().toString().contains("bbbb")){
                        blueScoreCharacteristic = c;
                        continue;
                    }else if(c.getUuid().toString().contains("aaaa")){
                        redScoreCharacteristic = c;
                        continue;
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d("service1", "Writing to scoreboard service");
        }
    };

    public boolean isBluetoothLeSupported(Activity activity){
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(activity, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean isBluetoothEnabled(BluetoothAdapter bluetoothAdapter){
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public void writeToBlueScoreCharacteristic(int blueScore){
        if(mGatt != null && blueScoreCharacteristic != null){
            blueScoreCharacteristic.setValue(blueScore, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            mGatt.writeCharacteristic(blueScoreCharacteristic);
        }else{
            if(mGatt == null) {
                Log.d("writeToBlueScoreCharacteristic", "Gatt not defined");
            }
            if(blueScoreCharacteristic == null) {
                Log.d("writeToBlueScoreCharacteristic", "Bluescore char not defined.");
            }
        }
    }

    public void writeToRedScoreCharacteristic(int redScore){
        if(mGatt != null && redScoreCharacteristic != null){
            redScoreCharacteristic.setValue(redScore, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            mGatt.writeCharacteristic(redScoreCharacteristic);
        }else{
            if(mGatt == null) {
                Log.d("writeToRedScoreChar", "Gatt not defined");
            }

            if(redScoreCharacteristic == null) {
                Log.d("writeToRedScoreChar", "Red score char not defined.");
            }
        }
    }

    public void close() {
        if (mGatt == null) {
            return;
        }

        mGatt.close();
        mGatt = null;
    }

    public static BluetoothDevice getDevice() {
        return device;
    }

    public static void setDevice(BluetoothDevice device) {
        CentralDeviceBleService.device = device;
    }

    public int getConnectionState() {
        return connectionState;
    }

    public void printAllServiceUuid(BluetoothGatt gatt){
        for(BluetoothGattService service : gatt.getServices()){
            Log.d("onServicesDiscovered:print", service.getUuid().toString());
            for(BluetoothGattCharacteristic characteristic : service.getCharacteristics()){
                Log.d("onServicesDiscovered:print", "   " + characteristic.getUuid().toString());
            }
        }
    }
}