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


    /**
     * Update users node for the current user
     * @param name
     * @param age
     * @param location
     * @param description
     */
    public void updateUser(String name, long age, String location, String description){
        Log.d(TAG, "updateUser: updating user infomration.");

        //updates users information in database

        if(name != null) {
            myRef.child(mContext.getString(R.string.dnname_users))
                    .child(userID)
                    .child(mContext.getString(R.string.field_name))
                    .setValue(name);
        }

        if(age != 0) {
            myRef.child(mContext.getString(R.string.dnname_users))
                    .child(userID)
                    .child(mContext.getString(R.string.field_age))
                    .setValue(age);
        }

        if(location != null) {
            myRef.child(mContext.getString(R.string.dnname_users))
                    .child(userID)
                    .child(mContext.getString(R.string.field_location))
                    .setValue(location);
        }

        if(description != null) {
            myRef.child(mContext.getString(R.string.dnname_users))
                    .child(userID)
                    .child(mContext.getString(R.string.field_description))
                    .setValue(description);
        }

    }



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

    /**
     * Retrieves the account settings for the user currently logged in
     * Database: user_account_settings node
     * @param dataSnapshot
     * @return
     */
    public User getUserInfo(DataSnapshot dataSnapshot){
        Log.d(TAG, "getUserAccountSettings: retrieving user account settings from firebase.");

        User user = new User();

        for(DataSnapshot ds: dataSnapshot.getChildren()){

            // users node
            if(ds.getKey().equals(mContext.getString(R.string.dnname_users))) { //user account settings node
                Log.d(TAG, "getUserAccountSettings: datasnapshot: " + ds); //useful for debugging

                user.setUser_id(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUser_id()
                );

                user.setEmail(
                        ds.child(userID)
                                .getValue(User.class)
                                .getEmail()
                );

                user.setName(
                        ds.child(userID)
                                .getValue(User.class)
                                .getName()
                );

                user.setAge(
                        ds.child(userID)
                                .getValue(User.class)
                                .getAge()
                );

                user.setLocation(
                        ds.child(userID)
                                .getValue(User.class)
                                .getLocation()
                );

                user.setDescription(
                        ds.child(userID)
                                .getValue(User.class)
                                .getDescription()
                );

                user.setPictures_number(
                        ds.child(userID)
                                .getValue(User.class)
                                .getPictures_number()
                );

                user.setProfile_photo(
                        ds.child(userID)
                                .getValue(User.class)
                                .getProfile_photo()
                );


                Log.d(TAG, "getUserAccountSettings: retrieved user information " + user.toString());

            }
        }

        return user;
    }

}










