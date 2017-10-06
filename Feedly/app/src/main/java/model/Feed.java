package model;

/**
 * Created by Junaid on 10/5/2017.
 */

public class Feed {
    String image;
    String title;
    String desc;
    String author;
    String pubData;

    public Feed(String image, String title, String desc, String author, String pubData){
        this.image = image;
        this.title = title;
        this.desc = desc;
        this.author = author;
        this.pubData = pubData;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPubData() {
        return pubData;
    }

    public void setPubData(String pubData) {
        this.pubData = pubData;
    }
}
