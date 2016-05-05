package com.example.moizqureshi.pushtest;

import com.parse.Parse;

/**
 * Created by moizqureshi on 5/4/16.
 */
public class myApplication extends android.app.Application {
    @Override
    public void onCreate(){
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("moizqureshipushtest")
                .server("http://pushtest126.herokuapp.com/parse/")
                .clientKey("qureshi1990")
                .build()
        );
    }
}
