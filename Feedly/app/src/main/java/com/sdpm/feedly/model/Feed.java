package com.sdpm.feedly.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Junaid on 10/5/2017.
 */

public class Feed implements Serializable {

    private String id;
    private String name;
    private String category;
    private String description;
    private String link;
    private String theXml;
    private ArrayList<Article> articleList;
    private boolean isNewsFeed;

    public Feed(String name, String category, String description, String link, ArrayList<Article> articleList) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.link = link;
        this.articleList = articleList;
        isNewsFeed = false;
    }

    public Feed(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ArrayList<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(ArrayList<Article> articleList) {
        this.articleList = articleList;
    }

    public String getTheXml() {
        return theXml;
    }

    public void setTheXml(String theXml) {
        this.theXml = theXml;
    }

    public boolean isNewsFeed() {
        return isNewsFeed;
    }

    public void setNewsFeed(boolean newsFeed) {
        isNewsFeed = newsFeed;
    }
}
