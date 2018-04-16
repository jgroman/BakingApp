/*
 * Copyright 2018 Jaroslav Groman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.jtek.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Recipe implements Parcelable {

    @SuppressWarnings("unused")
    private static final String TAG = Recipe.class.getSimpleName();

    // JSON field string ids
    private static final String RECIPE_ID = "id";
    private static final String NAME = "name";
    private static final String INGREDIENTS = "ingredients";
    private static final String STEPS = "steps";
    private static final String SERVINGS = "servings";
    private static final String IMAGE = "image";

    // Members
    private int mId;
    private String mName;
    private ArrayList<Ingredient> mIngredients;
    private ArrayList<Step> mSteps;
    private int mServings;
    private String mImage;

    // Getters & setters
    public int getId() { return mId; }
    public void setId(int id) { this.mId = id; }

    public String getName() { return mName; }
    public void setName(String name) { this.mName = name; }

    public ArrayList<Ingredient> getIngredients() { return mIngredients; }
    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.mIngredients = ingredients;
    }

    public ArrayList<Step> getSteps() { return mSteps; }
    public void setSteps(ArrayList<Step> steps) { this.mSteps = steps; }

    public int getServings() { return mServings; }
    public void setServings(int servings) { this.mServings = servings; }

    public String getImage() { return mImage; }
    public void setImage(String image) { this.mImage = image; }

    // Default constructor
    private Recipe() {
        mId = 0;
        mName = null;
        mIngredients = new ArrayList<Ingredient>();
        mSteps = new ArrayList<Step>();
        mServings = 0;
        mImage = null;
    }

    // Constructor converting JSON object to this class instance
    private static Recipe fromJson(JSONObject jsonObject) throws JSONException {
        Recipe r = new Recipe();

        if (jsonObject.has(RECIPE_ID)) { r.mId = jsonObject.getInt(RECIPE_ID); }

        if (jsonObject.has(NAME)) { r.mName = jsonObject.getString(NAME); }

        if (jsonObject.has(INGREDIENTS)) {
            r.mIngredients = Ingredient.fromJson(jsonObject.getJSONArray(INGREDIENTS));
        }

        if (jsonObject.has(STEPS)) {
            r.mSteps = Step.fromJson(jsonObject.getJSONArray(STEPS));
        }

        if (jsonObject.has(SERVINGS)) { r.mServings = jsonObject.getInt(SERVINGS); }

        if (jsonObject.has(IMAGE)) { r.mImage = jsonObject.getString(IMAGE); }

        return r;
    }

    // Factory method for converting JSON object array to a list of object instances
    public static ArrayList<Recipe> fromJson(JSONArray jsonArray) throws JSONException {
        JSONObject recipeJson;

        int objectCount = jsonArray.length();
        ArrayList<Recipe> recipes = new ArrayList<>(objectCount);

        for (int i = 0; i < objectCount; i++) {
            recipeJson = jsonArray.getJSONObject(i);
            Recipe recipe = Recipe.fromJson(recipeJson);
            if (recipe != null) { recipes.add(recipe); }
        }
        return recipes;
    }

    // Parcelable implementation
    @Override
    public int describeContents() {
        // No file descriptors in class members, returning 0
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mId);
        parcel.writeString(mName);
        parcel.writeTypedList(mIngredients);
        parcel.writeTypedList(mSteps);
        parcel.writeInt(mServings);
        parcel.writeString(mImage);
    }

    // Constructor from incoming Parcel
    private Recipe(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        if (mIngredients == null) { mIngredients = new ArrayList<>(); }
        in.readTypedList(mIngredients, Ingredient.CREATOR);
        if (mSteps == null) { mSteps = new ArrayList<>(); }
        in.readTypedList(mSteps, Step.CREATOR);
        mServings = in.readInt();
        mImage = in.readString();
    }

    // Parcelable creator
    static final Parcelable.Creator<Recipe> CREATOR
            = new Parcelable.Creator<Recipe>() {

        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };


    public static class Ingredient implements Parcelable {

        // JSON field string ids
        private static final String QUANTITY = "quantity";
        private static final String MEASURE = "measure";
        private static final String INGREDIENT = "ingredient";

        // Members
        private int mQuantity;
        private String mMeasure;
        private String mIngredient;

        // Getters & setters
        public int getQuantity() { return mQuantity; }
        public void setQuantity(int quantity) { this.mQuantity = quantity; }

        public String getMeasure() { return mMeasure; }
        public void setMeasure(String measure) { this.mMeasure = measure; }

        public String getIngredient() { return mIngredient; }
        public void setIngredient(String ingredient) { this.mIngredient = ingredient; }

        // Default constructor
        Ingredient() {}

        // Constructor converting JSON object to this class instance
        private static Ingredient fromJson(JSONObject jsonObject) throws JSONException {
            Ingredient i = new Ingredient();

            if (jsonObject.has(QUANTITY)) { i.mQuantity = jsonObject.getInt(QUANTITY); }

            if (jsonObject.has(MEASURE)) { i.mMeasure = jsonObject.getString(MEASURE); }

            if (jsonObject.has(INGREDIENT)) { i.mIngredient = jsonObject.getString(INGREDIENT); }

            return i;
        }

        // Factory method for converting JSON object array to a list of object instances
        private static ArrayList<Ingredient> fromJson(JSONArray jsonArray) throws JSONException {
            JSONObject ingredientJson;

            int objectCount = jsonArray.length();
            ArrayList<Ingredient> ingredients = new ArrayList<>(objectCount);

            for (int i = 0; i < objectCount; i++) {
                ingredientJson = jsonArray.getJSONObject(i);
                Ingredient ingredient = Ingredient.fromJson(ingredientJson);
                if (ingredient != null) { ingredients.add(ingredient); }
            }
            return ingredients;
        }

        // Parcelable implementation
        @Override
        public int describeContents() {
            // No file descriptors in class members, returning 0
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeInt(mQuantity);
            parcel.writeString(mMeasure);
            parcel.writeString(mIngredient);
        }

        // Constructor from incoming Parcel
        private Ingredient(Parcel in) {
            mQuantity = in.readInt();
            mMeasure = in.readString();
            mIngredient = in.readString();
        }

        // Parcelable creator
        static final Parcelable.Creator<Ingredient> CREATOR
                = new Parcelable.Creator<Ingredient>() {

            public Ingredient createFromParcel(Parcel in) {
                return new Ingredient(in);
            }

            public Ingredient[] newArray(int size) {
                return new Ingredient[size];
            }
        };
    }

    public static class Step implements Parcelable {

        // JSON field string ids
        private static final String STEP_ID = "id";
        private static final String SHORT_DESCRIPTION = "shortDescription";
        private static final String DESCRIPTION = "description";
        private static final String VIDEO_URL = "videoURL";
        private static final String THUMBNAIL_URL = "thumbnailURL";

        // Members
        private int mId;
        private String mShortDescription;
        private String mDescription;
        private String mVideoUrl;
        private String mThumbnailUrl;

        // Getters & setters
        public int getId() { return mId; }
        public void setId(int id) { this.mId = id; }

        public String getShortDescription() { return mShortDescription; }
        public void setShortDescription(String shortDescription) {
            this.mShortDescription = shortDescription;
        }

        public String getDescription() { return mDescription; }
        public void setDescription(String description) {this.mDescription = description; }

        public String getVideoUrl() { return mVideoUrl; }
        public void setVideoUrl(String videoUrl) { this.mVideoUrl = videoUrl; }

        public String getThumbnailUrl() { return mThumbnailUrl; }
        public void setThumbnailUrl(String thumbnailUrl) { this.mThumbnailUrl = thumbnailUrl; }

        // Default constructor
        Step() {}

        // Constructor converting JSON object to this class instance
        private static Step fromJson(JSONObject jsonObject) throws JSONException {
            Step s = new Step();

            if (jsonObject.has(STEP_ID)) { s.mId = jsonObject.getInt(STEP_ID); }

            if (jsonObject.has(SHORT_DESCRIPTION)) {
                s.mShortDescription = jsonObject.getString(SHORT_DESCRIPTION);
            }

            if (jsonObject.has(DESCRIPTION)) { s.mDescription = jsonObject.getString(DESCRIPTION); }

            if (jsonObject.has(VIDEO_URL)) { s.mVideoUrl = jsonObject.getString(VIDEO_URL); }

            if (jsonObject.has(THUMBNAIL_URL)) {
                s.mThumbnailUrl = jsonObject.getString(THUMBNAIL_URL);
            }

            return s;
        }

        // Factory method for converting JSON object array to a list of object instances
        public static ArrayList<Step> fromJson(JSONArray jsonArray) throws JSONException {
            JSONObject stepJson;

            int objectCount = jsonArray.length();
            ArrayList<Step> steps = new ArrayList<>(objectCount);

            for (int i = 0; i < objectCount; i++) {
                stepJson = jsonArray.getJSONObject(i);
                Step step = Step.fromJson(stepJson);
                if (step != null) { steps.add(step); }
            }
            return steps;
        }

        // Parcelable implementation
        @Override
        public int describeContents() {
            // No file descriptors in class members, returning 0
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeInt(mId);
            parcel.writeString(mShortDescription);
            parcel.writeString(mDescription);
            parcel.writeString(mVideoUrl);
            parcel.writeString(mThumbnailUrl);
        }

        // Constructor from incoming Parcel
        private Step(Parcel in) {
            mId = in.readInt();
            mShortDescription = in.readString();
            mDescription = in.readString();
            mVideoUrl = in.readString();
            mThumbnailUrl = in.readString();
        }

        // Parcelable creator
        static final Parcelable.Creator<Step> CREATOR
                = new Parcelable.Creator<Step>() {

            public Step createFromParcel(Parcel in) {
                return new Step(in);
            }

            public Step[] newArray(int size) {
                return new Step[size];
            }
        };
    }

}
