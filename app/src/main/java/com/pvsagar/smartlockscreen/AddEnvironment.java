package com.pvsagar.smartlockscreen;

import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.pvsagar.smartlockscreen.applogic_objects.BluetoothEnvironmentVariable;

import java.util.ArrayList;


public class AddEnvironment extends ActionBarActivity {

    private static final String LOG_TAG = AddEnvironment.class.getSimpleName();
    private static ArrayAdapter<String> bluetoothListAdapter;
    private static ArrayList<BluetoothDevice> bluetoothDevices;
    private static ArrayList<BluetoothDevice> mSelectedBluetoothDevices;
    private static ArrayList<Integer> mSelectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_environment);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_environment, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == BluetoothEnvironmentVariable.REQUEST_BLUETOOTH_ENABLE){
            /* Bluetooth request */
            CheckBox enableBluetoothCheckBox = (CheckBox)findViewById(R.id.checkbox_enable_bluetooth);
            TextView selectBluetoothDevicesTextView = (TextView)findViewById(R.id.text_view_bluetooth_devices_select);

            // Checking whether bluetooth is enabled or not
            if(resultCode == RESULT_OK){
                //Bluetooth Enabled
                //Get the list of paired devices, populate the list, set the adapter
                Toast.makeText(getBaseContext(),"Bluetooth switched on",Toast.LENGTH_SHORT).show();
                enableBluetoothCheckBox.setChecked(true);
                ArrayList<BluetoothDevice> bluetoothDevices = new BluetoothEnvironmentVariable().getPairedBluetoothDevices(this);
                ArrayList<String> deviceNamesArrayList = new ArrayList<String>();
                if(bluetoothDevices != null) {
                    //Populate the String list and Set the adapter for the list view
                    for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
                        deviceNamesArrayList.add(bluetoothDevice.getName());
                    }
                    AddEnvironment.bluetoothDevices = bluetoothDevices;
                    selectBluetoothDevicesTextView.setEnabled(true);
                }

            }
            else{
                //Bluetooth not enabled
                //Un check the checkbox, disable the list
                Toast.makeText(getBaseContext(),"Unable to switch on Bluetooth",Toast.LENGTH_SHORT).show();
                enableBluetoothCheckBox.setChecked(false);
                AddEnvironment.bluetoothDevices = new ArrayList<BluetoothDevice>();
                selectBluetoothDevicesTextView.setEnabled(false);

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private CheckBox enableBluetoothCheckBox;
        private TextView selectBluetoothDevicesTextView;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_environment, container, false);
            enableBluetoothCheckBox = (CheckBox)rootView.findViewById(R.id.checkbox_enable_bluetooth);
            selectBluetoothDevicesTextView = (TextView)rootView.findViewById(R.id.text_view_bluetooth_devices_select);

            /* Initialization */
            setUpBluetoothElements();

            return rootView;
        }

        public void setUpBluetoothElements(){

            /* Initialization */
            bluetoothDevices = new ArrayList<BluetoothDevice>();
            mSelectedItems = new ArrayList<Integer>();
            mSelectedBluetoothDevices = new ArrayList<BluetoothDevice>();
            selectBluetoothDevicesTextView.setEnabled(false);

            /* CheckBox CheckedChange Listener */
            enableBluetoothCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ArrayList<BluetoothDevice> bluetoothDevices = new BluetoothEnvironmentVariable().getPairedBluetoothDevices(getActivity());
                        ArrayList<String> deviceNamesArrayList = new ArrayList<String>();
                        if (bluetoothDevices != null) {
                            //Populate the String list and Set the adapter for the list view
                            for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
                                deviceNamesArrayList.add(bluetoothDevice.getName());
                            }
                            AddEnvironment.bluetoothDevices = bluetoothDevices;
                            selectBluetoothDevicesTextView.setEnabled(true);

                        } else {
                            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            if (mBluetoothAdapter == null) {
                                enableBluetoothCheckBox.setChecked(false);
                                //Disable the list
                                AddEnvironment.bluetoothDevices = new ArrayList<BluetoothDevice>();
                                selectBluetoothDevicesTextView.setEnabled(false);
                            }
                        }

                    } else {
                        //Disable the list
                        AddEnvironment.bluetoothDevices = new ArrayList<BluetoothDevice>();
                        selectBluetoothDevicesTextView.setEnabled(false);
                    }
                }
            });

            /* Text View onClickListener */
            selectBluetoothDevicesTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] bluetoothDevicesName = new String[bluetoothDevices.size()];

                    for(int i=0;i<bluetoothDevices.size();i++){
                        bluetoothDevicesName[i] = bluetoothDevices.get(i).getName();
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.dialog_pick_bluetooth_devices);
                    //Set boolean array for previously checked items
                    boolean[] checkedItems = new boolean[bluetoothDevices.size()];
                    for (Integer mSelectedItem : mSelectedItems) {
                        checkedItems[mSelectedItem] = true;
                    }

                    builder.setMultiChoiceItems(bluetoothDevicesName,checkedItems,new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if(isChecked){
                                mSelectedItems.add(which);
                            }
                            else{
                                mSelectedItems.remove(Integer.valueOf(which));
                            }
                        }
                    });
                    builder.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (Integer mSelectedItem : mSelectedItems) {
                                mSelectedBluetoothDevices.add(bluetoothDevices.get(mSelectedItem));
                            }
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }
    }
}