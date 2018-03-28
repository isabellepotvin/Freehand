package com.team06.freehand.Chat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.team06.freehand.Models.ChatMessage;
import com.team06.freehand.Models.Photo;
import com.team06.freehand.Models.User;
import com.team06.freehand.Models.UserChats;
import com.team06.freehand.Profile.AccountSettingsActivity;
import com.team06.freehand.R;
import com.team06.freehand.Utils.BottomNavigationViewHelper;
import com.team06.freehand.Utils.FirebaseMethods;
import com.team06.freehand.Utils.GridImageAdapter;
import com.team06.freehand.Utils.SectionsStatePagerAdapter;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by isabellepotvin on 2018-03-27.
 */

public class ChatFragment extends Fragment {

    private static final String TAG = "ChatFragment";

    private static final int ACTIVITY_NUM = 1;


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
    private Context mContext;
    BottomNavigationViewEx bottomNavigationView;

    public SectionsStatePagerAdapter pagerAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container,false);

        //widgets
        //newChatBtn = (Button) view.findViewById(R.id.btn_newchat);
        mListView = (ListView) view.findViewById(R.id.lvChats);
        mPicture = (CircleImageView) view.findViewById(R.id.person_picture);
        mName = (TextView) view.findViewById(R.id.person_name);

        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.relLayout);



        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mContext = getActivity();
        mFirebaseMethods = new FirebaseMethods(getActivity());

        setupListView();



        setupBottomNavigationView();
        setupFirebaseAuth();

        Log.d(TAG, "onCreateView: Database Reference: " + myRef);
        Log.d(TAG, "onCreateView: Database Reference: " + myRef.child(getString(R.string.dbname_user_chats)));


//        Query query = myRef.child(getString(R.string.dbname_user_chats)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//        FirebaseListOptions<String> options = new FirebaseListOptions.Builder<String>()
//                .setQuery(query, String.class)
//                .setLayout(R.layout.snippet_chatlist_rowview)
//                .build();


//        firebaseListAdapter = new FirebaseListAdapter<String>(options){
//            @Override
//            protected void populateView(View v, String model, int position) {
//                Log.d(TAG, "populateView: setting name.");
//                mName.setText(model);
//            }
//        };
//
//
//        mListView.setAdapter(firebaseListAdapter);
//        Log.d(TAG, "populateView: firebaseListAdapter");




//        newChatBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                randomUser();
//            }
//        });





        return view;
    }


    private void setupListView(){
        Log.d(TAG, "setupGridView: Setting up image grid.");

        final ArrayList<UserChats> users = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_chats))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for( DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    users.add(singleSnapshot.getValue(UserChats.class));
                }

                ArrayList<String> userNames = new ArrayList<String>();

                //add images to imgUrls array
                for(int i = 0; i < users.size(); i++){
                    userNames.add(users.get(i).getChat_id());
                }

                ArrayAdapter adapter = new ArrayAdapter(mContext, R.layout.snippet_chatlist_rowview, R.id.person_name, userNames);
                mListView.setAdapter(adapter);

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Log.d(TAG, "onItemClick: selected an item: " + users.get(position));

                        Log.d(TAG, "onItemClick: inflating " + getString(R.string.private_chat_fragment));

                        PrivateChatFragment fragment = new PrivateChatFragment();
                        FragmentTransaction transaction = ChatActivity.class.getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, fragment);

                        //add to back stack so that the back button on the phone will work properly
                        transaction.addToBackStack(getString(R.string.private_chat_fragment));
                        transaction.commit();

                        setImage(users.get(position), galleryImage, mAppend);


                        mSelectedImage = imgURLs.get(position);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    public void setViewPager(int fragmentNumber){
        mRelativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewPager: navigating to fragment #: " + fragmentNumber);
        mViewPager.setAdapter(pagerAdapter); //sets up fragments
        mViewPager.setCurrentItem(fragmentNumber); //will navigate to this fragment
    }

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
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(mContext, getActivity(), bottomNavigationView);
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
