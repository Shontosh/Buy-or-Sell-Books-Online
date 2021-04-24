package com.lab69.booksellapp.Model;

public class Post {
    private String postid;
    private String postimage;
    private String title;
    private String publisher;
    private String description;
    private String price;

    public Post() {
    }

    public Post(String postid, String postimage, String title, String publisher, String description, String price) {
        this.postid = postid;
        this.postimage = postimage;
        this.title = title;
        this.publisher = publisher;
        this.description = description;
        this.price = price;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
