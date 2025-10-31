package com.mrbackend.url;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UrlAdapter extends RecyclerView.Adapter<UrlAdapter.ViewHolder> {

    public interface OnEditClickListener {
        void onEditClick(int position);
    }

    private Context context;
    private ArrayList<UrlModel> urlList;
    private OnEditClickListener editClickListener;

    public UrlAdapter(Context context, ArrayList<UrlModel> urlList, OnEditClickListener listener) {
        this.context = context;
        this.urlList = urlList;
        this.editClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_url, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UrlModel model = urlList.get(position);
        holder.textViewUrl.setText(model.getUrl());

        holder.textViewUrl.setOnClickListener(v -> {
            String link = model.getUrl().trim();
            if (!link.startsWith("http://") && !link.startsWith("https://")) {
                link = "http://" + link;
            }

            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                context.startActivity(browserIntent);
            } catch (Exception e) {
                Toast.makeText(context, "آدرس معتبر نیست!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.buttonEdit.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUrl;
        Button buttonEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUrl = itemView.findViewById(R.id.textViewUrl);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
        }
    }
}
