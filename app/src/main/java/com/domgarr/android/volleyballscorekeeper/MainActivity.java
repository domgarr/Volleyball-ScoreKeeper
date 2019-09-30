package com.domgarr.android.volleyballscorekeeper;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private DialogFragment vNewGameDialogMessage;

    private TextView vRedScoreTextView;
    private TextView vBlueScoreTextView;
    private ImageButton vConnectToBluetoothImageView;
    private ImageButton vResetScoreImageView;

    private int mRedScore;
    private int mBlueScore;

    private CentralDeviceBleService ble;

    public final int SET_DEVICE_CODE = 2;
    public final static String REQ_CODE_DEVICE = "ScoreboardDevice";

    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private enum Team {
        RED, BLUE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*This dialog is needed for Bluetooth functionality. We must ask for permission from user. Adding
            the permission to Android Manifest wasn't enough.
        */
            if(this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access.");
                builder.setMessage("Please grant location access so this app can detect bluetooth advertisements.");
                builder.setPositiveButton("Okay", null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }

        //Dialog class prompt user if they really want to reset the game.
        vNewGameDialogMessage = new MainActivity.NewGameDialogMessage();

        vRedScoreTextView =  findViewById(R.id.red_text_view);
        vBlueScoreTextView = findViewById(R.id.blue_text_view);
        vConnectToBluetoothImageView = findViewById(R.id.connect_bluetooth_image_view);
        vResetScoreImageView = findViewById(R.id.reset_score_image_view);

        mRedScore = 0;
        mBlueScore = 0;

        render();

        vConnectToBluetoothImageView.setOnClickListener(bluetoothListListener);
        vResetScoreImageView.setOnClickListener(resetScoreListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ble.getInstance().getDevice() != null) {
            ble.getInstance().connectToGattService(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ble.getInstance().close();
    }

    private void render(){
        if(!won()) {
            vRedScoreTextView.setText("" +mRedScore);
            vBlueScoreTextView.setText("" + mBlueScore);
        }
    }

    //Start of click events.
    public void incrementBlueScore(View view){
        mBlueScore++;
        render();

        ble.getInstance().writeToBlueScoreCharacteristic(mBlueScore);
    }

    public void incrementRedScore(View view){
        mRedScore++;
        render();

        ble.getInstance().writeToRedScoreCharacteristic(mRedScore);
    }

    public void reset(View v){
        vNewGameDialogMessage.show(getSupportFragmentManager(), "reset");
    }

    public void decrementBlueScore(View view){
        if(mBlueScore > 0) {
            mBlueScore--;
        }
        render();

        ble.getInstance().writeToBlueScoreCharacteristic(mBlueScore);
    }

    public void decrementRedScore(View view){
        if(mRedScore > 0) {
            mRedScore--;
        }
        render();
        ble.getInstance().writeToRedScoreCharacteristic(mRedScore);
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

    View.OnClickListener bluetoothListListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, BluetoothPeripheralListActivity.class);
            startActivity(intent);
        }
    };

    public void resetScore(){
        //Reset score variables
        mRedScore = 0;
        mBlueScore = 0;

        //Update Scoreboard service
        ble.getInstance().writeToRedScoreCharacteristic(mRedScore);
        ble.getInstance().writeToBlueScoreCharacteristic(mBlueScore);

        render();
    }

    View.OnClickListener resetScoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new NewGameDialogMessage();
        }
    };

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
}
