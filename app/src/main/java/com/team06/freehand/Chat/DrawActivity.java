package com.team06.freehand.Chat;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.team06.freehand.Profile.EditProfileFragment;
import com.team06.freehand.R;
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
    private ImageButton currPaint, drawBtn, eraseBtn, settingsBtn, newBtn, saveBtn;

    @Override
    public void updatedBrushSettings(float brushSize) {
        drawView.setBrushSize(brushSize);
        drawView.setLastBrushSize(brushSize);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        setupFirebaseAuth();

        drawView = (DrawingView) findViewById(R.id.drawing);


        //LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);


        //paint colour button (selects the first paint colour)
        //currPaint = (ImageButton)paintLayout.getChildAt(0);
        //currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        //sets initial brush size
        drawView.setBrushSize(20);

        //draw button
        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);

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


        //brush colour
        BubbleSeekBar brushColour = (BubbleSeekBar)findViewById(R.id.brush_colour);
        //brushColour.setProgress(drawView.getLastBrushSize());

        brushColour.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {

                drawView.setErase(false);

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

        //drawBtn.setBackgroundColor(0x000000);
        //eraseBtn.setBackgroundColor(0x4CB8FB);
        //settingsBtn.setBackgroundColor(0x4CB8FB);

        //ImageButton imgView = (ImageButton) view;
        //imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        //BRUSH SETTINGS
        if(view.getId()==R.id.settings_btn){

            BrushSettingsDialog dialog = new BrushSettingsDialog();
            dialog.show(getSupportFragmentManager(), getString(R.string.brush_settings_dialog));

//            BrushSettingsDialog dialog = new BrushSettingsDialog();
//            dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
//            dialog.setTargetFragment(EditProfileFragment.this, 1);

//            final Dialog brushDialog = new Dialog(this);
//
//            brushDialog.setTitle("Brush settings:");
//            brushDialog.setContentView(R.layout.layout_brush_chooser);
//
//            //Sets listener for bubble seek bar
//            BubbleSeekBar brushSize = (BubbleSeekBar)brushDialog.findViewById(R.id.brush_size);
//            brushSize.setProgress(drawView.getLastBrushSize());
//            brushSize.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
//                @Override
//                public void onProgressChanged(int progress, float progressFloat) {
//                    drawView.setBrushSize(progressFloat);
//                    drawView.setLastBrushSize(progressFloat);
//                   // brushDialog.dismiss();
//                }
//                @Override
//                public void getProgressOnActionUp(int progress, float progressFloat) { }
//                @Override
//                public void getProgressOnFinally(int progress, float progressFloat) { }
//            });
//
//            brushDialog.show();




        }

        //DRAW
        else if(view.getId()==R.id.draw_btn) {
            drawView.setErase(false);
        }


        //ERASER
        else if(view.getId()==R.id.erase_btn) {
            drawView.setErase(true);
        }


        //NEW DRAWING
        else if(view.getId()==R.id.new_btn){
            //alerts user when they click new drawing button
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
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
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

                //saves image
                public void onClick(DialogInterface dialog, int which){
                    drawView.setDrawingCacheEnabled(true);

                    String imgSaved = MediaStore.Images.Media.insertImage( getContentResolver(), drawView.getDrawingCache(),
                            UUID.randomUUID().toString()+".png", "drawing");

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
