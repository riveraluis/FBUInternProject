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
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConversationMenuAdapter extends RecyclerView.Adapter<ConversationMenuAdapter.ViewHolder> {

    Context context;
    private List<Conversation> messagePreviews;
    public static final String TAG = "DirectMessageAdapter";
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String KEY_RECIPIENT = "recipient";

    public ConversationMenuAdapter(Context context, List<Conversation> messagePreviews) {
        this.context = context;
        this.messagePreviews = messagePreviews;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_preview, parent, false);
        return new ConversationMenuAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Conversation message = messagePreviews.get(position);
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
        Conversation tempConvo;

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
                    String recipientId;
                    if (!(tempConvo.getUserOne().equals(ParseUser.getCurrentUser().getObjectId()))) {
                        recipientId =  tempConvo.getUserOne();
                    }
                    else recipientId = tempConvo.getUserTwo();

                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo(KEY_OBJECT_ID, recipientId);

                    query.getInBackground(recipientId, new GetCallback<ParseUser>() {
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                i.putExtra(KEY_RECIPIENT, user);
                                context.startActivity(i);
                            }
                        }
                    });
                }
            });
        }

        public void bind(Conversation convo) throws ParseException {

            tempConvo = convo;
            String recipientId;
            if (!(convo.getUserOne().equals(ParseUser.getCurrentUser().getObjectId()))) {
                recipientId =  convo.getUserOne();
            }
            else recipientId = convo.getUserTwo();

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo(KEY_OBJECT_ID, recipientId);
            query.getInBackground(recipientId, new GetCallback<ParseUser>() {
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        tvPreviewName.setText(user.getString(SignupActivity.KEY_FULLNAME));
                        ParseFile profilePic = user.getParseFile(PostsAdapter.KEY_PROFILE_PIC);
                        if (profilePic != null) {
                            Glide.with(context).load(profilePic.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivPreviewProfilePic);
                        }
                        tvPreviewLatestMessage.setText(convo.getRecentMessage());
                    }
                    else Log.i(TAG, "User's full name was not retrieved correctly");
                }
            });
        }
    }
}
