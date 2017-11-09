package com.sdpm.feedly.utils;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by clinton on 11/8/17.
 */

public class TimeDateUtils {


    public static String getTimePassed(Context context, String theDate){

        DateFormat df  = null;
        Date startDate=null;
        try {
            df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
            startDate = df.parse(theDate);

        } catch (ParseException e) {

            try{
                df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                startDate = df.parse(theDate);
            }catch (ParseException f){
                return "Some time ago..";
            }


        }
        String s = DateUtils.getRelativeDateTimeString(context,startDate.getTime(),DateUtils.HOUR_IN_MILLIS,DateUtils.DAY_IN_MILLIS,0).toString();

        return  s;

    }



}
