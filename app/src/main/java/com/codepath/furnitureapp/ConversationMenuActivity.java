package com.codepath.furnitureapp;

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

public class ConversationMenuActivity extends AppCompatActivity {

    private RecyclerView rvConversation;
    private List<Conversation> allConversations;
    private ConversationMenuAdapter adapter;
    public static final String TAG = "ConversationMenuActivity";
    public static final String KEY_OBJECT_ID = "objectId";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_messages);

        rvConversation = findViewById(R.id.rvMessages);
        allConversations = new ArrayList<>();
        adapter = new ConversationMenuAdapter(this, allConversations);
        // Set the adapter on the recycler view
        rvConversation.setAdapter(adapter);
        // Set the layout manager on the recycler view
        rvConversation.setLayoutManager(new LinearLayoutManager(this));
        queryMessages();
    }

    public void queryMessages() {

        // Bind the post data to the view elements
        ParseQuery<Conversation> userOneQueryAsSender = ParseQuery.getQuery(Conversation.class);
        userOneQueryAsSender.whereEqualTo(Conversation.KEY_USERONE, ParseUser.getCurrentUser().getObjectId());

        ParseQuery<Conversation> userOneQueryAsRecipient = ParseQuery.getQuery(Conversation.class);
        userOneQueryAsRecipient.whereEqualTo(Conversation.KEY_USERTWO, ParseUser.getCurrentUser().getObjectId());

        List<ParseQuery<Conversation>> queries = new ArrayList<ParseQuery<Conversation>>();
        queries.add(userOneQueryAsSender);
        queries.add(userOneQueryAsRecipient);

        ParseQuery<Conversation> mainQuery = ParseQuery.or(queries);
        mainQuery.findInBackground(new FindCallback<Conversation>() {
            public void done(List<Conversation> conversations, ParseException e) {
                // results has the list of players that win a lot or haven't won much.
                if (e != null) {
                    Log.e(TAG, "Issue with getting conversations", e);
                    return;
                }
                if (conversations.isEmpty()) {
                    Log.i(TAG, "conversations list is empty: " + conversations.toString());
                }
                allConversations.clear();
                allConversations.addAll(conversations);
                adapter.notifyDataSetChanged();;
            }
        });
    }
}
