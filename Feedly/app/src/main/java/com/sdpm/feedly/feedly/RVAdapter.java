package com.sdpm.feedly.feedly;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import model.Feed;

/**
 * Created by Junaid on 10/5/2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.FeedViewHolder> {

    List<Feed> feeds;

    RVAdapter(List<Feed> feeds){
        this.feeds = feeds;
    }


    public static class FeedViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        ImageView feedImg;
        TextView feedTitle;
        TextView feedDesc;
        TextView feedInfo;

        FeedViewHolder(View view){
            super(view);
            cv = (CardView) view.findViewById(R.id.cv);
            feedImg = (ImageView) view.findViewById(R.id.feed_photo);
            feedTitle = (TextView) view.findViewById(R.id.feed_title);
            feedDesc = (TextView) view.findViewById(R.id.feed_description);
            feedInfo = (TextView) view.findViewById(R.id.feed_info);
        }
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedrow,parent,false);
        FeedViewHolder fv = new FeedViewHolder(v);
        return fv;
    }

    @Override
    public void onBindViewHolder(FeedViewHolder holder, int position) {
        holder.feedImg.setImageResource(R.drawable.food);
        holder.feedTitle.setText(feeds.get(position).getTitle());
        holder.feedDesc.setText(feeds.get(position).getDesc());
        holder.feedInfo.setText("by "+feeds.get(position).getAuthor()+" - "+feeds.get(position).getPubData());
    }

    @Override
    public int getItemCount() {
        return this.feeds.size();
    }
}
