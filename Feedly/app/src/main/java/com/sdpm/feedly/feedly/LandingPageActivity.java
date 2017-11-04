package com.sdpm.feedly.feedly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LandingPageActivity extends AppCompatActivity {

    public static final String token = "Logged";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        /**
         *
         * Check if file exists in memory.
         * If yes, login and redirect to homepage
         * If not, redirect to login page
         */

        Intent intent = null;
        if(token.equalsIgnoreCase("logged")){
            intent = new Intent(LandingPageActivity.this,HomeNav.class);
        }else{
            intent = new Intent(LandingPageActivity.this,LoginActivity.class);
        }

            startActivity(intent);

    }
}
