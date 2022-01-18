package com.emrislm.yuiidroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private String URL_STRING_ANIME = "https://api.jikan.moe/v3/top/anime/1/bypopularity";
    private AnimeTop tempTopAnime;
    private ArrayList<AnimeTop> topAnimeList;

    // define variables for the widgets
    private RecyclerView listView_TopAnimesListView;
    //private RecyclerView listView_TopMangasListView;
    private AdapterAnime adapterAnime;

    private static final String TAG = "HomeFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // get references to the widgets
        listView_TopAnimesListView = (RecyclerView) view.findViewById(R.id.ListView_topAnimesListView);
        //listView_TopMangasListView = (RecyclerView) view.findViewById(R.id.ListView_topMangasListView);

        new getTopFromSearch().start();
        return view;
    }

    public void updateDisplay() {
        if (topAnimeList == null) {
            Log.d("dinges", "ERR: arr is leeg?");
            return;
        }

        // create List objects
        ArrayList<String> titles = new ArrayList<String>();
        for (AnimeTop anime : topAnimeList) {
            titles.add(anime.getTitle());
        }
        ArrayList<String> imgurls = new ArrayList<String>();
        for (AnimeTop anime : topAnimeList) {
            imgurls.add(anime.getImage_url());
        }

        // create and set the adapter
        adapterAnime = new AdapterAnime(getActivity(), titles, imgurls);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL, false);

        listView_TopAnimesListView.setLayoutManager(gridLayoutManager);
        listView_TopAnimesListView.setAdapter(adapterAnime);

        Log.d("dinges", "Feed displayed");
    }

    public void getTopResults_anime() {
        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(URL_STRING_ANIME);

        topAnimeList = new ArrayList<>();

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray results  = jsonObj.getJSONArray("top");

                for (int i = 0; i < 5; i++) {
                    tempTopAnime = new AnimeTop();
                    JSONObject result = results.getJSONObject(i);

                    tempTopAnime.setMal_id(result.getInt("mal_id"));
                    tempTopAnime.setRank(result.getInt("rank"));
                    tempTopAnime.setTitle(result.getString("title"));
                    tempTopAnime.setUrl(result.getString("url"));
                    tempTopAnime.setImage_url(result.getString("image_url"));
                    tempTopAnime.setType(result.getString("type"));
                    //tempTopAnime.setEpisodes(result.getInt("episodes"));
                    tempTopAnime.setStart_date(result.getString("start_date"));
                    tempTopAnime.setEnd_date(result.getString("end_date"));
                    tempTopAnime.setMembers(result.getInt("members"));
                    tempTopAnime.setScore(result.getDouble("score"));

                    topAnimeList.add(tempTopAnime);
                }

                Log.d("dinges", "tempanime is gevuld");
                // check of alle anime binnen zijn
//                for (AnimeTop a: topAnimeList) {
//                    Log.d("dinges", a.getTitle());
//                }
            }
            catch (JSONException e) {
                Log.d("dinges", e.toString());
            }
        }
    }

    class getTopFromSearch extends Thread {
        @Override
        public void run() {

            //loop the json + fill arraylist with anime
            getTopResults_anime();

            if (isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("dinges", "runOnUiThread");
                        HomeFragment.this.updateDisplay();
                    }
                });
            }
        }
    }
}
