package com.team06.freehand.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.team06.freehand.Models.User;
import com.team06.freehand.R;
import com.team06.freehand.Utils.BottomNavigationViewHelper;
import com.team06.freehand.Utils.FirebaseMethods;
import com.team06.freehand.Utils.UniversalImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by isabellepotvin on 2018-03-07.
 */

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private static final int ACTIVITY_NUM = 2;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;


    private TextView mName, mAge, mLocation, mDescription;
    private ProgressBar mProgressBar;
    private CircleImageView mProfilePhoto;
    private View mVertLine;
    private TextView mTextAge;

    private GridView gridView;
    private ImageView settingsBtn;
    BottomNavigationViewEx bottomNavigationView;

    private Context mContext;
    

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container,false);


        mName = (TextView) view.findViewById(R.id.name);
        mAge = (TextView) view.findViewById(R.id.age);
        mLocation = (TextView) view.findViewById(R.id.location);
        mDescription = (TextView) view.findViewById(R.id.description);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mVertLine = (View) view.findViewById(R.id.vertLine);
        mTextAge = (TextView) view.findViewById(R.id.text_age);

        gridView = (GridView) view.findViewById(R.id.gridView);
        settingsBtn = (ImageView) view.findViewById(R.id.btn_settings);
        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mContext = getActivity();
        mFirebaseMethods = new FirebaseMethods(getActivity());

        Log.d(TAG, "onCreateView: started.");

        setupBottomNavigationView();
        setupSettingsBtn();

        setupFirebaseAuth();

        return view;
    }

    private void setProfileWidgets(User user){
        //Log.d(TAG, "setProfileWidgets: settings widgets with data retrieved from firebase database: " + userSettings.toString());
        //Log.d(TAG, "setProfileWidgets: settings widgets with data retrieved from firebase database: " + userSettings.getSettings().getUsername());


        UniversalImageLoader.setImage(user.getProfile_photo(), mProfilePhoto, null, "");

        mName.setText(user.getName());
        mAge.setText(String.valueOf(user.getAge()));
        mLocation.setText(user.getLocation());
        mDescription.setText(user.getDescription());

        mProgressBar.setVisibility(View.GONE);
        mVertLine.setVisibility(View.VISIBLE);
        mTextAge.setVisibility(View.VISIBLE);


    }

    /**
     * method that sets up toolbar
     */
    private void setupSettingsBtn(){

        //Account settings button
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigation to account settings.");
                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                startActivity(intent);
            }
        });

    }

    // ***************************
    // BottomNavigationView setup
    // ***************************

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
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
