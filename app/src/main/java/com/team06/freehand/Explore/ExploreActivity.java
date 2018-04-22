package com.team06.freehand.Explore;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.team06.freehand.Chat.InfoChat;
import com.team06.freehand.Login.LoginActivity;
import com.team06.freehand.Models.ChatMessage;
import com.team06.freehand.Models.Photo;
import com.team06.freehand.Models.User;
import com.team06.freehand.Models.UserLikes;
import com.team06.freehand.R;
import com.team06.freehand.Utils.BottomNavigationViewHelper;
import com.team06.freehand.Utils.ChatListAdapter;
import com.team06.freehand.Utils.DrawingListAdapter;
import com.team06.freehand.Utils.FirebaseMethods;
import com.team06.freehand.Utils.GridImageAdapter;
import com.team06.freehand.Utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExploreActivity extends AppCompatActivity {

    private static final String TAG = "ExploreActivity";
    private static final int ACTIVITY_NUM = 0;
    private static final int NUM_GRID_COLUMNS = 1;
    private Context mContext = ExploreActivity.this;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    //widgets
    private TextView mName, mAge, mLocation, mDescription;
    private ProgressBar mProgressBar;
    private View mVertLine;
    private TextView mTextAge;
    private GridView gridView;
    private CircleImageView btnYes, btnNo;
    RelativeLayout rl_userinfo, rl_gridview;

    //vars
    private String randomUserID = null;
    private String previousUserID = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        Log.d(TAG, "onCreate: starting.");

        mName = (TextView) findViewById(R.id.name);
        mAge = (TextView) findViewById(R.id.age);
        mLocation = (TextView) findViewById(R.id.location);
        mDescription = (TextView) findViewById(R.id.description);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mVertLine = (View) findViewById(R.id.vertLine);
        mTextAge = (TextView) findViewById(R.id.text_age);
        gridView = (GridView) findViewById(R.id.gridViewExplore);
        btnYes = (CircleImageView) findViewById(R.id.btn_yes);
        btnNo = (CircleImageView) findViewById(R.id.btn_no);
        rl_userinfo = (RelativeLayout) findViewById(R.id.relLayout_userinfo);
        rl_gridview = (RelativeLayout) findViewById(R.id.relLayout_gridview);

        mFirebaseMethods = new FirebaseMethods(mContext);

        setupFirebaseAuth();

        initImageLoader();
        setupBottomNavigationView();

        displayNewUser();
        setupButtons();

    }


    private void setupButtons(){

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String otherUserID = randomUserID;

                mFirebaseMethods.saveLike(otherUserID);
                checkForConnection(otherUserID);

                //displays new user
                displayNewUser();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayNewUser();
            }
        });
    }


    // checks if the other user has liked the current user --> if they have
    // a new chat is created (if one does not already exist)
    private void checkForConnection(final String otherUserID){

        //**
        //checks if other user "liked" them
        //**

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_likes))
                .child(otherUserID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //iterates through all of the other user's "likes"
                for( DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    UserLikes userLike = singleSnapshot.getValue(UserLikes.class);

                    //if the other user has liked the current user
                    if(userLike.getOther_user_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                        Log.d(TAG, "onDataChange: Users" + FirebaseAuth.getInstance().getCurrentUser().getUid() +
                                " and " + otherUserID + "like each other.");

                        //**
                        //checks if the chat already exists
                        //**

                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
                        Query query = reference2
                                .child(getString(R.string.dbname_user_chats))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                boolean chatExists = false;

                                //iterates through all of the current user's "chats"
                                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                    //if chat already exist
                                    if(singleSnapshot.getKey().equals(otherUserID)){
                                        chatExists = true;
                                        Log.d(TAG, "onDataChange: Chat already exists.");
                                    }
                                }

                                //if the chat does not exist --> create new chat
                                if(!chatExists){

                                    mFirebaseMethods.createNewChat(otherUserID);

                                    Log.d(TAG, "onDataChange: Creating new chat between userIDs: " +
                                            FirebaseAuth.getInstance().getCurrentUser().getUid() + " and " + otherUserID);

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });


    }




    private void displayNewUser(){

        final ArrayList<String> userIDs = new ArrayList<>();
        final Random random = new Random();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference
                .child(getString(R.string.dbname_users));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    userIDs.add(singleSnapshot.getKey());
                }

                int numUsers = userIDs.size();

                do {
                    int randomNumber = random.nextInt(numUsers);
                    randomUserID = userIDs.get(randomNumber);
                    Log.d(TAG, "onDataChange: displayNewUser: " + randomUserID);
                } while(randomUserID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        || randomUserID.equals(previousUserID));

                previousUserID = randomUserID;

                setupGridView(randomUserID);
                setupUserInfo(randomUserID);
                Log.d(TAG, "displayNewUser: user id: " + randomUserID);

                //updateLayoutSizes();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    private void setupUserInfo(String user_id){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.dbname_users))
                .child(user_id);

        Log.d(TAG, "setupUserInfo: reference: " + reference );

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                mName.setText(user.getName());
                mAge.setText(String.valueOf(user.getAge()));
                mLocation.setText(user.getLocation());
                mDescription.setText(user.getDescription());

                mProgressBar.setVisibility(View.GONE);
                mVertLine.setVisibility(View.VISIBLE);
                mTextAge.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "setupUserInfo: onCancelled: query cancelled");
            }
        };
        reference.addValueEventListener(listener); //reads the data only once
    }


    private void setupGridView(String user_id){
        Log.d(TAG, "setupGridView: Setting up image grid.");

        final ArrayList<Photo> photos = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_photos))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for( DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    photos.add(singleSnapshot.getValue(Photo.class));
                }

                //setup image grid
                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = gridWidth/NUM_GRID_COLUMNS;
                gridView.setColumnWidth(imageWidth);

                ArrayList<String> imgUrls = new ArrayList<>();

                //add images to imgUrls array
                for(int i = 0; i < photos.size(); i++){
                    imgUrls.add(photos.get(i).getImage_path());
                }

                GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, "", imgUrls, getString(R.string.img_squareImageView));
                gridView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    private void updateLayoutSizes(){

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        int userinfoHeight = rl_userinfo.getHeight();
        Log.d(TAG, "updateLayoutSizes: height: " + userinfoHeight);

        //set the bottom margin of the grid view
        params.setMargins(0, 0, 0, userinfoHeight); //bottom margin: height of description box
        gridView.setLayoutParams(params);


    }

    /**
     * Universal Image Loader
     */
    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }


    // ***************************
    // BottomNavigationView setup
    // ***************************

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    /*
   - - - - - - - - - - - - - - - - - - - - - - - - Firebase - - - - - - - - - - - - - - - - - - - - - - - -
     */

    /**
     * checks to see if the @param 'user' is logged in
     * @param user
     */
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        //if the user is not authenticated, will navigate back to the login activity
        if(user == null){
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }

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
                checkCurrentUser(user);

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }






    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
