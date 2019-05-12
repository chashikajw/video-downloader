package com.dreamso.downvideoapp.fragments.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamso.downvideoapp.R;
import com.dreamso.downvideoapp.fragments.models.YoutubeDataModel;

import java.util.ArrayList;

public class VideoPostAdapter  extends RecyclerView.Adapter<VideoPostAdapter.YoutubePostHolder> {

    private ArrayList<YoutubeDataModel> dataSet;
    private Context mContext = null;

    public VideoPostAdapter(Context mContext, ArrayList<YoutubeDataModel> dataSet){
        this.dataSet = dataSet;
        this.mContext = mContext;
    }
    @Override
    public YoutubePostHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_post_layout,parent,false);
        YoutubePostHolder postHolder = new YoutubePostHolder(view);
        return postHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull YoutubePostHolder holder, int position) {

        //set the views here
        TextView textViewTitle = holder.textViewTitle;
        TextView textViewDes = holder.textViewDes;
        TextView textViewDate = holder.textViewDate;
        ImageView ImageThumb = holder.ImageThumb;

        YoutubeDataModel object = dataSet.get(position);

        textViewTitle.setText(object.getTitle());
        textViewDes.setText(object.getDescription());
        textViewDate.setText(object.getPublishedAt());
        //holder.bind(dataSet.get(position), listener);

        //TODO: image will be downloaded from url
        //Picasso.with(mContext).load(object.getThumbnail()).into(ImageThumb);

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public static class YoutubePostHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        TextView textViewDes;
        TextView textViewDate ;
        ImageView ImageThumb;

        public YoutubePostHolder(View itemView) {
            super(itemView);
            this.textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            this.textViewDes = (TextView) itemView.findViewById(R.id.textViewDes);
            this.textViewDate = (TextView) itemView.findViewById(R.id.textViewDate);
            this.ImageThumb = (ImageView) itemView.findViewById(R.id.ImageThumb);

        }
    }
}
