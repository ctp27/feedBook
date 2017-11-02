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

import com.sdpm.feedly.utils.HtmlParseUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import model.Article;

/**
 * Created by Junaid on 10/5/2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.FeedViewHolder> {

    ArrayList<Article> articles;
    private Context context;

    public RVAdapter(ArrayList<Article> articles, Context context) {
        this.articles = articles;
        this.context = context;
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

        holder.feedTitle.setText(articles.get(position).getTitle());
        /**
         * Method sets the image URL
         */
        setTheRowImage(holder,position);

        setThePartialDescription(holder,position);

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

    private void setThePartialDescription(FeedViewHolder holder, int position) {
        String tempDesc = articles.get(position).getDescription();
        if(HtmlParseUtils.containsHtml(tempDesc)){
            /**
             * If html then do this
             */
            holder.feedDesc.setText(HtmlParseUtils.getPartialDescription(tempDesc));
        }
        else {
            /**
             * If Not html then do this
             */
            if (tempDesc.length() <= 89) {
                holder.feedDesc.setText(Html.fromHtml(tempDesc));
            } else {
                holder.feedDesc.setText(Html.fromHtml(tempDesc.substring(0, 89)));
            }
        }
    }

    private void setTheRowImage(FeedViewHolder holder, int position) {
        String theUrl = null;

        theUrl = articles.get(position).getThumbnailLink();

        if(theUrl==null){
            String tempDesc = articles.get(position).getDescription();
            if(HtmlParseUtils.containsHtml(tempDesc)) {
                theUrl = HtmlParseUtils.getImageUrlFromDescription(tempDesc);
            }else{
                holder.feedImg.setImageResource(R.drawable.feed);
                return;
            }
        }
        if(!theUrl.isEmpty()) {
            if(HtmlParseUtils.isValidUrl(theUrl)) {
                Picasso.with(context).load(theUrl).error(R.drawable.feed)
                        .placeholder(R.drawable.feed)
                        .into(holder.feedImg);
            }
            else{
                theUrl = "https:"+theUrl;
                if(HtmlParseUtils.isValidUrl(theUrl)){
                    Picasso.with(context).load(theUrl).error(R.drawable.feed)
                            .placeholder(R.drawable.feed)
                            .into(holder.feedImg);
                }
                else {
                    holder.feedImg.setImageResource(R.drawable.feed);
                }
            }
        }else {
            holder.feedImg.setImageResource(R.drawable.feed);
        }
    }

    @Override
    public int getItemCount() {
        return this.articles.size();
    }


}
