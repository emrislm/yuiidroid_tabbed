package com.emrislm.yuiidroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AnimeActivity extends AppCompatActivity {

    String BASEURL = "https://api.jikan.moe/v3/anime/";

    ImageView coverImageView;
    TextView titleTextView;
    TextView episodesTextView;
    TextView scoreTextView;
    TextView descriptionTextView;
    RecyclerView staffListView;
    ListView statsListView;
    ListView commentsListView;

    int MAL_ID;

    AnimeStaff tempStaff;
    ArrayList<AnimeStaff> tempStaffList;

    AnimeStats tempStats;

    AnimeComment tempComment;
    ArrayList<AnimeComment> tempCommentsList;

    AdapterStaff adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime);

        // get references to widgets
        coverImageView = (ImageView) findViewById(R.id.activity_coverImageView);
        titleTextView = (TextView) findViewById(R.id.activity_titleTextView);
        episodesTextView = (TextView) findViewById(R.id.activity_episodesTextview);
        scoreTextView = (TextView) findViewById(R.id.activity_scoreTextView);
        descriptionTextView = (TextView) findViewById(R.id.activity_descriptionTextView);
        staffListView = (RecyclerView) findViewById(R.id.activity_staffRecyclerView);
        statsListView = (ListView) findViewById(R.id.statsListView);
        commentsListView = (ListView) findViewById(R.id.commentsListView);

        MAL_ID = getAnime();
        new GetData().start();
    }

    class GetData extends Thread {
        @Override
        public void run() {
            getStaff(MAL_ID);
            getStats(MAL_ID);
            getComments(MAL_ID);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AnimeActivity.this.updateDisplay();
                }
            });
        }
    }

    public void updateDisplay() {
        displayStaff();
        displayStats();
        displayComments();
    }

    //-----------------------------------------DISPLAY FUNC-------------------------------------
    public void displayComments() {
        if (tempComment == null) {
            Toast toast = Toast.makeText(this, "No comments found", Toast.LENGTH_LONG);
            toast.show();

            return;
        }

        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        for (AnimeComment comment : tempCommentsList) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("title", comment.getTitle());
            map.put("date_posted", comment.getPostedDateFormatted());
            map.put("author", comment.getAuthor());
            map.put("replies", String.valueOf(comment.getReplies()));
            data.add(map);
        }

        int resource = R.layout.listview_comments;
        String[] from = { "title", "date_posted", "author", "replies" };
        int[] to = { R.id.comment_title, R.id.comment_date, R.id.comment_author, R.id.comment_replies };

        // create and set the adapter
        SimpleAdapter adapter = new SimpleAdapter(this, data, resource, from, to);
        commentsListView.setAdapter(adapter);
    }
    public void displayStats() {
        if (tempStats == null) {
            Toast toast = Toast.makeText(this, "No stats found", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        ArrayList<HashMap<String, Integer>> data = new ArrayList<HashMap<String, Integer>>();
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("watching", tempStats.getWatching());
        map.put("completed", tempStats.getCompleted());
        map.put("on_hold", tempStats.getOn_hold());
        map.put("dropped", tempStats.getDropped());
        map.put("plan_to_watch", tempStats.getPlan_to_watch());
        map.put("total", tempStats.getTotal());
        data.add(map);

        int resource = R.layout.listview_stats;
        String[] from = { "watching", "completed", "on_hold", "dropped", "plan_to_watch", "total" };
        int[] to = { R.id.statsWatching, R.id.statsCompleted, R.id.statsOnHold, R.id.statsDropped, R.id.statsPlanToWatch, R.id.statsTotal };

        SimpleAdapter adapter = new SimpleAdapter(this, data, resource, from, to);
        statsListView.setAdapter(adapter);
    }
    public void displayStaff() {
        if (tempStaff == null) {
            Toast toast = Toast.makeText(this, "No staffmembers found", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        ArrayList<String> roles = new ArrayList<String>();
        for (AnimeStaff staff : tempStaffList) {
            roles.add(staff.getStaffRole());
        }
        ArrayList<String> names = new ArrayList<String>();
        for (AnimeStaff staff : tempStaffList) {
            names.add(staff.getStaffName());
        }
        ArrayList<String> imgurls = new ArrayList<String>();
        for (AnimeStaff staff : tempStaffList) {
            imgurls.add(staff.getStaffImg());
        }

        // create and set the adapter
        adapter = new AdapterStaff(AnimeActivity.this, imgurls, roles, names);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(AnimeActivity.this, 1, RecyclerView.HORIZONTAL, false);

        staffListView.setLayoutManager(gridLayoutManager);
        staffListView.setAdapter(adapter);
    }
    //-----------------------------------------END DISPLAY FUNC---------------------------------

    //-----------------------------------------DATA GETTERS-------------------------------------
    public void getComments(int id) {
        String COMMENTURL = BASEURL + String.valueOf(id) + "/forum";

        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(COMMENTURL);

        tempCommentsList = new ArrayList<>();

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray commentResults  = jsonObj.getJSONArray("topics");

                for (int i = 0; i < commentResults.length(); i++) {
                    tempComment = new AnimeComment();
                    JSONObject comment = commentResults.getJSONObject(i);

                    tempComment.setTitle(comment.getString("title"));
                    tempComment.setDate_posted(comment.getString("date_posted"));
                    tempComment.setAuthor(comment.getString("author_name"));
                    tempComment.setReplies(comment.getInt("replies"));

                    tempCommentsList.add(tempComment);
                }
            }
            catch (JSONException e) {
                Log.d("det", e.toString());
            }
        }
    }
    public void getStats(int id) {
        String STATSURL = BASEURL + String.valueOf(id) + "/stats";

        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(STATSURL);

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                tempStats = new AnimeStats();

                tempStats.setWatching(jsonObj.getInt("watching"));
                tempStats.setCompleted(jsonObj.getInt("completed"));
                tempStats.setOn_hold(jsonObj.getInt("on_hold"));
                tempStats.setDropped(jsonObj.getInt("dropped"));
                tempStats.setPlan_to_watch(jsonObj.getInt("plan_to_watch"));
                tempStats.setTotal(jsonObj.getInt("total"));
            }
            catch (JSONException e) {
                Log.d("det", e.toString());
            }
        }
    }
    public void getStaff(int id) {
        String STAFFURL = BASEURL + String.valueOf(id) + "/characters_staff";

        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(STAFFURL);

        tempStaffList = new ArrayList<>();

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray staffResults  = jsonObj.getJSONArray("staff");

                for (int i = 0; i < staffResults.length(); i++) {
                    tempStaff = new AnimeStaff();
                    JSONObject staff = staffResults.getJSONObject(i);

                    tempStaff.setStaffImg(staff.getString("image_url"));
                    tempStaff.setStaffName(staff.getString("name"));
                    tempStaff.setStaffRole(staff.getJSONArray("positions").getString(0));

                    tempStaffList.add(tempStaff);
                }
            }
            catch (JSONException e) {
                Log.d("det", e.toString());
            }
        }
    }
    public int getAnime() {
        // get the intent
        Intent intent = getIntent();

        // get data from the intent
        int mal_id = intent.getIntExtra("mal_id", 0);
        String coverUrl = intent.getStringExtra("image_url");
        String title = intent.getStringExtra("title");
        String episodes = intent.getIntExtra("episodes", 0) + " Episodes";
        double score = intent.getDoubleExtra("score", 0.00);
        String description = intent.getStringExtra("synopsis").replace('\n', ' ');

        // display data on the widgets
        Picasso.get().load(coverUrl).into(coverImageView);
        titleTextView.setText(title);
        episodesTextView.setText(String.valueOf(episodes));
        scoreTextView.setText(String.valueOf(score));
        descriptionTextView.setText(description);

        return mal_id;
    }
    //-----------------------------------------END DATA GETTERS---------------------------------
}
