package model;

import java.util.ArrayList;

/**
 * Created by Junaid on 10/5/2017.
 */

public class Feed {

    String name;
    String category;
    String description;
    String link;
    String theXml;
    ArrayList<Article> articleList;

    public Feed(String name, String category, String description, String link, ArrayList<Article> articleList) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.link = link;
        this.articleList = articleList;
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
}
