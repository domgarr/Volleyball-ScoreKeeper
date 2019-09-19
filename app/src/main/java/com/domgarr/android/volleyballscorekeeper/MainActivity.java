package com.domgarr.android.volleyballscorekeeper;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private DialogFragment vNewGameDialogMessage;

    private TextView vRedScoreTextView;
    private TextView vBlueScoreTextView;

    private int mRedScore;
    private int mBlueScore;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isBluetoothLeSupported();
        //mBluetoothAdapter = initBluetoothAdapter();

        //Check if Bluetooth is enabled. If it's not, ask the user to turn on bluetooth.
        if(!isBluetoothEnabled(mBluetoothAdapter)){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
            if(this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton("Ok", null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Dialog class prompt user if they really want to reset the game.
        vNewGameDialogMessage = new MainActivity.NewGameDialogMessage();

        vRedScoreTextView = (TextView) findViewById(R.id.red_text_view);
        vBlueScoreTextView = (TextView) findViewById(R.id.blue_text_view);

        mRedScore = 0;
        mBlueScore = 0;

        render();
    }

    private void render(){
        if(!won()) {
            vRedScoreTextView.setText("" + mRedScore);
            vBlueScoreTextView.setText("" + mBlueScore);
        }
    }

    //Start of click events.
    public void incrementBlueScore(View view){
        mBlueScore++;
        render();
    }

    public void incrementRedScore(View view){
        mRedScore++;
        render();
    }

    public void reset(View v){
        vNewGameDialogMessage.show(getSupportFragmentManager(), "reset");
    }

    public void decrementBlueScore(View view){
        if(mBlueScore > 0) {
            mBlueScore--;
        }
        render();
    }

    public void decrementRedScore(View view){
        if(mRedScore > 0) {
            mRedScore--;
        }
        render();
    }

    private boolean won(){
        if(redWon()){
            //Adjust left and right padding to accommodate the Character concatenation.
            addPaddingForCharacterConcatenation();
            appendEndingGameText(Team.RED);

            return true;
        }else if(blueWon()){
            addPaddingForCharacterConcatenation();
            appendEndingGameText(Team.BLUE);

            return true;
        }
        return false;
    }

    private boolean redWon(){
        return mRedScore >= 25 && mRedScore >= mBlueScore + 2;
    }

    private boolean blueWon(){
        return mBlueScore >= 25 && mBlueScore >= mRedScore + 2;
    }

    private void addPaddingForCharacterConcatenation(){
        vRedScoreTextView.setPadding(8,32,8,32);
        vBlueScoreTextView.setPadding(8,32,8,32);
    }

    private void appendEndingGameText(Team winner) {
        if(winner == Team.RED){
            vRedScoreTextView.setText( "W " + mRedScore);
            vBlueScoreTextView.setText("L " + mBlueScore);
        }else{
            vRedScoreTextView.setText( "L " + mRedScore);
            vBlueScoreTextView.setText("W " + mBlueScore);
        }
    }

    public void connect(View view){
        Intent intent = new Intent(this, BluetoothPeripheralListActivity.class);
        startActivity(intent);
    }

    public void resetScore(){
        mRedScore = 0;
        mBlueScore = 0;
        //Return padding to original values.
        vBlueScoreTextView.setPadding(96,32,96,32);
        vRedScoreTextView.setPadding(96,32,96,32);

        render();
    }

    private enum Team {
        RED, BLUE
    }

     //A nested class that prompts the user before resetting.
      public static class NewGameDialogMessage extends DialogFragment {

          @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure you want to reset?")
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Nothing to do here.
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity callingActivity = (MainActivity) getActivity();
                            callingActivity.resetScore();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    public boolean isBluetoothLeSupported(){
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        return true;
    }



    public boolean isBluetoothEnabled(BluetoothAdapter bluetoothAdapter){
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }





}
