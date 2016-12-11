package com.example.stan.picbt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by stan on 2.12.2016.
 */

class ConnectThread extends Thread {
    private static final String TAG = "ConnectThread";
    private String readBytesStr;
    private BluetoothSocket mmSocket;
    private BluetoothAdapter mBluetoothAdapter;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private final static int MESSAGE_READ = 9999;
    private Handler myHandler;


    public ConnectThread(BluetoothSocket socket, Handler handler) {
        //Log.v(TAG, "inside the connectthread function");
        myHandler = handler;
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        //Log.v(TAG, "inside the run function");
        byte[] buffer = new byte[2048];  // buffer store for the stream
        int bytes; // bytes returned from read()
        readBytesStr = "";

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                //Log.v(TAG, "instream: " + bytes);
                if(readBytesStr.length() == 0)
                    readBytesStr = new String(buffer, 0, bytes, "ASCII");
                else
                    readBytesStr = readBytesStr + new String(buffer, 0, bytes, "ASCII");


            } catch (IOException e) {
                Log.v(TAG, "inputstream error: " + e.getMessage());
                break;
            }
            // Send the obtained string to the UI activity
            myHandler.obtainMessage(MESSAGE_READ, 0, 0, readBytesStr).sendToTarget();
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        //Log.v(TAG, "inside write function");
        try {
            mmOutStream.write(bytes);
            mmOutStream.flush();
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void reset() {
        Log.v(TAG, "inside reset function");
        //try {
            //mmInStream.mark(2048);
            //mmInStream.reset();
        //} catch (IOException e) { }
        readBytesStr = "";
    }
}