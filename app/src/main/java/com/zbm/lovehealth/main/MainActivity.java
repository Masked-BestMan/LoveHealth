package com.zbm.lovehealth.main;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.zbm.lovehealth.BaseActivity;
import com.zbm.lovehealth.R;
import com.zbm.lovehealth.ThemeMessageEvent;
import com.zbm.lovehealth.UserFragment;
import com.zbm.lovehealth.category.HomeFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private MenuItem menuItem;
    private ColorStateList dayColorStateList,nightColorStateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        int[][] state={{android.R.attr.state_checked},{-android.R.attr.state_checked}};
        int[] dayColor={getResources().getColor(R.color.theme_color_deep_day),getResources().getColor(R.color.theme_color_simple_day)};
        int[] nightColor={getResources().getColor(R.color.theme_color_simple_day),getResources().getColor(R.color.theme_color_simple_night)};
        dayColorStateList=new ColorStateList(state,dayColor);
        nightColorStateList=new ColorStateList(state,nightColor);
        if (!getSharedPreferences("Love Health", MODE_PRIVATE).getBoolean("in_night_mode", false)) {
            bottomNavigationView.setItemTextColor(dayColorStateList);
            bottomNavigationView.setItemIconTintList(dayColorStateList);
            bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.text_full_white));
        } else {
            bottomNavigationView.setItemTextColor(nightColorStateList);
            bottomNavigationView.setItemIconTintList(nightColorStateList);
            bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.theme_color_deep_night));
        }
        List<Fragment> list = new ArrayList<>();
        list.add(new HomeFragment());
        list.add(new UserFragment());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_nav_home:
                        viewPager.setCurrentItem(0, true);
                        break;
                    case R.id.bottom_nav_user:
                        viewPager.setCurrentItem(1, true);
                        break;
                }
                return false;
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onThemeMessageEvent(ThemeMessageEvent event) {
        if (event.isNight()) {
            bottomNavigationView.setItemIconTintList(nightColorStateList);
            bottomNavigationView.setItemTextColor(nightColorStateList);
            bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.theme_color_deep_night));
        }else {
            bottomNavigationView.setItemIconTintList(dayColorStateList);
            bottomNavigationView.setItemTextColor(dayColorStateList);
            bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.text_full_white));
        }
    }
}
