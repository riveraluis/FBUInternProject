package com.codepath.furnitureapp;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_PRICE = "price";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_FURNITURE = "furniture";
    public static final String KEY_COMMON_FIELDS = "commonFields";
    public static final String KEY_SCHOOL = "school";
    public static final String KEY_USERS_WHO_LIKED = "usersWhoLiked";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public ParseObject getFurniture() { return getParseObject(KEY_FURNITURE); }

    public void setFurniture(Furniture furniture) { put(KEY_FURNITURE, furniture); }

    public int getPrice() { return getInt(KEY_PRICE); }

    public void setPrice(int price) { put(KEY_PRICE, price); }

    public int getCommonFields() { return getInt(KEY_COMMON_FIELDS); }

    public void setCommonFields(int commonFields) { put(KEY_COMMON_FIELDS, commonFields); }

    public String getSchool() { return(KEY_SCHOOL); }

    public void setSchool(String school) { put(KEY_SCHOOL, school); }

    public List<String> getLikedArray() { return getList(KEY_USERS_WHO_LIKED);}

    public void setLikedArray(List<String> list) { put(KEY_USERS_WHO_LIKED, list); }

}
