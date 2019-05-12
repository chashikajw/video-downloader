package com.dreamso.downvideoapp.fragments.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dreamso.downvideoapp.fragments.tabFragments.ChannelTabFragment;
import com.dreamso.downvideoapp.fragments.tabFragments.ForYouTabFragment;
import com.dreamso.downvideoapp.fragments.tabFragments.TrendingTabFragment;

public class TabAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public TabAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ForYouTabFragment tab1 = new ForYouTabFragment();
                return tab1;
            case 1:
                TrendingTabFragment tab2 = new TrendingTabFragment();
                return tab2;
            case 2:
                ChannelTabFragment tab3 = new ChannelTabFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
