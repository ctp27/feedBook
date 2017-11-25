package com.sdpm.feedly.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdpm.feedly.feedly.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.Feed;

/**
 * Created by clinton on 11/8/17.
 */

public class CheckboxExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> categoriesList; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Feed>> feedsListUnderCategory;
    private static ArrayList<Feed> feedsToDelete;
    private int counter;
    Button removeBtn;

    public CheckboxExpandableListAdapter(Context context, List<String> categoriesList,
                                 HashMap<String, List<Feed>> feedsListUnderCategory, Button removeBtn) {
        this._context = context;
        this.categoriesList = categoriesList;
        this.feedsListUnderCategory = feedsListUnderCategory;
        feedsToDelete = new ArrayList<>();
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

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.editable_feed_subrow, null);
        }

        ImageView feedImg;
        CheckBox theCheckbox;
        final TextView feedTitle;

        feedImg = (ImageView) convertView.findViewById(R.id.edit_feed_photo);
        feedTitle = (TextView) convertView.findViewById(R.id.edit_feed_title);
        theCheckbox = (CheckBox) convertView.findViewById(R.id.edit_feed_checkbox);

        if(feedsToDelete.size() == 0) {
            theCheckbox.setChecked(false);
        }/*else{
            boolean inDeleteList = false;
            for(int i=0 ;i<feedsToDelete.size(); i++){
                if(feedsToDelete.get(i).getName().equals(childFeed.getName())){
                    inDeleteList = true;
                    break;
                }
            }
            if(!inDeleteList){
                theCheckbox.setChecked(false);
            }
        }*/

        feedTitle.setText(childFeed.getName());
        feedImg.setImageResource(R.drawable.feed);

        theCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    feedsToDelete.add(childFeed);
                    counter++;
                    removeBtn.setText("Remove ("+counter+")");
                    removeBtn.setEnabled(true);
                }
                else{
                    if(feedsToDelete.contains(childFeed)){
                        for(int i=0;i<feedsToDelete.size();i++){
                            if(feedsToDelete.get(i).getName() == childFeed.getName()){
                                feedsToDelete.remove(i);
                                counter--;
                                if(counter==0){
                                    removeBtn.setText("Remove");
                                    removeBtn.setEnabled(false);
                                } else {
                                    removeBtn.setText("Remove ("+counter+")");
                                }

                                break;
                            }
                        }
                    }
                }

            }
        });



        return convertView;
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

    public static ArrayList<Feed> getFeedsToDelete() {
        return feedsToDelete;
    }

    public void resetViewAfterDelete(){
        feedsToDelete = new ArrayList<>();
        counter = 0;
        removeBtn.setText("Remove");
        removeBtn.setEnabled(false);
    }

}
