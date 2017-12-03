package com.sdpm.feedly.feedly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sdpm.feedly.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserInterestsActivity extends AppCompatActivity {

    private User thisUser;
    private CheckBox cb_cooking;
    private CheckBox cb_film;
    private CheckBox cb_gaming;
    private CheckBox cb_science;
    private CheckBox cb_marketing;
    private CheckBox cb_tech;
    private CheckBox cb_news;
    private CheckBox cb_culture;
    private  List<Object> selectedInterests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interests);
        getUserFromPreviousActivity();
        // Initialising fields
        cb_cooking = (CheckBox) findViewById(R.id.cb_cooking);
        cb_film = (CheckBox) findViewById(R.id.cb_film);
        cb_gaming = (CheckBox) findViewById(R.id.cb_gaming);
        cb_science = (CheckBox) findViewById(R.id.cb_science);
        cb_marketing = (CheckBox) findViewById(R.id.cb_marketing);
        cb_tech = (CheckBox) findViewById(R.id.cb_tech);
        cb_news = (CheckBox) findViewById(R.id.cb_news);
        cb_culture = (CheckBox) findViewById(R.id.cb_culture);
        Button b_submit = (Button) findViewById(R.id.b_select_interests);


        // Initialising user interests array
        selectedInterests = new ArrayList<>();

        b_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                persistAndRedirect();
            }
        });

        }

    private void persistAndRedirect(){
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
        persistDataInDatabase();
        // Redirecting to the Home Page
        Intent intent = new Intent(UserInterestsActivity.this, HomeNav.class);
//                startActivity(intent);
    }



    private void persistDataInDatabase() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        thisUser.setPreferences(selectedInterests);
        database.child("Users").push().setValue(thisUser);
        SharedPreferences userDetails = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();
        edit.clear();
        edit.putString("email",thisUser.getEmail_id());
        edit.commit();

    }

    private void getUserFromPreviousActivity() {

        Intent incomingIntent = getIntent();
        if(incomingIntent!=null){
            if(incomingIntent.hasExtra(SignUpActivity.NEW_USER_KEY)){
                thisUser = (User) incomingIntent.getSerializableExtra(SignUpActivity.NEW_USER_KEY);
            }
        }

    }

}

