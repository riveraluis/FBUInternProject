package com.codepath.furnitureapp;

import android.content.Context;
import android.content.Intent;
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
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecentMessagesMenuAdapter extends RecyclerView.Adapter<RecentMessagesMenuAdapter.ViewHolder> {

    Context context;
    private List<RecentMessage> messagePreviews;
    public static final String TAG = "DirectMessageAdapter";
    public static final String KEY_OBJECT_ID = "objectId";

    public RecentMessagesMenuAdapter(Context context, List<RecentMessage> messagePreviews) {
        this.context = context;
        this.messagePreviews = messagePreviews;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_preview, parent, false);
        return new RecentMessagesMenuAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        RecentMessage message = messagePreviews.get(position);
        try {
            holder.bind(message);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return messagePreviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivPreviewProfilePic;
        private TextView tvPreviewName;
        private TextView tvPreviewLatestMessage;
        RecentMessage tempMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPreviewProfilePic = itemView.findViewById(R.id.ivProfilePicMessagePreview);
            tvPreviewName = itemView.findViewById(R.id.tvMessagePreviewName);
            tvPreviewLatestMessage = itemView.findViewById(R.id.tvLatestMessage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ChatActivity.class);
                    // Query for correct user in order to direct correct chat
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo(KEY_OBJECT_ID, tempMessage.getRecentSentTo());
                    query.getInBackground(tempMessage.getRecentSentTo(), new GetCallback<ParseUser>() {
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                i.putExtra(SignupActivity.KEY_FULLNAME, user);
                                context.startActivity(i);
                            }
                        }
                    });
                }
            });
        }

        public void bind(RecentMessage message) throws ParseException {
            // Bind the post data to the view elements
            tempMessage = message;
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo(KEY_OBJECT_ID, message.fetchIfNeeded().getString(RecentMessage.SENT_TO_KEY));
            query.getInBackground(message.getRecentSentTo(), new GetCallback<ParseUser>() {
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        tvPreviewName.setText(user.getString(SignupActivity.KEY_FULLNAME));
                        ParseFile profilePic = user.getParseFile(PostsAdapter.KEY_PROFILE_PIC);
                        if (profilePic != null) {
                            Glide.with(context).load(profilePic.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivPreviewProfilePic);
                        }
                        tvPreviewLatestMessage.setText(message.getRecentMessage());
                    }
                    else Log.i(TAG, "User's full name was not retrieved correctly");
                }
            });
        }
    }
}
