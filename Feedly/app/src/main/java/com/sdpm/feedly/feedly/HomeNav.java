package com.sdpm.feedly.feedly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdpm.feedly.utils.DownloadXml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    DatabaseReference database;
    private ArrayList<Feed> theFeeds;
    private ArrayList<Feed> exploreFeeds;
    String email = "";
    ExpandableListAdapter expListAdapter = null;
    CheckboxExpandableListAdapter checkboxExpandableListAdapter = null;
    ExpandableListView expListView;
    ExpandableListView editFeedsExpListView;
    List<String> personalCategoriesList;
    HashMap<String, List<Feed>> personalFeedsUnderCategory;
    private DrawerLayout drawer;
    private NavigationView navView;
    private NavigationView searchView;

    private Button logoutBtn;
    private Button editFeedContentBtn;
    private Button editCancelBtn;
    private Button removeFeedsBtn;
    private Button addSourceBtn;
    private Button settingsBtn;
    private Toolbar toolbar;
    private TextView todaysDefaultText;

    private LinearLayout navDrawerLayout;
    private LinearLayout editContentLayout;

    private static final String TAG = "HomeNav";
    public static final String TODAYS_FEED = "Today";
    public static final String EXPLORE_FEED= "Explore";
    private static String defaultFeed;
    private boolean resumeFromSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ChangeTheme.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_no_login_side_nav);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * TODO: defaultFeed should be assigned based on the usersettings
         */

        SharedPreferences userDetails = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        long rb_button = userDetails.getInt("default_view",0);

        if(rb_button==2131296451){
            defaultFeed = TODAYS_FEED;

        }
        else if(rb_button == 2131296450)
        {
            Log.d(TAG,Long.toString(rb_button));
            defaultFeed = EXPLORE_FEED;
        }else{
            defaultFeed = EXPLORE_FEED;
        }

        /* Sets the Navigation drawer based on logged in state */
        setTheNavDrawer();
        setSearchDrawer();

        database = FirebaseDatabase.getInstance().getReference();

        prepareData();




        /* Initializes all widgets */
        initializeObjects();

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
    public void onResume(){
        super.onResume();
        if(resumeFromSearch) {
            createExpandableListOfPersonalFeeds();
            resumeFromSearch = false;
        }

    }

    private void displayPersonalFeeds(){
        List<String> thisList=null;
        List<Feed> personalFeeds = new ArrayList<>();
        if(personalCategoriesList!=null) {
             thisList = personalCategoriesList;
        }

        for(String thisCategory : thisList){

            List<Feed> theCategory=personalFeedsUnderCategory.get(thisCategory);
            for(Feed f: theCategory){
                personalFeeds.add(f);
            }
        }

            theFeeds = (ArrayList<Feed>) personalFeeds;
            LoadDataOnScreen();

    }

    private void initializeObjects(){

        todaysDefaultText = (TextView) findViewById(R.id.todays_default_text);
        navDrawerLayout = (LinearLayout) findViewById(R.id.nav_drawer_view);
        editContentLayout = (LinearLayout) findViewById(R.id.edit_content_view);

        removeFeedsBtn = (Button) findViewById(R.id.remove_feed_btn);
        editFeedContentBtn = (Button) findViewById(R.id.edit_content_button);
        addSourceBtn = (Button) findViewById(R.id.add_content_button);
        logoutBtn = (Button) findViewById(R.id.logout_button);
        settingsBtn = (Button) findViewById(R.id.settings_button);

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeNav.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userDetails = getSharedPreferences("LoginInfo", MODE_PRIVATE);
                SharedPreferences.Editor edit = userDetails.edit();
                edit.clear();
                edit.putString("email","");
                edit.commit();
                Intent intent = new Intent(HomeNav.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        addSourceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeFromSearch = true;
                Intent intent = new Intent(HomeNav.this,AddActivity.class);
                intent.putExtra("email",email);
                startActivity(intent);
            }
        });

        editFeedContentBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTheEditContentDrawer();
            }
        });

        editCancelBtn = (Button) findViewById(R.id.edit_cancel_btn);
        editCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideTheEditContentDrawer();
            }
        });

        removeFeedsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<Feed> feedsToDelete = CheckboxExpandableListAdapter.getFeedsToDelete();
                database.child("PersonalFeeds").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean hasPersonalFeeds = false;
                        DatabaseReference dbSnapPersonal = null;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if(snapshot.hasChild(email.split("@")[0])){
                                dataSnapshot = snapshot.child(email.split("@")[0]);
                                hasPersonalFeeds = true;
                                dbSnapPersonal = database.child("PersonalFeeds").child(snapshot.getKey()).child(email.split("@")[0]);
                                break;
                            }
                        }
                        if(hasPersonalFeeds && dbSnapPersonal != null) {
                            boolean deletedSuccessfully = false;
                            DataSnapshot childDataSnapshot;
                            for(Feed f : feedsToDelete){
                                childDataSnapshot = null;
                                DatabaseReference dbSnap = null;
                                for(DataSnapshot tempSnapShot : dataSnapshot.getChildren()) {
                                    if (tempSnapShot.hasChild(f.getCategory())) {
                                        childDataSnapshot = tempSnapShot;
                                        dbSnap = dbSnapPersonal.child(tempSnapShot.getKey());
                                        break;
                                    }
                                }
                                if(childDataSnapshot != null && dbSnap != null && childDataSnapshot.hasChild(f.getCategory())){
                                    DataSnapshot subSnapShot = childDataSnapshot.child(f.getCategory());
                                    for(DataSnapshot s : subSnapShot.getChildren()) {
                                        if (s.child("name").getValue().toString().equals(f.getName())) {
                                            dbSnap.child(subSnapShot.getKey()).child(s.getKey()).removeValue();
                                            for(Feed f1 : personalFeedsUnderCategory.get(f.getCategory())){
                                                if(f1.getName().equals(f.getName())) {
                                                    personalFeedsUnderCategory.get(f.getCategory()).remove(f1);
                                                    if(personalFeedsUnderCategory.get(f.getCategory()).size() == 0){
                                                        personalFeedsUnderCategory.remove(f.getCategory());
                                                        personalCategoriesList.remove(f.getCategory());
                                                    }
                                                    deletedSuccessfully = true;
                                                    break;
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                            if(deletedSuccessfully) {
                                checkboxExpandableListAdapter.resetViewAfterDelete();
                                expListAdapter.notifyDataSetChanged();
                                checkboxExpandableListAdapter.notifyDataSetChanged();
                                if(defaultFeed.equalsIgnoreCase(TODAYS_FEED)){
                                    displayPersonalFeeds();
                                    defaultFeed = TODAYS_FEED;
                                }
                                else {
                                    prepareData(); ////why need this call ???? -----------------
                                    defaultFeed = EXPLORE_FEED;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError d) {
                        Log.d("Login DbError Msg ->", d.getMessage());
                        Log.d("Login DbError Detail ->", d.getDetails());
                    }
                });
            }
        });
    }


    /**
     * Initializes and sets the Search drawer components
     *
     */
    private void setSearchDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.no_login_drawer_layout);
        searchView = (NavigationView) drawer.findViewById(R.id.search_view);

        ScrollView view = (ScrollView) getLayoutInflater().inflate(R.layout.search_side_layout, null);

        searchView.addView(view);
    }

    /**
     * Initializes and sets the Navigation drawer components
     *
     */
    private void setTheNavDrawer(){
        drawer = (DrawerLayout) findViewById(R.id.no_login_drawer_layout);
        navView = (NavigationView) drawer.findViewById(R.id.nav_view);

        LinearLayout layout;

        SharedPreferences userDetails = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        email = userDetails.getString("email", "");
        if (email.equals("")) { // user not logged in
            layout = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_no_login_side_nav, null);
        }
        else {
            layout = (LinearLayout) getLayoutInflater().inflate(R.layout.personal_layout_nav, null);
        }
        navView.addView(layout);

        //   drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        ListView lv = (ListView) findViewById(R.id.default_nav_list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0: displayPersonalFeeds();
                            defaultFeed =TODAYS_FEED;
                            break;
                    case 1: // Read Later
                        displayReadLaterArticle();
                        break;
                    case 2: //explore
                        if(defaultFeed.equalsIgnoreCase(EXPLORE_FEED)) {
                            theFeeds = exploreFeeds;
                            if (theFeeds != null) {
                                mSectionsPagerAdapter.notifyDataSetChanged();
                                getSupportActionBar().setTitle(theFeeds.get(0).getCategory() + "/" + theFeeds.get(0).getName());
                            }
                        }else {
                            defaultFeed = EXPLORE_FEED;
                            prepareData();
                        }
                        break;
                }
            }
        });

        expListView = (ExpandableListView) findViewById(R.id.personal_feeds_expandable_lv);
        editFeedsExpListView = (ExpandableListView) findViewById(R.id.editcontent_expandable);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                theFeeds = new ArrayList<Feed>();
                theFeeds.add(personalFeedsUnderCategory.get(personalCategoriesList.get(groupPosition).toString()).get(childPosition));
                if(theFeeds != null) {
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    String category = theFeeds.get(0).getCategory();
                    String feedName = theFeeds.get(0).getName();
                    getSupportActionBar().setTitle(category+"/"+feedName);
                }
                return false;
            }
        });

        editFeedsExpListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView ctv = (CheckedTextView)view;
                if (ctv.isChecked()){
                    Toast.makeText(getApplicationContext(),"uncheckd",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"checked",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void  prepareData(){
        exploreFeeds = new ArrayList<>();
        database.child("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for(DataSnapshot subSnapShot : snapshot.getChildren()){
                        exploreFeeds.add(new Feed(subSnapShot.child("name").getValue().toString(),snapshot.getKey().toString(),"",subSnapShot.child("rssLink").getValue().toString(),new ArrayList<Article>()));
                    }
                }
                if(exploreFeeds != null) {
                    theFeeds = exploreFeeds;
                    LoadDataOnScreen();
                    if(!email.equals("")) {
                        createExpandableListOfPersonalFeeds();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError d) {
                Log.d("Login DbError Msg ->", d.getMessage());
                Log.d("Login DbError Detail ->", d.getDetails());
            }
        });
    }

    public void displayReadLaterArticle(){
        database.child("ReadLater").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean hasReadLater = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.hasChild(email.split("@")[0])){
                        hasReadLater = true;
                        dataSnapshot = snapshot.child(email.split("@")[0]);
                        break;
                    }
                }

                if(hasReadLater) {
                    ArrayList<Feed> readLaterFeedList = new ArrayList<>();
                    ArrayList<Article> readLaterArticleList = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Article a = snapshot.getValue(Article.class);
                        readLaterArticleList.add(a);
                    }
                    readLaterFeedList.add(new Feed("",DownloadXml.READLATER,"",null,readLaterArticleList));
                    theFeeds = readLaterFeedList;

                    if(mSectionsPagerAdapter != null){
                        mSectionsPagerAdapter.notifyDataSetChanged();
                    } else {
                        LoadDataOnScreen();
                    }
                    getSupportActionBar().setTitle(theFeeds.get(0).getCategory());
                }
            }

            @Override
            public void onCancelled(DatabaseError d) {
                Log.d("Login DbError Msg ->", d.getMessage());
                Log.d("Login DbError Detail ->", d.getDetails());
            }
        });

    }

    public void LoadDataOnScreen(){
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        int limit = (mSectionsPagerAdapter.getCount() > 1 ? mSectionsPagerAdapter.getCount() - 1 : 1);
        mViewPager.setOffscreenPageLimit(limit);

        if(theFeeds != null && !theFeeds.isEmpty()) {
            getSupportActionBar().setTitle(theFeeds.get(0).getCategory()+"/"+theFeeds.get(0).getName());
        }

    }

    public void createExpandableListOfPersonalFeeds(){
        if(personalCategoriesList != null){
            personalCategoriesList.clear();
        } else {
            personalCategoriesList = new ArrayList<>();
        }

        if(personalFeedsUnderCategory != null) {
            personalFeedsUnderCategory.clear();
        } else {
            personalFeedsUnderCategory = new HashMap<String, List<Feed>>();
        }

        database.child("PersonalFeeds").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean hasPersonalFeeds = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.hasChild(email.split("@")[0])){
                        dataSnapshot = snapshot.child(email.split("@")[0]);
                        hasPersonalFeeds = true;
                        break;
                    }
                }
                if(hasPersonalFeeds) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for(DataSnapshot tempSnapShot : snapshot.getChildren()) {
                            snapshot = tempSnapShot;
                            break;
                        }
                        personalCategoriesList.add(snapshot.getKey().toString());
                        List<Feed> feedList = new ArrayList<Feed>();
                        for (DataSnapshot subSnapShot : snapshot.getChildren()) {
                            feedList.add(new Feed(subSnapShot.child("name").getValue().toString(), snapshot.getKey().toString(), "", subSnapShot.child("rssLink").getValue().toString(), new ArrayList<Article>()));
                        }
                        personalFeedsUnderCategory.put(snapshot.getKey().toString(), feedList);
                    }

                    if (personalCategoriesList.size() != 0 && personalFeedsUnderCategory.size() != 0) {
                        if(expListAdapter != null && checkboxExpandableListAdapter != null) {
                            expListAdapter.notifyDataSetChanged();
                            checkboxExpandableListAdapter.notifyDataSetChanged();
                        }else {
                            expListAdapter = new ExpandableListAdapter(getBaseContext(), personalCategoriesList, personalFeedsUnderCategory);
                            expListView.setAdapter(expListAdapter);

                            /* Set the edit content adapter */
                            checkboxExpandableListAdapter = new CheckboxExpandableListAdapter(getBaseContext(), personalCategoriesList, personalFeedsUnderCategory, removeFeedsBtn);
                            editFeedsExpListView.setAdapter(checkboxExpandableListAdapter);
                        }
                    }
                    if(defaultFeed.equalsIgnoreCase(TODAYS_FEED)){
                        displayPersonalFeeds();
                        defaultFeed =TODAYS_FEED;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError d) {
                Log.d("Login DbError Msg ->", d.getMessage());
                Log.d("Login DbError Detail ->", d.getDetails());
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
//        if (id == R.id.action_settings) {
//            Intent intent = new Intent(HomeNav.this, SettingsActivity.class);
//            startActivity(intent);
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(theFeeds!=null && position < theFeeds.size()) {
            String category = theFeeds.get(position).getCategory();
            String feedName = theFeeds.get(position).getName();
            getSupportActionBar().setTitle(category+"/"+feedName);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void openLoginView(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
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
        TextView todays;

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
                todays = (TextView) rootView.findViewById(R.id.todays_default_text);
                todays.setVisibility(View.INVISIBLE);
                rv = (RecyclerView) rootView.findViewById(R.id.rvArticles);
                rv.setHasFixedSize(true);
                llm = new LinearLayoutManager(getActivity());
                rv.setLayoutManager(llm);
                displayFeed();
            }
            else{
                 todays = (TextView) rootView.findViewById(R.id.todays_default_text);
                 todays.setVisibility(View.VISIBLE);

            }
            return rootView;
        }

        private void displayFeed(){
            DownloadXml downloadXml;
            if(DownloadXml.READLATER.equals(feed.getCategory())) {
                downloadXml = new DownloadXml(getContext(), rv, DownloadXml.READLATER);
            } else {
                downloadXml = new DownloadXml(getContext(), rv, DownloadXml.EXPLORE_FEEDS);
            }
            downloadXml.execute(feed);
            feedCachedUrl = feed.getLink();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter{

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
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
        public int getItemPosition(Object object) {
            return POSITION_NONE;
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

        @Override
        public Parcelable saveState() {
            Bundle bundle = (Bundle) super.saveState();
            if(bundle!=null)
                bundle.putParcelableArray("states", null); // Never maintain any states from the base class, just null it out
            return bundle;
        }
    }


    private void showTheEditContentDrawer(){
        editContentLayout.setVisibility(View.VISIBLE);
        navDrawerLayout.setVisibility(View.GONE);
    }

    private void hideTheEditContentDrawer(){
        editContentLayout.setVisibility(View.GONE);
        navDrawerLayout.setVisibility(View.VISIBLE);
    }
}