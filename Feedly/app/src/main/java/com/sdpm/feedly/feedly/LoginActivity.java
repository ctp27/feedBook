package com.sdpm.feedly.feedly;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Initialising Fields
        final EditText et_email = (EditText) findViewById(R.id.email_id);
        final EditText et_password = (EditText) findViewById(R.id.password);
        final Button b_login = (Button) findViewById(R.id.button_login);
        final Button b_sign_up = (Button) findViewById(R.id.button_sign_up);
        final Button b_forgot_password = (Button) findViewById(R.id.button_forgot_password);


        final String dummy_user="harsha";
        final String dummy_pass="jasti";

        // Buttons
        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = et_email.getText().toString();
                final String password = et_password.getText().toString();

                Log.d("tag",email);
                Log.d("tag",password);


                if(email.equals(dummy_user) && password.equals(dummy_pass)){

//                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                    builder.setMessage("Login Successful")
//                            .create()
//                            .show();


                    Intent intent = new Intent(LoginActivity.this, HomeNav.class);
                    startActivity(intent);

                }

                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Login Failed")
                            .setNegativeButton("Retry",null)
                            .create()
                            .show();
                }

            }
        });

        b_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

//        b_forgot_password.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
//                startActivity(intent);
//
//            }
//        });


    }

}
