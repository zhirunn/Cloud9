// tab layout taken from https://guides.codepath.com/android/google-play-style-tabs-using-tablayout
// (C) 2015 CodePath modified by Cloud 9

package com.example.yunita.tradiogc.record;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class RecordFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Current", "Completed", "Past" };
    private Context context;

    public RecordFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    /**
     * Gets the number of the page.
     *
     * @return Integer
     */
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    /**
     * Gets the next tab.
     *
     * @param position tab index
     * @return RecordPageFragment
     */
    @Override
    public Fragment getItem(int position) {
        return RecordPageFragment.newInstance(position + 1);
    }

    /**
     * Gets the title of the current tab.
     *
     * @param position tab index
     * @return CharSequence
     */
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
