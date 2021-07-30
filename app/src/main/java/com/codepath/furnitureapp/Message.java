package com.codepath.furnitureapp;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String USER_ID_KEY = "userId";
    public static final String BODY_KEY = "body";
    public static final String SENT_TO_KEY = "sentTo";

    public String getUserId() {
        return getString(USER_ID_KEY);
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public String getSentTo() { return getString(SENT_TO_KEY); }

    public void setUserId(String userId) {
        put(USER_ID_KEY, userId);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }

    public void setSentTo(String sentTo) { put(SENT_TO_KEY, sentTo); }
}
