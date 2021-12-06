package com.emrislm.yuiidroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MangaActivity extends AppCompatActivity {

    String BASEURL = "https://api.jikan.moe/v3/anime/";

    ImageView coverImageView;
    TextView titleTextView;
    TextView chaptersTextView;
    TextView volumesTextView;
    TextView scoreTextView;
    TextView descriptionTextView;

    int MAL_ID;

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

        MAL_ID = getManga();
        new GetMangaData().start();
    }

    class GetMangaData extends Thread {
        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MangaActivity.this.updateDisplay();
                }
            });
        }
    }

    public void updateDisplay() {

    }

    //-----------------------------------------DISPLAY FUNC-------------------------------------

    //-----------------------------------------END DISPLAY FUNC---------------------------------

    //-----------------------------------------DATA GETTERS-------------------------------------
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