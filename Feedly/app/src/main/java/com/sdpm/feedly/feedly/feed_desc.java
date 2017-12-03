package com.sdpm.feedly.feedly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdpm.feedly.model.Article;
import com.sdpm.feedly.utils.TempStores;
import com.sdpm.feedly.utils.TimeDateUtils;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import bolts.Task;

public class feed_desc extends AppCompatActivity  implements ViewPager.OnPageChangeListener {

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
    ArrayList<Article> articles;
    Article articleOnScreen;
    private String category;
    static String email;
    ArrayList<String> personalBoardList;
    static String personalBoardName = null;
    ArrayAdapter<String> arrayAdapterPersonalBoard;
    private static DrawerLayout drawer;
    private static NavigationView navView;
    private Map<String,Boolean> articlesViewMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_nav_feed_desc);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences userDetails = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        email = userDetails.getString("email", "");

        setSideNavBar();
        populatePersonalBoardList();

//        articles = (ArrayList<Article>) getIntent().getSerializableExtra("articlesList");
        //articles = (ArrayList<Article>)TempStores.getTheFeeds();
        articles = new ArrayList((ArrayList<Article>)TempStores.getTheFeeds());
        category = getIntent().getExtras().getString("category");
        getSupportActionBar().setTitle(category);
        int position = getIntent().getIntExtra("position",0);
        articleOnScreen = articles.get(position);

        if(articles != null) {
            initViewMap(articles, position);
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),articles);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(position);

        int limit = (mSectionsPagerAdapter.getCount() > 1 ? mSectionsPagerAdapter.getCount() - 1 : 1);
        //mViewPager.setOffscreenPageLimit(limit);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(articles!=null && position < articles.size()) {
            articleOnScreen = articles.get(position);
            if(articleOnScreen.getLink()!= null && articlesViewMap.get(articleOnScreen.getLink()) == false) {
                IncrementViewOfArticle(articleOnScreen);
                articlesViewMap.put(articleOnScreen.getLink(),true);
            }
        } else {
            articleOnScreen = null;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void initViewMap(ArrayList<Article> articlesList, int position) {
        articlesViewMap = new HashMap();
        for(Article a : articlesList) {
            if(a.getLink() != null)
            articlesViewMap.put(a.getLink(),false);
        }

        if(articlesList.get(position).getLink() != null) {
            articlesViewMap.put(articlesList.get(position).getLink(), true);
            IncrementViewOfArticle(articlesList.get(position));
        }
    }

    public void IncrementViewOfArticle(final Article article) {
        final DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference();

        database.child("Views").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isExistingArticle = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.child("Link").getValue().equals(article.getLink())) {
                        isExistingArticle = true;
                        int viewCount = 1 + Integer.parseInt(snapshot.child("Count").getValue().toString());
                        database.child("Views").child(snapshot.getKey()).child("Count").setValue(String.valueOf(viewCount));
                    }
                }
                if(!isExistingArticle) {
                    Map viewMap = new HashMap();
                    viewMap.put("Link", article.getLink());
                    viewMap.put("Count", "1");
                    database.child("Views").push().setValue(viewMap);
                }
            }

            @Override
            public void onCancelled(DatabaseError d) {
                Log.d("Views DbError Msg ->", d.getMessage());
                Log.d("Views DbError Detail ->", d.getDetails());
            }
        });
    }

    private void setSideNavBar(){
        final Button createBoardBtn, personalBoardDoneBtn;
        final EditText personalBoardNameEditTxt;
        ListView lvPersonalBoard;
        LinearLayout layout;

        drawer = (DrawerLayout) findViewById(R.id.side_nav_feed_desc_drawer_layout);
        navView = (NavigationView) drawer.findViewById(R.id.feed_desc_nav_view);
        layout = (LinearLayout) getLayoutInflater().inflate(R.layout.feed_desc_nav, null);
        navView.addView(layout);

        personalBoardDoneBtn = (Button) findViewById(R.id.create_board_done_button);
        personalBoardNameEditTxt = (EditText) findViewById(R.id.create_board_edit_text);
        createBoardBtn = (Button) findViewById(R.id.create_board_button);
        lvPersonalBoard = (ListView) findViewById(R.id.personal_board_list_view);
        personalBoardList = new ArrayList<String>();

        arrayAdapterPersonalBoard = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, personalBoardList);

        lvPersonalBoard.setAdapter(arrayAdapterPersonalBoard);

        lvPersonalBoard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                //check article is not already in board
                // if yes call addArticleInPersonalBoard Function
                final DatabaseReference database;
                database = FirebaseDatabase.getInstance().getReference();

                database.child("PersonalBoard").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Boolean articleAlreadyExisitsInBoard = false;
                        DatabaseReference personalBoardDBRef = null;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if(snapshot.hasChild(email.split("@")[0])){
                                dataSnapshot = snapshot.child(email.split("@")[0]);
                                personalBoardDBRef  = database.child("PersonalBoard").child(snapshot.getKey()).child(email.split("@")[0]);
                                break;
                            }
                        }
                        if(personalBoardDBRef  != null) {
                            Boolean boardExists = false;
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if(snapshot.child(personalBoardList.get(position)).exists()) {
                                    dataSnapshot = snapshot.child(personalBoardList.get(position));
                                    boardExists = true;
                                    break;
                                }
                            }
                            if(boardExists) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if(snapshot.child("link").getValue().toString().equals(articleOnScreen.getLink())){
                                        articleAlreadyExisitsInBoard = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if(articleAlreadyExisitsInBoard) {
                            Toast.makeText(getApplicationContext(), "Article already exists in board " + personalBoardList.get(position), Toast.LENGTH_LONG).show();
                        } else {
                            addArticleInPersonalBoard(articleOnScreen,  personalBoardList.get(position));
                            drawer.closeDrawer(Gravity.START);
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

        createBoardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personalBoardName = null;
                personalBoardNameEditTxt.setVisibility(View.VISIBLE);
                personalBoardNameEditTxt.setText("");
                personalBoardDoneBtn.setVisibility(View.VISIBLE);
                createBoardBtn.setVisibility(View.GONE);
            }
        });

        personalBoardDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personalBoardName = personalBoardNameEditTxt.getText().toString();
                if(!personalBoardName.equals("")) {
                    if(personalBoardList.contains(personalBoardName)) {
                        Toast.makeText(getApplicationContext(),"Personal Board Name " + personalBoardName +" already exists",Toast.LENGTH_LONG).show();
                        personalBoardName = "";
                    } else { //add in db with selected article and close nav bar
                        personalBoardDoneBtn.setVisibility(View.GONE);
                        personalBoardNameEditTxt.setVisibility(View.GONE);
                        createBoardBtn.setVisibility(View.VISIBLE);
                        personalBoardList.add(personalBoardName);
                        arrayAdapterPersonalBoard.notifyDataSetChanged();
                        addArticleInPersonalBoard(articleOnScreen, personalBoardName);
                        drawer.closeDrawer(Gravity.START);
                    }
                }
            }
        });
    }

    /**
     * Fetch board names from db and notify adapter
     */
    private void populatePersonalBoardList() {
        final DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference();

        database.child("PersonalBoard").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference personalBoardDBRef = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.hasChild(email.split("@")[0])){
                        dataSnapshot = snapshot.child(email.split("@")[0]);
                        personalBoardDBRef  = database.child("PersonalBoard").child(snapshot.getKey()).child(email.split("@")[0]);
                        break;
                    }
                }

                if(personalBoardDBRef  != null) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for(DataSnapshot subsnapshot : snapshot.getChildren()) {
                            personalBoardList.add(subsnapshot.getKey().toString());
                        }
                    }
                } else {
                    personalBoardList.add("My Board");
                }
                arrayAdapterPersonalBoard.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError d) {
                Log.d("Login DbError Msg ->", d.getMessage());
                Log.d("Login DbError Detail ->", d.getDetails());
            }
        });
    }

    /**
     * is article already in the board will be handled before calling this function
     **/
    private void addArticleInPersonalBoard(final Article article, final String boardName) {
        final DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference();

        database.child("PersonalBoard").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference personalBoardDBRef = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.hasChild(email.split("@")[0])){
                        dataSnapshot = snapshot.child(email.split("@")[0]);
                        personalBoardDBRef  = database.child("PersonalBoard").child(snapshot.getKey()).child(email.split("@")[0]);
                        break;
                    }
                }
                Map personalBoardArticle = new HashMap();
                personalBoardArticle.put("author", article.getAuthor());
                personalBoardArticle.put("description", article.getDescription());
                personalBoardArticle.put("link", article.getLink());
                personalBoardArticle.put("publishedDate", article.getPublishedDate());
                personalBoardArticle.put("thumbnailLink", article.getThumbnailLink());
                personalBoardArticle.put("title", article.getTitle());
                if(personalBoardDBRef  == null) {
                    Map personalBoardArticleKey = new HashMap();
                    personalBoardArticleKey.put("1",personalBoardArticle);

                    Map personalBoard = new HashMap();
                    personalBoard.put(boardName, personalBoardArticleKey);

                    Map personalBoardKey = new HashMap();
                    personalBoardKey.put("1", personalBoard);

                    Map myPersonalBoard = new HashMap();
                    myPersonalBoard.put(email.split("@")[0],personalBoardKey);

                    database.child("PersonalBoard").push().setValue(myPersonalBoard);
                } else {
                    Boolean boardExists = false;
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(snapshot.child(boardName).exists()){
                            boardExists = true;
                            personalBoardDBRef = personalBoardDBRef.child(snapshot.getKey()).child(boardName);
                            break;
                        }
                    }
                    if(boardExists) {
                        personalBoardDBRef.push().setValue(personalBoardArticle);
                    } else {
                        Map personalBoardArticleKey = new HashMap();
                        personalBoardArticleKey.put("1",personalBoardArticle);

                        Map personalBoard = new HashMap();
                        personalBoard.put(boardName, personalBoardArticleKey);

                        personalBoardDBRef.push().setValue(personalBoard);
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
        getMenuInflater().inflate(R.menu.menu_feed_desc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home){
            finish();
            return true;
        } else if(id == R.id.action_sort_by_rating){
            sortArticlesByRating();
            return true;
        } else if(id == R.id.action_sort_by_views){
            sortArticleByViews();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sortArticlesByRating() {
        //sort by rating and notify data set change for msectionadapter
        final DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference();

        database.child("Ratings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<Double,ArrayList<Article>> articlesTreeMap;
                articlesTreeMap = new TreeMap<>(Collections.reverseOrder());
                for(Article a : articles) {
                    DataSnapshot ratingArticleSnapShot = null;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (a.getLink() != null && a.getLink() != "" && snapshot.child("Link").getValue().equals(a.getLink())) {
                            ratingArticleSnapShot = snapshot;
                            break;
                        }
                    }
                    Double rating = 0.0;
                    if (ratingArticleSnapShot != null) {
                        int totalVoteCount = 0, weightedSum = 0;
                        for(int i=1 ;i<=5 ;i++) {
                            int numberOfVotes = Integer.parseInt(ratingArticleSnapShot.child("Votes").child(String.valueOf(i)).getValue().toString());
                            weightedSum += (i * numberOfVotes);
                            totalVoteCount += numberOfVotes;
                        }
                        rating = ((double) weightedSum) / ((double) totalVoteCount);
                    }
                    if(!articlesTreeMap.containsKey(rating)) {
                        articlesTreeMap.put(rating, new ArrayList<Article>());
                    }
                    articlesTreeMap.get(rating).add(a);
                }

                articles.clear();
                for(Map.Entry<Double,ArrayList<Article>> entry : articlesTreeMap.entrySet()) {
                    ArrayList<Article> list = entry.getValue();
                    articles.addAll(list);
                }
                mSectionsPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError d) {
                Log.d("Login DbError Msg ->", d.getMessage());
                Log.d("Login DbError Detail ->", d.getDetails());
            }
        });
    }

    private  void sortArticleByViews() {
        //sort by views and notify data set change for msectionPagerAdapter
        final DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference();

        database.child("Views").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<Integer,ArrayList<Article>> articlesTreeMap;
                articlesTreeMap = new TreeMap<>(Collections.reverseOrder());
                for(Article a : articles) {
                    DataSnapshot viewArticleSnapShot = null;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (a.getLink() != null && a.getLink() != "" && snapshot.child("Link").getValue().equals(a.getLink())) {
                            viewArticleSnapShot = snapshot;
                            break;
                        }
                    }
                    int views = 0;
                    if (viewArticleSnapShot != null) {
                        views = Integer.parseInt(viewArticleSnapShot.child("Count").getValue().toString());
                    }
                    if(!articlesTreeMap.containsKey(views)) {
                        articlesTreeMap.put(views, new ArrayList<Article>());
                    }
                    articlesTreeMap.get(views).add(a);
                }

                articles.clear();
                for(Map.Entry<Integer,ArrayList<Article>> entry : articlesTreeMap.entrySet()) {
                    ArrayList<Article> list = entry.getValue();
                    articles.addAll(list);
                }
                mSectionsPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError d) {
                Log.d("Login DbError Msg ->", d.getMessage());
                Log.d("Login DbError Detail ->", d.getDetails());
            }
        });
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String KEY = "KEY";
        private static final String ARG_CATEGORY = "CATEGORY";
        Article a;
        TextView articleTitle;
        TextView articleDesc;
        ImageView articleImg;
        Button linkButton;
        TextView articleInfo;
        TextView totalArticleRating;
        TextView articleRatingTxtView;
        TextView numOfViews;
        FloatingActionMenu materialDesignFAM;
        FloatingActionButton floatingActionButtonShare, floatingActionButtonPersonalBoard, floatingActionButtonReadLater;

        RatingBar ratBar;

        View rootView;
        public PlaceholderFragment() {

        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(Article article, String Category) {
            PlaceholderFragment fragment = new PlaceholderFragment();

            Bundle args = new Bundle();
            if(article != null) {
                    int key = article.getTitle().hashCode();
                    args.putInt(KEY,key);
                    TempStores.setArticle(key,article);
                    args.putString(ARG_CATEGORY,Category);

            } else {
                args.putString(null, null);
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Spanned spanned;
            rootView = inflater.inflate(R.layout.fragment_feed_desc, container, false);
            int theKey = getArguments().getInt(KEY);
            a = TempStores.getArticle(theKey);
            String theCategory = getArguments().getString(ARG_CATEGORY);

            articleTitle = (TextView) rootView.findViewById(R.id.article_title);
            articleInfo = (TextView) rootView.findViewById(R.id.article_info);
            articleImg = (ImageView) rootView.findViewById(R.id.article_photo);
            articleDesc = (TextView) rootView.findViewById(R.id.article_desc);
            materialDesignFAM = (FloatingActionMenu) rootView.findViewById(R.id.material_design_android_floating_action_menu);
            floatingActionButtonShare = (FloatingActionButton) rootView.findViewById(R.id.fab_btn_fb_share);
            floatingActionButtonPersonalBoard = (FloatingActionButton) rootView.findViewById(R.id.fab_btn_add_personal_board);
            floatingActionButtonReadLater = (FloatingActionButton) rootView.findViewById(R.id.fab_btn_add_read_later);
            linkButton = (Button) rootView.findViewById(R.id.article_link_button);
            totalArticleRating = (TextView) rootView.findViewById(R.id.total_article_rating);
            numOfViews = (TextView) rootView.findViewById(R.id.num_of_views);
            articleRatingTxtView = (TextView) rootView.findViewById(R.id.article_rating_txtView);
            ratBar = (RatingBar) rootView.findViewById(R.id.rat);

            if(a != null) {
                if (a.getLink() != null && a.getLink() != "") {
                    floatingActionButtonShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            shareOnFB(a.getLink());
                        }
                    });
                    linkButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openWebViewActivity(a.getLink());
                        }
                    });
                    getNumberOfViews(a);
                    setArticleRatings(a);
                } else {
                    floatingActionButtonShare.setVisibility(View.GONE);
                    linkButton.setVisibility(View.GONE);
                    totalArticleRating.setVisibility(View.GONE);
                    articleRatingTxtView.setVisibility(View.GONE);
                    ratBar.setVisibility(View.GONE);
                }

                if(!email.equals("")) {
                    floatingActionButtonReadLater.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            saveInReadLater(a);
                        }
                    });
                    floatingActionButtonPersonalBoard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addToPersonalBoard(a);
                        }
                    });
                } else {
                    floatingActionButtonReadLater.setVisibility(View.GONE);
                    floatingActionButtonPersonalBoard.setVisibility(View.GONE);
                }

                articleTitle.setText(a.getTitle());
                if(a.getThumbnailLink()!=null){
                    Picasso.with(getContext()).load(a.getThumbnailLink()).error(R.drawable.feed)
                            .placeholder(R.drawable.feed)
                            .into(articleImg);
                }else {
                    articleImg.setImageResource(R.drawable.feed);
                }
                String author;
                if(a.getAuthor()!=null){
                    author = a.getAuthor();
                }
                else{
                    author = "Feedly";
                }
                String theDate = a.getPublishedDate();
                if(theDate==null){
                    theDate = "N/A";
                }
                articleInfo.setText(theCategory+"/"+author+"/"+ TimeDateUtils.getTimePassed(getContext(),theDate));
                String description = a.getDescription();
                if(description == null){
                    description = "";
                }


                if (Build.VERSION.SDK_INT >= 24) {

                    spanned = Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY, new Html.ImageGetter() {
                        @Override
                        public Drawable getDrawable(String source) {
                            if(!source.startsWith("http")){
                                source = "http:" + source;
                            }
                            LevelListDrawable d = new LevelListDrawable();
                            Drawable empty = ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher);
                            d.addLevel(0, 0, empty);
                            d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

                            new LoadImage().execute(source, d);

                            return d;
                        }
                    }, null);
                } else {
                    spanned = Html.fromHtml(description, new Html.ImageGetter() {
                        @Override
                        public Drawable getDrawable(String source) {
                            if(!source.startsWith("http")){
                                source = "http:" + source;
                            }
                            LevelListDrawable d = new LevelListDrawable();
                            Drawable empty = ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher);
                            d.addLevel(0, 0, empty);
                            d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

                            new LoadImage().execute(source, d);

                            return d;
                        }
                    }, null);
                }
                CharSequence sequence = spanned;
                SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
                URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
                for (URLSpan span : urls) {
                    makeLinkClickable(strBuilder, span);
                }
                articleDesc.setText(strBuilder);
                articleDesc.setMovementMethod(LinkMovementMethod.getInstance());
            }
            return rootView;
        }

        private  void getNumberOfViews(final Article a) {
            final DatabaseReference database;
            database = FirebaseDatabase.getInstance().getReference();

            database.child("Views").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(snapshot.child("Link").getValue().equals(a.getLink())) {
                            numOfViews.setText(snapshot.child("Count").getValue().toString() + " Views");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError d) {
                    Log.d("Views DbError Msg ->", d.getMessage());
                    Log.d("Views DbError Detail ->", d.getDetails());
                }
            });
        }

        private void setArticleRatings(final Article a) {

            final DatabaseReference database;
            database = FirebaseDatabase.getInstance().getReference();

            database.child("Ratings").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DatabaseReference ratingDBRef = null;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(snapshot.child("Link").getValue().equals(a.getLink())){
                            dataSnapshot = snapshot;
                            ratingDBRef = database.child("Ratings").child(snapshot.getKey());
                            break;
                        }
                    }

                    if(ratingDBRef != null) {
                        showArticleRating(ratingDBRef);
                    }

                    if(email != null && email != "") {
                        if (!dataSnapshot.child("Voters").child(email.split("@")[0]).exists()) {
                            //set on click listener of rating
                            final DatabaseReference ratingDBRefCopy = ratingDBRef;
                            final DataSnapshot dataSnapshotCopy = dataSnapshot;
                            ratBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                @Override
                                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                                    int rating = (int) v;
                                    ratingBar.setIsIndicator(true);
                                    if (ratingDBRefCopy == null) { //Article rated first time
                                        Map articleLinkMap = new HashMap();
                                        articleLinkMap.put("Link", a.getLink());

                                        Map voterMap = new HashMap();
                                        voterMap.put(email.split("@")[0],String.valueOf(rating));

                                        Map ratingVotesCountMap = new HashMap();
                                        for(int i = 1; i <= 5 ; i++) {
                                            if( i == rating) {
                                                ratingVotesCountMap.put(String.valueOf(i), "1");
                                            } else {
                                                ratingVotesCountMap.put(String.valueOf(i), "0");
                                            }
                                        }
                                        String key = database.child("Ratings").push().getKey().toString();
                                        database.child("Ratings").child(key).setValue(articleLinkMap);
                                        database.child("Ratings").child(key).child("Voters").setValue(voterMap);
                                        database.child("Ratings").child(key).child("Votes").setValue(ratingVotesCountMap);
                                        showArticleRating(database.child("Ratings").child(key));

                                    } else {
                                        ratingDBRefCopy.child("Voters").child(email.split("@")[0]).setValue(String.valueOf(rating));
                                        int votes = 1 + Integer.parseInt(dataSnapshotCopy.child("Votes").child(String.valueOf(rating)).getValue().toString());
                                        ratingDBRefCopy.child("Votes").child(String.valueOf(rating)).setValue(String.valueOf(votes));
                                        showArticleRating(ratingDBRefCopy);
                                    }
                                }
                            });
                        } else {
                            ratBar.setIsIndicator(true);
                        }
                    } else {
                        ratBar.setVisibility(View.GONE);
                        articleRatingTxtView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError d) {
                    Log.d("Login DbError Msg ->", d.getMessage());
                    Log.d("Login DbError Detail ->", d.getDetails());
                }
            });
        }

        private void showArticleRating(DatabaseReference ratingDBRef) {
            ratingDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    float rating = 0;
                    int totalVoteCount = 0, weightedSum = 0;
                    for(int i=1 ;i<=5 ;i++) {
                        int numberOfVotes = Integer.parseInt(dataSnapshot.child("Votes").child(String.valueOf(i)).getValue().toString());
                        weightedSum += (i * numberOfVotes);
                        totalVoteCount += numberOfVotes;
                    }
                    rating = ((float) weightedSum) / ((float) totalVoteCount);
                    if(totalVoteCount > 1) {
                        totalArticleRating.setText("Rating: " + String.valueOf(rating) + "/5.0 (" + String.valueOf(totalVoteCount) + " Votes)");
                    } else {
                        totalArticleRating.setText("Rating: " + String.valueOf(rating) + "/5.0 (" + String.valueOf(totalVoteCount) + " Vote)");
                    }
                    if(email != null && email != "" &&  dataSnapshot.child("Voters").child(email.split("@")[0]).exists()) {
                        articleRatingTxtView.setText("Your Rating");
                        ratBar.setRating(Float.parseFloat(dataSnapshot.child("Voters").child(email.split("@")[0]).getValue().toString()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError d) {
                    Log.d("Login DbError Msg ->", d.getMessage());
                    Log.d("Login DbError Detail ->", d.getDetails());
                }
            });
        }

        private void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span)
        {
            final int start = strBuilder.getSpanStart(span);
            final int end = strBuilder.getSpanEnd(span);
            final int flags = strBuilder.getSpanFlags(span);
            ClickableSpan clickable = new ClickableSpan() {
                public void onClick(View view) {
                    openWebViewActivity(span.getURL());
                }
            };
            strBuilder.setSpan(clickable, start, end, flags);
            strBuilder.removeSpan(span);
        }

        private  void addToPersonalBoard(final Article a) {

            drawer.openDrawer(Gravity.START);

        }

        private void saveInReadLater(final Article a){
            final DatabaseReference database;
            database = FirebaseDatabase.getInstance().getReference();

            database.child("ReadLater").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DatabaseReference readLaterDBRef = null;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(snapshot.hasChild(email.split("@")[0])){
                            dataSnapshot = snapshot.child(email.split("@")[0]);
                            readLaterDBRef = database.child("ReadLater").child(snapshot.getKey()).child(email.split("@")[0]);
                            break;
                        }
                    }
                    Map ReadLaterArticle = new HashMap();
                    ReadLaterArticle.put("author", a.getAuthor());
                    ReadLaterArticle.put("description", a.getDescription());
                    ReadLaterArticle.put("link", a.getLink());
                    ReadLaterArticle.put("publishedDate", a.getPublishedDate());
                    ReadLaterArticle.put("thumbnailLink", a.getThumbnailLink());
                    ReadLaterArticle.put("title", a.getTitle());
                    if(readLaterDBRef == null) {
                        Map readLaterKey = new HashMap();
                        readLaterKey.put("1",ReadLaterArticle);

                        Map myReadLater = new HashMap();
                        myReadLater.put(email.split("@")[0],readLaterKey);

                        database.child("ReadLater").push().setValue(myReadLater);
                    } else {
                        Boolean articleAlreadyAdded = false;
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if(snapshot.child("link").getValue().toString().equals(a.getLink())){
                                articleAlreadyAdded = true;
                                break;
                            }
                        }
                        if(!articleAlreadyAdded) {
                            readLaterDBRef.push().setValue(ReadLaterArticle);
                        } else {
                            Toast.makeText(getContext(),"Article Already added in Personal Board",Toast.LENGTH_LONG);
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
        private void shareOnFB(String url){
            ShareLinkContent content = new ShareLinkContent.Builder().setContentUrl(Uri.parse(url)).build();
            ShareDialog.show(this, content);
        }

        private void openWebViewActivity(String url){
            Intent i = new Intent("android.intent.action.feed.webview");
            i.putExtra("URL", url);
            startActivity(i);

        }


        class LoadImage extends AsyncTask<Object, Void, Bitmap> {

            private LevelListDrawable mDrawable;

            @Override
            protected Bitmap doInBackground(Object... params) {
                String source = (String) params[0];
                mDrawable = (LevelListDrawable) params[1];
                try {
                    InputStream is = new URL(source).openStream();
                    return BitmapFactory.decodeStream(is);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                Context thisContext = getContext();
                if (bitmap != null && thisContext!=null) {
                    BitmapDrawable d = new BitmapDrawable(thisContext.getResources(), bitmap);
                    int height = bitmap.getHeight();
                    mDrawable.addLevel(1, 1, d);
                    View thisView = getView();
                    if (thisView != null) {
                        if (bitmap.getHeight() < thisView.getWidth()) {
                            height = thisView.getWidth();
                        }
                        mDrawable.setBounds(0, 0, thisView.getWidth(), height);
                        mDrawable.setLevel(1);
                        CharSequence t = articleDesc.getText();
                        articleDesc.setText(t);
                    } else {
                        mDrawable.setBounds(0, 0, 0, 0);
                        mDrawable.setLevel(1);
                        CharSequence t = articleDesc.getText();
                        articleDesc.setText(t);
                    }
                }
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<Article> articlesList;

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<Article> articlesList) {
            super(fm);
            this.articlesList = articlesList;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(articlesList != null && articlesList.size() > position) {
                return PlaceholderFragment.newInstance(articlesList.get(position),category);
            }
            return PlaceholderFragment.newInstance(null,null);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            // Show total pages => size of list.
            return articlesList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(articlesList != null && articlesList.size() > position) {
                return articlesList.get(position).getTitle();
            }
            return null;
        }


    }

}
