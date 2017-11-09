package com.sdpm.feedly.feedly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    DatabaseReference database;
    Boolean isValidUser = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Initialising Fields & Buttons
        final EditText et_emailId = (EditText) findViewById(R.id.email_id);
        final EditText et_password = (EditText) findViewById(R.id.password);
        final Button b_login = (Button) findViewById(R.id.button_login);
        final Button b_sign_up = (Button) findViewById(R.id.button_sign_up);
        final Button b_settings = (Button) findViewById(R.id.button_settings);

        database = FirebaseDatabase.getInstance().getReference();

        // Login Button
        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String emailId = et_emailId.getText().toString();
                final String password = et_password.getText().toString();
                Log.d("tag",emailId);
                Log.d("tag",password);
                if(emailId.equals("") || password.equals("")){
                    Toast.makeText(getApplicationContext(),"Enter Valid Email Id and Password",Toast.LENGTH_SHORT).show();
                } else {
                    isValidUser = false;
                    database.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (snapshot.child("email_id").getValue().toString().equals(emailId) && snapshot.child("password").getValue().toString().equals(password)) {
                                    isValidUser = true;
                                    break;
                                }
                            }
                            if (isValidUser) {
                                SharedPreferences userDetails = getSharedPreferences("LoginInfo", MODE_PRIVATE);
                                SharedPreferences.Editor edit = userDetails.edit();
                                edit.clear();
                                edit.putString("email",emailId);
                                edit.commit();
                                Intent intent = new Intent(LoginActivity.this, HomeNav.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid Email Id or Password", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError d) {
                            Log.d("Login DbError Msg ->", d.getMessage());
                            Log.d("Login DbError Detail ->", d.getDetails());
                        }
                    });
                }
            }
        });

        // SignUp Button
        b_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });

        // Testing Settings Button

        b_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
                startActivity(intent);

            }
        });


    }

}
