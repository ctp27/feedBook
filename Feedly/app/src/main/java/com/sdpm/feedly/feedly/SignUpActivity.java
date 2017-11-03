package com.sdpm.feedly.feedly;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    // setting registration success to false as default
    boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialising Fields & Button

        final EditText et_fullname = (EditText) findViewById(R.id.full_name);
        final EditText et_email_id = (EditText) findViewById(R.id.email_id);
        final EditText et_password = (EditText) findViewById(R.id.password);
        final Button button_register = (Button) findViewById(R.id.button_register);

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
                    if (email.contains("@")){
                        success = true;
                    }

                    // Registration is successful; changing success to true
                    else {
                        Toast.makeText(getApplicationContext(), "Email-ID doesn't seem right", Toast.LENGTH_SHORT).show();
                        success=false;
                    }
                }

                // Navigating to the Home Page
                if (success) {

                    Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUpActivity.this, HomeNav.class);
                    startActivity(intent);
                }

            }
        });
    }
}

