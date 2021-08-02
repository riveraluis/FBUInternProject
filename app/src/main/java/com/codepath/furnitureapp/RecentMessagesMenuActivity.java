package com.codepath.furnitureapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class RecentMessagesMenuActivity extends AppCompatActivity {

    private RecyclerView rvRecentMessages;
    private List<RecentMessage> messagePreviews;
    private RecentMessagesMenuAdapter adapter;
    public static final String TAG = "RecentMessagesMenuActivity";
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String KEY_RECENT_MESSAGES = "recentMessages";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_messages);

        rvRecentMessages = findViewById(R.id.rvMessages);
        messagePreviews = new ArrayList<>();
        adapter = new RecentMessagesMenuAdapter(this, messagePreviews);
        // Set the adapter on the recycler view
        rvRecentMessages.setAdapter(adapter);
        // Set the layout manager on the recycler view
        rvRecentMessages.setLayoutManager(new LinearLayoutManager(this));
        queryMessages();
    }

    public void queryMessages() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereMatches(KEY_OBJECT_ID, ParseUser.getCurrentUser().getObjectId());
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    if (user.getList(KEY_RECENT_MESSAGES) != null) {
                        messagePreviews.addAll(user.getList(KEY_RECENT_MESSAGES));
                    }
                    else setContentView(R.layout.activity_no_results);
                }
                else {
                    Log.i(TAG, "Direct messages not retrieved correctly.");
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
