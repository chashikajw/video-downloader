package com.dreamso.downvideoapp.fragments.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dreamso.downvideoapp.R;

public class BrowserItemAdapter  extends BaseAdapter {

    Context context;
    int logos[];
    LayoutInflater inflter;

    public BrowserItemAdapter(Context applicationContext, int[] logos) {
        this.context = applicationContext;
        this.logos = logos;
        inflter = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return logos.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.browser_item_layout, null); // inflate the layout
        ImageView icon = (ImageView) view.findViewById(R.id.img); // get the reference of ImageView
        icon.setImageResource(logos[i]); // set logo images
        return view;
    }
}
