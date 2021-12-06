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

public class AdapterCharacters extends RecyclerView.Adapter<AdapterCharacters.ViewHolder> {

    List<String> imgurls;
    List<String> roles;
    List<String> names;

    LayoutInflater inflater;
    Context context;

    public AdapterCharacters(Context context, List<String> imgurls, List<String> roles, List<String> names) {
        this.context = context;
        this.roles = roles;
        this.imgurls = imgurls;
        this.names = names;

        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public AdapterCharacters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.listview_characters, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCharacters.ViewHolder holder, int position) {
        holder.role.setText(roles.get(position));
        holder.name.setText(names.get(position));
        Picasso.get().load(imgurls.get(position)).into(holder.cover);
    }

    @Override
    public int getItemCount() { return roles.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView role;
        TextView name;
        ImageView cover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            role = itemView.findViewById(R.id.characterRoleTextView);
            name = itemView.findViewById(R.id.characterNameTextView);
            cover = itemView.findViewById(R.id.characterImageImageView);
        }
    }
}
