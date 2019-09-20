package com.domgarr.android.volleyballscorekeeper;

import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>  {
    private static ArrayList<String> mDataset;
    private static HashMap<String, ScanResult> mScanResults;

    static View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String key = ((TextView)v).getText().toString();
            ScanResult scanResult= mScanResults.get(key);
            MainActivity.device = scanResult.getDevice();
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView textview;

        public MyViewHolder(TextView v){
            super(v);
            textview = v;
            textview.setOnClickListener(listener);
        }
    }

    public MyAdapter(ArrayList<String> myDataset,  HashMap<String, ScanResult> scanResults){
        mDataset = myDataset;
        mScanResults = scanResults;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        TextView v = (TextView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view, viewGroup,false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder myViewHolder, int position) {
        myViewHolder.textview.setText(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
