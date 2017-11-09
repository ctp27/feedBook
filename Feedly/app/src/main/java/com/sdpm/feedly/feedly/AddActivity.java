package com.sdpm.feedly.feedly;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Article;
import model.Feed;

public class AddActivity extends AppCompatActivity {


    private ArrayList<Feed> allFeeds;
    private DatabaseReference database;
    private List<String> allCategoriesList;

    private HashMap<String, List<Feed>> personalFeedsUnderCategory;
    private List<String> personalCategoriesList;

    private HashMap<String, List<Feed>> allFeedsUnderCategory;
    private ExpandableListView addFeedsView;
    private String email;

    private static final String TAG = "AddActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance().getReference();
        email = getIntent().getExtras().getString("email");
        getPersonalFeeds();
        addFeedsView = (ExpandableListView) findViewById(R.id.add_source_expandable);
//        populateFeedsFromDatabase();
        createExpandableList();


        AddSourceExpandableAdapter theAdapter = new AddSourceExpandableAdapter(AddActivity.this,allCategoriesList,
                allFeedsUnderCategory);
        addFeedsView.setAdapter(theAdapter);


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
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPersonalFeeds(){
        personalCategoriesList = new ArrayList<>();
        personalFeedsUnderCategory = new HashMap<String,List<Feed>>();
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


                }
            }

            @Override
            public void onCancelled(DatabaseError d) {
                Log.d("Login DbError Msg ->", d.getMessage());
                Log.d("Login DbError Detail ->", d.getDetails());
            }
        });



    }

    private void createExpandableList(){
        allFeeds = new ArrayList<>();
        allCategoriesList = new ArrayList<>();
        allFeedsUnderCategory = new HashMap<String,List<Feed>>();
        database.child("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    allCategoriesList.add(snapshot.getKey().toString());
                    List<Feed> thisFeedList = new ArrayList<>();
//                    Log.d(TAG,snapshot.getKey().toString());
                    for(DataSnapshot subSnapshot: snapshot.getChildren()){
                        thisFeedList.add(new Feed(subSnapshot.child("name").getValue().toString(), snapshot.getKey().toString(), "", subSnapshot.child("rssLink").getValue().toString(), new ArrayList<Article>()));
                    }
                    allFeedsUnderCategory.put(snapshot.getKey().toString(), thisFeedList);
//
                }
                AddSourceExpandableAdapter addSourceExpandableAdapter = new AddSourceExpandableAdapter(AddActivity.this, allCategoriesList, allFeedsUnderCategory);
                addFeedsView.setAdapter(addSourceExpandableAdapter);
//
            }

            @Override
            public void onCancelled(DatabaseError d) {
                Log.d("Login DbError Msg ->", d.getMessage());
                Log.d("Login DbError Detail ->", d.getDetails());
            }
        });

    }


    public class AddSourceExpandableAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<String> categoriesList; // header titles
        // child data in format of header title, child title
        private HashMap<String, List<Feed>> feedsListUnderCategory;
        private int counter;
        Button removeBtn;

        public AddSourceExpandableAdapter(Context context, List<String> categoriesList,
                                          HashMap<String, List<Feed>> feedsListUnderCategory) {
            this._context = context;
            this.categoriesList = categoriesList;
            this.feedsListUnderCategory = feedsListUnderCategory;
            counter = 0;
            this.removeBtn = removeBtn;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {

            return this.feedsListUnderCategory.get(this.categoriesList.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final Feed childFeed = (Feed) getChild(groupPosition, childPosition);
            final String category = this.categoriesList.get(groupPosition);
            boolean isAdded = isAlreadyAdded(childFeed);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.add_source_subrow, null);
            }

            ImageView feedImg;
            final ImageButton imageButton;
            final TextView feedTitle;

            feedImg = (ImageView) convertView.findViewById(R.id.add_feed_photo);
            feedTitle = (TextView) convertView.findViewById(R.id.add_feed_title);
            imageButton = (ImageButton) convertView.findViewById(R.id.add_feed_btn);
            if(!isAdded) {
                enableTheButton(imageButton);
            }
            else{
                disableTheButton(imageButton);
            }

            feedTitle.setText(childFeed.getName());
            feedImg.setImageResource(R.drawable.feed);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateTheDatabase(v,imageButton,childFeed,category);
                }
            });

            return convertView;
        }

        private void updateTheDatabase(View v, ImageButton btn, final Feed childfeed, final String category) {
            database.child("PersonalFeeds").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(personalCategoriesList.size() == 0){ //first personal feed
                        Map rssMap= new HashMap();
                        rssMap.put("name",childfeed.getName());
                        rssMap.put("rssLink",childfeed.getLink());

                        Map uniqueRssKeyMap = new HashMap();
                        uniqueRssKeyMap.put("1",rssMap);

                        Map categoryMap = new HashMap();
                        categoryMap.put(category,uniqueRssKeyMap);

                        Map uniqueCategoryKeyMap = new HashMap();
                        uniqueCategoryKeyMap.put("1",categoryMap);

                        Map personalMap = new HashMap();
                        personalMap.put(email.split("@")[0],uniqueCategoryKeyMap);


                        database.child("PersonalFeeds").push().setValue(personalMap);
                        personalCategoriesList.add(category);
                        ArrayList<Feed> a = new ArrayList<>();
                        a.add(childfeed);
                        personalFeedsUnderCategory.put(category,a);
                    }else { //add under existing category or make new category
                        DatabaseReference dbSnapPersonal = null;
                        DataSnapshot snapshotPersonal = null;
                        Map rssMap= new HashMap();
                        rssMap.put("name",childfeed.getName());
                        rssMap.put("rssLink",childfeed.getLink());
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                           if(snapshot.hasChild(email.split("@")[0])){
                               dbSnapPersonal = database.child("PersonalFeeds").child(snapshot.getKey()).child(email.split("@")[0]);
                               snapshotPersonal = snapshot.child(email.split("@")[0]);
                               break;
                           }
                        }
                        if (dbSnapPersonal != null) {
                            if (personalCategoriesList.contains(category)) { //add under existing category
                                for(DataSnapshot snapshot : snapshotPersonal.getChildren()){
                                    if(snapshot.hasChild(category)){
                                        dbSnapPersonal.child(snapshot.getKey()).child(category).push().setValue(rssMap);
                                        personalFeedsUnderCategory.get(category).add(childfeed);
                                        break;
                                    }
                                }
                            } else { //new category
                                Map uniqueRssKeyMap = new HashMap();
                                uniqueRssKeyMap.put("1",rssMap);

                                Map categoryMap = new HashMap();
                                categoryMap.put(category,uniqueRssKeyMap);

                                dbSnapPersonal.push().setValue(categoryMap);
                                personalCategoriesList.add(category);
                                ArrayList<Feed> a = new ArrayList<>();
                                a.add(childfeed);
                                personalFeedsUnderCategory.put(category,a);
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError d){
                    Log.d("Login DbError Msg ->",d.getMessage());
                    Log.d("Login DbError Detail ->",d.getDetails());
                }
            });

            /* : Uncomment this method call once entry is added in database. Disables the button */

            disableTheButton(btn);

        }

        private void disableTheButton(ImageButton btn){
            btn.setImageResource(R.drawable.ic_check_black_24dp);
            btn.setEnabled(false);
        }

        private void enableTheButton(ImageButton btn){
            btn.setImageResource(R.drawable.ic_add_black_24dp);
            btn.setEnabled(true);
        }
        private boolean isAlreadyAdded(Feed childFeed) {
            boolean exists = false;
            for(String a : personalCategoriesList){

                List<Feed> theFeeds = personalFeedsUnderCategory.get(a);
                for(Feed f : theFeeds){
                    if(f.getName().equalsIgnoreCase(childFeed.getName())){
                        exists = true;
                    }

                }

            }
            return exists;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.feedsListUnderCategory.get(this.categoriesList.get(groupPosition))
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.categoriesList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this.categoriesList.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String categoryTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.personal_feed_lv_row, null);
            }

            TextView categoryHeader = (TextView) convertView.findViewById(R.id.category_name);
            //  categoryHeader.setTypeface(null, Typeface.BOLD);
            categoryHeader.setText(categoryTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }


    }
}
