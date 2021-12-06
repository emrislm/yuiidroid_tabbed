package com.emrislm.yuiidroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterManga extends RecyclerView.Adapter<AdapterManga.ViewHolder> {

    List<String> titles;
    List<String> imgurls;
    LayoutInflater inflater;
    Context context;

    public AdapterManga(Context context, List<String> titles, List<String> imgurls) {
        this.context = context;
        this.titles = titles;
        this.imgurls = imgurls;

        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public AdapterManga.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.listview_manga, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterManga.ViewHolder holder, int position) {
        holder.title.setText(titles.get(position));
        Picasso.get().load(imgurls.get(position)).into(holder.cover);
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView cover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.manga_titleTextView);
            cover = itemView.findViewById(R.id.manga_coverImageView);
        }
    }
}