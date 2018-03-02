package com.team06.freehand.Profile;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.team06.freehand.R;
import com.team06.freehand.Utils.BottomNavigationViewHelper;
import com.team06.freehand.Utils.GridImageAdapter;
import com.team06.freehand.Utils.UniversalImageLoader;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private static final int ACTIVITY_NUM = 2;
    private static final int NUM_GRID_COLUMNS = 2;
    private Context mContext = ProfileActivity.this;

    private ProgressBar mProgressBar;
    private ImageView profilePhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: starting.");

        setupBottomNavigationView();

        setupActivityWidgets();
        setProfileImage();
        tempGridSetup();
    }


    private void tempGridSetup(){
        ArrayList<String> imgURLs = new ArrayList<>();

        imgURLs.add("https://mayhemandmuse.com/wp-content/uploads/2012/06/intense-emotional-rainbow-color-painting-gay-rights-woman-girl-watercolor-portrait-painting-art.jpg");
        imgURLs.add("https://s-media-cache-ak0.pinimg.com/736x/03/1b/f4/031bf433b23e4d24a9ed95450d013639.jpg");
        imgURLs.add("https://i.pinimg.com/736x/55/5a/1b/555a1b87d32b1325ad2209038e7cf5d7--beautiful-eyes-pretty-eyes.jpg");
        imgURLs.add("https://steemitimages.com/DQmZsdticAXhtKB7mQk5ZZevnpkNrKxZmKzZXgbmKfCPJHz/art-quiz-bloomberg-question-02.jpg");
        imgURLs.add("https://s-media-cache-ak0.pinimg.com/736x/d4/3c/e0/d43ce059d34b39bef244b92d905bddba--painting-art-ideas-acrylics-artistic-painting.jpg");
        imgURLs.add("http://moziru.com/images/drawn-animal-detailed-drawing-1.jpg");
        imgURLs.add("http://moziru.com/images/drawn-girl-pretty-hair-18.jpg");
        imgURLs.add("https://www.painterartist.com/static/ptr/product_content/painter/2018/bob-ross/gallery/03.jpg");

        setupImageGrid(imgURLs);
    }

    private void setupImageGrid(ArrayList<String> imgURLs){
        GridView gridView = findViewById(R.id.gridView);

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, "", imgURLs);
        gridView.setAdapter(adapter);
    }


    private void setProfileImage(){
        Log.d(TAG, "setProfileImage: setting profile photo.");
        String imgURL = "moziru.com/images/drawn-profile-draw-13.jpg";
        UniversalImageLoader.setImage(imgURL, profilePhoto, null, "https://");
    }

    private void setupActivityWidgets(){
        //hides progress bar
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        profilePhoto = (ImageView) findViewById(R.id.profile_photo);
    }





    // ***************************
    // BottomNavigationView setup
    // ***************************

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
