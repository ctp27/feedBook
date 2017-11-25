package com.sdpm.feedly.feedly;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.sdpm.feedly.utils.TempStores;
import com.sdpm.feedly.utils.TimeDateUtils;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import model.Article;

public class feed_desc extends AppCompatActivity {

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
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_desc);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

//        articles = (ArrayList<Article>) getIntent().getSerializableExtra("articlesList");
        articles = (ArrayList<Article>)TempStores.getTheFeeds();
        category = getIntent().getExtras().getString("category");
        getSupportActionBar().setTitle(category);
        int position = getIntent().getIntExtra("position",0);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),articles);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(position);

        int limit = (mSectionsPagerAdapter.getCount() > 1 ? mSectionsPagerAdapter.getCount() - 1 : 1);
        //mViewPager.setOffscreenPageLimit(limit);
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
        if (id == R.id.action_settings) {
            return true;
        }else if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        FloatingActionButton btnShareOnFB;
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
            btnShareOnFB = (FloatingActionButton) rootView.findViewById(R.id.fab);
            linkButton = (Button) rootView.findViewById(R.id.article_link_button);

            if(a != null) {
                if (a.getLink() != null && a.getLink() != "") {
                    btnShareOnFB.setOnClickListener(new View.OnClickListener() {
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
                } else {
                    btnShareOnFB.setVisibility(View.GONE);
                    linkButton.setVisibility(View.GONE);
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

                if (Build.VERSION.SDK_INT >= 24) {
                    spanned = Html.fromHtml(a.getDescription(), Html.FROM_HTML_MODE_LEGACY, new Html.ImageGetter() {
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
                    spanned = Html.fromHtml(a.getDescription(), new Html.ImageGetter() {
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
                if (bitmap != null) {
                    BitmapDrawable d = new BitmapDrawable(getContext().getResources(), bitmap);
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
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

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
