package com.codepath.furnitureapp;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Furniture")
public class Furniture extends ParseObject {

    public static final String KEY_CATEGORY = "category";
    public static final String KEY_MATERIAL = "material";
    public static final String KEY_COLOR = "color";
    public static final String KEY_CONDITION = "condition";

    public String getCategory() { return getString(KEY_CATEGORY); }

    public void setCategory(String category) { put(KEY_CATEGORY, category); }

    public String getMaterial() { return getString(KEY_MATERIAL); }

    public void setMaterial(String material) { put(KEY_MATERIAL, material); }

    public String getFurnitureColor() { return getString(KEY_COLOR); }

    public void setFurnitureColor(String color) { put(KEY_COLOR, color); }

    public String getCondition() { return getString(KEY_CONDITION); }

    public void setCondition(String condition) { put(KEY_CONDITION, condition); }

}
