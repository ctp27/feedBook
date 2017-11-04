package com.sdpm.feedly.feedly;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initializing TextViews

        TextView tv_theme = (TextView) findViewById(R.id.tv_theme);
        TextView tv_defaultView = (TextView) findViewById(R.id.tv_defaultView);
        TextView tv_changePassword = (TextView) findViewById(R.id.tv_changePassword);

        // Change Theme functionality goes here


        // Change View functionality goes here


        // Change Password Functionality goes here

        tv_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);

            }
        });

    }
}
