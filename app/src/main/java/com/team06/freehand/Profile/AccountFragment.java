package com.team06.freehand.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team06.freehand.Dialogs.ConfirmPasswordDialog;
import com.team06.freehand.Login.LoginActivity;
import com.team06.freehand.Models.User;
import com.team06.freehand.R;
import com.team06.freehand.Utils.FirebaseMethods;

/**
 * Created by isabellepotvin on 2018-02-24.
 */

public class AccountFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //widgets
    private EditText mEmail, mCurrentPassword, mNewPassword;
    private TextView mToolbarName, mCurrentEmail, btnSavePassword, btnSaveEmail;
    private Button btnDeleteAccount;
    private ImageView checkmark;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        setupFirebaseAuth();
        mFirebaseMethods = new FirebaseMethods(getActivity());

//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        Log.d(TAG, "onCreateView: email: " + user.getEmail());

        mToolbarName = (TextView) view.findViewById(R.id.toolbarName);
        mToolbarName.setText(getString(R.string.account_fragment));

        checkmark = (ImageView) view.findViewById(R.id.saveChanges);
        checkmark.setVisibility(View.GONE);

        mEmail = (EditText) view.findViewById(R.id.email);

        mCurrentEmail = (TextView) view.findViewById(R.id.current_email);
        mCurrentEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        mCurrentPassword = (EditText) view.findViewById(R.id.current_password);
        mNewPassword = (EditText) view.findViewById(R.id.new_password);



        //back arrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                getActivity().finish();
            }
        });

        //button to save email changes
        btnSaveEmail = (TextView) view.findViewById(R.id.btnEmail);
        btnSaveEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: attempting to save email changes.");
                saveEmailSettings();
            }
        });

        //btn to save password changes
        btnSavePassword = (TextView) view.findViewById(R.id.btnPassword);
        btnSavePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: attempting to save password changes.");
                savePasswordSettings();
            }
        });


//        //delete account button
//        btnDeleteAccount = (Button) view.findViewById(R.id.btnDeleteAccount);
//        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: attempting to delete account.");
//
//                //alert
//                AlertDialog.Builder newDialog = new AlertDialog.Builder(getActivity());
//                newDialog.setTitle("Delete");
//                newDialog.setMessage("Are you sure you want to permanently delete your account?");
//                newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
//                    public void onClick(DialogInterface dialog, int which){
//                        mFirebaseMethods.deleteUser();
//                        mAuth.signOut();
//                        dialog.dismiss();
//                    }
//                });
//                newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
//                    public void onClick(DialogInterface dialog, int which){
//                        dialog.cancel();
//                    }
//                });
//                newDialog.show();
//            }
//        });

        return view;
    }


    private void savePasswordSettings(){

        final String currentPassword = mCurrentPassword.getText().toString();
        final String newPassword = mNewPassword.getText().toString();

        if(!currentPassword.equals("") && !newPassword.equals("")){

            AuthCredential credential = EmailAuthProvider
                    .getCredential(mAuth.getCurrentUser().getEmail(), currentPassword);

            mAuth.getCurrentUser().reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User re-authenticated.");

                                mFirebaseMethods.updatePassword(newPassword);

                            } else {
                                Log.d(TAG, "onComplete: re-authentication failed.");
                                //Toast.makeText(getActivity(), "Failed to change password", Toast.LENGTH_SHORT).show();
                                mFirebaseMethods.displayAuthErrorMessages(task);
                            }
                        }
                    });
        }
        else{
            Log.d(TAG, "saveEmailSettings: no email changes detected.");
            Toast.makeText(getActivity(), "You must fill out all the fields" , Toast.LENGTH_SHORT).show();
        }
    }




    private void saveEmailSettings(){

        final String email = mEmail.getText().toString();

        //case1: if the user made a change to their email
        if(!FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(email) && !email.equals("")){

            Log.d(TAG, "saveEmailSettings: fragment manager: " + getFragmentManager());

            ConfirmPasswordDialog.OnConfirmPasswordListener mOnConfirmPasswordListener = new ConfirmPasswordDialog.OnConfirmPasswordListener() {
                @Override
                public void onConfirmPasswordPassword(String password) {
                    //Log.d(TAG, "onConfirmPasswordPassword: got the password " + password); //just for testing // you never want to print passwords to a log

                    // Get auth credentials from the user for re-authentication. The example below shows
                    // email and password credentials but there are multiple possible providers,
                    // such as GoogleAuthProvider or FacebookAuthProvider.
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(mAuth.getCurrentUser().getEmail(), password);

                    ////////////////////////// Prompt the user to re-provide their sign-in credentials
                    mAuth.getCurrentUser().reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User re-authenticated.");

                                        ////////////////////////// check to see if the email is not already present in the database
                                        mAuth.fetchProvidersForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                                if(task.isSuccessful()){

                                                    try{

                                                        ////////////////////////// if the email is not available
                                                        if(task.getResult().getProviders().size() == 1){
                                                            Log.d(TAG, "onComplete: that email is already in use.");
                                                            Toast.makeText(getActivity(), "That email is already in use", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else {
                                                            Log.d(TAG, "onComplete: That email is available");

                                                            ////////////////////////// the email is available so update it
                                                            mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                Log.d(TAG, "User email address updated.");

                                                                                Toast.makeText(getActivity(), "Verification email sent", Toast.LENGTH_SHORT).show();
                                                                                mFirebaseMethods.updateEmail(mEmail.getText().toString());
                                                                                mFirebaseMethods.sendVerificationEmail();

                                                                                mAuth.signOut();

                                                                            }
                                                                        }
                                                                    });
                                                        }

                                                    }catch (NullPointerException e){
                                                        Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage() );
                                                    }

                                                }
                                                else{
                                                    mFirebaseMethods.displayAuthErrorMessages(task);
                                                }
                                            }
                                        });



                                    } else {
                                        Log.d(TAG, "onComplete: re-authentication failed.");
                                        Toast.makeText(getActivity(), "Failed to update email", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            };


            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog(getActivity(), mOnConfirmPasswordListener, getString(R.string.string_change_email_to), mEmail.getText().toString());
            dialog.show();

        }else{
            Log.d(TAG, "saveEmailSettings: no email changes detected.");
            Toast.makeText(getActivity(), "You have not made any changes" , Toast.LENGTH_SHORT).show();
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
        userID = mAuth.getCurrentUser().getUid();

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

                    Log.d(TAG, "onAuthStateChanged: navigating back to login screen.");
                    Intent intent = new Intent(getActivity(), LoginActivity.class);

                    //clears the activity stack --> so that the user cannot press the back button and regain access to the app
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                }
                // ...
            }
        };

        //allows us to read or write from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //updates email in database to make sure it matches the authentication email
                mFirebaseMethods.updateEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());

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
