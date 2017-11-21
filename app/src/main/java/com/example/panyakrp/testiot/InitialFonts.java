package com.example.panyakrp.testiot;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class InitialFonts extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/test.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }
}
