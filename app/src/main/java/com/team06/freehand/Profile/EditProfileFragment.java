package com.team06.freehand.Profile;

import android.app.Activity;
import android.content.Intent;
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
import com.team06.freehand.Dialogs.BrushSettingsDialog;
import com.team06.freehand.Dialogs.ConfirmPasswordDialog;
import com.team06.freehand.Login.LoginActivity;
import com.team06.freehand.Models.User;
import com.team06.freehand.R;
import com.team06.freehand.Share.ShareActivity;
import com.team06.freehand.Utils.FirebaseMethods;
import com.team06.freehand.Utils.UniversalImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by isabellepotvin on 2018-02-24.
 */

public class EditProfileFragment extends Fragment {

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
        boolean changedEmail = false;

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

        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: changing profile photo.");
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //will give it a number of 268435456 (important that it is not zero)
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });

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
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //retrieve user information from database
                setProfileWidgets(mFirebaseMethods.getUserInfo(dataSnapshot, userID));

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
