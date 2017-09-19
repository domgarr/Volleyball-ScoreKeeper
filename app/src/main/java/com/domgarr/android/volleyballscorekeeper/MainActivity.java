package com.domgarr.android.volleyballscorekeeper;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private DialogFragment newGameDialogMessage;

    private TextView redScoreTextView;
    private TextView blueScoreTextView;

    private int redScore;
    private int blueScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Dialog class prompt user if they really want to reset the game.
        newGameDialogMessage = new MainActivity.NewGameDialogMessage();

        redScoreTextView = (TextView) findViewById(R.id.red_text_view);
        blueScoreTextView = (TextView) findViewById(R.id.blue_text_view);

        redScore = 0;
        blueScore = 0;

        render();
    }

    private void render(){
        if(!won()) {
            redScoreTextView.setText("" + redScore);
            blueScoreTextView.setText("" + blueScore);
        }
    }

    //Start of click events.
    public void incrementBlueScore(View view){
        blueScore++;
        render();
    }

    public void incrementRedScore(View view){
        redScore++;
        render();
    }

    public void reset(View v){
        newGameDialogMessage.show(getSupportFragmentManager(), "reset");
    }

    public void decrementBlueScore(View view){
        if(blueScore > 0) {
            blueScore--;
        }
        render();
    }

    public void decrementRedScore(View view){
        if(redScore > 0) {
            redScore--;
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
        return redScore >= 25 && redScore >= blueScore + 2;
    }

    private boolean blueWon(){
        return blueScore >= 25 && blueScore >= redScore + 2;
    }

    private void addPaddingForCharacterConcatenation(){
        redScoreTextView.setPadding(8,32,8,32);
        blueScoreTextView.setPadding(8,32,8,32);
    }

    private void appendEndingGameText(Team winner) {
        if(winner == Team.RED){
            redScoreTextView.setText( "W " + redScore);
            blueScoreTextView.setText("L " + blueScore);
        }else{
            redScoreTextView.setText( "L " + redScore);
            blueScoreTextView.setText("W " + blueScore);
        }
    }

    public void resetScore(){
        redScore = 0;
        blueScore = 0;
        //Return padding to original values.
        blueScoreTextView.setPadding(96,32,96,32);
        redScoreTextView.setPadding(96,32,96,32);

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
}
