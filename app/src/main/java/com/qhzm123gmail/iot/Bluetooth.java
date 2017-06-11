package com.qhzm123gmail.iot;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by qhzm1 on 2017-06-11.
 */

public class Bluetooth {
    private static final String TAG = "BlutoothClass";
    private static final int BLUETOOTH_OFF = 100;
    private static final int REQUEST_DEVICE_SCAN = 101;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    private int mState;

    private static final int STATE_NONE = 0;
    private static final int STATE_LISTEN = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_CONNECTED = 3;

    private BluetoothAdapter bluetoothAdapter;

    private Activity mainActivty;
    private Handler handler;

    public Bluetooth(Activity activity, Handler han) {
        mainActivty = activity;
        handler = han;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(mainActivty, "@string/bluetooth_null", Toast.LENGTH_SHORT).show();
            ActivityCompat.finishAffinity(mainActivty);
        }
        else {
            Toast.makeText(mainActivty, "Hello", Toast.LENGTH_SHORT).show();
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent intend = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mainActivty.startActivityForResult(intend, BLUETOOTH_OFF);
        }
    }

    public void ScanDevice() {
        Intent scanIntent = new Intent(mainActivty, DeviceListActivity.class);
        mainActivty.startActivityForResult(scanIntent, REQUEST_DEVICE_SCAN);
    }

    public void getDeviceInfo(Intent data) {
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        connect(device);
    }

    private synchronized void setState(int state) {
        mState = state;
    }

    private synchronized int getState() {
        return mState;
    }

    public synchronized void start() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    public synchronized void connect(BluetoothDevice device) {
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
            if (mConnectedThread != null) {
                mConnectedThread.cancel();
                mConnectedThread = null;
            }

            mConnectThread = new ConnectThread(device);
            setState(STATE_CONNECTING);
        }
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_NONE);
    }

    public void wirte(byte[] out) {
        ConnectedThread connectedThread;
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return;
            connectedThread = mConnectedThread;
        }
    }

    private void connectionFailed() {
        setState(STATE_LISTEN);
    }

    private void connectionLost() {
        setState(STATE_LISTEN);
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mSocket;
        private final BluetoothDevice mDevice;

        public ConnectThread(BluetoothDevice device) {
            mDevice = device;
            BluetoothSocket temp = null;

            try {
                temp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create failed", e);
            }
            mSocket = temp;
        }

            public void run() {
                setName("ConnectThread");

            bluetoothAdapter.cancelDiscovery();

            try {
                mSocket.connect();
            } catch (IOException e) {
                connectionFailed();

                try {
                    mSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "소켓을 닫을 수가 없습니다.");
               }
               Bluetooth.this.start();
                return;
            }
            synchronized(Bluetooth.this) {
                    mConnectThread = null;
            }
            connected(mSocket, mDevice);
        }
        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "소켓을 닫을 수가 없습니다.", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket socket) {
            mSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();
            } catch(IOException e) {
                Log.e(TAG, "temp 소켓이 생성되지 않았습니다.", e);
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while(true) {
                try {
                    bytes = inputStream.read(buffer);
                } catch (IOException e) {
                    Log.e(TAG, "연결되지 않았습니다.", e);
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                outputStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "쓰기 예외 발생", e);
            }
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch(IOException e) {
                Log.e(TAG, "소켓을 닫을 수 없습니다.", e);
            }
        }
    }




}
