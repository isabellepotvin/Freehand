package com.team06.freehand.Chat;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.team06.freehand.Models.Photo;
import com.team06.freehand.Models.User;
import com.team06.freehand.Models.UserChats;
import com.team06.freehand.R;
import com.team06.freehand.Utils.BottomNavigationViewHelper;
import com.team06.freehand.Utils.FirebaseMethods;
import com.team06.freehand.Utils.ChatListAdapter;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private static final int ACTIVITY_NUM = 1;
    private Context mContext = ChatActivity.this;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    private FirebaseListAdapter<String> firebaseListAdapter;

    //widgets
    private Button newChatBtn;
    private ListView mListView;
    private TextView mName;
    private CircleImageView mPicture;

    //vars
    private String randomUserID = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Log.d(TAG, "onCreate: starting.");

        //widgets
        //newChatBtn = (Button) findViewById(R.id.btn_newchat);
        mListView = (ListView) findViewById(R.id.lvChats);
        mPicture = (CircleImageView) findViewById(R.id.person_picture);
        mName = (TextView) findViewById(R.id.person_name);


        mFirebaseMethods = new FirebaseMethods(mContext);

        //setupListView();

        setupBottomNavigationView();
        setupFirebaseAuth();

        newChatBtn = (Button) findViewById(R.id.btn_newchat);

        newChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomUser();
            }
        });

        userObjects();

        Log.d(TAG, "onCreateView: Database Reference: " + myRef);
        Log.d(TAG, "onCreateView: Database Reference: " + myRef.child(getString(R.string.dbname_user_chats)));




    }

    private void userObjects(){

        Log.d(TAG, "userObjects: settings up list view.");

        final ArrayList<UserChats> userChats = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_chats))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //gets the chat information
                for( DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    userChats.add(singleSnapshot.getValue(UserChats.class));
                }

                //creates an ArrayList of Users
                final ArrayList<UserChatInfo> chatList = new ArrayList<>();

                //allows us to read from the database
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //adds users to the array list
                        for(int i = 0; i < userChats.size(); i++){

                            Log.d(TAG, "onDataChange: user ID: " + userChats.get(i).getOther_user_id());

                            User user = mFirebaseMethods.getUserInfo(dataSnapshot, userChats.get(i).getOther_user_id()); //gets all user information

                            Log.d(TAG, "onDataChange: user: " + user.toString());

                            //adds user's name and profile photo to the chat list array
                            chatList.add(new UserChatInfo(user.getName(),
                                    user.getProfile_photo(), userChats.get(i).getLast_timestamp()));

                            Log.d(TAG, "onDataChange: chatList: " + chatList.get(i).getImgUrl());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                //populates list view
                ChatListAdapter adapter = new ChatListAdapter(mContext, R.layout.snippet_chatlist_rowview, chatList);
                mListView.setAdapter(adapter);


                //when a chat is clicked in the list
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Log.d(TAG, "onItemClick: selected an item: " + chatList.get(position));

                        Log.d(TAG, "onClick: navigation to private chat.");
                        Intent intent = new Intent(mContext, PrivateChatActivity.class);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });


    }


//    private void setupListView(){
//        Log.d(TAG, "setupGridView: Setting up image grid.");
//
//        final ArrayList<UserChats> users = new ArrayList<>();
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        Query query = reference
//                .child(getString(R.string.dbname_user_chats))
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for( DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
//                    users.add(singleSnapshot.getValue(UserChats.class));
//                }
//
//                ArrayList<User> users = new ArrayList<String>();
//
//                userObjects(mFirebaseMethods.getUserInfo(dataSnapshot, mAuth.getCurrentUser().getUid()));
//
//                //add images to imgUrls array
//                for(int i = 0; i < users.size(); i++){
//                    userNames.add(users.get(i).getChat_id());
//                }
//
//                ArrayAdapter adapter = new ArrayAdapter(mContext, R.layout.snippet_chatlist_rowview, R.id.person_name, userNames);
//                mListView.setAdapter(adapter);
//
//                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                        Log.d(TAG, "onItemClick: selected an item: " + users.get(position));
//
//                        Log.d(TAG, "onClick: navigation to private chat.");
//                        Intent intent = new Intent(mContext, PrivateChatActivity.class);
//                        startActivity(intent);
//                    }
//                });
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "onCancelled: query cancelled.");
//            }
//        });
//    }

    private void randomUser(){

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
                }while(randomUserID == FirebaseAuth.getInstance().getCurrentUser().getUid());

                Log.d(TAG, "onDataChange: randomUserID: " + randomUserID);


                mFirebaseMethods.createNewChat(randomUserID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

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


//    private void setupChatsList(){
//        Log.d(TAG,"setupChatsList: initializing 'Chats' list.");
//        ListView listView = (ListView) findViewById(R.id.lvChats);
//
//
//        //Adds items to list view
//        ArrayList<String> chats = new ArrayList<>();
//        chats.add("Connor");
//        chats.add("Abby");
//        chats.add("Zach");
//        chats.add("Omar");
//        chats.add("Emily");
//        chats.add("Nicholas");
//        chats.add("Aly");
//        chats.add("Melvin");
//        chats.add("Ellie");
//        chats.add("Isabelle");
//        chats.add("Myriam");
//        chats.add("Camille");
//        chats.add("Taylor");
//        chats.add("Natalie");
//        chats.add("Daniel");
//        chats.add("Josh");
//        chats.add("Connor");
//        chats.add("Abby");
//        chats.add("Zach");
//        chats.add("Omar");
//        chats.add("Emily");
//        chats.add("Nicholas");
//        chats.add("Aly");
//        chats.add("Melvin");
//        chats.add("Ellie");
//        chats.add("Isabelle");
//        chats.add("Myriam");
//        chats.add("Camille");
//        chats.add("Taylor");
//        chats.add("Natalie");
//        chats.add("Daniel");
//        chats.add("Josh");
//
//
//        ArrayAdapter adapter = new ArrayAdapter(mContext, R.layout.snippet_chatlist_rowview, R.id.person_name, chats);
//        listView.setAdapter(adapter);
//    }

}
