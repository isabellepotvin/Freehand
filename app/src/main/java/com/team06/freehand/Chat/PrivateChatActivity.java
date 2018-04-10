package com.team06.freehand.Chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.team06.freehand.Models.ChatMessage;
import com.team06.freehand.R;
import com.team06.freehand.Utils.DrawingListAdapter;
import com.team06.freehand.Utils.FirebaseMethods;

import java.util.ArrayList;

/**
 * Created by isabellepotvin on 2018-03-27.
 */

public class PrivateChatActivity extends AppCompatActivity {

    private static final String TAG = "PrivateChatActivity";
    private static final int ACTIVITY_NUM = 1;
    private Context mContext = PrivateChatActivity.this;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    //widgets
    private ImageView backArrow;
    private RelativeLayout drawingSpace;
    private TextView tvName;
    private ListView mListView;

    //vars
    private String chatID;
    private String personName;
    private String otherUserID;
    private int UpdateListRequestCode = 2;
    DrawingListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);

        setupFirebaseAuth();
        getIntentExtras();

        //list view
        mListView = (ListView) findViewById(R.id.list_of_messages);


        //name
        tvName = (TextView) findViewById(R.id.person_name);
        tvName.setText(personName);


        //drawing space button
        drawingSpace = (RelativeLayout) findViewById(R.id.btn_drawingSpace);
        drawingSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigation to drawing space.");
                Intent intent = new Intent(mContext, DrawActivity.class);

                Bundle extras = new Bundle();

                extras.putString(getString(R.string.chat_id), chatID);
                extras.putString(getString(R.string.other_user_id), otherUserID);
                intent.putExtras(extras);

                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        //back arrow for navigating back to "ChatActivity"
        backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                PrivateChatActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });


        setupListView();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == UpdateListRequestCode){

        }

    }

    private void setupListView(){

        Log.d(TAG, "setupListView: settings up list view.");

        final ArrayList<ChatMessage> chatDrawings = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_chats))
                .child(chatID);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //gets the chat information
                for( DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    chatDrawings.add(singleSnapshot.getValue(ChatMessage.class));
                }

                //populates list view
                adapter = new DrawingListAdapter(mContext, R.layout.snippet_drawinglist_rowview_, chatDrawings, mAuth.getCurrentUser().getUid());
                mListView.setAdapter(adapter);
                mListView.setSelection(adapter.getCount() - 1);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });


    }

    private void getIntentExtras (){
        Log.d(TAG, "getIntentExtras: getting extras");
        Bundle extras = getIntent().getExtras();
        chatID = extras.getString(getString(R.string.chat_id));
        personName = extras.getString(getString(R.string.person_name));
        otherUserID = extras.getString(getString(R.string.other_user_id));

        Log.d(TAG, "getIntentExtras: chatID: " + chatID);
        Log.d(TAG, "getIntentExtras: personName: " + personName);
        Log.d(TAG, "getIntentExtras: personName: " + otherUserID);
    }

    public void updateList(){
        adapter.notifyDataSetChanged();
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
