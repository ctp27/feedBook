package com.sdpm.feedly.feedly;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.sdpm.feedly.utils.DownloadXml;

import java.util.ArrayList;

import model.Article;
import model.Feed;

public class Home extends AppCompatActivity {

    private ArrayList<Feed> theFeeds;
    private String feedUrl;
    private String feedCachedUrl;
    private RecyclerView rv;
    private LinearLayoutManager llm;
    private RVAdapter adapter;

    {
        theFeeds= new ArrayList<>();
        theFeeds.add(new Feed("The Daily Notebook","","","https://mubi.com/notebook/posts.atom",new ArrayList<Article>()));
        theFeeds.add(new Feed("IGN","","","http://feeds.ign.com/ign/articles?format=xml",new ArrayList<Article>()));
        theFeeds.add(new Feed("Food52","","","https://food52.com/blog.rss",new ArrayList<Article>()));
        theFeeds.add(new Feed("Scientific American","","","http://rss.sciam.com/ScientificAmerican-Global?fmt=xml",new ArrayList<Article>()));
        theFeeds.add(new Feed("Eurogamer","","","http://www.eurogamer.net/?format=rss",new ArrayList<Article>()));

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            feedUrl=theFeeds.get(0).getLink();

            rv = (RecyclerView) findViewById(R.id.rvFeeds);
            rv.setHasFixedSize(true);
            llm = new LinearLayoutManager(this);
            rv.setLayoutManager(llm);

            displayFeed();

    }

    /**
     * This method displays downloads and parses all feeds. Currently set to display only one feed.
     * TODO: Modify according to front end. See Download XML class

     */
    private void displayFeed(){

        if(!feedUrl.equalsIgnoreCase(feedCachedUrl)) {
            DownloadXml theDownloader = new DownloadXml(rv,DownloadXml.EXPLORE_FEEDS);
            theDownloader.execute(theFeeds);
            feedCachedUrl = feedUrl;
        }
        else{
            Log.d("display","Page not refreshed");
        }

    }


}
