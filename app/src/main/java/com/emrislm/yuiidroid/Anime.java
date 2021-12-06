package com.emrislm.yuiidroid;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Anime {

    private int mal_id;
    private String url;
    private String image_url;
    private String title;
    private Boolean airing;
    private String synopsis;
    private String type;
    private int episodes;
    private double score;
    private String start_date;
    private String end_date;
    private int members;
    private String rated;

    public String getStartDateFormatted() {
        String[] split = start_date.split("T");
        return split[0];
    }
    public String getEndDateFormatted() {
        String[] split = end_date.split("T");
        return split[0];
    }

    public int getMal_id() {
        return mal_id;
    }
    public void setMal_id(int mal_id) {
        this.mal_id = mal_id;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage_url() {
        return image_url;
    }
    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getAiring() {
        return airing;
    }
    public void setAiring(Boolean airing) {
        this.airing = airing;
    }

    public String getSynopsis() {
        return synopsis;
    }
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public int getEpisodes() {
        return episodes;
    }
    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }

    public double getScore() { return score; }
    public void setScore(double score) {
        this.score = score;
    }

    public String getStart_date() {
        return start_date;
    }
    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }
    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public int getMembers() {
        return members;
    }
    public void setMembers(int members) {
        this.members = members;
    }

    public String getRated() {
        return rated;
    }
    public void setRated(String rated) {
        this.rated = rated;
    }
}
