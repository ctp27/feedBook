package com.sdpm.feedly.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import model.Article;
import model.Feed;

import static android.content.ContentValues.TAG;

/**
 * Created by clinton on 10/14/17.
 */

public class Test extends AsyncTask<ArrayList<Feed>,Void, ArrayList<Feed>> {


    @Override
    protected void onPostExecute(ArrayList<Feed> theFeeds) {
        super.onPostExecute(theFeeds);

        XmlParser parser = new XmlParser();

        for(Feed f : theFeeds){
            parser.parse(f.getTheXml());
            f.setArticleList(parser.getApplications());
        }

        for(Feed f: theFeeds){
            Feed temp = f;

            for(Article a:temp.getArticleList()){
                Log.d("ENtry",a.toString());
            }
        }


    }

    @Override
    protected ArrayList<Feed> doInBackground(ArrayList<Feed>... params) {

        ArrayList<Feed> theFeeds = params[0];

        for(Feed f: theFeeds){
            String s = downloadXML(f.getLink());
            if (s == null) {
                Log.e(TAG, "doInBackground: Error downloading");
            }
            f.setTheXml(s);
        }

        return theFeeds;
    }

    private String downloadXML(String urlPath) {
        StringBuilder xmlResult = new StringBuilder();

        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int response = connection.getResponseCode();
            Log.d(TAG, "downloadXML: The response code was " + response);
//                InputStream inputStream = connection.getInputStream();
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader reader = new BufferedReader(inputStreamReader);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            int charsRead;
            char[] inputBuffer = new char[2000];
            while (true) {
                charsRead = reader.read(inputBuffer);
                if (charsRead < 0) {
                    break;
                }
                if (charsRead > 0) {
                    xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                }
            }
            reader.close();

            return xmlResult.toString();
        } catch (MalformedURLException e) {
            Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "downloadXML: IO Exception reading data: " + e.getMessage());
        } catch (SecurityException e) {
            Log.e(TAG, "downloadXML: Security Exception.  Needs permisson? " + e.getMessage());
//                e.printStackTrace();
        }
        return xmlResult.toString();
    }
}
