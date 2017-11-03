package com.sdpm.feedly.feedly;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sdpm.feedly.utils.DownloadXml;

import java.util.ArrayList;

import model.Article;
import model.Feed;

public class HomeNav extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ArrayList<Feed> theFeeds;


    {
        theFeeds= new ArrayList<>();
        theFeeds.add(new Feed("The Daily Notebook","Film","","https://mubi.com/notebook/posts.atom",new ArrayList<Article>()));
        theFeeds.add(new Feed("IGN","Gaming","","http://feeds.ign.com/ign/articles?format=xml",new ArrayList<Article>()));
        theFeeds.add(new Feed("Food52","Food","","https://food52.com/blog.rss",new ArrayList<Article>()));
        theFeeds.add(new Feed("Scientific American","Science","","http://rss.sciam.com/ScientificAmerican-Global?fmt=xml",new ArrayList<Article>()));
        theFeeds.add(new Feed("Eurogamer","Gaming","","http://www.eurogamer.net/?format=rss",new ArrayList<Article>()));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_login_side_nav);

        /**
         * TODO: action should be assigned based on the usersettings
         */
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),theFeeds);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        int limit = (mSectionsPagerAdapter.getCount() > 1 ? mSectionsPagerAdapter.getCount() - 1 : 1);
        mViewPager.setOffscreenPageLimit(limit);

        if(theFeeds != null) {
            getSupportActionBar().setTitle(theFeeds.get(0).getCategory());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabHome);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(theFeeds!=null && position < theFeeds.size()) {
            getSupportActionBar().setTitle(theFeeds.get(position).getCategory());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_FEED = "FEED";
        private Feed feed;
        private RecyclerView rv;
        private LinearLayoutManager llm;
        String feedCachedUrl = "";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(Feed feed) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putSerializable(ARG_FEED,feed);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home_nav, container, false);
            feed = (Feed) getArguments().getSerializable(ARG_FEED);
            if (feed != null) {
                rv = (RecyclerView) rootView.findViewById(R.id.rvArticles);
                rv.setHasFixedSize(true);
                llm = new LinearLayoutManager(getActivity());
                rv.setLayoutManager(llm);
                displayFeed();
            }
            return rootView;
        }

        private void displayFeed(){
            DownloadXml downloadXml = new DownloadXml(getContext(),rv,DownloadXml.EXPLORE_FEEDS);
            downloadXml.execute(feed);
            feedCachedUrl = feed.getLink();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        ArrayList<Feed> theFeeds;
        public SectionsPagerAdapter(FragmentManager fm, ArrayList<Feed> theFeeds) {
            super(fm);
            this.theFeeds = theFeeds;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(theFeeds != null && position < theFeeds.size()) {
                return PlaceholderFragment.newInstance(theFeeds.get(position));
            }
            return PlaceholderFragment.newInstance(null);
        }

        @Override
        public int getCount() {
            // Show n total pages.
            return theFeeds.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
           if(theFeeds != null && theFeeds.size() > position){
               return theFeeds.get(position).getName();
           }
            return null;
        }



    }



}
