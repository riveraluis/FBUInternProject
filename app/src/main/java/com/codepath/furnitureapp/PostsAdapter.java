package com.codepath.furnitureapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.furnitureapp.Fragments.ProfileFragment;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashSet;
import java.util.List;
import androidx.fragment.app.FragmentManager;


public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    Context context;
    private List<Post> posts;
    private List<String> likedPosts;
    public static final String TAG = "PostsAdapter";
    public static final String KEY_PROFILE_PIC = "profilePicture";

    public PostsAdapter(Context context, List<Post> posts ) {
        this.context = context;
        this.posts = posts;
    }

    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostsAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvDescription;
        private TextView tvPrice;
        private ImageButton likeButton;
        private Post tempPost;
        private ImageView profilePic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            likeButton = itemView.findViewById(R.id.ibLikeButton);
            profilePic = itemView.findViewById(R.id.ivProfileImage);
            itemView.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onDoubleClick(View v) {
                    likePost(tempPost);
                }
            });
            profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // direct user to either their profile or another user's profile
                    if (tempPost.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                        MainActivity.bottomNavigationView.setSelectedItemId(R.id.action_profile);
                    }
                    else {
                        Intent i = new Intent(context, ClickUserProfileActivity.class);
                        i.putExtra("user", tempPost.getUser());
                        context.startActivity(i);
                    }
                }
            });
        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            tempPost = post;
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            tvPrice.setText(String.valueOf(post.getPrice()));

            // If the current user exists in the list of users who liked this post,
            // then set the like button to selected
            if (post.getLikedArray().contains(ParseUser.getCurrentUser().getObjectId())) {
                likeButton.setSelected(true);
            }
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    likePost(post);
                }
            });

            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }
            ParseFile profilepic = post.getUser().getParseFile(KEY_PROFILE_PIC);
            if (profilepic != null) {
                Glide.with(context).load(profilepic.getUrl()).apply(RequestOptions.circleCropTransform()).into(profilePic);
            }
        }

        public void likePost(Post post) {
            likeButton.setSelected(!likeButton.isSelected());
            likedPosts =  post.getLikedArray();
            if (likeButton.isSelected()) {
                if (!(post.getLikedArray().contains(ParseUser.getCurrentUser().getObjectId())))
                    likedPosts.add(ParseUser.getCurrentUser().getObjectId());
            }

            else likedPosts.remove(ParseUser.getCurrentUser().getObjectId());

            post.setLikedArray(likedPosts);

            post.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error while saving post!", e);
                        Toast.makeText(context, "Error while saving post!", Toast.LENGTH_SHORT).show();
                    }
                    Log.i(TAG, "Post save was successful!");
                }
            });

            likedPosts.clear();
        }
    }
}
