package com.sdpm.feedly.feedly;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Initialising Fields & Buttons
        final EditText et_email = (EditText) findViewById(R.id.email_id);
        final EditText et_password = (EditText) findViewById(R.id.password);
        final Button b_login = (Button) findViewById(R.id.button_login);
        final Button b_sign_up = (Button) findViewById(R.id.button_sign_up);

        // Dummy Data for login
        final String dummy_user="feedly";
        final String dummy_pass="feedly";


        // Login Button
        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = et_email.getText().toString();
                final String password = et_password.getText().toString();
                Log.d("tag",email);
                Log.d("tag",password);

                // Login Authentication
                if(email.equals(dummy_user) && password.equals(dummy_pass)){

                    Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, HomeNav.class);
                    startActivity(intent);

                }

                else{
                    Toast.makeText(getApplicationContext(),"Invalid Login",Toast.LENGTH_SHORT).show();
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


    }

}
