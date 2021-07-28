package com.codepath.furnitureapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Furniture.class);
        ParseObject.registerSubclass(Message.class);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        // any network interceptors must be added with the Configuration Builder given this syntax
        builder.networkInterceptors().add(httpLoggingInterceptor);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("tBpIRlSHCbWHQO45REv0XSErNCD0f4blrKrGU6A0")
                .clientKey("KyGYEjnPebySkFxWAuIVjEickxqP90sWHfLVzRN1")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
