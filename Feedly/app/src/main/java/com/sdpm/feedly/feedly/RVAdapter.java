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
import com.sdpm.feedly.utils.TempStores;
import com.sdpm.feedly.utils.TimeDateUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import model.Article;

/**
 * Created by Junaid on 10/5/2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.FeedViewHolder> {

    ArrayList<Article> articles;
    private Context context;
    private String feedCategory;

    public RVAdapter(ArrayList<Article> articles, Context context, String feedCategory) {
        this.articles = articles;
        this.context = context;
        this.feedCategory = feedCategory;

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

        /* Method sets the imageView for the current position*/
        setTheRowImage(holder,position);

        setThePartialDescription(holder,position);

        String author = articles.get(position).getAuthor();
        String dateString=null;
        if(articles.get(position).getPublishedDate()!=null){
            dateString = articles.get(position).getPublishedDate();
        }else{
            dateString = "N/A";
        }

        if(author!=null) {

            holder.feedInfo.setText("by " + articles.get(position).getAuthor() + " - "+ TimeDateUtils.getTimePassed(context,dateString));
        }
        else{
            holder.feedInfo.setText("by Feedly - "+ TimeDateUtils.getTimePassed(context,dateString));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent("android.intent.action.feed.desc");
                i.putExtra("position",position);
                i.putExtra("category",feedCategory);
//                i.putExtra("articlesList",articles);
                TempStores.setTheFeeds(articles);
                view.getContext().startActivity(i);
            }
        });
    }


    /**
     * This method sets the partial description in the RecyclerView. If the description contains
     * HTML, it extracts the text and displays it. The partial description just displays a string
     * of 90 characters
     * @param holder the FeedViewHolder object which contains the front end components
     * @param position the current row position to be set
     */

    private void setThePartialDescription(FeedViewHolder holder, int position) {
        String tempDesc = articles.get(position).getDescription();
        if(HtmlParseUtils.containsHtml(tempDesc)){
//             * If html then do this
            holder.feedDesc.setText(HtmlParseUtils.getPartialDescription(tempDesc));
        }
        else {
//             * If Not html then do this
            if (tempDesc.length() <= 89) {
                holder.feedDesc.setText(Html.fromHtml(tempDesc));
            } else {
                holder.feedDesc.setText(Html.fromHtml(tempDesc.substring(0, 89)));
            }
        }
    }

    /**
     * Private method called by the onBindViewHolder() method of the recycler view. This method
     * sets the ImageView in the recycler view using the link in the ThumbnailLink field. If the
     * field is null, it displays the default placeholder image
     *
     * @param holder theFeedViewHolder object that contains the imageView
     * @param position the current row position of the recycler view
     */

    private void setTheRowImage(FeedViewHolder holder, int position) {
        String thumbnail= articles.get(position).getThumbnailLink();
        if(thumbnail!=null){
            Picasso.with(context).load(thumbnail).error(R.drawable.feed)
                        .placeholder(R.drawable.feed)
                        .into(holder.feedImg);
        }
        else{
            holder.feedImg.setImageResource(R.drawable.feed);
        }
    }

    @Override
    public int getItemCount() {
        return this.articles.size();
    }


}
