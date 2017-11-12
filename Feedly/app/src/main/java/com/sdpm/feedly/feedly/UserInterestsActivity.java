package com.sdpm.feedly.feedly;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UserInterestsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interests);

        // Initialising fields
        final CheckBox cb_cooking = (CheckBox) findViewById(R.id.cb_cooking);
        final CheckBox cb_film = (CheckBox) findViewById(R.id.cb_film);
        final CheckBox cb_gaming = (CheckBox) findViewById(R.id.cb_gaming);
        final CheckBox cb_science = (CheckBox) findViewById(R.id.cb_science);
        final CheckBox cb_marketing = (CheckBox) findViewById(R.id.cb_marketing);
        final CheckBox cb_tech = (CheckBox) findViewById(R.id.cb_tech);
        final CheckBox cb_news = (CheckBox) findViewById(R.id.cb_news);
        final CheckBox cb_culture = (CheckBox) findViewById(R.id.cb_culture);
        Button b_submit = (Button) findViewById(R.id.b_select_interests);

        // Initialising user interests array
        final List<String> selectedInterests = new ArrayList<String>();

        b_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Checking User interests from checkboxes
                if (cb_cooking.isChecked()){
                    selectedInterests.add(cb_cooking.getText().toString());
                }
                if (cb_film.isChecked()){
                    selectedInterests.add(cb_film.getText().toString());
                }
                if (cb_gaming.isChecked()){
                    selectedInterests.add(cb_gaming.getText().toString());
                }
                if (cb_science.isChecked()){
                    selectedInterests.add(cb_science.getText().toString());
                }
                if (cb_marketing.isChecked()){
                    selectedInterests.add(cb_marketing.getText().toString());
                }
                if (cb_tech.isChecked()){
                    selectedInterests.add(cb_tech.getText().toString());
                }
                if (cb_news.isChecked()){
                    selectedInterests.add(cb_news.getText().toString());
                }
                if (cb_culture.isChecked()){
                    selectedInterests.add(cb_culture.getText().toString());
                }

                // Insert User interests array into database here
                Log.d("Tag", String.valueOf(selectedInterests));

                // Insert statement goes here //

                // Redirecting to the Home Page
                Intent intent = new Intent(UserInterestsActivity.this, HomeNav.class);
                startActivity(intent);

            }
        });

        }

    }

