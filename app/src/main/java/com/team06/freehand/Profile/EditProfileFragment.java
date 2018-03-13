package com.team06.freehand.Profile;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.team06.freehand.Dialogs.ConfirmPasswordDialog;
import com.team06.freehand.Models.User;
import com.team06.freehand.R;
import com.team06.freehand.Utils.FirebaseMethods;
import com.team06.freehand.Utils.UniversalImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by isabellepotvin on 2018-02-24.
 */

public class EditProfileFragment extends Fragment implements ConfirmPasswordDialog.OnConfirmPasswordListener {

    @Override
    public void onConfirmPasswordPassword(String password) {
        Log.d(TAG, "onConfirmPasswordPassword: got the password " + password); //just for testing // you never want to print passwords to a log

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
                                                                    Toast.makeText(getActivity(), "email updated", Toast.LENGTH_SHORT).show();
                                                                    mFirebaseMethods.updateEmail(mEmail.getText().toString());
                                                                }
                                                            }
                                                        });
                                            }

                                        }catch (NullPointerException e){
                                            Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage() );
                                        }

                                    }
                                }
                            });



                        } else {
                            Log.d(TAG, "onComplete: re-authentication failed.");
                        }
                    }
                });
    }





    private static final String TAG = "EditProfileFragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //EditProfile fragment widgets
    private EditText mName, mAge, mLocation, mDescription, mEmail;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;

    //vars
    private User mUser;





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);

        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mChangeProfilePhoto = (TextView) view.findViewById(R.id.changeProfilePhoto);

        mName = (EditText) view.findViewById(R.id.name);
        mAge = (EditText) view.findViewById(R.id.age);
        mLocation = (EditText) view.findViewById(R.id.location);
        mDescription = (EditText) view.findViewById(R.id.description);
        mEmail = (EditText) view.findViewById(R.id.email);

        mFirebaseMethods = new FirebaseMethods(getActivity());

        //setProfileImage();
        setupFirebaseAuth();

        //back arrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                getActivity().finish();
            }
        });

        ImageView checkmark = (ImageView) view.findViewById(R.id.saveChanges);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: attempting to save changes.");
                saveProfileSettings();
                getActivity().finish();
            }
        });

        return view;
    }

    /**
     * Retrieves the data contained in the widgets and submits it to the database
     * Before doing so it checks to make sure the username chosen is unique
     */

    private void saveProfileSettings(){

        final String name = mName.getText().toString();
        final long age = Long.parseLong(mAge.getText().toString());
        final String location = mLocation.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmail.getText().toString();



        //case1: if the user made a change to their email
        if(!mUser.getEmail().equals(email)){

            //step 1: reauthenticate
            //          - Confirm the password and email

            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
            dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
            dialog.setTargetFragment(EditProfileFragment.this, 1); //once the dialog closes, this is its target fragment


            //step 2: check if the email already is registered
            //          - 'fetchProvidersForEmail(String email)' //will check the authentication database to see if that email is available
            //step 3: change the email
            //          - submit the new email to the database and authentication
        }

        /**
         * change the rest of the settings that do not require uniqueness
         */
        if(!mUser.getName().equals(name)){ //if they changed their name
            //update display name
            mFirebaseMethods.updateUser(name, 0, null, null);
        }
        if(mUser.getAge() != age){ //if they changed their age
            //update website
            mFirebaseMethods.updateUser(null, age, null, null);
        }
        if(!mUser.getLocation().equals(location)){ //if they changed their location
            //update description
            mFirebaseMethods.updateUser(null, 0, location, null);
        }
        if(!mUser.getDescription().equals(description)){ //if they changed their description
            //update description
            mFirebaseMethods.updateUser(null, 0, null, description);
        }


    }


    private void setProfileWidgets(User user) {
        //Log.d(TAG, "setProfileWidgets: settings widgets with data retrieved from firebase database: " + user.toString());
        //Log.d(TAG, "setProfileWidgets: settings widgets with data retrieved from firebase database: " + user.getEmail());

        mUser = user;

        UniversalImageLoader.setImage(user.getProfile_photo(), mProfilePhoto, null, "");

        mName.setText(user.getName());
        mAge.setText(String.valueOf(user.getAge()));
        mLocation.setText(user.getLocation());
        mDescription.setText(user.getDescription());
        mEmail.setText(user.getEmail());

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
                }
                // ...
            }
        };

        //allows us to read or write from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //retrieve user information from database
                setProfileWidgets(mFirebaseMethods.getUserInfo(dataSnapshot));

                //retrieve images for the user in question
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
