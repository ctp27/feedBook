package com.sdpm.feedly.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdpm.feedly.feedly.R;
import com.sdpm.feedly.model.Feed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by clinton on 12/6/17.
 */

public class SourceListAdapter extends ArrayAdapter<String> {

    private static final String TAG = SourceListAdapter.class.getSimpleName();
    private List<String> sourceList;
    private List<String> personalCategoriesList;
    private HashMap<String, List<Feed>> personalFeedsUnderCategory;
    private SourceListAdapterListener theListener;
    private List<Feed> allFeeds;
    private Context context;
    private String userEmail;

    public interface SourceListAdapterListener{
        void onAddSource();
        void onSourceViewClick(Feed f);
    }

    public SourceListAdapter(@NonNull Context context, int resource, List<String> sourceList,
                             List<String> personalCategoriesList, HashMap<String, List<Feed>> personalFeedsUnderCategory,
                                SourceListAdapterListener theListener,
                                    List<Feed> allFeeds) {
        super(context, resource, sourceList);
        this.context = context;
        this.sourceList = sourceList;
        this.personalCategoriesList = personalCategoriesList;
        this.personalFeedsUnderCategory = personalFeedsUnderCategory;
        this.theListener = theListener;
        this.allFeeds = allFeeds;
        getUserEmailFromPreference();
        Log.d(TAG,userEmail);

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.add_source_subrow, null);
        }


        String s = getItem(position);
        final Feed thisFeed = getFeedFromName(s);
        boolean isAdded = isAlreadyAdded(s);

        TextView textView = (TextView) v.findViewById(R.id.add_feed_title);
        textView.setText(s);

        final ImageButton button = (ImageButton) v.findViewById(R.id.add_feed_btn);
        ImageView feedImg = (ImageView) v.findViewById(R.id.add_feed_photo);
        if(!isAdded) {
            enableTheButton(button);
        }
        else{
            disableTheButton(button);
        }

        feedImg.setImageResource(R.drawable.feed);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theListener.onSourceViewClick(thisFeed);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addThisFeed(v,button,thisFeed,thisFeed.getCategory());

            }
        });

        return v;
    }


    private void addThisFeed(View v, final ImageButton btn, final Feed childfeed, final String category){
        disableTheButton(btn);
       final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
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
                    personalMap.put(userEmail.split("@")[0],uniqueCategoryKeyMap);


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
                        if(snapshot.hasChild(userEmail.split("@")[0])){
                            dbSnapPersonal = database.child("PersonalFeeds").child(snapshot.getKey()).child(userEmail.split("@")[0]);
                            snapshotPersonal = snapshot.child(userEmail.split("@")[0]);
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
                notifyDataSetChanged();
                theListener.onAddSource();


            }

            @Override
            public void onCancelled(DatabaseError d){
                Log.d("Login DbError Msg ->",d.getMessage());
                Log.d("Login DbError Detail ->",d.getDetails());
            }
        });

    }


    private void disableTheButton(ImageButton btn){
        btn.setImageResource(R.drawable.ic_check_black_24dp);
        btn.setEnabled(false);
    }


    private void enableTheButton(ImageButton btn){
        btn.setImageResource(R.drawable.ic_add_black_24dp);
        btn.setEnabled(true);
    }


    private boolean isAlreadyAdded(String childFeedName) {
        boolean exists = false;
        for(String a : personalCategoriesList){

            List<Feed> theFeeds = personalFeedsUnderCategory.get(a);
            for(Feed f : theFeeds){
                if(f.getName().equalsIgnoreCase(childFeedName)){
                    exists = true;
                }

            }

        }
        return exists;
    }

    private void getUserEmailFromPreference(){
        SharedPreferences userDetails = context.getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        String email = userDetails.getString("email", "");
        if(email!=null){
            userEmail = email;
        }
    }

    private Feed getFeedFromName(String name){
        for(Feed f: allFeeds){
            if(f.getName().equalsIgnoreCase(name)){
                return f;
            }
        }
        return null;
    }
}

