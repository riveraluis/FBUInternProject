package com.codepath.furnitureapp;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("RecentMessage")
public class RecentMessage extends ParseObject {
    public static final String MESSAGE_KEY = "message";
    public static final String SENT_TO_KEY = "sentTo";

    public String getRecentMessage() { return getString(MESSAGE_KEY); }
    public String getRecentSentTo() { return getString(SENT_TO_KEY); }
    public void setRecentMessage(String message) { put(MESSAGE_KEY, message); }
    public void setRecentSentTo(String sentTo) { put(SENT_TO_KEY, sentTo); }
}
