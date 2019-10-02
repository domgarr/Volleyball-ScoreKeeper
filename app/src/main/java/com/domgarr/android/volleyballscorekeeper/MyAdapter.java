package com.domgarr.android.volleyballscorekeeper;

import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>  {
    private final String TAG = MyAdapter.this.getClass().getSimpleName();

    private Context mBtPeripheralSearchContext;
    private static ArrayList<String> mDataset;
    private static HashMap<String, ScanResult> mScanResults;

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView deviceName;
        private TextView connected;
        private LinearLayout recyclerLayout;


        public MyViewHolder(View v){
            super(v);
            deviceName = v.findViewById(R.id.device_name_textview);
            deviceName.setClickable(false);
            Log.d("MyAdapter", deviceName.isClickable() + ""  );

            connected = v.findViewById(R.id.connected_textview);
            connected.setClickable(false);
            Log.d("MyAdapter", connected.isClickable() + "" );

            recyclerLayout = v.findViewById(R.id.recycler_view_layout);
            recyclerLayout.setClickable(true);
        }
    }

    public MyAdapter(Context btPeriphalSearchContext, ArrayList<String> myDataset, HashMap<String, ScanResult> scanResults){
        mBtPeripheralSearchContext = btPeriphalSearchContext;
        mDataset = myDataset;
        mScanResults = scanResults;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view, viewGroup,false);

            MyViewHolder vh = new MyViewHolder( v);
            return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder myViewHolder, int position) {

        myViewHolder.recyclerLayout.setActivated(true);
        myViewHolder.recyclerLayout.setOnClickListener(listener);
        myViewHolder.deviceName.setText(mDataset.get(position));
        myViewHolder.connected.setText(mDataset.get(position));

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.d("MyAdapter", "Entering onClick method.");

            LinearLayout ll = (LinearLayout) v;

            Log.d("MyAdapter:OnClick:ChildCount:", ll.getChildCount() + "" );
            String key = null;
            for(int i = 0; i < ll.getChildCount() ; i++){
                View childView = ll.getChildAt(i);
                Log.d("MyAdapter:OnClick", childView.getId() + "");
                if(childView.getId() == R.id.linear_layout_containing_device){
                    LinearLayout layoutContainingDevice = (LinearLayout) childView;
                    for(int j = 0; j < layoutContainingDevice.getChildCount() ; j++){
                        View childViewInsideDeviceLayout = layoutContainingDevice.getChildAt(j);
                        if(childViewInsideDeviceLayout.getId() == R.id.device_name_textview){
                            key = ((TextView)childViewInsideDeviceLayout).getText().toString();
                        }
                    }
                }
            }

            if(key != null) {
                ScanResult scanResult = mScanResults.get(key);
                Log.d("MyAdapter:ScanResult", scanResult.getDevice().toString());

                CentralDeviceBleService centralBleService = CentralDeviceBleService.getInstance();
                centralBleService.setDevice(scanResult.getDevice());


            }else{
                Log.d("MyAdapter", "KEY IS NULL");
            }
        }
    };
}
