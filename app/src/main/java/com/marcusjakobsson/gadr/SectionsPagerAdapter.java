package com.marcusjakobsson.gadr;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlbratt on 2017-10-30.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
    }

    @Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }


    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }


//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        Fragment fragment = (Fragment) super.instantiateItem(container, position);
//        fragmentList.add(position, fragment);
//        return fragment;
//    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        fragmentList.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return fragmentList.get(position);
    }

//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        //return super.instantiateItem(container, position);
//        Fragment fragment = (Fragment) super.instantiateItem(container, position);
//        fragmentList.add(position,fragment);
//        fragmentTitleList.add(position,fragmentTitleList.get(position));
//        return fragment;
//    }
}
