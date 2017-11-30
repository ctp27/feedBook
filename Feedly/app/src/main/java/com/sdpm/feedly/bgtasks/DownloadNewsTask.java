package com.sdpm.feedly.bgtasks;

import android.os.AsyncTask;

import com.sdpm.feedly.model.Feed;
import com.sdpm.feedly.utils.ConnectionUtils;
import com.sdpm.feedly.utils.JsonParser;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by clinton on 11/25/17.
 */

public class DownloadNewsTask extends AsyncTask<URL,Void,String> {

    private String location;
    private DownloadNewsTaskListener theListener;

    public DownloadNewsTask(DownloadNewsTaskListener theListener, String location){
        this.theListener = theListener;
        this.location = location;
    }

    public interface DownloadNewsTaskListener{
        void onNewsDownloaded(List<Feed> localNewsFeed);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        List<Feed> theList = null;
        if(s!=null){
            try {
                 theList = JsonParser.getNewsFeedFromJson(s,location);
                 theListener.onNewsDownloaded(theList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            theListener.onNewsDownloaded(null);
        }
    }

    @Override
    protected String doInBackground(URL... urls) {
        String s= null;
        try {
             s = ConnectionUtils.getJsonResponseFromHttpUrl(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return s;
        }

        return s;
    }



}
