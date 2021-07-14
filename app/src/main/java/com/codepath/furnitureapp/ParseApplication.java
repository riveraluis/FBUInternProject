package com.codepath.furnitureapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("tBpIRlSHCbWHQO45REv0XSErNCD0f4blrKrGU6A0")
                .clientKey("KyGYEjnPebySkFxWAuIVjEickxqP90sWHfLVzRN1")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
