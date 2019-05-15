package com.dreamso.downvideoapp.fragments.tabFragments;


import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.dreamso.downvideoapp.R;
import com.dreamso.downvideoapp.activities.facebook.FacebookWebActivity;
import com.dreamso.downvideoapp.fragments.adapters.BrowserItemAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForYouTabFragment extends Fragment {


    private GridView mList_browseItems = null;
    private BrowserItemAdapter adapter = null;
    int logos[] = {R.drawable.icons_youtube_button, R.drawable.icons_facebook};
    public ForYouTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragmenttab_foryou, container, false);
        mList_browseItems = (GridView) rootView.findViewById(R.id.simpleGridView);
        adapter = new BrowserItemAdapter(getContext(), logos);
        mList_browseItems.setAdapter(adapter);

        // implement setOnItemClickListener event on GridView
        mList_browseItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // set an Intent to Another Activity
               // Intent intent = new Intent(getContext(), FacebookWebActivity.class);
                //intent.putExtra("image", logos[position]); // put image data in Intent
                //startActivity(intent); // start Intent
                boolean isFacebookInstalled = true;
                try{
                    ApplicationInfo info = getActivity().getPackageManager().getApplicationInfo("com.facebook.katana", 0 );
                    isFacebookInstalled = true;
                }
                catch( PackageManager.NameNotFoundException e ){
                    isFacebookInstalled=false;
                }

                if(isFacebookInstalled)
                {
                    //start the facebook app
                    //Intent intent = new Intent("android.intent.category.LAUNCHER");
                    //intent.setClassName("com.facebook.katana", "com.facebook.katana.LoginActivity");
                    //startActivity(intent);

                    Intent facebook = new Intent(getContext(), FacebookWebActivity.class);
                    startActivity(facebook);
                }
                else
                {
                    Intent facebook = new Intent(getContext(), FacebookWebActivity.class);
                    startActivity(facebook);
                }
            }
        });


        return rootView;
    }

}
