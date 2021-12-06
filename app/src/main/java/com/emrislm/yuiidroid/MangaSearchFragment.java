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

public class MangaSearchFragment extends Fragment implements View.OnClickListener {

    private String URL_STRING = "https://api.jikan.moe/v3/search/manga?q=";
    private Manga tempManga;
    private ArrayList<Manga> mangaList;

    // define variables for the widgets
    private EditText editText_mangaInput;
    private RecyclerView listView_mangasListView;
    private ImageButton button_search;
    private AdapterManga adapterManga;

    private static final String TAG = "MangaSearchFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView Method");

        View view = inflater.inflate(R.layout.fragment_manga_search, container, false);

        // get references to the widgets
        editText_mangaInput = (EditText) view.findViewById(R.id.EditText_mangaInput);
        listView_mangasListView = (RecyclerView) view.findViewById(R.id.ListView_mangasListView);
        button_search = (ImageButton) view.findViewById(R.id.Button_search_manga);

        // set the listeners
        button_search.setOnClickListener(this);
        listView_mangasListView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), listView_mangasListView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //get manga
                        Manga manga = mangaList.get(position);

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

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Button_search_manga:
                Log.d("dinges", "KNOP GEDRUKT");
                String inputText = editText_mangaInput.getText().toString();
                URL_STRING = URL_STRING + inputText;

                new MangaSearchFragment.getMangasFromSearch().start();
                Log.d("dinges", "getMangasFromSearch UITGEVOERD");

                inputText = "";
                break;
        }
    }

    public void updateDisplay() {
        if (mangaList == null) {
            Log.d("dinges", "ERR: Unable to get results");
            return;
        }

        // create List objects
        ArrayList<String> titles = new ArrayList<String>();
        for (Manga manga : mangaList) {
            titles.add(manga.getTitle());
        }
        ArrayList<String> imgurls = new ArrayList<String>();
        for (Manga manga : mangaList) {
            imgurls.add(manga.getImage_url());
        }

        // create and set the adapter
        adapterManga = new AdapterManga(getActivity(), titles, imgurls);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL, false);

        listView_mangasListView.setLayoutManager(gridLayoutManager);
        listView_mangasListView.setAdapter(adapterManga);

        Log.d("dinges", "Feed displayed");
    }

    public void getMangaResults() {
        HttpHandler sh = new HttpHandler();
        String jsonStr = sh.makeServiceCall(URL_STRING);

        mangaList = new ArrayList<Manga>();

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray results  = jsonObj.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    tempManga = new Manga();
                    JSONObject result = results.getJSONObject(i);

                    tempManga.setMal_id(result.getInt("mal_id"));
                    tempManga.setUrl(result.getString("url"));
                    tempManga.setImage_url(result.getString("image_url"));
                    tempManga.setTitle(result.getString("title"));
                    tempManga.setPublishing(result.getBoolean("publishing"));
                    tempManga.setSynopsis(result.getString("synopsis"));
                    tempManga.setType(result.getString("type"));
                    tempManga.setChapters(result.getInt("chapters"));
                    tempManga.setVolumes(result.getInt("volumes"));
                    tempManga.setScore(result.getDouble("score"));
                    tempManga.setStart_date(result.getString("start_date"));
                    tempManga.setEnd_date(result.getString("end_date"));
                    tempManga.setMembers(result.getInt("members"));

                    mangaList.add(tempManga);
                }
            }
            catch (JSONException e) {
                Log.d("dinges", e.toString());
            }
        }
    }

    class getMangasFromSearch extends Thread {
        @Override
        public void run() {

            //loop the json + fill arraylist with manga
            getMangaResults();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("dinges", "runOnUiThread");
                    MangaSearchFragment.this.updateDisplay();
                }
            });
        }
    }
}