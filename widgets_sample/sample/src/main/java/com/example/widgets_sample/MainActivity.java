package com.example.widgets_sample;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.github.sounder.widgets.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TabLayout mTabLayout;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MyFragment());
        fragments.add(new MyFragment());
        fragments.add(new MyFragment());
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(myPagerAdapter);

        mTabLayout.setOnTabChangeListener(new TabLayout.OnTabChangeListener() {
            @Override
            public void onTabSelect(TextView view, int position) {
                view.getPaint().setFakeBoldText(true);
            }

            @Override
            public void onTabUnSelect(TextView view, int position) {
                view.getPaint().setFakeBoldText(false);
            }
        });

        mTabLayout.setupWithViewPager(mViewPager);


        mTabLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mViewPager.setCurrentItem(1);
                mTabLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    static class MyPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> mFragmentList;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragmentList = fragments;
        }

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mFragmentList.get(i);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            String s = "";
            for (int i = 0; i < position; i++) {
                s += "000";
            }
            return mFragmentList.get(position).getClass().getSimpleName() + s;
        }
    }
}
