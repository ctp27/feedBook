package com.sdpm.feedly.feedly;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sdpm.feedly.utils.ChangeTheme;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChangeTheme.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_change_password);


        // Dummy data for old password
        final String pwd = "jasti";

        // Initializing Fields & Button

        final EditText et_oldPassword = (EditText) findViewById(R.id.et_oldPassword);
        final EditText et_newPassword = (EditText) findViewById(R.id.et_newPassword);
        final EditText et_confirmPassword = (EditText) findViewById(R.id.et_confirmPassword);
        Button b_updatePassword = (Button) findViewById(R.id.button_updatePassword);


        // Update Password Button Functionality

        b_updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String oldPassword = et_oldPassword.getText().toString();
                String newPassword = et_newPassword.getText().toString();
                String confirmPassword = et_confirmPassword.getText().toString();

                if(oldPassword.equals(pwd)){

                    if(newPassword.equals(confirmPassword)){

                        if(oldPassword.equals(newPassword)){

                            Toast.makeText(getApplicationContext(),"This password has already been used, pick a new one",Toast.LENGTH_SHORT).show();

                        }
                        else {

                            // Success
                            Toast.makeText(getApplicationContext(), "Your password has been updated", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }

                    else{

                        Toast.makeText(getApplicationContext(),"Passwords don't match",Toast.LENGTH_SHORT).show();

                    }

                }
                else{

                    Toast.makeText(getApplicationContext(),"Your Old password isn't right",Toast.LENGTH_SHORT).show();

                }



            }
        });


    }

}
