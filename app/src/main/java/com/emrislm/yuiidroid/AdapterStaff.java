package com.emrislm.yuiidroid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class AdapterStaff extends RecyclerView.Adapter<AdapterStaff.ViewHolder> {

    List<String> imgurls;
    List<String> roles;
    List<String> names;

    LayoutInflater inflater;
    Context context;

    public AdapterStaff(Context context, List<String> imgurls, List<String> roles, List<String> names) {
        this.context = context;
        this.roles = roles;
        this.imgurls = imgurls;
        this.names = names;

        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public AdapterStaff.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.listview_staff, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterStaff.ViewHolder holder, int position) {
        holder.role.setText(roles.get(position));
        holder.name.setText(names.get(position));
        Picasso.get().load(imgurls.get(position)).into(holder.cover);
    }

    @Override
    public int getItemCount() {
        return roles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView role;
        TextView name;
        ImageView cover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            role = itemView.findViewById(R.id.staffRoleTextView);
            name = itemView.findViewById(R.id.staffNameTextView);
            cover = itemView.findViewById(R.id.staffImageImageView);
        }
    }
}