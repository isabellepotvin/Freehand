package com.team06.freehand.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.team06.freehand.Chat.PrivateChatActivity;
import com.team06.freehand.Login.LoginActivity;
import com.team06.freehand.Models.ChatMessage;
import com.team06.freehand.Models.Photo;
import com.team06.freehand.Models.User;
import com.team06.freehand.Models.UserChats;
import com.team06.freehand.Models.UserLikes;
import com.team06.freehand.Profile.AccountSettingsActivity;
import com.team06.freehand.Profile.ProfileActivity;
import com.team06.freehand.R;
import com.team06.freehand.Share.NextActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.transform.Templates;

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
    private StorageReference mStorageReference;
    private String userID;

    //vars
    private double mPhotoUploadProgress = 0;
    private Context mContext;

    //constructor
    public FirebaseMethods(Context context){
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }



    // - - - - - - - - - - - - - - - - - - - - - - - - - - Chat Related Methods - - - - - - - - - - - - - - - - - - - - - - - - - -

    public void uploadNewMessage(Bitmap bm, final int count, final String chatID, final String otherUserID){

        Log.d(TAG, "uploadNewMessage: uploading new message bitmap.");

        Toast.makeText(mContext, "Sending...", Toast.LENGTH_SHORT).show();

        FilePaths filePaths = new FilePaths();

        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_MESSAGE_STORAGE + "/" + chatID + "/" + (count + 1));

        byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100, mContext);

        if(bytes != null) {
            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(mContext, "Drawing sent", Toast.LENGTH_SHORT).show();

                    //add the new drawing to 'chats' node
                    uploadNewMessageInfo(chatID, firebaseUrl.toString(), otherUserID);

                    //navigate the user back to the private chat
                    ((Activity) mContext).finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Sorry, we were unable to send your drawing", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    //if(progress - 5 > mPhotoUploadProgress){
                    //    Toast.makeText(mContext, "Sending drawing: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                    //    mPhotoUploadProgress = progress;
                    //}

                    //Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });
        }

    }

    public void uploadNewMessageInfo(String chatID, String url, String otherUserID){
        Log.d(TAG, "uploadNewMessage: attempting to upload new message.");

        String timestamp = getTimestamp();
        String newMessageKey = myRef.child(mContext.getString(R.string.dbname_chats))
                .child(chatID)
                .push().getKey();

        ChatMessage chatMessage = new ChatMessage();

        chatMessage.setImage_path(url);
        chatMessage.setSender_user_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        chatMessage.setReceiver_user_id(otherUserID);
        chatMessage.setTimestamp(timestamp);

        //insert into database
        myRef.child(mContext.getString(R.string.dbname_chats))
                .child(chatID)
                .child(newMessageKey).setValue(chatMessage);

        //updates "last_timestamp" for each user
        myRef.child(mContext.getString(R.string.dbname_user_chats))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(otherUserID)
                .child(mContext.getString(R.string.field_last_timestamp))
                .setValue(timestamp);

        myRef.child(mContext.getString(R.string.dbname_user_chats))
                .child(otherUserID)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_last_timestamp))
                .setValue(timestamp);
    }

    public void createNewChat(String otherUserID){
        Log.d(TAG, "createNewChat: attempting to create new chat.");

        String newChatKey = myRef.child(mContext.getString(R.string.dbname_chats))
                .push().getKey();

        String timestamp = getTimestamp();

        //insert into database for current user
        myRef.child(mContext.getString(R.string.dbname_user_chats))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(otherUserID)
                .setValue(new UserChats(newChatKey, otherUserID, timestamp));

        //insert into database for other user
        myRef.child(mContext.getString(R.string.dbname_user_chats))
                .child(otherUserID)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(new UserChats(newChatKey, FirebaseAuth.getInstance().getCurrentUser().getUid(), timestamp));

    }



    // - - - - - - - - - - - - - - - - - - - - - - - - - - Explore Related Methods - - - - - - - - - - - - - - - - - - - - - - - - - -

    public void saveLike(String otherUserID){

        String timestamp = getTimestamp();

        //save like to database
        myRef.child(mContext.getString(R.string.dbname_user_likes))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(otherUserID)
                .setValue(new UserLikes(otherUserID, timestamp));

    }


    // - - - - - - - - - - - - - - - - - - - - - - - - - - Photo Related Methods - - - - - - - - - - - - - - - - - - - - - - - - - -


    public void deletePhoto(final String photoID){

        FilePaths filePaths = new FilePaths();
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference photoRef = mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/" + photoID);

        //delete from storage
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                //delete from database
                myRef.child(mContext.getString(R.string.dbname_user_photos))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(photoID).removeValue();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Photo delete failed.");
                Toast.makeText(mContext, "Sorry, we were unable to delete your image", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void uploadNewPhoto(String photoType, final int count, final String imgUrl, Bitmap bm){
        Log.d(TAG, "uploadNewPhoto: attempting to upload new photo.");

        FilePaths filePaths = new FilePaths();

        //case 1: new photo
        if(photoType.equals(mContext.getString(R.string.new_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Log.d(TAG, "uploadNewPhoto: user id" + FirebaseAuth.getInstance().getCurrentUser().getUid());

            final String newPhotoKey = generateNewPhotoKey();

            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/" + newPhotoKey);


            if(bm == null){
                bm = ImageManager.getBitmap(imgUrl, mContext); //convert image url to bitmap
            }

            if(bm != null) {

                byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100, mContext);

                if(bytes != null) {
                    UploadTask uploadTask = null;
                    uploadTask = storageReference.putBytes(bytes);


                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                            Toast.makeText(mContext, "Photo upload success", Toast.LENGTH_SHORT).show();

                            //add the new photo to 'user_photos' node
                            addPhotoToDatabase(firebaseUrl.toString(), newPhotoKey);

                            //navigate to the profile so the user can see their photo
                            Intent intent = new Intent(mContext, ProfileActivity.class);

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            mContext.startActivity(intent);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Photo upload failed.");
                            Toast.makeText(mContext, "Photo upload failed", Toast.LENGTH_SHORT).show();


                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            if (progress - 15 > mPhotoUploadProgress) {
                                Toast.makeText(mContext, "Upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                                mPhotoUploadProgress = progress;
                            }

                            Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                        }
                    });
                }
            }

        }

        //case 2: new profile photo
        else if(photoType.equals(mContext.getString(R.string.profile_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading new PROFILE photo.");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

            if(bm == null){
                bm = ImageManager.getBitmap(imgUrl, mContext); //convert image url to bitmap
            }

            if(bm !=null) {
                byte[] bytes = ImageManager.getBytesFromBitmap(bm, 80, mContext);

                if(bytes != null) {
                    UploadTask uploadTask = null;
                    uploadTask = storageReference.putBytes(bytes);


                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                            Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                            //insert into 'user_account_settings' node
                            setProfilePhoto(firebaseUrl.toString());

                            //sets the viewpager to the edit_profile_fragment
                            ((AccountSettingsActivity) mContext).setViewPager(
                                    ((AccountSettingsActivity) mContext).pagerAdapter
                                            .getFragmentNumber(mContext.getString(R.string.edit_profile_fragment))
                            );

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Photo upload failed.");
                            Toast.makeText(mContext, "Photo upload failed: ", Toast.LENGTH_SHORT).show();


                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            if (progress - 15 > mPhotoUploadProgress) {
                                Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                                mPhotoUploadProgress = progress;
                            }

                            Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                        }
                    });
                }
            }

        }

    }


    private void setProfilePhoto(String url){
        Log.d(TAG, "setProfilePhoto: settings new profile image: " + url);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(url);
    }

    private String generateNewPhotoKey(){
        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();

        return newPhotoKey;
    }

    private void addPhotoToDatabase(String url, String newPhotoKey){
        Log.d(TAG, "addPhotoToDatabase: adding photo to database.");

        Photo photo = new Photo();
        photo.setDate_created(getTimestamp());
        photo.setImage_path(url);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);

        //insert into database
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(newPhotoKey).setValue(photo);

    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Eastern"));
        return sdf.format(new Date());
    }

    public Date getDateFromTimestamp(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Eastern"));

        try {
            return sdf.parse(date);
        }catch (ParseException e){
            Log.e(TAG, "getDateFromTimestamp: " + e.getMessage());
            return null;
        }
    }

    public int getImageCount(DataSnapshot dataSnapshot, String chatID){
        int count = 0;

        if(chatID == null) {
            for (DataSnapshot ds : dataSnapshot
                    .child(mContext.getString(R.string.dbname_user_photos))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .getChildren()) {
                count++;
            }
        }
        else {
            for (DataSnapshot ds : dataSnapshot
                    .child(mContext.getString(R.string.dbname_chats))
                    .child(chatID)
                    .getChildren()) {
                count++;
            }
        }

        return count;
    }


    // - - - - - - - - - - - - - - - - - - - - - - - - - - User Related Methods - - - - - - - - - - - - - - - - - - - - - - - - - -

    public void resetPassword(String emailAddress ){

        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(mContext, "Password reset email sent", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updatePassword(String newPassword){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");

                            Toast.makeText(mContext, "Password updated", Toast.LENGTH_SHORT).show();
                            ((Activity)mContext).finish();
                        }
                        else{
                            Log.d(TAG, "onComplete: Failed to update password.");
                            //Toast.makeText(mContext, "Sorry, we couldn't update your password", Toast.LENGTH_SHORT).show();
                            displayAuthErrorMessages(task);
                        }
                    }
                });

    }

//      if i were to add the feature to delete an account, I would need to delete more information from the
//      database such as chats, pictures, etc.

//      public void deleteUser(){
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        user.delete()
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "User account deleted.");
//
//                            //delete from database
//                            myRef.child(mContext.getString(R.string.dbname_user_photos))
//                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
//
//                        }
//                        else{
//                            Log.d(TAG, "onFailure: Account delete failed.");
//                            Toast.makeText(mContext, "Sorry, we were unable to delete your account", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "onFailure: Account delete failed.");
//                Toast.makeText(mContext, "Sorry, we were unable to delete your account", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }


    /**
     * Update users node for the current user
     * @param name
     * @param age
     * @param location
     * @param description
     */
    public void updateUser(String name, long age, String location, String description){
        Log.d(TAG, "updateUser: updating user information.");

        //updates users information in database

        if(name != null) {
            myRef.child(mContext.getString(R.string.dbname_users))
                    .child(userID)
                    .child(mContext.getString(R.string.field_name))
                    .setValue(name);
        }

        if(age != 0) {
            myRef.child(mContext.getString(R.string.dbname_users))
                    .child(userID)
                    .child(mContext.getString(R.string.field_age))
                    .setValue(age);
        }

        if(location != null) {
            myRef.child(mContext.getString(R.string.dbname_users))
                    .child(userID)
                    .child(mContext.getString(R.string.field_location))
                    .setValue(location);
        }

        if(description != null) {
            myRef.child(mContext.getString(R.string.dbname_users))
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
        myRef.child(mContext.getString(R.string.dbname_users))
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

                            displayAuthErrorMessages(task);
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

    public <T> void displayAuthErrorMessages(@NonNull Task<T> task){
        try {
            throw task.getException();
        } catch(FirebaseAuthWeakPasswordException e) {
            Toast.makeText(mContext, e.getReason(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "task failure: " + e.getErrorCode());
            Log.d(TAG, "task failure: " + e.getReason());

        } catch(FirebaseAuthActionCodeException e) {
            Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "task failure: " + e.getErrorCode());

        } catch(FirebaseAuthEmailException e) {
            Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "task failure: " + e.getErrorCode());

        } catch(FirebaseAuthInvalidCredentialsException e) {
            Log.d(TAG, "task failure: " + e.getErrorCode());

            if(e.getErrorCode().equals("ERROR_WRONG_PASSWORD")){
                Toast.makeText(mContext, "Wrong Password", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

        } catch(FirebaseAuthInvalidUserException e) {
            Log.d(TAG, "signInWithEmail:failure: " + e.getErrorCode());
            Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

        } catch(FirebaseAuthRecentLoginRequiredException e) {
            Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "task failure: " + e.getErrorCode());

        } catch(FirebaseAuthUserCollisionException e) {
            Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "task failure: " + e.getErrorCode());
        } catch(Exception e) {
            Toast.makeText(mContext, mContext.getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "task failure: " + e.getMessage());
        }

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
                                Toast.makeText(mContext, "Couldn't send verification email", Toast.LENGTH_SHORT).show();
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
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);
    }

    /**
     * Retrieves the account settings for the user currently logged in
     * Database: user node
     * @param dataSnapshot
     * @return
     */
    public User getUserInfo(DataSnapshot dataSnapshot, String userID){
        Log.d(TAG, "getUserInfo: retrieving user information from firebase.");

        User user = new User();

        for(DataSnapshot ds: dataSnapshot.getChildren()){

            // users node
            if(ds.getKey().equals(mContext.getString(R.string.dbname_users))) { //user node
                Log.d(TAG, "getUserInfo: datasnapshot: " + ds); //useful for debugging

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


                Log.d(TAG, "getUserInfo: retrieved user information " + user.toString());

            }
        }

        return user;
    }


}










