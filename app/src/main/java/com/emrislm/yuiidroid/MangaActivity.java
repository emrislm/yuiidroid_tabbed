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

public class MangaActivity extends AppCompatActivity {

    String BASEURL = "https://api.jikan.moe/v3/manga/";

    ImageView coverImageView;
    TextView titleTextView;
    TextView chaptersTextView;
    TextView volumesTextView;
    TextView scoreTextView;
    TextView descriptionTextView;
    RecyclerView charactersListView;
    ListView statsListView;

    int MAL_ID;

    MangaCharacters tempCharacters;
    ArrayList<MangaCharacters> tempCharactersList;

    MangaStats tempStats;

    AdapterCharacters adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga);

        // get references to widgets
        coverImageView = (ImageView) findViewById(R.id.manga_activity_coverImageView);
        titleTextView = (TextView) findViewById(R.id.manga_activity_titleTextView);
        chaptersTextView = (TextView) findViewById(R.id.manga_activity_chaptersTextview);
        volumesTextView = (TextView) findViewById(R.id.manga_activity_volumesTextview);
        scoreTextView = (TextView) findViewById(R.id.manga_activity_scoreTextView);
        descriptionTextView = (TextView) findViewById(R.id.manga_activity_descriptionTextView);
        charactersListView = (RecyclerView) findViewById(R.id.manga_activity_charactersRecyclerView);
        statsListView = (ListView) findViewById(R.id.manga_statsListView);

        MAL_ID = getManga();
        new GetMangaData().start();
    }

    class GetMangaData extends Thread {
        @Override
        public void run() {
            getCharacters(MAL_ID);
            getStats(MAL_ID);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MangaActivity.this.updateDisplay();
                }
            });
        }
    }

    public void updateDisplay() {
        displayCharacters();
        displayStats();
    }

    //-----------------------------------------DISPLAY FUNC-------------------------------------
    public void displayStats() {
        if (tempStats == null) {
            Toast toast = Toast.makeText(this, "No stats found", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        ArrayList<HashMap<String, Integer>> data = new ArrayList<HashMap<String, Integer>>();
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("reading", tempStats.getReading());
        map.put("completed", tempStats.getCompleted());
        map.put("on_hold", tempStats.getOn_hold());
        map.put("dropped", tempStats.getDropped());
        map.put("plan_to_read", tempStats.getPlan_to_read());
        map.put("total", tempStats.getTotal());
        data.add(map);

        int resource = R.layout.listview_stats_manga;
        String[] from = { "reading", "completed", "on_hold", "dropped", "plan_to_read", "total" };
        int[] to = { R.id.manga_statsReading, R.id.manga_statsCompleted, R.id.manga_statsOnHold, R.id.manga_statsDropped, R.id.manga_statsPlanToRead, R.id.manga_statsTotal };

        SimpleAdapter adapter = new SimpleAdapter(this, data, resource, from, to);
        statsListView.setAdapter(adapter);
    }
    public void displayCharacters() {
        if (tempCharacters == null) {
            Toast toast = Toast.makeText(this, "No characters found", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        ArrayList<String> roles = new ArrayList<String>();
        for (MangaCharacters characters : tempCharactersList) {
            roles.add(characters.getCharacterRole());
        }
        ArrayList<String> names = new ArrayList<String>();
        for (MangaCharacters characters : tempCharactersList) {
            names.add(characters.getCharacterName());
        }
        ArrayList<String> imgurls = new ArrayList<String>();
        for (MangaCharacters characters : tempCharactersList) {
            imgurls.add(characters.getCharacterImg());
        }

        // create and set the adapter
        adapter = new AdapterCharacters(MangaActivity.this, imgurls, roles, names);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MangaActivity.this, 1, RecyclerView.HORIZONTAL, false);

        charactersListView.setLayoutManager(gridLayoutManager);
        charactersListView.setAdapter(adapter);
    }
    //-----------------------------------------END DISPLAY FUNC---------------------------------

    //-----------------------------------------DATA GETTERS-------------------------------------
    public void getStats(int id) {
        String STATSURL = BASEURL + String.valueOf(id) + "/stats";

        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(STATSURL);

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                tempStats = new MangaStats();

                tempStats.setReading(jsonObj.getInt("reading"));
                tempStats.setCompleted(jsonObj.getInt("completed"));
                tempStats.setOn_hold(jsonObj.getInt("on_hold"));
                tempStats.setDropped(jsonObj.getInt("dropped"));
                tempStats.setPlan_to_read(jsonObj.getInt("plan_to_read"));
                tempStats.setTotal(jsonObj.getInt("total"));
            }
            catch (JSONException e) {
                Log.d("det", e.toString());
            }
        }
    }
    public void getCharacters(int id) {
        String CHARACTERSURL = BASEURL + String.valueOf(id) + "/characters";

        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(CHARACTERSURL);

        tempCharactersList = new ArrayList<>();

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray charactersResults  = jsonObj.getJSONArray("characters");

                for (int i = 0; i < charactersResults.length(); i++) {
                    tempCharacters = new MangaCharacters();
                    JSONObject character = charactersResults.getJSONObject(i);

                    tempCharacters.setCharacterImg(character.getString("image_url"));
                    tempCharacters.setCharacterName(character.getString("name"));
                    tempCharacters.setCharacterRole(character.getString("role"));

                    tempCharactersList.add(tempCharacters);
                }
            }
            catch (JSONException e) {
                Log.d("det", e.toString());
            }
        }
    }
    public int getManga() {
        // get the intent
        Intent intent = getIntent();

        // get data from the intent
        int mal_id = intent.getIntExtra("mal_id", 0);
        String coverUrl = intent.getStringExtra("image_url");
        String title = intent.getStringExtra("title");
        String chapters = intent.getIntExtra("chapters", 0) + " Chapters";
        String volumes = intent.getIntExtra("volumes", 0) + " Volumes";
        double score = intent.getDoubleExtra("score", 0.00);
        String description = intent.getStringExtra("synopsis").replace('\n', ' ');

        // display data on the widgets
        Picasso.get().load(coverUrl).into(coverImageView);
        titleTextView.setText(title);
        chaptersTextView.setText(String.valueOf(chapters));
        volumesTextView.setText(String.valueOf(volumes));
        scoreTextView.setText(String.valueOf(score));
        descriptionTextView.setText(description);

        return mal_id;
    }
    //-----------------------------------------END DATA GETTERS---------------------------------
}
