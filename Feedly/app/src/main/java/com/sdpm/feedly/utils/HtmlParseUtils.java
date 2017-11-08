package com.sdpm.feedly.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by clinton on 10/31/17.
 */

public class HtmlParseUtils {

    private static final int DESC_LENGTH=89;
    private static final String TAG = "HtmlParseUtils";

    /**
     *
     * @param theString the string that is to be checked
     * @return boolean value stating whether the string contains html or not. Returns true if html
     * is present or returns false if not
     */

    public static boolean containsHtml(String theString){

        String textOfHtmlString = Jsoup.parse(theString).text();
        return !textOfHtmlString.equals(theString);

    }

    /**
     * This method parses a string containing HTML tags and extracts the text only.
     * @param htmlString the String containing HTML
     * @return returns a string with only text
     */

    public static String getPartialDescription(String htmlString){

        String s = Jsoup.parse(htmlString).text();
        if(s.length()<=DESC_LENGTH){
            return s;
        }
        else
            return s.substring(0,DESC_LENGTH);

    }

    /**
     * This method extracts the first image url it finds in the given string.
     *
     * @param theDescription the String containing HTML with img tags
     * @return returns the URL of the first image found. Returns an empty string if no images.
     */
    public static String getImageUrlFromDescription(String theDescription){

        String url=null;
        if(theDescription!=null && containsHtml(theDescription)) {
            Document theDoc = Jsoup.parse(theDescription);
            Elements images = theDoc.getElementsByTag("img");

            if(images.size()>0){
                url = images.get(0).attr("src");
//                Log.d(TAG, "The url"+url);
            }

        }

        return url;
    }

    /**
     * Method which checks if the URL passes is a valid URL or not
     * @param theUrl the string containing the URL
     * @return returns true if URL is valid. False if URL is not valid
     */

    public static boolean isValidUrl(String theUrl){
        try {
            URL url = new URL(theUrl);
            return true;
        } catch (MalformedURLException e) {
           return false;
        }

    }

}
