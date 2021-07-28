package com.codepath.furnitureapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;



public class ClickUserProfileActivity extends AppCompatActivity {

    public static final String TAG = "ProfileFragment";
    private RecyclerView rvGridPosts;
    protected ProfilePostsAdapter profilePostsAdapter;
    protected List<Post> allPosts;
    private ImageView ivProfilePicture;
    private ImageButton ivSeeGridPosts;
    private ImageButton ivSeeFavorited;
    private TextView tvFullName;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvUserProfile;
    private boolean selectedFavorites = false;
    private ImageView ivMessages;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile);
        ParseUser user = getIntent().getParcelableExtra("user");

        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvUsername = findViewById(R.id.tvProfileUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvFullName = findViewById(R.id.tvNameOfUser);
        ivSeeGridPosts = findViewById(R.id.ivSeeGridPosts);
        ivSeeFavorited = findViewById(R.id.ivSeeFavorited);
        tvUserProfile = findViewById(R.id.tvMyProfile);
        ivMessages = findViewById(R.id.ivMessages);

        // Set up RecyclerView and adapter
        rvGridPosts = findViewById(R.id.rvGridPosts);
        allPosts = new ArrayList<>();
        profilePostsAdapter = new ProfilePostsAdapter(getApplicationContext(), allPosts);
        // Set the adapter on the recycler view
        rvGridPosts.setAdapter(profilePostsAdapter);
        // Set the layout manager on the recycler view
        rvGridPosts.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        // Query posts
        queryPosts(user);

        tvUsername.setText(user.getUsername());
        Log.i(TAG, "users email " + user.getEmail());
        tvEmail.setText(user.getEmail());
        tvFullName.setText(user.getString(SignupActivity.KEY_FULLNAME));
        tvUserProfile.setText(user.getUsername() + "'s Profile");

        // Set grid to be default selected
        // Set listeners to toggle between favorites and grid view
        ivSeeGridPosts.setSelected(true);
        ivSeeGridPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivSeeFavorited.isSelected()) {
                    ivSeeFavorited.setSelected(false);
                    ivSeeGridPosts.setSelected(true);
                }
                queryPosts(user);
            }
        });

        ivSeeFavorited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivSeeGridPosts.isSelected()) {
                    ivSeeGridPosts.setSelected(false);
                    ivSeeFavorited.setSelected(true);
                }
                selectedFavorites = true;
                queryPosts(user);
            }
        });

        ivMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                i.putExtra("receiver", user);
                startActivity(i);
            }
        });

        // Set Profile picture
        ParseFile profilePicture = user.getParseFile("profilePicture");
        if (profilePicture != null) {
            profilePicture.getDataInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        if (bitmap != null)
                            Glide.with(getApplicationContext()).load(bitmap).apply(RequestOptions.circleCropTransform()).into(ivProfilePicture);
                    }
                    else Log.d(TAG, "ParseFile ParseException: " + e.toString());
                }
            });
        }
        else { Log.d(TAG, "Selected user does not have a profile picture."); }
    }

    private void queryPosts(ParseUser user) {
        // Specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // Include data referred by user key
        query.include(Post.KEY_USER);
        if (!selectedFavorites)
            query.whereEqualTo(Post.KEY_USER, user);

        query.addDescendingOrder(Post.KEY_CREATED_AT);

        // Start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // Check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // Only add liked posts when on favorites page then return
                if (selectedFavorites) {
                    allPosts.clear();
                    for (Post post: posts)
                        if (post.getLikedArray().contains(user.getObjectId()))
                            allPosts.add(post);

                    profilePostsAdapter.notifyDataSetChanged();
                    selectedFavorites = false;
                    return;
                }

                // Save received posts to list and notify adapter of new data
                allPosts.clear();
                allPosts.addAll(posts);
                profilePostsAdapter.notifyDataSetChanged();
            }
        });
    }
}
