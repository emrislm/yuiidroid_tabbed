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

    private String URL_STRING_MANGA = "https://api.jikan.moe/v3/top/manga/1/bypopularity";
    private MangaTop tempTopManga;
    private ArrayList<MangaTop> topMangaList;

    // define variables for the widgets
    private RecyclerView listView_TopAnimesListView;
    private AdapterAnime adapterAnime;

    private RecyclerView listView_TopMangasListView;
    private AdapterManga adapterManga;

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
        listView_TopMangasListView = (RecyclerView) view.findViewById(R.id.ListView_topMangasListView);

        // set the listeners
        listView_TopAnimesListView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), listView_TopAnimesListView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //get anime
                        AnimeTop topanime = topAnimeList.get(position);
                        Anime anime = new Anime();

                        // convert AnimeTop to Anime
                        anime.setMal_id(topanime.getMal_id());
                        anime.setImage_url(topanime.getImage_url());
                        anime.setTitle(topanime.getTitle());
                        anime.setEpisodes(0);
                        anime.setScore(topanime.getScore());
                        anime.setSynopsis("/");

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
        listView_TopMangasListView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), listView_TopMangasListView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //get manga
                        MangaTop topManga = topMangaList.get(position);
                        Manga manga = new Manga();

                        // convert MangaTop to Manga
                        manga.setMal_id(topManga.getMal_id());
                        manga.setImage_url(topManga.getImage_url());
                        manga.setTitle(topManga.getTitle());
                        manga.setChapters(0);
                        manga.setVolumes(0);
                        manga.setScore(topManga.getScore());
                        manga.setSynopsis("/");

                        //create intent
                        Intent intent = new Intent(getContext(), MangaActivity.class);
                        intent.putExtra("mal_id", manga.getMal_id());
                        intent.putExtra("image_url", manga.getImage_url());
                        intent.putExtra("title", manga.getTitle());
                        intent.putExtra("chapters", manga.getChapters());
                        intent.putExtra("volumes", manga.getVolumes());
                        intent.putExtra("score", manga.getScore());
                        intent.putExtra("synopsis", manga.getSynopsis());

                        getActivity().startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) { }
                })
        );

        new getTopFromSearch().start();
        return view;
    }

    public void updateDisplay_ANIME() {
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, RecyclerView.VERTICAL, false);

        listView_TopAnimesListView.setLayoutManager(gridLayoutManager);
        listView_TopAnimesListView.setAdapter(adapterAnime);

        Log.d("dinges", "Feed displayed");
    }

    public void updateDisplay_MANGA() {
        if (topMangaList == null) {
            Log.d("dinges", "ERR: arr is leeg?");
            return;
        }

        // create List objects
        ArrayList<String> titles = new ArrayList<String>();
        for (MangaTop manga : topMangaList) {
            titles.add(manga.getTitle());
        }
        ArrayList<String> imgurls = new ArrayList<String>();
        for (MangaTop manga : topMangaList) {
            imgurls.add(manga.getImage_url());
        }

        // create and set the adapter
        adapterManga = new AdapterManga(getActivity(), titles, imgurls);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, RecyclerView.VERTICAL, false);

        listView_TopMangasListView.setLayoutManager(gridLayoutManager);
        listView_TopMangasListView.setAdapter(adapterManga);

        Log.d("dinges", "Feed displayed");
    }

    public void getTopResults_ANIME() {
        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(URL_STRING_ANIME);

        topAnimeList = new ArrayList<>();

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray results  = jsonObj.getJSONArray("top");

                for (int i = 0; i < 9; i++) {
                    tempTopAnime = new AnimeTop();
                    JSONObject result = results.getJSONObject(i);

                    tempTopAnime.setMal_id(result.getInt("mal_id"));
                    tempTopAnime.setRank(result.getInt("rank"));
                    tempTopAnime.setTitle(result.getString("title"));
                    tempTopAnime.setUrl(result.getString("url"));
                    tempTopAnime.setImage_url(result.getString("image_url"));
                    tempTopAnime.setType(result.getString("type"));
                    tempTopAnime.setStart_date(result.getString("start_date"));
                    tempTopAnime.setEnd_date(result.getString("end_date"));
                    tempTopAnime.setMembers(result.getInt("members"));
                    tempTopAnime.setScore(result.getDouble("score"));

                    topAnimeList.add(tempTopAnime);
                }

                Log.d("dinges", "tempanime is gevuld");
            }
            catch (JSONException e) {
                Log.d("dinges", e.toString());
            }
        }
    }

    public void getTopResults_MANGA() {
        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(URL_STRING_MANGA);

        topMangaList = new ArrayList<>();

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray results  = jsonObj.getJSONArray("top");

                for (int i = 0; i < 9; i++) {
                    tempTopManga = new MangaTop();
                    JSONObject result = results.getJSONObject(i);

                    tempTopManga.setMal_id(result.getInt("mal_id"));
                    tempTopManga.setRank(result.getInt("rank"));
                    tempTopManga.setTitle(result.getString("title"));
                    tempTopManga.setUrl(result.getString("url"));
                    tempTopManga.setImage_url(result.getString("image_url"));
                    tempTopManga.setType(result.getString("type"));
                    tempTopManga.setStart_date(result.getString("start_date"));
                    tempTopManga.setEnd_date(result.getString("end_date"));
                    tempTopManga.setMembers(result.getInt("members"));
                    tempTopManga.setScore(result.getDouble("score"));

                    topMangaList.add(tempTopManga);
                }

                Log.d("dinges", "tempmanga is gevuld");
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
            getTopResults_ANIME();
            getTopResults_MANGA();

            if (isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("dinges", "runOnUiThread");
                        HomeFragment.this.updateDisplay_ANIME();
                        HomeFragment.this.updateDisplay_MANGA();
                    }
                });
            }
        }
    }
}
