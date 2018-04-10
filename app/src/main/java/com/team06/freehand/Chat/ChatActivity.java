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
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.team06.freehand.Models.User;
import com.team06.freehand.Models.UserChats;
import com.team06.freehand.R;
import com.team06.freehand.Utils.BottomNavigationViewHelper;
import com.team06.freehand.Utils.FirebaseMethods;
import com.team06.freehand.Utils.ChatListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

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

    //widgets
    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Log.d(TAG, "onCreate: starting.");

        //widgets
        mListView = (ListView) findViewById(R.id.lvChats);

        mFirebaseMethods = new FirebaseMethods(mContext);


        setupBottomNavigationView();
        setupFirebaseAuth();

        setupListView();

        Log.d(TAG, "onCreateView: Database Reference: " + myRef);
        Log.d(TAG, "onCreateView: Database Reference: " + myRef.child(getString(R.string.dbname_user_chats)));


    }

    private void setupListView(){

        Log.d(TAG, "setupListView: settings up list view.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_chats))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: dataChanged.");

                final ArrayList<UserChats> userChats = new ArrayList<>();

                //gets the chat information
                for( DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    userChats.add(singleSnapshot.getValue(UserChats.class));
                }

                //sorts the chats in descending order of the "last_timestamp"
                Collections.sort(userChats, new Comparator<UserChats>() {
                    @Override
                    public int compare(UserChats first, UserChats second) {
                        Date firstDate = mFirebaseMethods.getDateFromTimestamp(first.getLast_timestamp());
                        Date secondDate = mFirebaseMethods.getDateFromTimestamp(second.getLast_timestamp());

                        return secondDate.compareTo(firstDate); //descending order
                    }
                });

                //creates an ArrayList of Users
                final ArrayList<InfoChat> chatList = new ArrayList<>();

                //allows us to read from the database
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //adds users to the array list
                        for(int i = 0; i < userChats.size(); i++){

                            Log.d(TAG, "onDataChange: user ID: " + userChats.get(i).getOther_user_id());

                            User user = mFirebaseMethods.getUserInfo(dataSnapshot, userChats.get(i).getOther_user_id()); //gets all user information

                            Log.d(TAG, "onDataChange: user name: " + user.getName());

                            //adds user's name and profile photo to the chat list array
                            chatList.add(new InfoChat(user.getName(),
                                    user.getProfile_photo(), userChats.get(i).getLast_timestamp()));

                            Log.d(TAG, "onDataChange: last timestamp: " + userChats.get(i).getLast_timestamp());

                            Log.d(TAG, "onDataChange: chatList: " + chatList.get(i).getImgUrl());

                        }

                        //populates list view
                        ChatListAdapter adapter = new ChatListAdapter(mContext, R.layout.snippet_chatlist_rowview, chatList);
                        mListView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

                //when a chat is clicked in the list
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Log.d(TAG, "onItemClick: selected an item: " + userChats.get(position).getChat_id());

                        Log.d(TAG, "onClick: navigation to private chat.");
                        Intent intent = new Intent(mContext, PrivateChatActivity.class);
                        Bundle extras = new Bundle();

                        extras.putString(getString(R.string.chat_id), userChats.get(position).getChat_id());
                        extras.putString(getString(R.string.person_name), chatList.get(position).getName());
                        extras.putString(getString(R.string.other_user_id), userChats.get(position).getOther_user_id());
                        intent.putExtras(extras);

                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
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

}
