package com.sdpm.feedly.bgtasks;

import android.os.AsyncTask;
import android.util.SparseIntArray;

import com.sdpm.feedly.model.Article;
import com.sdpm.feedly.model.Feed;
import com.sdpm.feedly.utils.ConnectionUtils;
import com.sdpm.feedly.utils.XmlParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clinton on 12/3/17.
 */

public class SuggestedFeedsTask extends AsyncTask<List<Feed>,Void,List<Feed>> {

    private SuggestedFeedsTaskListener theListener;
    private List<Object> preferenceList;
    private SparseIntArray countMap;

    public SuggestedFeedsTask(SuggestedFeedsTaskListener theListener,List<Object> preferenceList) {
        this.theListener = theListener;
        this.preferenceList = preferenceList;
        populateSparseArray();
    }

    public interface SuggestedFeedsTaskListener{
        void onPostExecuteSuggestionsTask(Feed suggestedFeed);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<Feed> feeds) {
        super.onPostExecute(feeds);
        int innerCounter = countMap.get(preferenceList.size());
        Feed suggestedFeed = new Feed("Suggested Feeds",DownloadXml.SUGGESTED_FEEDS,"","",null);
        ArrayList<Article> suggestedFeedArticles = new ArrayList<>();
        for(Feed f: feeds){
            if(f.getTheXml()!=null) {
                XmlParser parser = new XmlParser();
                parser.parse(f.getTheXml());
                f.setArticleList(parser.getApplications());
                for (int i = 0; i < innerCounter; i++) {
                    suggestedFeedArticles.add(f.getArticleList().get(i));
                }
            }
        }
        suggestedFeed.setArticleList(suggestedFeedArticles);
        theListener.onPostExecuteSuggestionsTask(suggestedFeed);
    }

    @Override
    protected List<Feed> doInBackground(List<Feed>[] lists) {
        List<Feed> listOfFeeds = lists[0];
        List<Feed> requiredFeeds = new ArrayList<>();
        for(Feed f: listOfFeeds){
            if(preferenceList.contains(f.getCategory())){
                String s = ConnectionUtils.downloadXML(f.getLink());
                if (s != null) {
                    f.setTheXml(s);
                    requiredFeeds.add(f);
                }
            }

        }
        return requiredFeeds;
    }

    private void populateSparseArray(){
        countMap = new SparseIntArray();
        countMap.put(1,3);
        countMap.put(2,3);
        countMap.put(3,3);
        countMap.put(4,2);
        countMap.put(4,2);
        countMap.put(5,2);
        countMap.put(6,2);

    }
}
