package com.sdpm.feedly.feedly;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    DatabaseReference database;
    // setting registration success to false as default
    boolean success = false;
    boolean existingUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialising Fields & Button

        final EditText et_fullname = (EditText) findViewById(R.id.full_name);
        final EditText et_email_id = (EditText) findViewById(R.id.email_id);
        final EditText et_password = (EditText) findViewById(R.id.password);
        final Button button_register = (Button) findViewById(R.id.button_register);
        final Button button_back_to_login = (Button) findViewById(R.id.button_back_to_login);

        // Registration Button
        button_register.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {

                final String fullname = et_fullname.getText().toString();
                final String email = et_email_id.getText().toString();
                final String password = et_password.getText().toString();

                Log.d("tag",fullname);
                Log.d("tag",email);
                Log.d("tag",password);

                // Checking if fields are empty
                if(fullname.equals("") && email.equals("") && password.equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter your details", Toast.LENGTH_SHORT).show();
                    success=false;
                    return;
                }

                // Checking if fullname is empty
                else if (fullname.matches("")) {
                    Toast.makeText(getApplicationContext(), "You did not enter your name", Toast.LENGTH_SHORT).show();
                    success=false;
                    return;
                }

                // Checking if email is empty
                else if (email.matches("")){
                    Toast.makeText(getApplicationContext(), "You did not enter the email-ID", Toast.LENGTH_SHORT).show();
                    success=false;
                    return;
                }
                // Checking if password is empty
                else if (password.matches("")){
                    Toast.makeText(getApplicationContext(), "You did not enter a password", Toast.LENGTH_SHORT).show();
                    success=false;
                    return;
                }

                // All fields are present
                else{

                    // Checking if email Id is valid
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        success = true;
                    }

                    // Registration is successful; changing success to true
                    else {
                        Toast.makeText(getApplicationContext(), "Please enter valid Email Id", Toast.LENGTH_SHORT).show();
                        success=false;
                    }
                }

                // Navigating to the Home Page
                if (success) {
                    database = FirebaseDatabase.getInstance().getReference();
                    existingUser = false;
                    database.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if(snapshot.child("email_id").getValue().toString().equals(email)){
                                    existingUser = true;
                                    break;
                                }
                            }
                            if(existingUser){
                                Toast.makeText(getApplicationContext(),"Email Id is already registered",Toast.LENGTH_SHORT).show();
                            }else {
                                Map m = new HashMap();
                                m.put("full_name",fullname);
                                m.put("email_id",email);
                                m.put("password", password);
                                database.child("Users").push().setValue(m);
                                SharedPreferences userDetails = getSharedPreferences("LoginInfo", MODE_PRIVATE);
                                SharedPreferences.Editor edit = userDetails.edit();
                                edit.clear();
                                edit.putString("email",email);
                                edit.commit();
                                Intent intent = new Intent(SignUpActivity.this, UserInterestsActivity.class);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError d){
                            Log.d("Login DbError Msg ->",d.getMessage());
                            Log.d("Login DbError Detail ->",d.getDetails());
                        }
                    });
                }

            }
        });

        button_back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

    }
}

