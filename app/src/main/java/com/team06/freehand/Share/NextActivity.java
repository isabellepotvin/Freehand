package com.team06.freehand.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team06.freehand.R;
import com.team06.freehand.Utils.FirebaseMethods;
import com.team06.freehand.Utils.UniversalImageLoader;

/**
 * Created by isabellepotvin on 2018-03-17.
 */

public class NextActivity extends AppCompatActivity {

    private static final String TAG = "NextActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    //widgets
    private EditText mCaption;

    //vars
    private String mAppend = "file:/";
    private int imageCount = 0;
    private String imgUrl;
    private Bitmap bitmap;
    private Intent intent;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        //Log.d(TAG, "onCreate: got the chosen image: " + getIntent().getStringExtra(getString(R.string.selected_image)));

        mFirebaseMethods = new FirebaseMethods(NextActivity.this);

        setupFirebaseAuth();


        //back arrow button
        ImageView backArrow = (ImageView) findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: closing the activity.");
                finish();
            }
        });

        //share button
        final TextView share = (TextView) findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                share.setClickable(false);

                Log.d(TAG, "onClick: navigating to the final share screen.");


                //upload the image to firebase
                Toast.makeText(NextActivity.this, "Attempting to upload new photo", Toast.LENGTH_SHORT).show();

                //if it came from the gallery
                if(intent.hasExtra(getString(R.string.selected_image))){
                    imgUrl = intent.getStringExtra(getString(R.string.selected_image));
                    Log.d(TAG, "onClick: got imgUrl: " + imgUrl);
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), imageCount, imgUrl, null);
                }
                //if it came from the camera
                else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                    bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), imageCount, null, bitmap);
                }
            }
        });

        setImage();
    }




    /**
     * gets the image url from the incoming intent and displays the chosen image
     */
    private void setImage(){
        intent = getIntent();
        ImageView image = (ImageView) findViewById(R.id.imageShare);

        //if it came from the gallery
        if(intent.hasExtra(getString(R.string.selected_image))){
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: got new image url " + imgUrl);
            UniversalImageLoader.setImage(imgUrl, image, null, mAppend);
        }

        //if it came from the camera
        else if(intent.hasExtra(getString(R.string.selected_bitmap))){
            bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
            Log.d(TAG, "setImage: got new bitmap.");
            image.setImageBitmap(bitmap);
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
        Log.d(TAG, "onDataChange: image count: " + imageCount);

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
                imageCount = mFirebaseMethods.getImageCount(dataSnapshot, null); //gets the image count
                Log.d(TAG, "onDataChange: image count: " + imageCount);
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



