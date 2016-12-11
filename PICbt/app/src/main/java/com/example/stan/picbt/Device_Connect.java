package com.example.stan.picbt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static android.R.attr.id;
import static com.example.stan.picbt.R.color.colorPrimary;
import static com.example.stan.picbt.R.layout.device_connect;
import static java.lang.Integer.parseInt;

public class Device_Connect extends AppCompatActivity {

    private static final String TAG = "DeviceConnect";
    TextView status;
    int max, min, value, address;
    String transferring;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private String macId;
    private final static String uuid_str = "00001101-0000-1000-8000-00805F9B34FB";
    private UUID MY_UUID = UUID.nameUUIDFromBytes(uuid_str.getBytes());
    private Handler myHandler;
    private final static int MESSAGE_READ = 9999;
    private ConnectThread btThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(device_connect);
        //Log.v(TAG, "in onCreate");

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b!=null)
        {
            macId =(String) b.get("MAC_ID");
        }

        //Log.v(TAG, "Found device " + macId);
        status = (TextView)findViewById(R.id.textbtdisp);
        status.setText(macId);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macId);
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        UUID uuid = device.getUuids()[0].getUuid();
        //Log.v(TAG, "uuid: " + uuid);

        try {
            mmSocket = device.createRfcommSocketToServiceRecord(uuid);
            //mmSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);

        } catch (IOException e) {
            Log.v(TAG, "socket connection creation failed..: " + e.getMessage());
            //errorExit("Fatal Error", "In onCreate() and socket create failed: " + e.getMessage() + ".");
            throw new RuntimeException(e);
        }

        // Reuse existing handler if you don't
        // have to override the message processing
        //myHandler = getWindow().getDecorView().getHandler();
        part = "";
        myHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                /*
                case SOCKET_CONNECTED: {
                    mBluetoothConnection = (ConnectionThread) msg.obj;
                    if (!mServerMode)
                        mBluetoothConnection.write("this is a message".getBytes());
                    break;
                }
                case DATA_RECEIVED: {
                    data = (String) msg.obj;
                    tv.setText(data);
                    if (mServerMode)
                        mBluetoothConnection.write(data.getBytes());
                }
                */
                    case MESSAGE_READ: {
                        //Log.v(TAG, "inside the handler");

                        String temp =  (String)(msg.obj);
                        char[] tmp = temp.toCharArray();
                        Log.v(TAG, "message: " + temp);
                        if(temp.indexOf("err") > 0)
                        {
                            btThread.reset();
                            if(transferring != "")
                                transferData(transferring);
                        }
                        else
                        {
                            if(temp.indexOf("ok") > 0)
                            {
                                transferring = "";
                                btThread.reset();
                            }
                            else if(tmp[0] == 2 && tmp[temp.length()-1] == 3)
                            {
                                //Log.v(TAG, "inside if filter");
                                int start;
                                int end;
                                while(temp.length() > 3)
                                {
                                    start = 0;
                                    end = 0;
                                    start = temp.substring(start).indexOf("<");
                                    Log.v(TAG, "start: " + start + " temp: " + temp);
                                    end = temp.substring(end).indexOf("/>");
                                    if(end > 0)
                                    {
                                        String vars = temp.substring(start+1, end);
                                        int end2 = vars.indexOf(",");
                                        while (end2 > 0)
                                        {
                                            Log.v(TAG, "vars: " + vars);
                                            value = Integer.parseInt(vars.substring(0,end2));
                                            end2++;
                                            vars = vars.substring(end2,vars.length());
                                            end2 = vars.indexOf(",");
                                            max = Integer.parseInt(vars.substring(0,end2));
                                            end2++;
                                            vars = vars.substring(end2,vars.length());
                                            end2 = vars.indexOf(",");
                                            min = Integer.parseInt(vars.substring(0,end2));
                                            end2++;
                                            vars = vars.substring(end2,vars.length());
                                            address = Integer.parseInt(vars);
                                            end2 = 0;

                                            Log.v(TAG, "value: " + value + " max: " + max + " min: " + min + " address: " + address);

                                            createRows(value, max, min, address);
                                        }

                                        temp = temp.substring(end+2);
                                    }
                                }

                                btThread.reset();
                            }
                        }
                    }
                }
            }
        };
    }

    public void createRows(int value, int max, int min, int address) {
        Log.v(TAG, "creating row");

        TableLayout textLayout = (TableLayout)findViewById(R.id.table2);
        //textLayout.setLayoutParams(new LinearLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        //textLayout.setOrientation(TableLayout.HORIZONTAL);
        //
        TableRow tr1 = new TableRow(this);
        TableRow.LayoutParams lpar = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        lpar.gravity= Gravity.CENTER_HORIZONTAL;
        tr1.setLayoutParams(lpar);
        tr1.setId(address);
        textLayout.addView(tr1);

        TextView tmin = new TextView(this);
        tmin.setText(" " + min);

        TextView tmax = new TextView(this);
        tmax.setText(" " + max);

        TextView tval = new TextView(this);
        tval.setGravity(Gravity.CENTER);
        tval.setText(" " + value);
        tval.setId(address%0x100+1); // 1 added, otherwise a nullpointer exception occurs.

        tr1.addView(tmin);
        tr1.addView(tval);
        tr1.addView(tmax);

        TableRow tr2 = new TableRow(this);
        TableRow.LayoutParams lpar2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        tr2.setLayoutParams(lpar2);
        textLayout.addView(tr2);

        SeekBar seekBar = new SeekBar(this);
        seekBar.setMax(max);
        //seekBar.setMinimumWidth(400);
        ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
        thumb.setIntrinsicHeight(40);
        thumb.setIntrinsicWidth(30);

        seekBar.setThumb(thumb);
        seekBar.setProgress(value);
        seekBar.setVisibility(View.VISIBLE);
        //seekBar.setBackgroundColor(Color.BLUE);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        lp.span = 3;
        seekBar.setLayoutParams(lp);
        seekBar.setId(address%0x100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
                //System.out.println(".....111.......");
                TextView txt = (TextView)findViewById(arg0.getId()+1);
                int value = Integer.parseInt(txt.getText().toString().substring(1,txt.length()));
                int address = 0x7000+arg0.getId();
                Log.v(TAG, "id: " + (txt.getId()-1) + " value: " + value);
                transferData("<61,"+address+","+value+"/>");
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
                //System.out.println(".....222.......");
            }

            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                // TODO Auto-generated method stub
                //System.out.println(".....333......."+arg1);
                TextView txt = (TextView)findViewById(arg0.getId()+1);
                txt.setText(" " + arg1);

            }
        });
        tr2.addView(seekBar);

    }

    @Override
    protected void onStart() {
        super.onResume();
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            Log.v(TAG, "no connection..: " + connectException.getMessage());
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        btThread = new ConnectThread(mmSocket, myHandler);
        btThread.start();

        // send command to list all the variables.
        transferData("<50/>");
    }

    @Override
    protected void onStop() {
        super.onPause();
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }

    void transferData(String output)
    {
        transferring = output;
        byte[] tmp = output.getBytes();
        btThread.write(tmp);
    }
}
