package com.dreamso.downvideoapp.fragments.tabFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dreamso.downvideoapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForYouTabFragment extends Fragment {


    public ForYouTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragmenttab_foryou, container, false);


        return rootView;
    }

}
