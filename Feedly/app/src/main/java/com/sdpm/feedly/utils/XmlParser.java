package com.sdpm.feedly.utils;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

import com.sdpm.feedly.model.Article;

/**
 * Created by clinton on 10/8/17.
 */


/**
 * Main parser class responsible for parsing the XML feeds
 */

public class XmlParser {

    private static final String TAG = "ParseApplications";
    private ArrayList<Article> applications;


    public XmlParser() {
        this.applications = new ArrayList<>();

    }

    public ArrayList<Article> getApplications() {
        return applications;
    }

    /**
     * This method takes in the URL of the feed as a parameter. It contains the operations to download
     * the feed. It returns a string with the downloaded XML data.
     *
     * @param xmlData Takes in the downloaded XML feed as a string
     * @return Returns the status of the parsed XML
     */

    public boolean parse(String xmlData) {
        boolean status = true;
        Article currentRecord = null;
        boolean inEntry = false;
        String textValue = "";
        
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventType = xpp.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();
                String prefix = xpp.getPrefix();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
//                        Log.d(TAG, "parse: Starting tag for " + tagName);
                        if("item".equalsIgnoreCase(tagName) || "entry".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            currentRecord = new Article();
                        }
                        else if("thumbnail".equalsIgnoreCase(tagName)){
                            String temp = xpp.getAttributeValue(null,"url");
                            if(currentRecord!=null && temp!=null) {
                                currentRecord.setThumbnailLink(temp);
                            }
                        }
                        else if("content".equalsIgnoreCase(tagName)){
                            String temp = xpp.getAttributeValue(null,"url");
                            if(currentRecord!=null && temp!=null) {
                                if(currentRecord.getThumbnailLink()==null) {
                                    currentRecord.setThumbnailLink(temp);
                                }
                            }
                        }
                        else if("link".equalsIgnoreCase(tagName)){
                            String temp = xpp.getAttributeValue(null,"href");
                                if(currentRecord!=null) {
                                    currentRecord.setLink(temp);
                                }
                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
//                        Log.d(TAG, "parse: Ending tag for " + tagName);
                        if(inEntry) {
                            if("item".equalsIgnoreCase(tagName) || "entry".equalsIgnoreCase(tagName)) {
                                setTheThumbnail(currentRecord);
                                applications.add(currentRecord);
                                inEntry = false;
                            } else if("title".equalsIgnoreCase(tagName)) {
                                currentRecord.setTitle(textValue);
                            } else if("link".equalsIgnoreCase(tagName)
                                        || "id".equalsIgnoreCase(tagName)
                                        || "feedburner:origlink".equalsIgnoreCase(tagName)) {
                                if(currentRecord.getLink()==null){
                                    currentRecord.setLink(textValue);
                                }
                            } else if("description".equalsIgnoreCase(tagName)
                                        || "content".equalsIgnoreCase(tagName)) {

                                if(prefix==null) {
                                    currentRecord.setDescription(textValue);
                                }

                            } else if("summary".equalsIgnoreCase(tagName)) {
                                currentRecord.setSummary(textValue);
                            } else if("pubdate".equalsIgnoreCase(tagName)
                                        || "published".equalsIgnoreCase(tagName)) {
                                currentRecord.setPublishedDate(textValue);
                            }else if("pubdate".equalsIgnoreCase(tagName)) {
                                currentRecord.setPublishedDate(textValue);
                            }else if("author".equalsIgnoreCase(tagName)
                                    || "name".equalsIgnoreCase(tagName)
                                    || "creator".equalsIgnoreCase(tagName)){
                                if(tagName.equalsIgnoreCase("creator")){
                                    if(prefix.equalsIgnoreCase("dc")){
                                        currentRecord.setAuthor(textValue);
                                    }
                                }else {
                                    currentRecord.setAuthor(textValue);
                                }
                            }
                            else if("encoded".equalsIgnoreCase(tagName)) {
                                if(prefix.equalsIgnoreCase("content")) {
                                    currentRecord.setContent(textValue);
                                }
                            }
                            else if("thumbnail".equalsIgnoreCase(tagName)) {
                                currentRecord.setThumbnailLink(xpp.getAttributeValue(null, "url"));
                            }

                        }
                        break;

                    default:
                        // Nothing else to do.
                }
                eventType = xpp.next();

            }
//            for (Article app: applications) {
//                Log.d(TAG, "******************");
//                Log.d(TAG, app.toString());
//            }


        } catch(Exception e) {
            status = false;
            e.printStackTrace();
        }

        return status;
    }

    /**
     * This method sets the thumbnailLink field for each article entry.
     * If no thumbnail is found, it sets the first image from the description (if present) as
     * the thumbnail.
     * If no images are found in the entire feed, it sets the thumbnailLink to null
     * @param article the list of articles whose thumbnails have to be set.
     */

    private void setTheThumbnail(Article article) {

            String theUrl = null;

            theUrl = article.getThumbnailLink();

            if (theUrl == null) {
                String tempDesc = article.getDescription();
                if (HtmlParseUtils.containsHtml(tempDesc)) {
                    theUrl = HtmlParseUtils.getImageUrlFromDescription(tempDesc);
                    if(theUrl==null && article.getContent()!=null){
                        Log.d(TAG,"Article content is not null");
                        if(HtmlParseUtils.containsHtml(article.getContent())){
                            Log.d(TAG,"Article contains HTML");
                            theUrl = HtmlParseUtils.getImageUrlFromDescription(article.getContent());
                        }
                    }
                    setTheUrl(theUrl,article);
                }
            }else {
                setTheUrl(theUrl,article);
            }

    }

    /**
     * This method is used by the setTheThumbnail() method. It simply sets the URL after
     * checking whether it is valid. Also adds http to some relative URLS found in some feeds.
     * This method is used internally by the setTheThumbnail()
     * @param theUrl theUrl to be checked
     * @param a the article in which the URL is to be set
     */

    private void setTheUrl(String theUrl, Article a) {
        if (theUrl!=null && !theUrl.isEmpty()) {
            if (HtmlParseUtils.isValidUrl(theUrl)) {
                a.setThumbnailLink(theUrl);
            } else {
                theUrl = "https:" + theUrl;
                if (HtmlParseUtils.isValidUrl(theUrl)) {
                    a.setThumbnailLink(theUrl);
                } else {
                    a.setThumbnailLink(null);
                }
            }
        } else {
            a.setThumbnailLink(null);
        }
    }

}
