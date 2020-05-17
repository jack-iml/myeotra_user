package com.myeotra.user.ui.activity.your_trips;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.myeotra.user.R;
import com.myeotra.user.base.BaseActivity;
import com.myeotra.user.ui.fragment.past_trip.PastTripFragment;
import com.myeotra.user.ui.fragment.upcoming_trip.UpcomingTripFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class YourTripActivity extends BaseActivity {

    /*@BindView(R.id.toolbar)
    Toolbar toolbar;*/
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.container_main)
    ViewPager container;

    TabPagerAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_your_trip;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Activity title will be updated after the locale has changed in Runtime
        setTitle(getString(R.string.your_trips));


        tabs.addTab(tabs.newTab().setText(getString(R.string.past)));
        tabs.addTab(tabs.newTab().setText(getString(R.string.upcoming)));

        adapter = new TabPagerAdapter(getSupportFragmentManager(), tabs.getTabCount());
        container.setAdapter(adapter);
        container.canScrollHorizontally(0);
        container.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                container.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @OnClick({R.id.menu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu:
                onBackPressed();
                break;
        }
    }

    public class TabPagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        TabPagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new PastTripFragment();
                case 1:
                    return new UpcomingTripFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}