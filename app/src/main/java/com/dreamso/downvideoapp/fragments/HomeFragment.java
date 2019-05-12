package com.dreamso.downvideoapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dreamso.downvideoapp.R;
import com.dreamso.downvideoapp.fragments.adapters.TabAdapter;

public class HomeFragment extends Fragment {

    private TabLayout tabLayout = null;
    private ViewPager viewPager = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View RootView = inflater.inflate(R.layout.fragment_home, container, false);


        tabLayout = (TabLayout) RootView.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) RootView.findViewById(R.id.viewPagere);

        //setting the tabs title
        tabLayout.addTab(tabLayout.newTab().setText("For You"));
        tabLayout.addTab(tabLayout.newTab().setText("Trending"));
        tabLayout.addTab(tabLayout.newTab().setText("Channels"));

        //setup the view pager
        final TabAdapter adapter =  new TabAdapter(getFragmentManager() ,tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return RootView;
    }
}
