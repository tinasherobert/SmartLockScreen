package com.pvsagar.smartlockscreen.applogic_objects;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pvsagar.smartlockscreen.baseclasses.EnvironmentVariable;
import com.pvsagar.smartlockscreen.environmentdb.EnvironmentDatabaseContract;

import java.util.ArrayList;

/**
 * Created by aravind on 10/8/14.
 */
public class BluetoothEnvironmentVariable extends EnvironmentVariable {
    private static final String LOG_TAG = BluetoothEnvironmentVariable.class.getSimpleName();

    public static final int REQUEST_BLUETOOTH_ENABLE = 20;
    private static final int NUMBER_OF_STRING_VALUES = 2;
    private static final int INDEX_DEVICE_NAME = 0;
    private static final int INDEX_DEVICE_ADDRESS = 1;

    public BluetoothEnvironmentVariable() {
        super(EnvironmentVariable.TYPE_BLUETOOTH_DEVICES, 0 , NUMBER_OF_STRING_VALUES);
    }

    public BluetoothEnvironmentVariable(String deviceName, String deviceAddress){
        super(EnvironmentVariable.TYPE_BLUETOOTH_DEVICES, null,
                new String[]{deviceName, deviceAddress});
        Log.d(LOG_TAG, deviceName + ": " + deviceAddress);
    }

    @Override
    public boolean isStringValuesSupported() {
        return true;
    }

    @Override
    public boolean isFloatValuesSupported() {
        return false;
    }

    public String getDeviceName(){
        try {
            return getStringValue(INDEX_DEVICE_NAME);
        } catch (Exception e){
            Log.e(LOG_TAG, "Internal application error, please file a bug report to developer."
                    + e.getMessage());
            return null;
        }
    }

    public String getDeviceAddress(){
        try {
            return getStringValue(INDEX_DEVICE_ADDRESS);
        } catch (Exception e){
            Log.e(LOG_TAG, "Internal application error, please file a bug report to developer."
                    + e.getMessage());
            return null;
        }
    }

    public void setDeviceName(String deviceName){
        setStringValue(deviceName, INDEX_DEVICE_NAME);
    }

    public void setDeviceAddress(String deviceAddress){
        setStringValue(deviceAddress, INDEX_DEVICE_ADDRESS);
    }

    /**
     * This function requests the users to enable bluetooth
     * @param context Context of the calling activity
     */
    public void enableBluetooth(Context context){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Checking for Bluetooth Hardware
        if(mBluetoothAdapter == null){
            Log.e(LOG_TAG,"Bluetooth Hardware Not Found");
        }
        Log.v(LOG_TAG,"enabling bluetooth");
        Intent startBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        ((Activity)context).startActivityForResult(startBluetoothIntent,REQUEST_BLUETOOTH_ENABLE);
    }

    /**
     * This function returns the list of paired bluetooth devices.
     * @param context Context of the calling activity
     * @return Returns the list of paired bluetooth devices
     */
    public ArrayList<BluetoothDevice> getPairedBluetoothDevices(Context context){

        Log.v(LOG_TAG, "getBluetoothDevices Entered");
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Checking for Bluetooth Hardware
        if(mBluetoothAdapter == null){
            Log.e(LOG_TAG,"Bluetooth Hardware Not Found");
            return null;
        }
        //Checking whether bluetooth is enabled
        if(!mBluetoothAdapter.isEnabled()){
            Intent startBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity)context).startActivityForResult(startBluetoothIntent,REQUEST_BLUETOOTH_ENABLE);
            Log.v(LOG_TAG,"Bluetooth Not enabled");
            return null;
        }
        ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();
        for (BluetoothDevice bluetoothDevice : mBluetoothAdapter.getBondedDevices()) {
            bluetoothDevices.add(bluetoothDevice);
        }

        return bluetoothDevices;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues bluetoothValues = new ContentValues();
        bluetoothValues.put(EnvironmentDatabaseContract.BluetoothDevicesEntry.COLUMN_DEVICE_NAME,
                getDeviceName());
        bluetoothValues.put(EnvironmentDatabaseContract.BluetoothDevicesEntry.COLUMN_DEVICE_ADDRESS,
                getDeviceAddress());
        return bluetoothValues;
    }
}
