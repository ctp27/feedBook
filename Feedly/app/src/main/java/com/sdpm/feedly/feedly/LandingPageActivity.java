package com.sdpm.feedly.feedly;

import android.content.Intent;
import android.content.SharedPreferences;
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
        SharedPreferences userDetails = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        String email = userDetails.getString("email", "");

        Intent intent = null;
        if(email.equals("")){
            intent = new Intent(LandingPageActivity.this,LoginActivity.class);
        }else{
            intent = new Intent(LandingPageActivity.this,HomeNav.class);
        }

            startActivity(intent);

    }
}
