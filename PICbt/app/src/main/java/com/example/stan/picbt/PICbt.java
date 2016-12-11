package com.example.stan.picbt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Set;



public class PICbt extends AppCompatActivity {
    private static final String TAG = "PICbt";
    private final static int REQUEST_ENABLE_BT = 1;
    ListView listView;
    TextView btStatus;
    private String macId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picbt);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                btStatus = (TextView)findViewById(R.id.text1);
                btStatus.setText(R.string.text_bt_en);
            }
            else if (resultCode == RESULT_CANCELED) {
                btStatus = (TextView)findViewById(R.id.text1);
                btStatus.setText(R.string.text_bt_dis);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first

        btStatus = (TextView)findViewById(R.id.text1);
        btStatus.setText(R.string.text_bt);
        listView = (ListView) findViewById(R.id.list1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1);



        // The activity is either being restarted or started for the first time
        // so this is where we should make sure that Bluetooth is enabled
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.v(TAG, "Does not support Bluetooth");
            btStatus.setText(R.string.text_bt_no);
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        }
        else {
            btStatus.setText(R.string.text_bt_en);
        }

        // check for paired bluetooth devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                adapter.add(device.getName() + "\n" + device.getAddress());
                macId = device.getAddress();
            }
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.v(TAG, "clicked on an item in the paired list..");
                    Intent intent = new Intent(view.getContext(), Device_Connect.class);
                    intent.putExtra("MAC_ID", macId);
                    startActivity(intent);

                }
            });
        }
        // if no devices found
        else {
            adapter.add("no paired devices");
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.v(TAG, "clicked on an item in the no-paired list..");
                    Intent intentOpenBluetoothSettings = new Intent();
                    intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivity(intentOpenBluetoothSettings);

                }
            });
        }
    }


}
