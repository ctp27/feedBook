package model;

import java.util.Date;

/**
 * Created by clinton on 10/9/17.
 */

public class Article {

    private String title;

    private String link;

    private int id;

    private String author;

    private String content;

    private Date publishedAt;

    private Date updatedAt;


    public Article(String title, String link, int id, String author, String content) {
        this.title = title;
        this.link = link;
        this.id = id;
        this.author = author;
        this.content = content;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", id=" + id +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
