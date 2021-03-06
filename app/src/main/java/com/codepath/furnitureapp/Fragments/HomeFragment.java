package com.codepath.furnitureapp.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.furnitureapp.ConversationMenuActivity;
import com.codepath.furnitureapp.Post;
import com.codepath.furnitureapp.PostsAdapter;
import com.codepath.furnitureapp.R;
import com.codepath.furnitureapp.SignupActivity;
import com.codepath.furnitureapp.SimplePostsAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class HomeFragment extends Fragment {

    public static final String TAG = "PostsFragment";
    private RecyclerView rvPosts;
    protected SimplePostsAdapter adapter;
    protected List<Post> allPosts;
    private SwipeRefreshLayout swipeContainer;
    private ImageView ivDirectMessages;
    private TextView tvUniversityName;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.home_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        rvPosts = view.findViewById(R.id.rvPosts);
        allPosts = new ArrayList<>();
        swipeContainer = view.findViewById(R.id.swipeContainer);
        ivDirectMessages = view.findViewById(R.id.ivDirectMessages);
        tvUniversityName = view.findViewById(R.id.tvUniversityName);

        tvUniversityName.setText(ParseUser.getCurrentUser().getString(SignupActivity.KEY_UNIVERSITY));

        // Set refresh listener
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPosts();
                swipeContainer.setRefreshing(false);
            }
        });

        ivDirectMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ConversationMenuActivity.class);
                startActivity(i);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        adapter = new SimplePostsAdapter(getContext(), allPosts);
        // Set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        // Set the layout manager on the recycler view
        //rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
       rvPosts.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rvPosts.setItemAnimator(new SlideInUpAnimator());
        // Query posts
        queryPosts();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    protected void queryPosts() {
        // Specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // Include data referred by user key
        query.include(Post.KEY_USER);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.whereEqualTo(Post.KEY_SCHOOL, ParseUser.getCurrentUser().getString(SignupActivity.KEY_UNIVERSITY));

        // Start  asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // Check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                allPosts.clear();
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                    allPosts.add(post);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}