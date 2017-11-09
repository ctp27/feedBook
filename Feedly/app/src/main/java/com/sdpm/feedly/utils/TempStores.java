package com.sdpm.feedly.utils;

import android.app.Application;

import java.util.List;

import model.Article;

/**
 * Created by clinton on 11/8/17.
 */

public class TempStores extends Application {

    private static List<Article> theFeeds;


    public static List<Article> getTheFeeds() {
        return theFeeds;
    }

    public static void setTheFeeds(List<Article> theFeeds) {
        TempStores.theFeeds = theFeeds;
    }
}
