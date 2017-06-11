package com.qhzm123gmail.iot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Bluetooth bluetooth = null;
    private static final int BLUETOOTH_OFF = 100;
    private static final int REQUEST_DEVICE_SCAN = 101;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (bluetooth == null) {
            bluetooth = new Bluetooth(this, handler);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BLUETOOTH_OFF) {
            if (resultCode == Activity.RESULT_OK) {
                bluetooth.ScanDevice();
            }
            else {
                Toast.makeText(this, "블루투스를 사용할 수 없습니다.",
                        Toast.LENGTH_SHORT).show();
                ActivityCompat.finishAffinity(this);
            }
        }
        else if (requestCode == REQUEST_DEVICE_SCAN) {
            if (resultCode == Activity.RESULT_OK) {

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_developer) {
            Intent intent = new Intent(this, SettingDeveloper.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.menu_bluetooth) {
            bluetooth = null;
            bluetooth = new Bluetooth(this, handler);
            bluetooth.ScanDevice();
        }

        return super.onOptionsItemSelected(item);
    }


}
