package com.codepath.furnitureapp;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.furnitureapp.R;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatActivity extends AppCompatActivity {

    public static final String KEY_RECENT_MESSAGES = "recentMessages";
    public static final String KEY_RECIPIENT = "recipient";
    public static final String KEY_SENT_TO = "sentTo";
    static final String TAG = ChatActivity.class.getSimpleName();
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    static final long POLL_INTERVAL = TimeUnit.SECONDS.toMillis(10);
    private EditText etMessage;
    private ImageButton ibSend;
    private RecyclerView rvChat;
    private ArrayList<Message> mMessages;
    private boolean mFirstLoad;
    private ChatAdapter mAdapter;
    ParseUser recipient;

    private Handler myHandler = new android.os.Handler();
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            myHandler.postDelayed(this, POLL_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        recipient = getIntent().getParcelableExtra(KEY_RECIPIENT);

        // User login
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        } else { // If not logged in, login as a new anonymous user
            login();
        }
    }

    // Create an anonymous user using ParseAnonymousUtils and set sUserId
    void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Anonymous login failed: ", e);
                } else {
                    startWithCurrentUser();
                }
            }
        });
    }

    // Get the userId from the cached currentUser object
    void startWithCurrentUser() {
        setupMessagePosting();
    }

    // Set up button event handler which posts the entered message to Parse
    void setupMessagePosting() {
        etMessage = findViewById(R.id.etMessage);
        ibSend = findViewById(R.id.ibSend);
        rvChat = findViewById(R.id.rvChat);
        mMessages = new ArrayList<>();
        mFirstLoad = true;
        final String userId = ParseUser.getCurrentUser().getObjectId();
        mAdapter = new ChatAdapter(ChatActivity.this, userId, mMessages);
        rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
        rvChat.setLayoutManager(linearLayoutManager);
        ParseUser user = ParseUser.getCurrentUser();

        // When send button is clicked, create message object on Parse
        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
                Message message = new Message();
                message.setUserId(user.getObjectId());
                message.setBody(data);
                message.setSentTo(recipient.getObjectId());

                ParseQuery<Conversation> userOneQueryAsSender = ParseQuery.getQuery(Conversation.class);
                userOneQueryAsSender.whereEqualTo(Conversation.KEY_USERONE, ParseUser.getCurrentUser().getObjectId());

                ParseQuery<Conversation> userOneQueryAsRecipient = ParseQuery.getQuery(Conversation.class);
                userOneQueryAsRecipient.whereEqualTo(Conversation.KEY_USERTWO, ParseUser.getCurrentUser().getObjectId());

                ParseQuery<Conversation> userTwoAsSender = ParseQuery.getQuery(Conversation.class);
                userTwoAsSender.whereEqualTo(Conversation.KEY_USERONE, recipient.getObjectId());

                ParseQuery<Conversation> userTwoAsRecipient = ParseQuery.getQuery(Conversation.class);
                userTwoAsRecipient.whereEqualTo(Conversation.KEY_USERTWO, recipient.getObjectId());

                List<ParseQuery<Conversation>> queries = new ArrayList<ParseQuery<Conversation>>();
                queries.add(userOneQueryAsSender);
                queries.add(userOneQueryAsRecipient);
                queries.add(userTwoAsSender);
                queries.add(userTwoAsRecipient);

                Conversation convo = new Conversation();

                ParseQuery<Conversation> mainQuery = ParseQuery.or(queries);
                mainQuery.findInBackground(new FindCallback<Conversation>() {
                    public void done(List<Conversation> conversations, ParseException e) {
                        // results has the list of players that win a lot or haven't won much.
                        boolean foundConvo = false;
                        if (e != null) {
                            Log.e(TAG, "Issue with getting conversations", e);
                            return;
                        }

                        Log.i(TAG, "Conversations that match" + conversations.toString());

                        if (conversations.isEmpty()) {
                            Log.i(TAG, "Conversations list is empty" + conversations.toString());
                            convo.setRecentMessage(data);
                            convo.setUserOne(ParseUser.getCurrentUser().getObjectId());
                            convo.setUserTwo(recipient.getObjectId());
                            saveConversation(convo);

                        }
                        else {
                            ParseUser currentUser = ParseUser.getCurrentUser();

                            for (Conversation convo : conversations) {
                                String userOneId = convo.getUserOne();
                                String userTwoId = convo.getUserTwo();
                                String currentUserId = currentUser.getObjectId();
                                String recipientId = recipient.getObjectId();
                                if ((userOneId.equals(currentUserId) || userOneId.equals(recipientId))
                                        && (userTwoId.equals(currentUserId) || userTwoId.equals(recipientId))) {
                                    foundConvo = true;
                                    convo.setRecentMessage(data);
                                    convo.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Log.i(TAG, "saving updated conversation.");
                                        }
                                    });
                                }
                            }
                            if (!foundConvo) {
                                convo.setRecentMessage(data);
                                convo.setUserOne(ParseUser.getCurrentUser().getObjectId());
                                convo.setUserTwo(recipient.getObjectId());
                                saveConversation(convo);
                            }
                        }
                    }
                });

                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(ChatActivity.this, "Successfully created message on Parse",
                                Toast.LENGTH_SHORT).show();
                        refreshMessages();
                    }
                });

                etMessage.setText(null);
            }
        });
    }

    private void saveConversation(Conversation convo) {
        convo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(TAG, "saving new conversation.");
                }
                else {
                    Log.i(TAG, "exception: " + e.toString());
                }
            }
        });
    }

    // Query messages from Parse so we can load them into chat adapter
    void refreshMessages() {
            ParseQuery<Message> queryIncoming = ParseQuery.getQuery(Message.class);
            Log.i(TAG, "recipient info: " + recipient);
            queryIncoming.whereEqualTo(Message.USER_ID_KEY, recipient.getObjectId());
            queryIncoming.whereEqualTo(Message.SENT_TO_KEY, ParseUser.getCurrentUser().getObjectId());

            ParseQuery<Message> queryOutgoing = ParseQuery.getQuery(Message.class);
            queryOutgoing.whereEqualTo(Message.USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());
            queryOutgoing.whereEqualTo(Message.SENT_TO_KEY, recipient.getObjectId());

            // make a compound query of both incoming and outgoing messages
            List<ParseQuery<Message>> messagesQuery = new ArrayList<ParseQuery<Message>>();
            messagesQuery.add(queryIncoming);
            messagesQuery.add(queryOutgoing);

            ParseQuery<Message> mainQuery = ParseQuery.or(messagesQuery);
            mainQuery.orderByDescending("createdAt");
            mainQuery.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);

            mainQuery.findInBackground(new FindCallback<Message>() {
                public void done(List<Message> messages, ParseException e) {
                    if (e == null) {
                        mMessages.clear();
                        mMessages.addAll(messages);
                        mAdapter.notifyDataSetChanged();
                        // Scroll to the bottom of the list on initial load
                        if (mFirstLoad) {
                            rvChat.scrollToPosition(0);
                            mFirstLoad = false;
                        }
                    } else {
                        Log.e(TAG, "Error Loading Messages" + e);
                    }
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Only start checking for new messages when the app becomes active in foreground
        myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
    }

    @Override
    protected void onPause() {
        // Stop background task from refreshing messages, to avoid unnecessary traffic & battery drain
        myHandler.removeCallbacksAndMessages(null);
        super.onPause();
    }
}
