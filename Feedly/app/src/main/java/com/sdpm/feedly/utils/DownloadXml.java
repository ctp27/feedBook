package com.sdpm.feedly.utils;

import android.os.AsyncTask;

/**
 * Created by clinton on 10/8/17.
 */

public class DownloadXml extends AsyncTask<String, Void, String>  {

    private XmlParser theParser;

    public DownloadXml() {
        super();
    }

    public DownloadXml(XmlParser theParser){
        super();
        this.theParser = theParser;
    }
    
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }

    @Override
    protected String doInBackground(String... params) {
        return null;
    }
}
