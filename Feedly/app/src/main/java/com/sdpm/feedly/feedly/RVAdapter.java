package com.sdpm.feedly.feedly;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import model.Article;

/**
 * Created by Junaid on 10/5/2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.FeedViewHolder> {

    ArrayList<Article> articles;

    public RVAdapter(ArrayList<Article> articles){
        this.articles = articles;
    }


    public static class FeedViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        ImageView feedImg;
        TextView feedTitle;
        TextView feedDesc;
        TextView feedInfo;

        FeedViewHolder(View view) {
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


//    TODO: Need to add functionality to download and display thumbnails in recylcer view
    @Override
    public void onBindViewHolder(FeedViewHolder holder,final int position) {
        holder.feedImg.setImageResource(R.drawable.food);
        holder.feedTitle.setText(articles.get(position).getTitle());

        String tempDesc = articles.get(position).getDescription();
        if(tempDesc.length()<=25) {
            holder.feedDesc.setText(Html.fromHtml(tempDesc));
        }
        else{
            holder.feedDesc.setText(Html.fromHtml(tempDesc.substring(0,25)));
        }
        String author = articles.get(position).getAuthor();
        if(author!=null) {

            holder.feedInfo.setText("by " + articles.get(position).getAuthor() + " - ");
        }
        else{
            holder.feedInfo.setText("by Feedly - ");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent("android.intent.action.feed.desc");
                i.putExtra("position",position);
                i.putExtra("articlesList",articles);
                view.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.articles.size();
    }
}
