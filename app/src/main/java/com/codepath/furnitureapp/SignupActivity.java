package com.codepath.furnitureapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import okhttp3.Headers;

public class SignupActivity extends AppCompatActivity {

    public static final String UNIVERSITIES_URL = "http://universities.hipolabs.com/search?country=united%20states";
    public static final String TAG = "SignupActivity";
    public static final String KEY_UNIVERSITY = "school";
    public static final String KEY_FULLNAME = "fullName";
    private EditText etUsername;
    private EditText etPassword;
    private EditText etEmail;
    private EditText etFullName;
    private AutoCompleteTextView actvSuggestionBox;
    private Button btnDone;
    private ParseFile file;
    ArrayList<University> universities = new ArrayList<>();

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        btnDone = findViewById(R.id.btnDone);
        etFullName = findViewById(R.id.etFullname);
        actvSuggestionBox = findViewById(R.id.actvSuggestionBox);

        // Set adapter to see universities and choose
        ArrayAdapter<University> universitiesAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, universities);
        actvSuggestionBox.setAdapter(universitiesAdapter);

        // Call signupUser method which registers new user into Parse database
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupUser(etUsername.getText().toString(),
                        etPassword.getText().toString(),
                        etEmail.getText().toString(),
                        actvSuggestionBox.getText().toString(),
                        etFullName.getText().toString());
            }
        });

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(UNIVERSITIES_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess retrieving json data");
                JSONArray results = json.jsonArray;
                try {
                    universities.addAll(University.fromJsonArray(results));
                    Log.i(TAG, "Universities: " + universities.toString());
                } catch (Exception e) {
                    Log.e(TAG, "Hit json exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "onFailure with retrieving json data" + throwable);
            }
        });
    }

    private void saveToParse(String username, String password, String email, String school, String fullName) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put(KEY_FULLNAME, fullName);
        user.put(KEY_UNIVERSITY, school);
        user.put("profilePicture", file);
        Log.i(TAG, "User info: email: " + user.getEmail() + " full name: " + user.getString(KEY_FULLNAME));

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Go to MainActivity if signup was successful
                    Toast.makeText(SignupActivity.this, "Successful signup!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Log.e(TAG, "Issue with signup", e);
                    return;
                }
            }
        });
    }

    public Bitmap convertToBitmap(Drawable drawable) {
        try {
            Bitmap bitmap;
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            // Handle the error
            return null;
        }
    }

    public void signupUser(String username, String password, String email, String school, String fullName) {
        // convert drawable file to byte bitmap then byte array
        Drawable d = getResources().getDrawable(R.drawable.ic_baseline_person_24);
        Bitmap bitmap = convertToBitmap(d);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();

        // Create the ParseFile
        file = new ParseFile("profilePicture", bitmapdata);
        file.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                // If successful, add file to user and signUpInBackground
                if (null == e)
                    saveToParse(username, password, email, school, fullName);
            }
        });
    }
}
