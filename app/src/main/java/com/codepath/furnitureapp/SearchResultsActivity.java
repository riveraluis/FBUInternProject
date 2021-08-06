package com.codepath.furnitureapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

public class SearchResultsActivity extends AppCompatActivity {

    public static final String TAG = "SearchResultsActivity";
    private RecyclerView rvPosts;
    private PostsAdapter adapter;
    private ArrayList<Post> allPosts;
    private String category;
    private String material;
    private String color;
    private String priceSorting;
    private String condition;
    private boolean selectedPriceSorting = true;
    private int low = 0;
    private int high = 0;
    private SwipeRefreshLayout swipeContainer;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        rvPosts = findViewById(R.id.rvPosts);
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getApplicationContext(), allPosts);
        // Set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        // Set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        swipeContainer = findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPosts();
                swipeContainer.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Get info from bundle
        Bundle extras = getIntent().getExtras();
        category = extras.getString(Furniture.KEY_CATEGORY);
        material = extras.getString(Furniture.KEY_MATERIAL);
        color = extras.getString(Furniture.KEY_COLOR);
        priceSorting = extras.getString(Post.KEY_PRICE);
        condition = extras.getString(Furniture.KEY_CONDITION);

        // Query posts and set common fields
        setPriceRanges();
        queryPosts();
    }

    public void setPriceRanges() {
        String[] priceFilters = getResources().getStringArray(R.array.prices);
        if (priceSorting.equals(priceFilters[1])) {
            low = 0; high = 100;
        }
        else if (priceSorting.equals(priceFilters[2])) {
            low = 100; high = 200;
        }
        else if (priceSorting.equals(priceFilters[3])) {
            low = 200; high = 300;
        }
        else if (priceSorting.equals(priceFilters[4])) {
            low = 300; high = 400;
        }
        else if (priceSorting.equals(priceFilters[5])) {
            low = 400; high = 500;
        }
        else if (priceSorting.equals(priceFilters[6])) {
            low = 500;
        }
        else {
            selectedPriceSorting = false;
        }
    }

    protected void queryPosts() {
        // Specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_FURNITURE);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_SCHOOL, ParseUser.getCurrentUser().getString(SignupActivity.KEY_UNIVERSITY));
        query.whereNotEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());

        // Start  asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // Check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                //allPosts.clear();
                for (Post post : posts) {
                    // Clear out before using
                    ParseObject furniture = post.getParseObject(Post.KEY_FURNITURE);
                    post.setCommonFields(0);
                    // For debugging purposes print every post description to logcat
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());

                    int postPrice = post.getPrice();
                    Log.i(TAG, "postPrice " + postPrice + " low " + low + " high " + high);

                    if (selectedPriceSorting) {
                        // Check if item is within set range
                        if (postPrice >= low && postPrice < high && furniture != null && furniture.getString(Furniture.KEY_CATEGORY).equals(category)) {
                            setCommonFields(post);
                        }
                    }
                    else if (furniture != null && furniture.getString(Furniture.KEY_CATEGORY).equals(category)) {
                        setCommonFields(post);
                    }
                }
            }
        });

        addPosts();
    }

    public void setCommonFields(Post post) {
        // Check if search criteria matches furniture, and set commonFields
        ParseObject furniture = post.getParseObject(Post.KEY_FURNITURE);
        checkFieldMatches(post, Furniture.KEY_CATEGORY, category, furniture);
        checkFieldMatches(post, Furniture.KEY_MATERIAL, material, furniture);
        checkFieldMatches(post, Furniture.KEY_COLOR, color, furniture);
        checkFieldMatches(post, Furniture.KEY_CONDITION, condition, furniture);

        // Once we have info call this method to save in background thread
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving post!", e);
                }
                Log.i(TAG, "Post save was successful!");
            }
        });
    }

    public void checkFieldMatches(Post post, String field, String searchingForTag, ParseObject furniture) {
        String currentPostAttribute = furniture.getString(field);
        if (Objects.equals(currentPostAttribute, searchingForTag)) {
            // Increment commonFields if there is a match
            post.setCommonFields(post.getCommonFields() + 1);
        }
    }

    public void addPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.include(Post.KEY_FURNITURE);

        // Order items by relevance
        query.whereEqualTo(Post.KEY_SCHOOL, ParseUser.getCurrentUser().getString(SignupActivity.KEY_UNIVERSITY));
        query.orderByDescending(Post.KEY_COMMON_FIELDS);
        query.whereNotEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // Check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                allPosts.clear();
                for (Post post: posts) {
                    ParseObject furniture = post.getParseObject(Post.KEY_FURNITURE);
                    int postPrice = post.getPrice();
                    if (selectedPriceSorting) {
                        // Check if item is within set range
                        if (postPrice >= low && postPrice < high && furniture != null && furniture.getString(Furniture.KEY_CATEGORY).equals(category)) {
                            Log.i(TAG, "description: " + post.getDescription() + " CF: " + post.getCommonFields());
                            allPosts.add(post);
                        }
                    }
                    else if (furniture != null && furniture.getString(Furniture.KEY_CATEGORY).equals(category)) {
                        Log.i(TAG, "description: " + post.getDescription() + " CF: " + post.getCommonFields());
                        allPosts.add(post);
                    }
                }
                if (allPosts.isEmpty()){
                    setContentView(R.layout.activity_no_results);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
