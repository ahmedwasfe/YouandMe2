package com.ahmet.postphotos.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by ahmet on 2/4/2018.
 */

/* no-op */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> listFragment = new ArrayList<>();
    ArrayList<String> listTitle = new ArrayList<>();

    public SectionsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {

        return listFragment.get(position);
    }

    @Override
    public int getCount() {
        return listTitle.size();
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        return listTitle.get(position);
    }

    public void setFragment(Fragment fragment, String title){
        listFragment.add(fragment);
        listTitle.add(title);
    }
}
