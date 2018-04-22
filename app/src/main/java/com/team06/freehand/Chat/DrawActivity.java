package com.team06.freehand.Chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.team06.freehand.Share.ShareActivity;
import com.team06.freehand.Utils.FirebaseMethods;
import com.team06.freehand.Utils.Permissions;
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
    private ImageButton currPaint, drawBtn, eraseBtn, settingsBtn, newBtn, saveBtn, sendBtn;
    private ImageView closeBtn;

    //vars
    private int drawingCount = 0;
    private String chatID;
    private String otherUserID;

    //constants
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;




    @Override
    public void updatedBrushSettings(float brushSize, int opacity) {
        drawView.setBrushSize(brushSize);
        drawView.setLastBrushSize(brushSize);
        drawView.setOpacity(opacity);
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
        newBtn = (ImageButton)findViewById(R.id.colour_btn);
        newBtn.setOnClickListener(this);

        //save drawing button
        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        //close button
        closeBtn = (ImageView) findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(this);
        drawView.setupCloseBtn(closeBtn);

        //send button
        sendBtn = (ImageButton) findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(this);

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
                public void updatedBrushSettings(float brushSize, int opacity) {
                    drawView.setBrushSize(brushSize);
                    drawView.setLastBrushSize(brushSize);
                    drawView.setOpacity(opacity);
                }
            };

            BrushSettingsDialog dialog = new BrushSettingsDialog(DrawActivity.this, drawView.getLastBrushSize(), mBrushSettingsListener, drawView.getOpacity());

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            //WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
            //lWindowParams.width = displayMetrics.widthPixels - 20;
            //lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            dialog.show();
            //dialog.getWindow().setAttributes(lWindowParams);

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


//        //NEW DRAWING
//        else if(view.getId()==R.id.new_btn){
//            //alerts user when they click new drawing button
//            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
//            newDialog.setTitle("New Drawing");
//            newDialog.setMessage("Start new drawing? You will lose the current drawing.");
//            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
//                public void onClick(DialogInterface dialog, int which){
//                    drawView.startNew();
//                    dialog.dismiss();
//                }
//            });
//            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
//                public void onClick(DialogInterface dialog, int which){
//                    dialog.cancel();
//                }
//            });
//            newDialog.show();
//        }

        //NEW DRAWING
        else if(view.getId()==R.id.colour_btn){
            drawView.openColourPicker(this);
        }


        //SAVE DRAWING
        else if(view.getId()==R.id.save_btn){

            if(checkPermissionsArray(Permissions.WRITE_STORAGE_PERMISSION)){ //check if the permissions are allowed
                saveDrawing();
            }else{ //if they haven't been verified
                verifyPermissions(Permissions.WRITE_STORAGE_PERMISSION); //verify permissions
            }


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

    private void saveDrawing(){
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
                            "Drawing could not be saved", Toast.LENGTH_SHORT);
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

    /**
     * verify all the permissions passed to the array
     * @param permissions
     */
    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                DrawActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );

        //restarts the activity
        //((Activity)mContext).recreate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case VERIFY_PERMISSIONS_REQUEST : {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //saves the drawing
                    saveDrawing();
                }
                else{
                    Toast.makeText(this, "Drawing could not be saved", Toast.LENGTH_SHORT).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    /**
     * Check an array of permissions
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking persmissions array.");

        for(int i = 0; i < permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    /**
     * Check a signle permission if it has been verified
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(DrawActivity.this, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permissions was not granted for: " + permission);
            return false;
        }
        else{
            Log.d(TAG, "checkPermissions: \n Permissions was granted for: " + permission);
            return true;
        }
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
