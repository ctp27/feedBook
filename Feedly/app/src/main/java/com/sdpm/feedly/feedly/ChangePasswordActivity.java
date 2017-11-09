package com.sdpm.feedly.feedly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

public class ChangePasswordActivity extends AppCompatActivity {

    DatabaseReference database;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change Password");
        final SharedPreferences userDetails = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        email = userDetails.getString("email", "");
        // Initializing Fields & Button

        final EditText et_oldPassword = (EditText) findViewById(R.id.et_oldPassword);
        final EditText et_newPassword = (EditText) findViewById(R.id.et_newPassword);
        final EditText et_confirmPassword = (EditText) findViewById(R.id.et_confirmPassword);
        Button b_updatePassword = (Button) findViewById(R.id.button_updatePassword);


        // Update Password Button Functionality

        b_updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String oldPassword = et_oldPassword.getText().toString();
                final String newPassword = et_newPassword.getText().toString();
                String confirmPassword = et_confirmPassword.getText().toString();

                if(newPassword.equals(confirmPassword)){
                    database = FirebaseDatabase.getInstance().getReference();
                    database.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DataSnapshot userSnapShot = null;
                            DatabaseReference userDbRef = null;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if(snapshot.child("email_id").getValue().toString().equals(email)){
                                    userSnapShot = snapshot;
                                    userDbRef = database.child("Users").child(snapshot.getKey());
                                    break;
                                }
                            }
                            if(userSnapShot != null && userDbRef != null) {
                                if (userSnapShot.child("password").getValue().toString().equals(oldPassword)) {
                                    if(oldPassword.equals(newPassword)){
                                        Toast.makeText(getApplicationContext(), "Old password and New password are same", Toast.LENGTH_SHORT).show();
                                    }else {
                                        userDbRef.child("password").setValue(newPassword);
                                        Toast.makeText(getApplicationContext(), "Your password has been updated successfully", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(),"Enter correct Old Password",Toast.LENGTH_SHORT).show();
                                }
                             } else {
                                Toast.makeText(getApplicationContext(),"System Malfunction",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError d){
                            Log.d("Login DbError Msg ->",d.getMessage());
                            Log.d("Login DbError Detail ->",d.getDetails());
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(),"New Password and Confirm Password do not match",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
