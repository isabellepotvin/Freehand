package com.team06.freehand.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.team06.freehand.Chat.ChatActivity;
import com.team06.freehand.Explore.ExploreActivity;
import com.team06.freehand.Profile.ProfileActivity;
import com.team06.freehand.R;

/**
 * Created by isabellepotvin on 2018-02-20.
 */

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "setupBottomNavigationView: Setting up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);

       bottomNavigationViewEx.setIconSize(30.0f, 30.0f);
       bottomNavigationViewEx.setIconSizeAt(1, 33.0f, 33.0f);
       bottomNavigationViewEx.setIconsMarginTop(10);
       bottomNavigationViewEx.setTextSize(0.0f);

    }



    //*******************************
    //  Navigate between Activities
    //*******************************

    public static void enableNavigation(final Context context, final Activity callingActivity, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.ic_explore:
                        Intent intent1 = new Intent(context, ExploreActivity.class); //ACTIVITY_NUM = 0
                        context.startActivity(intent1);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_chat:
                        Intent intent2 = new Intent(context, ChatActivity.class); //ACTIVITY_NUM = 1
                        context.startActivity(intent2);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_profile:
                        Intent intent3 = new Intent(context, ProfileActivity.class); //ACTIVITY_NUM = 2
                        context.startActivity(intent3);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                }


                return false;
            }
        });
    }





}





















