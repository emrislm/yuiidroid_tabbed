package com.emrislm.yuiidroid;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AnimeSearchFragment extends Fragment implements View.OnClickListener {

    private String URL_STRING = "https://api.jikan.moe/v3/search/anime?q=";
    private Anime tempAnime;
    private ArrayList<Anime> animeList;

    // define variables for the widgets
    private EditText editText_animeInput;
    private RecyclerView listView_animesListView;
    private ImageButton button_search;
    private Adapter adapter;

    private static final String TAG = "AnimeSearchFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView Method");

        View view = inflater.inflate(R.layout.fragment_anime_search, container, false);

        // get references to the widgets
        editText_animeInput = (EditText) view.findViewById(R.id.EditText_animeInput);
        listView_animesListView = (RecyclerView) view.findViewById(R.id.ListView_animesListView);
        button_search = (ImageButton) view.findViewById(R.id.Button_search);

        // set the listeners
        button_search.setOnClickListener(this);
        listView_animesListView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), listView_animesListView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //get anime
                        Anime anime = animeList.get(position);

                        //create intent
                        Intent intent = new Intent(getContext(), AnimeActivity.class);
                        intent.putExtra("mal_id", anime.getMal_id());
                        intent.putExtra("image_url", anime.getImage_url());
                        intent.putExtra("title", anime.getTitle());
                        intent.putExtra("episodes", anime.getEpisodes());
                        intent.putExtra("score", anime.getScore());
                        intent.putExtra("synopsis", anime.getSynopsis());

                        getActivity().startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) { }
                })
        );

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Button_search:
                Log.d("dinges", "KNOP GEDRUKT");
                String inputText = editText_animeInput.getText().toString();
                URL_STRING = URL_STRING + inputText;

                new getAnimesFromSearch().start();
                Log.d("dinges", "getAnimesFromSearch UITGEVOERD");

                inputText = "";
                break;
        }
    }

    public void updateDisplay() {
        if (animeList == null) {
            Log.d("dinges", "ERR: Unable to get results");
            return;
        }

        // create List objects
        ArrayList<String> titles = new ArrayList<String>();
        for (Anime anime : animeList) {
            titles.add(anime.getTitle());
        }
        ArrayList<String> imgurls = new ArrayList<String>();
        for (Anime anime : animeList) {
            imgurls.add(anime.getImage_url());
        }

        // create and set the adapter
        adapter = new Adapter(getActivity(), titles, imgurls);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL, false);

        listView_animesListView.setLayoutManager(gridLayoutManager);
        listView_animesListView.setAdapter(adapter);

        Log.d("dinges", "Feed displayed");
    }


    public void getAnimeResults() {
        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(URL_STRING);

        animeList = new ArrayList<Anime>();

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray results  = jsonObj.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    tempAnime = new Anime();
                    JSONObject result = results.getJSONObject(i);

                    tempAnime.setMal_id(result.getInt("mal_id"));
                    tempAnime.setUrl(result.getString("url"));
                    tempAnime.setImage_url(result.getString("image_url"));
                    tempAnime.setTitle(result.getString("title"));
                    tempAnime.setAiring(result.getBoolean("airing"));
                    tempAnime.setSynopsis(result.getString("synopsis"));
                    tempAnime.setType(result.getString("type"));
                    tempAnime.setEpisodes(result.getInt("episodes"));
                    tempAnime.setScore(result.getDouble("score"));
                    tempAnime.setStart_date(result.getString("start_date"));
                    tempAnime.setEnd_date(result.getString("end_date"));
                    tempAnime.setMembers(result.getInt("members"));
                    tempAnime.setRated(result.getString("rated"));

                    animeList.add(tempAnime);
                }

                Log.d("dinges", "tempanime is gevuld");
            }
            catch (JSONException e) {
                Log.d("dinges", e.toString());
            }
        }
    }

    class getAnimesFromSearch extends Thread {
        @Override
        public void run() {

            //loop the json + fill arraylist with anime
            getAnimeResults();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("dinges", "runOnUiThread");
                    AnimeSearchFragment.this.updateDisplay();
                }
            });
        }
    }
}