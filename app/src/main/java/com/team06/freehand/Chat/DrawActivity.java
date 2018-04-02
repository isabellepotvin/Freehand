package com.team06.freehand.Chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team06.freehand.Dialogs.BrushSettingsDialog;
import com.team06.freehand.R;
import com.team06.freehand.Share.NextActivity;
import com.team06.freehand.Utils.FirebaseMethods;
import com.xw.repo.BubbleSeekBar;

import java.util.UUID;


public class DrawActivity extends AppCompatActivity implements OnClickListener, BrushSettingsDialog.brushSettingsListener {

    private static final String TAG = "DrawActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    //widgets
    private DrawingView drawView;
    private ImageButton currPaint, drawBtn, eraseBtn, settingsBtn, newBtn, saveBtn, closeBtn, sendBtn;

    //vars
    private int drawingCount = 0;
    private String chatID;
    private String otherUserID;


    @Override
    public void updatedBrushSettings(float brushSize) {
        drawView.setBrushSize(brushSize);
        drawView.setLastBrushSize(brushSize);
    }
    BrushSettingsDialog.brushSettingsListener mBrushSettingsListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        mFirebaseMethods = new FirebaseMethods(DrawActivity.this);

        setupFirebaseAuth();
        getIntentExtras();

        drawView = (DrawingView) findViewById(R.id.draw_view);
        Log.d(TAG, "onCreate: draw view: " + drawView);
        drawView.setDrawingCacheEnabled(true);


        //sets initial brush size
        drawView.setBrushSize(20);

        //draw button
        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        drawBtn.setSelected(true);

        //erase button
        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);

        //settings button
        settingsBtn = (ImageButton)findViewById(R.id.settings_btn);
        settingsBtn.setOnClickListener(this);

        //new drawing button
        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);

        //save drawing button
        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        //close button
        closeBtn = (ImageButton) findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(this);
        drawView.setupCloseBtn(closeBtn);

        //send button
        sendBtn = (ImageButton) findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(this);


        //brush colour
        BubbleSeekBar brushColour = (BubbleSeekBar)findViewById(R.id.brush_colour);
        //brushColour.setProgress(drawView.getLastBrushSize());

        brushColour.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {

                //drawView.setErase(false);

                //brush colour

                //float hsv[] = new float[] {progressFloat, 1.0f, 0.5f};
                //int hsl = Color.HSVToColor(hsv);

                String colourHex = String.format("%06x", progress); //converts int to hex colour

                //brush opacity
                String opacityHex = "FF";

                //sets brush colour and opacity
                String colour = "#" + opacityHex + colourHex;

                Log.d(TAG, "onProgressChanged: colour: " + colour);

                drawView.setColor(colour);

            }
            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) { }
            @Override
            public void getProgressOnFinally(int progress, float progressFloat) { }
        });
    }

    //creates dialog when user clicks brush, eraser, brush settings, save or new drawing buttons
    @Override
    public void onClick(View view){

        //ImageButton imgView = (ImageButton) view;
        //imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        //BRUSH SETTINGS
        if(view.getId()==R.id.settings_btn){


            mBrushSettingsListener = new BrushSettingsDialog.brushSettingsListener() {
                @Override
                public void updatedBrushSettings(float brushSize) {
                    drawView.setBrushSize(brushSize);
                    drawView.setLastBrushSize(brushSize);
                }
            };

            BrushSettingsDialog dialog = new BrushSettingsDialog(DrawActivity.this, drawView.getLastBrushSize(), mBrushSettingsListener);

            WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
            lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            dialog.show();
            dialog.getWindow().setAttributes(lWindowParams);

        }


        //DRAW
        else if(view.getId()==R.id.draw_btn) {
            drawView.setErase(false);
            drawBtn.setSelected(true);
            eraseBtn.setSelected(false);
        }


        //ERASER
        else if(view.getId()==R.id.erase_btn) {
            drawView.setErase(true);
            eraseBtn.setSelected(true);
            drawBtn.setSelected(false);
        }


        //NEW DRAWING
        else if(view.getId()==R.id.new_btn){
            //alerts user when they click new drawing button
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New Drawing");
            newDialog.setMessage("Start new drawing? You will lose the current drawing.");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        }


        //SAVE DRAWING
        else if(view.getId()==R.id.save_btn){
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save Drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

                //saves image
                public void onClick(DialogInterface dialog, int which){
                    drawView.setDrawingCacheEnabled(true);

                    String imgSaved = MediaStore.Images.Media.insertImage( getContentResolver(), drawView.getDrawingCache(),
                            UUID.randomUUID().toString()+".png", "drawing");

                    //Log.d(TAG, "onClick: img: " + drawView.getDrawingCache());

                    if(imgSaved!=null){
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    else{
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }

                    drawView.destroyDrawingCache();
                }

            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }


        //CLOSE BUTTON
        else if(view.getId() == R.id.close_btn){

            //alerts user when they close the drawing space
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("Exit");
            newDialog.setMessage("Are you sure you want to close the drawing space? You will lose the current drawing.");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    finish();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        }


        //SEND BUTTON
        else if (view.getId() == R.id.send_btn){

            Log.d(TAG, "onClick: sending drawing: bm: " + drawView.getDrawingCache());

            //if(drawView.getDrawingCache() != null) {
                mFirebaseMethods.uploadNewMessage(drawView.getDrawingCache(), drawingCount, chatID, otherUserID);
           // }
            //else{
              //  Toast.makeText(this, "You did not draw anything", Toast.LENGTH_SHORT).show();
            //}
        }


    }

    private void getIntentExtras (){
        Log.d(TAG, "getIntentExtras: getting extras");
        Bundle extras = getIntent().getExtras();
        chatID = extras.getString(getString(R.string.chat_id));
        otherUserID = extras.getString(getString(R.string.other_user_id));

        Log.d(TAG, "getIntentExtras: chatID: " + chatID);
        Log.d(TAG, "getIntentExtras: personName: " + otherUserID);
    }



     /*
   - - - - - - - - - - - - - - - - - - - - - - - - Firebase - - - - - - - - - - - - - - - - - - - - - - - -
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        //allows us to read or write from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                drawingCount = mFirebaseMethods.getImageCount(dataSnapshot, chatID); //gets the image count
                Log.d(TAG, "onDataChange: image count: " + drawingCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
