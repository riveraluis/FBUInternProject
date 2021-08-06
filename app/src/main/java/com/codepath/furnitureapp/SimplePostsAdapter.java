package com.codepath.furnitureapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class SimplePostsAdapter extends RecyclerView.Adapter<SimplePostsAdapter.ViewHolder> {

    Context context;
    private List<Post> posts;
    private List<String> likedPosts;
    public static final String TAG = "PostsAdapter";
    public static final String KEY_PROFILE_PIC = "profilePicture";
    public static final String KEY_USER = "user";

    public SimplePostsAdapter(Context context, List<Post> posts ) {
        this.context = context;
        this.posts = posts;
    }

    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.simple_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimplePostsAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;
        private ImageView likeButton;
        private ImageView profilePic;
        private TextView tvUsername;
        private Post tempPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImageSimple);
            likeButton = itemView.findViewById(R.id.ibLikeButtonSimple);
            profilePic = itemView.findViewById(R.id.ivProfileImageSimple);
            tvUsername = itemView.findViewById(R.id.tvUsernameSimple);
            itemView.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onDoubleClick(View v) {
                    likePost(tempPost);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent i = new Intent(context, PostDetailsActivity.class);
                    i.putExtra("post", tempPost);
                    context.startActivity(i);
                    return false;
                }
            });
        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            tempPost = post;
            tvUsername.setText(post.getUser().getUsername());
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

            int radius = 35; // corner radius, higher value = more rounded
            int margin = 0; // crop margin, set to 0 for corners with no crop
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
