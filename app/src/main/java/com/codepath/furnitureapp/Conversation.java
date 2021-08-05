package com.codepath.furnitureapp;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Conversation")
public class Conversation extends ParseObject {
    public static final String MESSAGE_KEY = "recentMessage";
    public static final String KEY_USERONE = "user1";
    public static final String KEY_USERTWO = "user2";


    public String getRecentMessage() { return getString(MESSAGE_KEY); }
    public String getUserOne() { return getString(KEY_USERONE); }
    public String getUserTwo() { return getString(KEY_USERTWO); }

    public void setRecentMessage(String message) { put(MESSAGE_KEY, message); }
    public void setUserOne(String userOne) { put(KEY_USERONE, userOne); }
    public void setUserTwo(String userTwo) { put(KEY_USERTWO, userTwo); }

}
