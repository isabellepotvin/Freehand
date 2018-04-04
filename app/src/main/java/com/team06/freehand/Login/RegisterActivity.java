package com.team06.freehand.Login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
 * Created by isabellepotvin on 2018-02-25.
 */

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private Context mContext;
    private String email, name, location , password, age;
    private long ageLong;
    private EditText mEmail, mName, mAge, mLocation, mPassword;
    private TextView loadingPleaseWait;
    private ProgressBar mProgressBar;
    private Button btnRegister;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods firebaseMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private ImageView mBgImage;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext = RegisterActivity.this;
        firebaseMethods = new FirebaseMethods(mContext);
        Log.d(TAG, "onCreate: started.");

        mBgImage = (ImageView) findViewById(R.id.bg_image);

        UniversalImageLoader.setImage("drawable://" + R.drawable.sketchbook_and_pens, mBgImage, null, "");

        initWidgets();
        setupFirebaseAuth();
        init();

    }

    //initializes button for register
    private void init(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEmail.getText().toString();
                name = mName.getText().toString();
                password = mPassword.getText().toString();
                age = mAge.getText().toString();
                location = mLocation.getText().toString();

                if(checkInputs(email, name, password, age, location)){ //if all the fields are filled out
                    mProgressBar.setVisibility(View.VISIBLE);
                    loadingPleaseWait.setVisibility(View.VISIBLE);

                    firebaseMethods.registerNewEmail(email, password);

                    mProgressBar.setVisibility(View.GONE);
                    loadingPleaseWait.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean checkInputs(String email, String name, String password, String age, String location){
        Log.d(TAG, "checkInputs: checking inputs for null values.");
        if(email.equals("") || name.equals("") || password.equals("") || age.equals("") || location.equals("")){
            Toast.makeText(mContext, R.string.toast_fill_out_all_fields, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(Long.parseLong(age) <= 18){
            Toast.makeText(mContext, R.string.toast_18_or_older, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Initialize the activity widgets
     */

    private void initWidgets(){
        Log.d(TAG, "initWidgets: Initializing Widgets.");

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        loadingPleaseWait = (TextView) findViewById(R.id.loadingPleaseWait);

        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);
        mName = (EditText) findViewById(R.id.input_name);
        mAge = (EditText) findViewById(R.id.input_age);
        mLocation = (EditText) findViewById(R.id.input_location);
        btnRegister = (Button) findViewById(R.id.btn_register);
        mContext = RegisterActivity.this;

        mProgressBar.setVisibility(View.GONE);
        loadingPleaseWait.setVisibility(View.GONE);
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

        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth){
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in" + user.getUid());

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) { //success

                            //add new user to the database
                            firebaseMethods.addNewUser(email, name, Long.parseLong(age), location, "");

                            Toast.makeText(mContext, "Signup successful. Sending verification email.", Toast.LENGTH_SHORT).show();

                            mAuth.signOut(); //signs the user out so they check the verification email

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { //error

                        }

                    });

                    finish(); //finishes the activity (goes back to previous activity)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                } else{

                    //user is signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
            }
        };
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
