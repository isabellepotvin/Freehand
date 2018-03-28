package com.team06.freehand.Chat;


import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.team06.freehand.Models.ChatMessage;
import com.team06.freehand.Profile.ProfileActivity;
import com.team06.freehand.Profile.ProfileFragment;
import com.team06.freehand.R;
import com.team06.freehand.Utils.BottomNavigationViewHelper;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private static final int ACTIVITY_NUM = 1;
    private Context mContext = ChatActivity.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: starting.");

        init();



    }

    private void init(){
        Log.d(TAG, "init: inflating " + getString(R.string.chat_fragment));

        ChatFragment fragment = new ChatFragment();
        FragmentTransaction transaction = ChatActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);

        //add to back stack so that the back button on the phone will work properly
        transaction.addToBackStack(getString(R.string.chat_fragment));
        transaction.commit();
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
