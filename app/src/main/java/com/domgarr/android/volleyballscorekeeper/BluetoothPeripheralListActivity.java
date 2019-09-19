package com.domgarr.android.volleyballscorekeeper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class BluetoothPeripheralListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private HashMap<String,ScanResult> scanResults;
    private BluetoothLeScanner scanner;
    private Handler handler = new Handler();

    private ProgressBar progressBar;

    private final int SCAN_PERIOD = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_peripheral_list);

        recyclerView = findViewById(R.id.my_recycler_view);
        progressBar = findViewById(R.id.progressBar);

        //This will improve performance if the content layout size does not differ
        recyclerView.setHasFixedSize(true);

        //Use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        BluetoothAdapter adapter = initBluetoothAdapter();
        scanner = adapter.getBluetoothLeScanner();


        scanResults = new HashMap<>();

        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                scanner.stopScan(scanCallBack);
                ArrayList<String> deviceNames = new ArrayList<>();

                Iterator scanResultsIterator = scanResults.entrySet().iterator();

                while(scanResultsIterator.hasNext()){
                    Map.Entry pair = (Map.Entry) scanResultsIterator.next();
                    deviceNames.add( (String) pair.getKey() );
                }

                progressBar.setVisibility(View.INVISIBLE);

                mAdapter = new MyAdapter(deviceNames);
                recyclerView.setAdapter(mAdapter);
            }
        }, SCAN_PERIOD);

        scanner.startScan(scanCallBack);
    }

    private ScanCallback scanCallBack = new ScanCallback(){
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            String deviceName = result.getDevice().getName();
            if(deviceName != null && !scanResults.containsKey(deviceName)) {
                scanResults.put(deviceName, result);
                Log.d("RE", result.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };



    public BluetoothAdapter initBluetoothAdapter(){
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        return bluetoothManager.getAdapter();
    }
}
