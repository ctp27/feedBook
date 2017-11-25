package com.sdpm.feedly.bgtasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.sdpm.feedly.adapters.RVAdapter;
import com.sdpm.feedly.utils.XmlParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import model.Feed;

import static android.content.ContentValues.TAG;

/**
 * Created by clinton on 10/8/17.
 */

/**
 * Main class responible for downloading and performing post download operations for the feeds
 */


public class DownloadXml extends AsyncTask<Feed,Void, Feed>  {

    public static final String EXPLORE_FEEDS="EXPLORE";
    public static final String TODAY = "T0DAY";
    public static final String READLATER = "Read Later";
    public static final String PERSONALBOARD = "Personal Board";

    private RecyclerView recyclerView;
    private String action;
    private Context context;
    /**
     * The constructor takes in the recycler view and the action to be performed post download as
     * parameters. The recycler view is used to update the view. The action determines what
     * action is to be performed post download.
     *
     * @param recyclerView the recycler view object to be updated
     * @param action String corresponding to the action to be performed. Can choose from the public
     *               static string fields of this class.

     */

    public DownloadXml(Context context, RecyclerView recyclerView, String action){
        this.context=context;
        this.action = action;
        this.recyclerView = recyclerView;
    }

    /**
     * This method loops through the list of downloaded feeds and accordingly updates the
     * recycler view of the home screen as per the explore feeds feature i.e displays all feeds.
     * The method parses the XML and sets the recycler view accordingly.
     *
     * TODO: Update this accordingly based on the front end Recycler view.
     * TODO: Right now I have set it to display the articles of just one feed. (See line 76)
     * @param theFeed  The object of Feed with the downloaded XML
     */

    private void displayExploreFeeds(Feed theFeed){
            XmlParser parser = new XmlParser();
            parser.parse(theFeed.getTheXml());
            theFeed.setArticleList(parser.getApplications());

            RVAdapter theAdapter = new RVAdapter(theFeed.getArticleList(),context,theFeed.getCategory());
            recyclerView.setAdapter(theAdapter);

    }




    /**
     * This method loops through the list of parsed feeds and accordingly updates the
     * recycler view of the home screen as per the Todays feeds feature i.e displays todays feeds
     *
     * TODO: Insert the code here for updating the recycler view to display todays feeds on home screen
     *
     * @param theFeeds  The arrayList of Feeds with the downloaded and parsed articles collection
     */

    private void displayTodaysFeeds(ArrayList<Feed> theFeeds){
    }


    /**
     * This method loops through the list of parsed feeds and accordingly updates the
     * recycler view of the home screen as per the Todays feeds feature i.e displays all feeds
     *
     * TODO: Insert the code here for updating the recycler view to display feeds to read later on the home screen
     *
     * @param theFeed  The read later feed from firebase
     */

    private void displayReadLaterFeeds(Feed theFeed){
        RVAdapter theAdapter = new RVAdapter(theFeed.getArticleList(),context,theFeed.getCategory());
        recyclerView.setAdapter(theAdapter);
    }

    private void displayPersonalBoard(Feed theFeed){
        RVAdapter theAdapter = new RVAdapter(theFeed.getArticleList(),context,theFeed.getCategory());
        recyclerView.setAdapter(theAdapter);
    }

    /**
     * Async task overrriden method which is called after the doInBackground() is completed.
     * This method checks the action to be performed, which is set while instantiating this class.
     * Based on the action, it calls the appropriate method using switch case
     *
     * @param theFeed Temperature in degrees Celsius (°C)
     */

    @Override
    protected void onPostExecute(Feed theFeed) {
        super.onPostExecute(theFeed);

        switch (action) {

            case DownloadXml.EXPLORE_FEEDS:
                displayExploreFeeds(theFeed);
                break;
            case DownloadXml.TODAY:
//                    displayTodaysFeeds(theFeeds);
                break;
            case DownloadXml.READLATER:
                    displayReadLaterFeeds(theFeed);
                break;
            case DownloadXml.PERSONALBOARD:
                displayPersonalBoard(theFeed);
                break;

            default:
        }
    }

    /**
     * Async task overrriden method which is called when the async task is called. The operations
     * specified in this method runs in the background. It takes in the arraylist as a parameter
     * which is passed while calling the execute() function of this class. (Check home.java line 108)
     *
     * @param params In this case, the array list passed while calling the execute() method
     * @return ArrayList<Feed> returns an array list of the feeds, with the downloaded XML feed
     */

    @Override
    protected Feed doInBackground(Feed... params) {

        Feed theFeed = params[0];
        if(this.action.equals(DownloadXml.EXPLORE_FEEDS)) {
            String s = downloadXML(theFeed.getLink());
            if (s == null) {
                Log.e(TAG, "doInBackground: Error downloading");
            } else {
                theFeed.setTheXml(s);
            }
        }
        return theFeed;
    }

    /**
     * This method takes in the URL of the feed as a parameter. It contains the operations to download
     * the feed. It returns a string with the downloaded XML data.
     *
     * @param urlPath Takes in the URL of the RSS feed
     * @return A string containing the downloaded XML data
     */

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
