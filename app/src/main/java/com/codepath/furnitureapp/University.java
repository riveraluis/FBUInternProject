package com.codepath.furnitureapp;

import android.graphics.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class University {

    private String name;
    private String country;

    public University(JSONObject jsonObject) throws JSONException {
        this.name = jsonObject.getString("name");
        this.country = jsonObject.getString("country");
    }

    public String getName() { return name; }

    public String getCountry() { return country; }

    @Override
    public String toString() {
        return name;
    }

    public static List<University> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<University> universitiesList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject e = jsonArray.getJSONObject(i);
            universitiesList.add(new University(e));
        }
        return universitiesList;
    }
}
