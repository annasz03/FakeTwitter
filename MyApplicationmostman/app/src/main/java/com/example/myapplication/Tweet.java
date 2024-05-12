package com.example.myapplication;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Tweet {
    private int id;
    private String name;
    private String test;
    private int like;
    private int comment;
    private int retweet;
    private String imageresource;
    private boolean liked;
    private Set<String> likedByUsers;

    public Tweet() {
    }

    public Tweet(int id, String name, String test, int like,  int comment, int retweet) {
this.id=id;
        this.name = name;
        this.test = test;
        this.like = like;
        this.comment = comment;
        this.retweet = retweet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getRetweet() {
        return retweet;
    }

    public void setRetweet(int retweet) {
        this.retweet = retweet;
    }

    public String getImageresource() {
        return imageresource;
    }

    public void setImageresource(String imageresource) {
        this.imageresource = imageresource;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

}
