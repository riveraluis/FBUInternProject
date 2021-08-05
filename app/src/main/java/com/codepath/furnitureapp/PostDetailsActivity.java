package com.codepath.furnitureapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.furnitureapp.Fragments.ProfileFragment;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import static com.parse.Parse.getApplicationContext;

public class PostDetailsActivity extends AppCompatActivity  {

    private Post post;
    private RecyclerView rvPosts;
    private TextView tvUsername;
    private ImageView ivImage;
    private ImageView ivProfilePic;
    private TextView tvDescription;
    private ImageButton ibLikeButton;
    private TextView tvPrice;
    private Spinner spPostOptions;
    private PostsAdapter adapter;
    private ArrayList<Post> allPosts;
    public static final String TAG = "PostDetailsActivity";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        spPostOptions = findViewById(R.id.spPostOptions);

        rvPosts = findViewById(R.id.rvPostsDetails);
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getApplicationContext(), allPosts);
        // Set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        // Set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Create and set array adapters for each spinner
        ArrayAdapter<String> postOptionsAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.postOptions));
        spPostOptions.setAdapter(postOptionsAdapter);

        post = getIntent().getParcelableExtra("post");
        allPosts.clear();
        allPosts.add(post);
        adapter.notifyDataSetChanged();

        spPostOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals("Delete"))
                {
                    Log.i(TAG, "delete was selected");
                    post.deleteInBackground();
                    spPostOptions.setSelection(0);
                    finish();
                }
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

    }

}
