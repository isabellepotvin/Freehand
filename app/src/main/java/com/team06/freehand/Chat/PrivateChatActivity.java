package com.team06.freehand.Chat;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.team06.freehand.Models.ChatMessage;
import com.team06.freehand.R;
import com.team06.freehand.Utils.FirebaseMethods;

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

    private FirebaseListAdapter<ChatMessage> adapter;

    //widgets


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);


        //back arrow for navigating back to "ChatActivity"
        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                PrivateChatActivity.this.finish();
            }
        });

    }


}
