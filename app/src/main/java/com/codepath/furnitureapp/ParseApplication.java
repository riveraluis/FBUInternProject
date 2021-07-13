package com.codepath.furnitureapp;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("tBpIRlSHCbWHQO45REv0XSErNCD0f4blrKrGU6A0")
                .clientKey("KyGYEjnPebySkFxWAuIVjEickxqP90sWHfLVzRN1")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
