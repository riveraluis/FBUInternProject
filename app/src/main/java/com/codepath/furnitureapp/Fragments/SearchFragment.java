package com.codepath.furnitureapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.codepath.furnitureapp.Furniture;
import com.codepath.furnitureapp.Post;
import com.codepath.furnitureapp.R;
import com.codepath.furnitureapp.SearchResultsActivity;

import static com.parse.Parse.getApplicationContext;


public class SearchFragment extends Fragment {

    public static final String TAG =  "SortByActivity";
    private Spinner spCategory;
    private Spinner spMaterial;
    private Spinner spColor;
    private Spinner spCondition;
    private Spinner spPrice;
    private Button btnDoneSortingBy;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_sortby_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spCategory = view.findViewById(R.id.spCategory);
        spMaterial = view.findViewById(R.id.spMaterial);
        spColor = view.findViewById(R.id.spColor);
        spPrice = view.findViewById(R.id.spPrice);
        spCondition = view.findViewById(R.id.spCondition);
        btnDoneSortingBy = view.findViewById(R.id.btnDoneSortingBy);

        // Create and set array adapters for each spinner
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.categories));
        spCategory.setAdapter(categoriesAdapter);

        ArrayAdapter<String> materialsAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.materials));
        spMaterial.setAdapter(materialsAdapter);

        ArrayAdapter<String> colorsAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.colors));
        spColor.setAdapter(colorsAdapter);

        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.condition));
        spCondition.setAdapter(conditionAdapter);

        ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.prices));
        spPrice.setAdapter(priceAdapter);

        btnDoneSortingBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = spCategory.getSelectedItem().toString();
                String material = spMaterial.getSelectedItem().toString();
                String color = spColor.getSelectedItem().toString();
                String condition = spCondition.getSelectedItem().toString();
                String price = spPrice.getSelectedItem().toString();

                Intent i = new Intent(getApplicationContext(), SearchResultsActivity.class);
                Bundle extras = new Bundle();
                extras.putString(Furniture.KEY_CATEGORY, category);
                extras.putString(Furniture.KEY_MATERIAL, material);
                extras.putString(Furniture.KEY_COLOR, color);
                extras.putString(Post.KEY_PRICE, price);
                extras.putString(Furniture.KEY_CONDITION, condition);
                i.putExtras(extras);
                startActivity(i);
            }
        });
    }
}