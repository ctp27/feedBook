package com.sdpm.feedly.adapters;

/**
 * Created by Junaid on 11/7/2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdpm.feedly.feedly.R;

import java.util.HashMap;
import java.util.List;

import model.Feed;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> categoriesList; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Feed>> feedsListUnderCategory;

    public ExpandableListAdapter(Context context, List<String> categoriesList,
                                 HashMap<String, List<Feed>> feedsListUnderCategory) {
        this._context = context;
        this.categoriesList = categoriesList;
        this.feedsListUnderCategory = feedsListUnderCategory;
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
            convertView = infalInflater.inflate(R.layout.personal_feed_lv_subrow, null);
        }

        ImageView feedImg;

        TextView feedTitle;

        feedImg = (ImageView) convertView.findViewById(R.id.elv_feed_photo);
        feedTitle = (TextView) convertView.findViewById(R.id.elv_feed_title);

        feedTitle.setText(childFeed.getName());
        feedImg.setImageResource(R.drawable.feed);
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
}