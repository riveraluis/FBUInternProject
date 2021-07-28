package com.codepath.furnitureapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

import static com.parse.Parse.getApplicationContext;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<Message> mMessages;
    private Context mContext;
    private String mUserId;
    // ambiguous numbers that are not the same
    private static final int MESSAGE_OUTGOING = 123;
    private static final int MESSAGE_INCOMING = 321;
    public static final String TAG = "ChatAdapter";

    public ChatAdapter(Context context, String userId, List<Message> messages) {
        mMessages = messages;
        this.mUserId = userId;
        mContext = context;
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isMe(position)) {
            return MESSAGE_OUTGOING;
        } else {
            return MESSAGE_INCOMING;
        }
    }

    private boolean isMe(int position) {
        Message message = mMessages.get(position);
        return message.getUserId() != null && message.getUserId().equals(mUserId);
    }

    private static void getProfileUrl(final String userId, ImageView ivProfilePic) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereMatches("objectId", userId);

        query.getInBackground(userId, new GetCallback<ParseUser>() {
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    ParseFile profilePicture = user.getParseFile("profilePicture");
                    if (profilePicture != null) {
                        profilePicture.getDataInBackground(new GetDataCallback() {
                            Context context;
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    if (bitmap != null)
                                        Glide.with(getApplicationContext()).load(bitmap).apply(RequestOptions.circleCropTransform()).into(ivProfilePic);
                                }
                                else Log.d(TAG, "ParseFile ParseException: " + e.toString());
                            }
                        });
                    }
                    else {
                        // user does not have a profile picture set, load default
                        Log.d(TAG, "ParseFile is null");
                        int drawableResourceId = getApplicationContext().getResources().
                                getIdentifier("ic_baseline_person_24", "drawable", getApplicationContext().getPackageName());
                        Glide.with(getApplicationContext()).load(drawableResourceId).into(ivProfilePic);
                    }
                }
                else Log.i(TAG, "User was not retrieved correctly");
            }
        });
    }

    public abstract class MessageViewHolder extends RecyclerView.ViewHolder {

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        abstract void bindMessage(Message message);
    }

    public class IncomingMessageViewHolder extends MessageViewHolder {
        ImageView imageOther;
        TextView body;
        TextView name;

        public IncomingMessageViewHolder(View itemView) {
            super(itemView);
            imageOther = itemView.findViewById(R.id.ivProfileOther);
            body = itemView.findViewById(R.id.tvBody);
            name = itemView.findViewById(R.id.tvName);
        }

        @Override
        public void bindMessage(Message message) {
            getProfileUrl(message.getUserId(), imageOther);
            body.setText(message.getBody());
            name.setText(message.getUserId());
        }
    }

    public class OutgoingMessageViewHolder extends MessageViewHolder {
        ImageView imageMe;
        TextView body;

        public OutgoingMessageViewHolder(View itemView) {
            super(itemView);
            imageMe = itemView.findViewById(R.id.ivProfileMe);
            body = itemView.findViewById(R.id.tvBody);
        }

        @Override
        public void bindMessage(Message message) {
            getProfileUrl(message.getUserId(), imageMe);
            body.setText(message.getBody());
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == MESSAGE_INCOMING) {
            View contactView = inflater.inflate(R.layout.message_incoming, parent, false);
            return new IncomingMessageViewHolder(contactView);
        } else if (viewType == MESSAGE_OUTGOING) {
            View contactView = inflater.inflate(R.layout.message_outgoing, parent, false);
            return new OutgoingMessageViewHolder(contactView);
        } else {
            throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = mMessages.get(position);
        holder.bindMessage(message);
    }
}
