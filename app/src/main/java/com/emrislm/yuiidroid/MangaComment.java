package com.emrislm.yuiidroid;

public class MangaComment {

    private String title;
    private String date_posted;
    private String author;
    private int replies;

    public String getPostedDateFormatted() {
        String[] split = date_posted.split("T");
        return split[0];
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDate_posted() { return date_posted; }
    public void setDate_posted(String date_posted) { this.date_posted = date_posted; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public int getReplies() { return replies; }
    public void setReplies(int replies) { this.replies = replies; }
}
