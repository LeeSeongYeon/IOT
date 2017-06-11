package com.qhzm123gmail.iot;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by qhzm1 on 2017-06-11.
 */

public class Bluetooth {
    private static final String TAG = "BlutoothClass";
    private static final int BLUETOOTH_OFF = 100;
    private static final int REQUEST_DEVICE_SCAN = 101;

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



}
