package model;

import java.io.Serializable;

/**
 * Created by clinton on 10/9/17.
 */

public class Article implements Serializable {

//    * commented fields wont be null

//  *the title/header of the article*
    private String title;

//  *the website URL for the complete article*
    private String link;

//  *Unique Id generated to identify feeds*
    private int id;

//  *Description of the article. Present in most cases*
    private String description;

//  Short summary of the article. May or may not be present
    private String summary;

//  the author of the article. May or may not be present
    private String author;

//  The date the article was published. May or may not be present
    private String publishedDate;

//  The content of the article. May or may not be present
    private String content;

    private String thumbnailLink;



    public Article(String title, String link, String summary, String author, String description, String publishedDate, int id) {
        this.title = title;
        this.link = link;
        this.id = id;
        this.summary = summary;
        this.author = author;
        this.description = description;
        this.publishedDate = publishedDate;
    }

    public Article() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }



    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", \nlink='" + link + '\'' +
                ", \nid=" + id +

                ", \nsummary='" + summary + '\'' +
                ", \nauthor='" + author + '\'' +
                ", \npublishedDate='" + publishedDate + '\'' +
                ", \ncontent='" + content + '\'' +
                ", \nthumbnailLink='" + thumbnailLink + '\'' +
                '}';
    }
}
