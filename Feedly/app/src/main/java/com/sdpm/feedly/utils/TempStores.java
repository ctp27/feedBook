package com.sdpm.feedly.utils;

import android.app.Application;

import java.util.HashMap;
import java.util.List;

import com.sdpm.feedly.model.Article;
import com.sdpm.feedly.model.Feed;

/**
 * Created by clinton on 11/8/17.
 */

public class TempStores extends Application {

    private static List<Article> theFeeds;


    private static HashMap<Integer,Article> articleHashMap = new HashMap<>();

    private static HashMap<Integer,Feed> feedHashMap = new HashMap<>();

    public static List<Article> getTheFeeds() {
        return theFeeds;
    }

    public static void setTheFeeds(List<Article> theFeeds) {
        TempStores.theFeeds = theFeeds;
    }


    public static void setFeed(int id,Feed feed ){
            feedHashMap.put(id, feed);
    }

    public static Feed retrieveFeed(int id){
        return feedHashMap.get(id);
    }

    public static void setArticle(int id,Article article){
        articleHashMap.put(id,article);
    }

    public static Article getArticle(int id){
        return articleHashMap.get(id);
    }

}
