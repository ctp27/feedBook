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

public class SignUpActivity extends AppCompatActivity {

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
            @Override
            public void onClick(View view) {

                final String fullname = et_fullname.getText().toString();
                final String email = et_email_id.getText().toString();
                final String password = et_password.getText().toString();

                Log.d("tag",fullname);
                Log.d("tag",email);
                Log.d("tag",password);

                boolean success = true;

                if (success) {
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

            }
        });
    }
}

