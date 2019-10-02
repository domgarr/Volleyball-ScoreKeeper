package com.domgarr.android.volleyballscorekeeper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class BtPeripheralSearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView vBackButtonImageView;
    private ImageView vDisconnectImageView;


    private HashMap<String,ScanResult> scanResults;
    private BluetoothLeScanner scanner;
    private Handler handler = new Handler();

    private ProgressBar progressBar;

    private final int SCAN_PERIOD = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_peripheral_list);

        setTitle("Connections");

        recyclerView = findViewById(R.id.my_recycler_view);
        progressBar = findViewById(R.id.progressBar);
        vBackButtonImageView = findViewById(R.id.back_arrow_image_button);
        vDisconnectImageView = findViewById(R.id.disconnect_bluetooth_image_button);

        vBackButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        vDisconnectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CentralDeviceBleService.getInstance().getConnectionState() == CentralDeviceBleService.STATE_CONNECTED) {
                    CentralDeviceBleService.getInstance().close();
                    MainActivity.vConnectToBluetoothImageView.setColorFilter(ContextCompat.getColor(BtPeripheralSearchActivity.this, R.color.blutooth_disconnected));
                    Toast.makeText(BtPeripheralSearchActivity.this, "Disconnecting from Scoreboard Service", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(BtPeripheralSearchActivity.this, "Not connected to a Scoreboard Service", Toast.LENGTH_SHORT).show();

                }
            }
        });

        //This will improve performance if the content layout size does not differ
        recyclerView.setHasFixedSize(true);

        //Use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        BluetoothAdapter adapter = initBluetoothAdapter();
        scanner = adapter.getBluetoothLeScanner();

        scanResults = new HashMap<>();

        //Display list of Bluetooth Devices if found, generate a Toast to notify user.
        handler.postDelayed(scanResultCallback, SCAN_PERIOD);

        scanner.startScan(scanCallBack);
    }

    float prevY = -1;
    /*
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(prevY != -1){
            if(prevY < ev.getY()){
                Log.d("Swipe Down Test", "REFRESH");
            }
        }

        if(ev.getAction() == MotionEvent.ACTION_MOVE){
            prevY = ev.getY();
            Log.d("Swipe Down Test", prevY + "");
        }else{
            prevY = -1;
        }

        return false;
    }
    */

    private ScanCallback scanCallBack = new ScanCallback(){
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            String deviceName = result.getDevice().getName();
            if(deviceName != null && !deviceName.isEmpty() && !scanResults.containsKey(deviceName)) {
                scanResults.put(deviceName, result);
                Log.d("scanCallBack", result.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    Runnable scanResultCallback = new Runnable() {
        @Override
        public void run() {
            scanner.stopScan(scanCallBack);
            toggleProgressBar();

            ArrayList<String> deviceNames = new ArrayList<>();

            Log.d("SCAN RESULTS", scanResults.toString());
            if(scanResults.isEmpty()){
                Log.d("SCAN RESULTS", "Size" + scanResults.size() );
                Toast.makeText(BtPeripheralSearchActivity.this, "No devices found.", Toast.LENGTH_SHORT).show();

                return;
            }

            Iterator scanResultsIterator = scanResults.entrySet().iterator();

            while(scanResultsIterator.hasNext()){
                Map.Entry pair = (Map.Entry) scanResultsIterator.next();
                deviceNames.add( (String) pair.getKey() );
            }

            //Init Adapter and set to Recycler view;
            mAdapter = new MyAdapter(getApplicationContext(), deviceNames, scanResults);
            recyclerView.setAdapter(mAdapter);
        }
    };

    private void toggleProgressBar(){
       if(progressBar.getVisibility() == View.GONE) {
           progressBar.setVisibility(View.VISIBLE);
       } else {
           progressBar.setVisibility(View.GONE);
       }
       }

    public BluetoothAdapter initBluetoothAdapter(){
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        return bluetoothManager.getAdapter();
    }



}
