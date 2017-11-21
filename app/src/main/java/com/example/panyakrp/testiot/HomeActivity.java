package com.example.panyakrp.testiot;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.example.panyakrp.testiot.fragment.BlankFragment;
import com.example.panyakrp.testiot.fragment.HomeFragment;
import com.example.panyakrp.testiot.fragment.TimelineFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity {

    Toolbar toolbar;
    Fragment selectFragment;
    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        setSupportActionBar(toolbar);

        toolbar.setBackgroundColor(getResources().getColor(R.color.menu1));

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_home:
                        setFram("1");
                        toolbar.setBackgroundColor(getResources().getColor(R.color.menu1));
                        break;
                    case R.id.tab_timeline:
                        setFram("2");
                        toolbar.setBackgroundColor(getResources().getColor(R.color.menu2));
                        break;
                }
            }
        });

    }


    public void setFram(String page) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        selectFragment = new BlankFragment();
        progressBar.setVisibility(View.VISIBLE);

        switch (page) {
            case "1":
                selectFragment = new HomeFragment();
                break;
            case "2":
                selectFragment = new TimelineFragment();
                break;
        }

        ft.replace(R.id.content, selectFragment);
        ft.commit();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}

