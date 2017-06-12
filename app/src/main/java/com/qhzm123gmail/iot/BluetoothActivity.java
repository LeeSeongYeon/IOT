package com.qhzm123gmail.iot;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by qhzm1 on 2017-06-12.
 */

public class BluetoothActivity extends Fragment{
    private static final String TAG = "BluetoothActivity";

    private Switch light1, light2, light3, motor;
    private String mConnectDeviceName = null;

    private ArrayAdapter<String> mCommunicationArrayAdapter;
    private StringBuffer mOutStringBuffer;
    private BluetoothAdapter mBluetoothAdapter = null;

    private Bluetooth mBluetooth = null;

    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Activity activity = getActivity();
            Toast.makeText(activity, "블루투스를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            activity.finish();
        }
    }

    public void onStart() {
        super.onStart();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
        else if (mBluetooth == null) {
            mBluetooth.start();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        light1 = (Switch) view.findViewById(R.id.switch_light1);
        light2 = (Switch) view.findViewById(R.id.switch_light2);
        light3 = (Switch) view.findViewById(R.id.switch_light3);

    }
}
