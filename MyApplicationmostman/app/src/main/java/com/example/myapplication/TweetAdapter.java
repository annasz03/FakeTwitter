package com.example.myapplication;

import android.content.Context;
import android.text.NoCopySpan;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;


public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> implements Filterable {
    private ArrayList<Tweet> tweets;
    private ArrayList<Tweet> tweetsAll;
    private Context context;
    private int lastposition = -1;


    TweetAdapter(Context context, ArrayList<Tweet> tweets) {
        this.tweets = tweets;
        this.tweetsAll = tweets;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.listazas, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TweetAdapter.ViewHolder holder, int position) {
        Tweet currenttweet = tweets.get(position);
        holder.bindTo(currenttweet);
//bin
        holder.name.setText(currenttweet.getName());
        holder.test.setText(currenttweet.getTest());

        if (isCurrentUserTweet(currenttweet)) {
            holder.bin.setVisibility(View.VISIBLE);
            holder.pen.setVisibility(View.VISIBLE);
        } else {
            holder.bin.setVisibility(View.GONE);
            holder.pen.setVisibility(View.GONE);
        }
//bin
        if(holder.getAdapterPosition()>lastposition){
            Animation animation= AnimationUtils.loadAnimation(context, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastposition= holder.getAdapterPosition();
        }
    }

    private boolean isCurrentUserTweet(Tweet tweet) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser != null && tweet.getName().equals(currentUser.getDisplayName());
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }


    @Override
    public Filter getFilter() {
        return tweetFilter;
    }


    private Filter tweetFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Tweet> filteredlist = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (charSequence == null || charSequence.length() == 0) {
                results.count = tweetsAll.size();
                results.values = tweetsAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Tweet tweet : tweetsAll) {
                    if (tweet.getTest().toLowerCase().contains(filterPattern)) {
                        filteredlist.add(tweet);
                    }
                }


                results.count = tweetsAll.size();
                results.values = tweetsAll;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            tweets= (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };



    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView test;
        private TextView like;
        private TextView comment;
        private TextView retweet;
        private ImageView kep;
        private ImageView kep2;

        public ImageView bin;
        public ImageView pen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name= itemView.findViewById(R.id.name);
            test= itemView.findViewById(R.id.test);
            like= itemView.findViewById(R.id.like);
            comment= itemView.findViewById(R.id.comment);
            retweet= itemView.findViewById(R.id.retweet);
            kep= itemView.findViewById(R.id.kep);
            kep2= itemView.findViewById(R.id.kep2);
            bin = itemView.findViewById(R.id.bin);
            pen = itemView.findViewById(R.id.pen);
        }

        public void bindTo(Tweet currenttweet) {
            name.setText(currenttweet.getName());
            test.setText(currenttweet.getTest());
            like.setText(String.valueOf(currenttweet.getLike()));
            comment.setText(String.valueOf(currenttweet.getComment()));
            retweet.setText(String.valueOf(currenttweet.getRetweet()));

            //kep betoltese
            if (currenttweet.getImageresource() != null && !currenttweet.getImageresource().isEmpty()) {
                kep2.setVisibility(View.VISIBLE);
                Picasso.get().load(currenttweet.getImageresource()).into(kep2);
            } else {
                kep2.setVisibility(View.GONE);
            }
        }


    }
}

