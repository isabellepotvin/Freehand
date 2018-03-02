package com.team06.freehand;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.team06.freehand.Explore.ExploreActivity;
import com.team06.freehand.Utils.UniversalImageLoader;

public class HomeActivity extends AppCompatActivity {

    private Button eButton;
    private Context mContext = HomeActivity.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initImageLoader(); //initializes universal image loader


        //if user is logged in, then open explore page
        eButton = (Button) findViewById(R.id.explore_button);
        eButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openExploreActivity();
            }
        });


        //if user is not logged in, then open login

    }

    /**
     * Universal Image Loader
     */
    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }


    //method that opens the Explore Activity
    public void openExploreActivity() {
        Intent intent = new Intent(this, ExploreActivity.class);
        startActivity(intent);
    }





}
