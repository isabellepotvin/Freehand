package com.team06.freehand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button eButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


    //method that opens the Explore Activity
    public void openExploreActivity() {
        Intent intent = new Intent(this, ExploreActivity.class);
        startActivity(intent);
    }





}
