package com.verlif.idea.singledown.module.main;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.verlif.idea.singledown.R;
import com.verlif.idea.singledown.manager.ServerInfoManager;
import com.verlif.idea.singledown.manager.ThumbnailManager;
import com.verlif.idea.singledown.model.FileInfo;
import com.verlif.idea.singledown.model.map.FileInDraw;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainFileManager {

    private Activity activity;
    private RecyclerView recyclerView;
    private MapAdapter mapAdapter;

    public MainFileManager(Activity activity, int mapRecyclerId) {
        this.activity = activity;
        recyclerView = activity.findViewById(mapRecyclerId);
    }

    public void init() {
        init(new ArrayList<>(), null);
    }

    public void init(List<FileInfo> fileInfoList, OnClicked onClicked) {
        mapAdapter = new MapAdapter(fileInfoList, onClicked);
        recyclerView.setAdapter(mapAdapter);
        if (recyclerView.getItemDecorationCount() > 0) {
            recyclerView.removeItemDecorationAt(0);
        }
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, 12, false));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                4,
                StaggeredGridLayoutManager.VERTICAL));
    }

    public void refreshMap(List<FileInfo> fileInfoList) {
        recyclerView.setAdapter(new MapAdapter(fileInfoList, null));
    }

    private class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {

        private List<FileInfo> fileInfoList;
        private OnClicked onClicked;

        private ThumbnailManager thumbnailManager;

        public MapAdapter(List<FileInfo> fileInfoList, OnClicked onClicked) {
            this.fileInfoList = fileInfoList;
            if (onClicked != null) {
                this.onClicked = onClicked;
            }
            thumbnailManager = ThumbnailManager.newInstance(
                    ServerInfoManager.newInstance(activity).getRootUrl()
            );
        }

        @NonNull
        @Override
        public MapAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_mapblock_default, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MapAdapter.ViewHolder holder, int position) {
            FileInfo fileInfo = fileInfoList.get(position);
            FileInDraw fileInDraw = new FileInDraw(fileInfo);
            if (fileInDraw.getForegroundResId() != 0) {
                holder.foreground.setImageResource(fileInDraw.getForegroundResId());
            } else {
                holder.foreground.setBackground(null);
            }
            if (fileInDraw.getType() != null) {
                holder.type.setVisibility(View.VISIBLE);
                holder.type.setText(fileInDraw.getType());
            } else holder.type.setVisibility(View.INVISIBLE);
            if (ThumbnailManager.isPicture(fileInfo.getFileName())) {
                File file = thumbnailManager.getThumbnail(fileInfo);
                if (file != null) {
                    holder.foreground.setImageURI(Uri.fromFile(file));
                }
            }
            holder.MapName.setText(fileInfo.getFileName());
            holder.itemView.setOnClickListener(v -> {
                onClicked.onClick(fileInDraw);
            });
            holder.itemView.setOnLongClickListener(v -> {
                onClicked.onLongTouch(fileInDraw);
                return false;
            });
        }

        @Override
        public int getItemCount() {
            return fileInfoList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView foreground;
            private TextView type;
            private TextView MapName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                foreground = itemView.findViewById(R.id.view_mapBlock_foreground);
                type = itemView.findViewById(R.id.view_mapBlock_type);
                MapName = itemView.findViewById(R.id.view_mapBlock_mapName);
            }
        }
    }

    interface OnClicked {
        void onClick(FileInDraw fileInDraw);

        void onLongTouch(FileInDraw fileInDraw);
    }
}
