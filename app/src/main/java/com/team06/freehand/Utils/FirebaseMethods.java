package com.team06.freehand.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.team06.freehand.Models.User;
import com.team06.freehand.R;

/**
 * Created by isabellepotvin on 2018-02-25.
 */

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String userID;

    private Context mContext;

    //constructor
    public FirebaseMethods(Context context){
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }


//    /**
//     * Update 'user_account_settings node for the current user
//     * @param displayName
//     * @param website
//     * @param description
//     * @param phoneNumber
//     */
//    public void updateUserAccountSettings(String displayName, String website, String description, long phoneNumber){
//        Log.d(TAG, "updateUserAccountSettings: updating user account settings.");
//
//        //updates user account settings node
//
//        if(displayName != null) {
//            myRef.child(mContext.getString(R.string.dnname_user_account_settings))
//                    .child(userID)
//                    .child(mContext.getString(R.string.field_display_name))
//                    .setValue(displayName);
//        }
//
//        if(website != null) {
//            myRef.child(mContext.getString(R.string.dnname_user_account_settings))
//                    .child(userID)
//                    .child(mContext.getString(R.string.field_website))
//                    .setValue(website);
//        }
//
//        if(description != null) {
//            myRef.child(mContext.getString(R.string.dnname_user_account_settings))
//                    .child(userID)
//                    .child(mContext.getString(R.string.field_description))
//                    .setValue(description);
//        }
//
//        if(phoneNumber != 0) {
//            myRef.child(mContext.getString(R.string.dnname_user_account_settings))
//                    .child(userID)
//                    .child(mContext.getString(R.string.field_phone_number))
//                    .setValue(phoneNumber);
//        }
//
//    }


//    /**
//     * update username in the 'users' node and 'user_account_settings' node
//     * @param username
//     */
//    public void updateUsername(String username){
//        Log.d(TAG, "updateUsername: updating username to " + username);
//
//        //updates users node
//        myRef.child(mContext.getString(R.string.dnname_users))
//                .child(userID)
//                .child(mContext.getString(R.string.field_username))
//                .setValue(username);
//
//        //updates user account settings node
//        myRef.child(mContext.getString(R.string.dnname_user_account_settings))
//                .child(userID)
//                .child(mContext.getString(R.string.field_username))
//                .setValue(username);
//    }


    /**
     * update the email in the 'users' node
     * @param email
     */
    public void updateEmail(String email){
        Log.d(TAG, "updateEmail: updating email to " + email);

        //updates users node
        myRef.child(mContext.getString(R.string.dnname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);
    }




    /**
     * Register a new email and password to Firebase Authentication
     * @param email
     * @param password
     */
    public void registerNewEmail(final String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete: " + task.isSuccessful());

                        // If authentication fails, display a message to the user.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mContext, R.string.auth_failed, Toast.LENGTH_SHORT).show();

                        }
                        //if successful
                        else if (task.isSuccessful()) {
                            //send verification email
                            sendVerificationEmail();

                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed: " + userID);

                        }

                    }
                });

    }



    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {

                            }else{
                                Toast.makeText(mContext, "couldn't send verification email.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }


    /**
     * Add information to the users nodes
     * Add information to the user_account_settings node
     * @param email
     * @param name
     * @param age
     * @param location
     * @param profile_photo
     */
    public void addNewUser(String email, String name, long age, String location, String profile_photo){

        User user = new User(userID, email, name, age, location, "", 0, profile_photo);

        //users node
        myRef.child(mContext.getString(R.string.dnname_users))
                .child(userID)
                .setValue(user);
    }

//    /**
//     * Retrieves the account settings for the user currently logged in
//     * Database: user_account_settings node
//     * @param dataSnapshot
//     * @return
//     */
//    public UserSettings getUserSettings(DataSnapshot dataSnapshot){
//        Log.d(TAG, "getUserAccountSettings: retrieving user account settings from firebase.");
//
//        UserAccountSettings settings = new UserAccountSettings();
//        User user = new User();
//
//        for(DataSnapshot ds: dataSnapshot.getChildren()){
//
//            // user_account_settings node
//            if(ds.getKey().equals(mContext.getString(R.string.dnname_user_account_settings))){ //user account settings node
//                Log.d(TAG, "getUserAccountSettings: datasnapshot: " + ds); //useful for debugging
//                //Log.d(TAG, "getUserAccountSettings: datasnapshot: " + ds.child(userID)); //useful for debugging
//
//                try{
//
//                    settings.setDisplay_name(
//                        ds.child(userID)
//                                .getValue(UserAccountSettings.class)
//                                .getDisplay_name()
//                    );
//
//                    settings.setUsername(
//                            ds.child(userID)
//                                    .getValue(UserAccountSettings.class)
//                                    .getUsername()
//                    );
//
//                    settings.setWebsite(
//                            ds.child(userID)
//                                    .getValue(UserAccountSettings.class)
//                                    .getWebsite()
//                    );
//
//                    settings.setDescription(
//                            ds.child(userID)
//                                    .getValue(UserAccountSettings.class)
//                                    .getDescription()
//                    );
//
//                    settings.setProfile_photo(
//                            ds.child(userID)
//                                    .getValue(UserAccountSettings.class)
//                                    .getProfile_photo()
//                    );
//
//                    settings.setPosts(
//                            ds.child(userID)
//                                    .getValue(UserAccountSettings.class)
//                                    .getPosts()
//                    );
//
//                    settings.setFollowing(
//                            ds.child(userID)
//                                    .getValue(UserAccountSettings.class)
//                                    .getFollowing()
//                    );
//
//                    settings.setFollowers(
//                            ds.child(userID)
//                                    .getValue(UserAccountSettings.class)
//                                    .getFollowers()
//                    );
//
//                    Log.d(TAG, "getUserAccountSettings: retrieved user_account_settings information " + settings.toString());
//
//                }catch (NullPointerException e){
//                    Log.e(TAG, "getUserAccountSettings: NullPointerException: " + e.getMessage() );
//                }
//
//            }
//
//            // users node
//            if(ds.getKey().equals(mContext.getString(R.string.dnname_users))) { //user account settings node
//                Log.d(TAG, "getUserAccountSettings: datasnapshot: " + ds); //useful for debugging
//
//                user.setUsername(
//                        ds.child(userID)
//                                .getValue(User.class)
//                                .getUsername()
//                );
//
//                user.setEmail(
//                        ds.child(userID)
//                                .getValue(User.class)
//                                .getEmail()
//                );
//
//                user.setPhone_number(
//                        ds.child(userID)
//                                .getValue(User.class)
//                                .getPhone_number()
//                );
//
//                user.setUser_id(
//                        ds.child(userID)
//                                .getValue(User.class)
//                                .getUser_id()
//                );
//
//                Log.d(TAG, "getUserAccountSettings: retrieved user information " + user.toString());
//
//            }
//        }
//
//        return new UserSettings(user, settings);
//    }

}










