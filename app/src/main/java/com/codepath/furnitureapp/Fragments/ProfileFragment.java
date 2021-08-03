package com.codepath.furnitureapp.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.furnitureapp.Post;
import com.codepath.furnitureapp.PostsAdapter;
import com.codepath.furnitureapp.ProfilePostsAdapter;
import com.codepath.furnitureapp.ProfileSettings;
import com.codepath.furnitureapp.R;
import com.codepath.furnitureapp.SignupActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.codepath.furnitureapp.Fragments.ComposeFragment.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.codepath.furnitureapp.Fragments.ComposeFragment.PICK_PHOTO_CODE;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    private RecyclerView rvGridPosts;
    protected ProfilePostsAdapter profilePostsAdapter;
    protected List<Post> allPosts;
    private ImageView ivProfileSettings;
    private ImageView ivProfilePicture;
    private ImageButton ivSeeGridPosts;
    private ImageButton ivSeeFavorited;
    private TextView tvFullName;
    private TextView tvUsername;
    private TextView tvEmail;
    private boolean selectedFavorites = false;
    public static final String KEY_PROFILE_PIC = "profilePicture";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Create toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.profile_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        tvUsername = view.findViewById(R.id.tvProfileUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        ivProfileSettings = view.findViewById(R.id.ivProfileSettings);
        tvFullName = view.findViewById(R.id.tvNameOfUser);
        ivSeeGridPosts = view.findViewById(R.id.ivSeeGridPosts);
        ivSeeFavorited = view.findViewById(R.id.ivSeeFavorited);

        // Set up RecyclerView and adapter
        rvGridPosts = view.findViewById(R.id.rvGridPosts);
        allPosts = new ArrayList<>();
        profilePostsAdapter = new ProfilePostsAdapter(getContext(), allPosts);
        // Set the adapter on the recycler view
        rvGridPosts.setAdapter(profilePostsAdapter);
        // Set the layout manager on the recycler view
        rvGridPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        // Query posts
        queryPosts();

        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        tvEmail.setText(ParseUser.getCurrentUser().getEmail());
        tvFullName.setText(ParseUser.getCurrentUser().getString(SignupActivity.KEY_FULLNAME));

        // Go to profile settings when clicked
        ivProfileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProfileSettings.class);
                startActivity(i);
            }
        });

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
                queryPosts();
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
                queryPosts();
            }
        });

        ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });


        // Set Profile picture
        ParseFile profilePicture = ParseUser.getCurrentUser().getParseFile("profilePicture");
        if (profilePicture != null) {
            profilePicture.getDataInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        if (bitmap != null)
                            Glide.with(getContext()).load(bitmap).apply(RequestOptions.circleCropTransform()).into(ivProfilePicture);
                    }
                    else Log.d(TAG, "ParseFile ParseException: " + e.toString());
                }
            });
        }
        else Log.d(TAG, "ParseFile is null");
    }

    public void queryPosts() {
        // Specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // Include data referred by user key
        query.include(Post.KEY_USER);
        if (!selectedFavorites)
            query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());

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
                        if (post.getLikedArray().contains(ParseUser.getCurrentUser().getObjectId()))
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
    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, PICK_PHOTO_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();

            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);
            
            Glide.with(this).load(selectedImage).apply(RequestOptions.circleCropTransform()).into(ivProfilePicture);
            // Convert bitmap to image
            try {
               convertBitmapToFile(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void convertBitmapToFile(Bitmap bitmap) throws Exception {
        // Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();

        ParseFile file = new ParseFile(KEY_PROFILE_PIC, bitmapdata);
        file.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (null == e)
                    saveProfilePicture(ParseUser.getCurrentUser(), file);
            }
        });
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // Check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // On newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // Support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void saveProfilePicture(ParseUser user, ParseFile file) {
        if (file == null) {
            Log.i(TAG, "photofile is null");
            return;
        }
        user.put("profilePicture", file);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.i(TAG, "saving user profile picture");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}