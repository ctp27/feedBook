package com.sdpm.feedly.utils;

import com.sdpm.feedly.model.Article;
import com.sdpm.feedly.model.Feed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clinton on 11/25/17.
 */

public class JsonParser {

    private static final String TAG = JsonParser.class.getSimpleName();
    private static final String DESCRIPTION_TAG = "description";
    private static final String AUTHOR_TAG = "name";
    private static final String TITLE_TAG = "title";
    private static final String DATE_TAG = "publishedAt";
    private static final String LINK_TAG = "url";
    private static final String THUMBNAIL_LINK_TAG = "urlToImage";
    private static final String SOURCE_TAG = "source";

    public static List<Feed> getNewsFeedFromJson(String JsonString, String location) throws JSONException{


        List<Feed> feedList = new ArrayList<>();
        Feed newsFeed = new Feed("Local News","News","","",null);

        ArrayList<Article> newsArticles = new ArrayList<>();

        JSONObject mainObject = new JSONObject(JsonString);

        if(mainObject.getString("status").equalsIgnoreCase("error")){
            return null;
        }

        JSONArray newsResultsArray = mainObject.getJSONArray("articles");

        for(int i = 0;i<newsResultsArray.length();i++){
            String link=null;
            String thumbnailLink = null;
            String description = null;
            String author = null;
            String title = null;
            String pubDate = null;
            JSONObject thisObject = newsResultsArray.getJSONObject(i);

            JSONObject sourceObject = thisObject.getJSONObject(SOURCE_TAG);

            if(!sourceObject.isNull(AUTHOR_TAG) && !sourceObject.getString(AUTHOR_TAG).isEmpty()) {
                author = sourceObject.getString(AUTHOR_TAG);
            }

            if(!thisObject.isNull(TITLE_TAG) && !thisObject.getString(TITLE_TAG).isEmpty()) {
                title = thisObject.getString(TITLE_TAG);
            }

            if(!thisObject.isNull(DESCRIPTION_TAG) && !thisObject.getString(DESCRIPTION_TAG).isEmpty()) {
                description = thisObject.getString(DESCRIPTION_TAG);
            }


            if(!thisObject.isNull(LINK_TAG) && !thisObject.getString(LINK_TAG).isEmpty()){
                link = thisObject.getString(LINK_TAG);
            }


            if(!thisObject.isNull(THUMBNAIL_LINK_TAG) && !thisObject.getString(THUMBNAIL_LINK_TAG).isEmpty()){
                thumbnailLink = thisObject.getString(THUMBNAIL_LINK_TAG);
            }


            if(!thisObject.isNull(DATE_TAG) && !thisObject.getString(DATE_TAG).isEmpty()) {
                pubDate = thisObject.getString(DATE_TAG);
            }

            newsArticles.add(new Article(title,link,description,author,pubDate,thumbnailLink));
        }

//        for(Article a : newsArticles){
//            Log.d(TAG,a.toString());
//        }

        newsFeed.setArticleList(newsArticles);
        newsFeed.setNewsFeed(true);
        newsFeed.setCategory(location);
        feedList.add(newsFeed);


        return feedList;
    }
}
