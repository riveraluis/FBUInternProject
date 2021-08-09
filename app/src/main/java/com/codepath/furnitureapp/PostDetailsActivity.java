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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.furnitureapp.Fragments.ProfileFragment;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.parse.Parse.getApplicationContext;

public class PostDetailsActivity extends AppCompatActivity  {

    private Post post;
    private TextView tvUsername;
    private ImageView ivImage;
    private ImageView ivProfilePic;
    private TextView tvDescription;
    private ImageView ibLikeButton;
    private TextView tvPrice;
    private List<String> likedPosts;
    private Spinner spPostOptions;
    public static final String TAG = "PostDetailsActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        post = getIntent().getParcelableExtra("post");

        tvUsername = findViewById(R.id.tvUsername);
        ivImage = findViewById(R.id.ivImage);
        tvDescription = findViewById(R.id.tvDescription);
        tvPrice = findViewById(R.id.tvPrice);
        ibLikeButton = findViewById(R.id.ibLikeButton);
        ivProfilePic = findViewById(R.id.ivProfileImage);
        spPostOptions = findViewById(R.id.spPostOptions);

        if (post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            // Create and set array adapters for each spinner
            ArrayAdapter<String> postOptionsAdapter = new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.postOptions));
            spPostOptions.setAdapter(postOptionsAdapter);

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
                public void onNothingSelected(AdapterView<?> parent) { }
            });
        }

        if (post.getLikedArray().contains(ParseUser.getCurrentUser().getObjectId())) {
            ibLikeButton.setSelected(true);
        }
        ibLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost(post);
            }
        });

        tvDescription.setText(post.getDescription());
        tvUsername.setText(post.getUser().getUsername());
        tvPrice.setText(String.valueOf(post.getPrice()));

        ParseFile image = post.getImage();
        int radius = 35; // corner radius, higher value = more rounded
        int margin = 0; // crop margin, set to 0 for corners with no crop
        if (image != null) {
            Glide.with(getApplicationContext()).load(image.getUrl()).transform(new CenterCrop(), new RoundedCornersTransformation(radius, margin))
                    .into(ivImage);
        }

        ParseFile profilepic = post.getUser().getParseFile("profilePicture");
        if (profilepic != null) {
            Glide.with(getApplicationContext()).load(profilepic.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivProfilePic);
        }

        
        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // direct user to either their profile or another user's profile
                if (post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                    onBackPressed();
                    MainActivity.bottomNavigationView.setSelectedItemId(R.id.action_profile);
                }
                else {
                    Intent i = new Intent(getApplicationContext(), ClickUserProfileActivity.class);
                    i.putExtra("user", post.getUser());
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    onBackPressed();
                    startActivity(i);
                }

            }
        });

        overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
    }

    public void likePost(Post post) {
        ibLikeButton.setSelected(!ibLikeButton.isSelected());
        likedPosts =  post.getLikedArray();
        if (ibLikeButton.isSelected()) {
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
                    Toast.makeText(getApplicationContext(), "Error while saving post!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save was successful!");
            }
        });

        likedPosts.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing, R.anim.bottom_down);
    }

}
