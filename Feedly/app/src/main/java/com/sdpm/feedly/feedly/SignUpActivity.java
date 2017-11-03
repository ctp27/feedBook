package com.sdpm.feedly.feedly;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialising Fields

        final EditText et_fullname = (EditText) findViewById(R.id.full_name);
        final EditText et_email_id = (EditText) findViewById(R.id.email_id);
        final EditText et_password = (EditText) findViewById(R.id.password);
        final TextView et_sign_up_message = (TextView) findViewById(R.id.sign_up_message);
        final Button button_register = (Button) findViewById(R.id.button_register);

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

                if(fullname.equals("") && email.equals("") && password.equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter your details", Toast.LENGTH_SHORT).show();
                    success=false;
                    return;
                }

                else if (fullname.matches("")) {
                    Toast.makeText(getApplicationContext(), "You did not enter your name", Toast.LENGTH_SHORT).show();
                    success=false;
                    return;
                }

                else if (email.matches("")){
                    Toast.makeText(getApplicationContext(), "You did not enter the email-ID", Toast.LENGTH_SHORT).show();
                    success=false;
                    return;
                }

                else if (password.matches("")){
                    Toast.makeText(getApplicationContext(), "You did not enter a password", Toast.LENGTH_SHORT).show();
                    success=false;
                    return;
                }

                else{
                    if (email.contains("@")){
                        success = true;
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Email-ID doesn't seem right", Toast.LENGTH_SHORT).show();
                        success=false;
                    }
                }

                if (success) {

                    Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUpActivity.this, HomeNav.class);
                    startActivity(intent);
                }

            }
        });
    }
}

