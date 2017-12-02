package com.sdpm.feedly.utils;

import android.net.Uri;
import android.util.Log;

import com.sdpm.feedly.feedly.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static android.content.ContentValues.TAG;

/**
 * Created by clinton on 11/26/17.
 */

public class ConnectionUtils {

    private static final String NEWSAPI_API_KEY = BuildConfig.API_KEY;

    private static final String NEWS_API_BASE_URL = "https://newsapi.org/v2/everything";
    private static final String QUERY_PARAM = "q=";
    private static final String API_KEY_PARAM = "apiKey";
    private static final String SPACE=" ";
    private static final String NEWS_QUERY = "news";

    public static URL buildNewsUrlFromLocation(String location){

        Uri builtUri = Uri.parse(NEWS_API_BASE_URL).buildUpon()
                .encodedQuery(QUERY_PARAM+location+SPACE+NEWS_QUERY)
                .appendQueryParameter(API_KEY_PARAM,NEWSAPI_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Built URI " + url);
        return url;


    }


    public static String getJsonResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * This method takes in the URL of the feed as a parameter. It contains the operations to download
     * the feed. It returns a string with the downloaded XML data.
     *
     * @param urlPath Takes in the URL of the RSS feed
     * @return A string containing the downloaded XML data
     */

    public static String downloadXML(String urlPath) {
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
