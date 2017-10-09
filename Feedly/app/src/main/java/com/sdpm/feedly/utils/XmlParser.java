package com.sdpm.feedly.utils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

import model.Feed;

/**
 * Created by clinton on 10/8/17.
 */

public class XmlParser {

    private static final String TAG = "ParseApplications";
    private ArrayList<Feed> applications;

    public XmlParser() {
        this.applications = new ArrayList<>();
    }

    public ArrayList<Feed> getApplications() {
        return applications;
    }

    public boolean parse(String xmlData) {
        boolean status = true;
        Feed currentRecord = null;
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
                switch (eventType) {
                    case XmlPullParser.START_TAG:
//                        Log.d(TAG, "parse: Starting tag for " + tagName);
                        if("item".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            currentRecord = new Feed();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
//                        Log.d(TAG, "parse: Ending tag for " + tagName);
                        if(inEntry) {
                            if("item".equalsIgnoreCase(tagName)) {
                                applications.add(currentRecord);
                                inEntry = false;
                            } else if("title".equalsIgnoreCase(tagName)) {
//                                currentRecord.setName(textValue);
                            } else if("link".equalsIgnoreCase(tagName)) {
//                                currentRecord.setArtist(textValue);
                            } else if("description".equalsIgnoreCase(tagName)) {
//                                currentRecord.setReleaseDate(textValue);
                            }else if ("")

                        }
                        break;

                    default:
                        // Nothing else to do.
                }
                eventType = xpp.next();

            }
//            for (FeedEntry app: applications) {
//                Log.d(TAG, "******************");
//                Log.d(TAG, app.toString());
//            }

        } catch(Exception e) {
            status = false;
            e.printStackTrace();
        }

        return status;
    }

}
