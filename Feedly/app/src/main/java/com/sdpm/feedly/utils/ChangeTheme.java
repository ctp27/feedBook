package com.sdpm.feedly.utils;

import android.app.Activity;
import android.content.Intent;

import com.sdpm.feedly.feedly.R;

/**
 * Created by Dan on 11/9/2017.
 */

public class ChangeTheme {
    private static int sTheme;

    public final static int THEME_MATERIAL_LIGHT = 0;
    public final static int THEME_MATERIAL_DARK = 1;

    public static void switchTheme(Activity activity) {
        if (sTheme == 0) {
            sTheme = 1;
        }
        else {
            sTheme = 0;
        }
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    public static void onActivityCreateSetTheme(Activity activity) {
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

