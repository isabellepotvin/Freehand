package com.team06.freehand.Login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.team06.freehand.Explore.ExploreActivity;
import com.team06.freehand.R;
import com.team06.freehand.Utils.FirebaseMethods;
import com.team06.freehand.Utils.UniversalImageLoader;

/**
 * Created by isabellepotvin on 2018-02-25.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods mFirebaseMethods;


    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;
    private TextView mPleaseWait, mForgotPassword;
    private ImageView mBgImage;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_login);

            mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
            mPleaseWait = (TextView) findViewById(R.id.pleaseWait);
            mEmail = (EditText) findViewById(R.id.input_email);
            mPassword = (EditText) findViewById(R.id.input_password);
            mContext = LoginActivity.this;
            mFirebaseMethods = new FirebaseMethods(mContext);
            mBgImage = (ImageView) findViewById(R.id.bg_image);

            UniversalImageLoader.setImage("drawable://" + R.drawable.sketchbook_and_pens, mBgImage, null, "");

            Log.d(TAG, "onCreate: started.");

            mPleaseWait.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);

            setupFirebaseAuth();
            init(); //login button

            mForgotPassword = (TextView)  findViewById(R.id.tv_forgot_password);
            mForgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final String email = mEmail.getText().toString();

                    if(isStringNull(email)){
                        Toast.makeText(mContext, "Please enter your email", Toast.LENGTH_SHORT).show();
                    }else{
                        AlertDialog.Builder newDialog = new AlertDialog.Builder(mContext);
                        newDialog.setTitle("Reset Password");
                        newDialog.setMessage("Do you want us to send you an email to reset your password?");
                        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which){
                                mFirebaseMethods.resetPassword(email);
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
                }
            });

        }catch (OutOfMemoryError e){

            Log.e(TAG, "onCreate: OutOfMemoryError" + e.getMessage() );

            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }



    }

    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: checking string if null.");

        if(string.equals("")){ //if string is null
            Log.d(TAG, "isStringNull: string is null.");
            return true;
        }
        else{ //if string is not null
            Log.d(TAG, "isStringNull: string is not null.");
            return false;
        }
    }



    /*
   - - - - - - - - - - - - - - - - - - - - - - - - Firebase - - - - - - - - - - - - - - - - - - - - - - - -
     */

    private void init(){

        //initialize button for logging in
        Button btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: attempting to log in.");

                //saves the inputs to strings
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(isStringNull(email) || isStringNull(password)){ //if the email or password is null
                    Toast.makeText(mContext, R.string.toast_fill_out_all_fields, Toast.LENGTH_SHORT).show();
                }else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);

                    //sign in existing users
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "signInWithEmail:onComplete: " + task.isSuccessful());
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    //if not successful
                                    if(!task.isSuccessful()) {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());

                                        mFirebaseMethods.displayAuthErrorMessages(task);



                                        mProgressBar.setVisibility(View.GONE);
                                        mPleaseWait.setVisibility(View.GONE);

                                    }
                                    else { //if successful
                                        try{
                                            if(user.isEmailVerified()){
                                                Log.d(TAG, "onComplete: Success. Email is verified.");
                                                Intent intent = new Intent(LoginActivity.this, ExploreActivity.class);
                                                startActivity(intent);
                                            }else{
                                                Toast.makeText(mContext, "Email is not verified. \nCheck your email inbox.", Toast.LENGTH_SHORT).show();
                                                mProgressBar.setVisibility(View.GONE);
                                                mPleaseWait.setVisibility(View.GONE);
                                                mAuth.signOut();
                                            }
                                        }catch (NullPointerException e){
                                            Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage() );
                                        }
                                    }

                                    // ...
                                }
                            });
                }


            }
        });

        TextView linkSignUp = (TextView) findViewById(R.id.link_signup);
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigating to register screen");
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        /*
        If the user is logged in then navigate to HomeActivity and call 'finish()'
         */
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, ExploreActivity.class);
            startActivity(intent);
            finish();
        }


    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

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