package com.sdpm.feedly.feedly;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import model.Article;
import model.Feed;

public class Home extends AppCompatActivity {

    List<Article> articles = new ArrayList<Article>();
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
        })
        ;
        /**************************************** put data from parsed xml in the feeds list *************************/
        /***********************************************************************************************************/
        Article f = new Article("TITLE: This is a title This is a title his is a title this is a title this is a title","",0,"this is a description this is a description this is a description this is a description this is a description","");
        for(int i=0;i<20;i++){
            articles.add(f);
        }
        /***********************************************************************************************************/
        /***********************************************************************************************************/

        RecyclerView rv = (RecyclerView) findViewById(R.id.rvFeeds);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);


        RVAdapter adapter = new RVAdapter(articles);
        rv.setAdapter(adapter);


    }
}
