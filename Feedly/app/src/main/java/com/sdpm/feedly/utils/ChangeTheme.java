package com.sdpm.feedly.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.sdpm.feedly.feedly.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Dan on 11/9/2017.
 */

public class ChangeTheme {
    private static int sTheme;

    public final static int THEME_MATERIAL_LIGHT = 0;
    public final static int THEME_MATERIAL_DARK = 1;

    public static void switchTheme(Activity activity) {
        SharedPreferences userDetails = activity.getSharedPreferences("LoginInfo", MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();
        if (sTheme == 0) {
            sTheme = 1;
        }
        else {
            sTheme = 0;
        }
        edit.putInt("theme",sTheme);
        edit.apply();
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        SharedPreferences userDetails = activity.getSharedPreferences("LoginInfo", MODE_PRIVATE);
        sTheme = userDetails.getInt("theme",0);
        switch (sTheme) {
            default:
            case THEME_MATERIAL_LIGHT:
                activity.setTheme(R.style.Theme_Material_Light);
                break;
            case THEME_MATERIAL_DARK:
                activity.setTheme(R.style.Theme_Material_Dark);
                break;
        }
    }

}

