package com.sdpm.feedly.feedly;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.sdpm.feedly.utils.ChangeTheme;

import static com.sdpm.feedly.feedly.R.id.rgroup;

public class DefaultViewActivity extends AppCompatActivity {

    RadioGroup rg;
    RadioButton rb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChangeTheme.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_default_view);

        RadioButton default_rb = (RadioButton) findViewById(R.id.rb_home);
        int def_rb = default_rb.getId();

        SharedPreferences userDetails = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        long rb_button = userDetails.getInt("default_view",def_rb);

        rg = (RadioGroup) findViewById(rgroup);
        rg.check((int) rb_button);

    }


    public void rbclick(View view) {
        int radiobuttonid = rg.getCheckedRadioButtonId();
        rb = (RadioButton) findViewById(radiobuttonid);

        SharedPreferences userDetails = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();
        long rb_button = userDetails.getInt("default_view",0);
        edit.putInt("default_view",rb.getId());
        edit.apply();

        Toast.makeText(getApplicationContext(),rb.getText(),Toast.LENGTH_SHORT).show();

    }
}

