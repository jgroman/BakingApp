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

package cz.jtek.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import cz.jtek.bakingapp.R;
import cz.jtek.bakingapp.model.Recipe;
import cz.jtek.bakingapp.model.UdacityApi;
import cz.jtek.bakingapp.utils.MockDataUtils;
import cz.jtek.bakingapp.utils.NetworkUtils;
import cz.jtek.bakingapp.utils.NetworkUtils.AsyncTaskResult;

/**
 * This activity displays complete list of available recipes using RecipeListFragment
 */
public class MainActivity extends AppCompatActivity
        implements RecipeListFragment.OnRecipeClickListener {

    @SuppressWarnings("unused")
    private static final String TAG = MainActivity.class.getSimpleName();

    private Context mContext;
    private ArrayList<Recipe> mRecipeList;

    private boolean mIsTabletLayout;
    
    // AsyncTaskLoader
    private static final int LOADER_ID_RECIPE_LIST = 0;

    // Arguments bundle keys
    public static final String BUNDLE_RECIPE_LIST = "recipe-list";

    // Instance state bundle keys
    private static final String KEY_RECIPE_LIST = "recipe-list";

    // Extras keys
    static final String EXTRA_RECIPE = "recipe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        
        // Detecting tablet layout based on value from dimens.xml
        mIsTabletLayout = getResources().getBoolean(R.bool.is_tablet_layout);

        if (savedInstanceState != null) {
            // Retrieving recipe list
            mRecipeList = savedInstanceState.getParcelableArrayList(KEY_RECIPE_LIST);
        }
        else {

            // Using loader to obtain recipe list
            if (NetworkUtils.isNetworkAvailable(this)) {
                // Initialize recipe list loader
                getSupportLoaderManager().initLoader(LOADER_ID_RECIPE_LIST, null, recipeListLoaderListener);
            }
            else {
                // Network not available, show error message
                Log.d(TAG, "onCreate: Network not available");
            }

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Store recipe list
        outState.putParcelableArrayList(KEY_RECIPE_LIST, mRecipeList);

        // Saving state in superclass
        super.onSaveInstanceState(outState);
    }

    /**
     * Loader callbacks for recipe list loader
     */
    private LoaderManager.LoaderCallbacks<AsyncTaskResult<ArrayList<Recipe>>> recipeListLoaderListener =
            new LoaderManager.LoaderCallbacks<AsyncTaskResult<ArrayList<Recipe>>>() {

                @NonNull
                @Override
                public Loader<AsyncTaskResult<ArrayList<Recipe>>> onCreateLoader(int id, @Nullable Bundle args) {
                    //mLoadingIndicator.setVisibility(View.VISIBLE);
                    return new RecipeListLoader(mContext, args);
                }

                @Override
                public void onLoadFinished(@NonNull Loader<AsyncTaskResult<ArrayList<Recipe>>> loader,
                                           AsyncTaskResult<ArrayList<Recipe>> data) {

                    //mLoadingIndicator.setVisibility(View.INVISIBLE);

                    if (data.hasException()) {
                        // There was an error during data loading
                        Exception ex = data.getException();
                        //showErrorMessage(getResources().getString(R.string.error_msg_no_data));
                    }
                    else {
                        // Valid results received
                        mRecipeList = data.getResult();

                        // RecipeListFragment receives complete recipe list as an argument
                        Bundle fragmentBundle = new Bundle();
                        fragmentBundle.putParcelableArrayList(BUNDLE_RECIPE_LIST, mRecipeList);

                        RecipeListFragment recipeListFragment = new RecipeListFragment();
                        recipeListFragment.setArguments(fragmentBundle);

                        // Add recipe list fragment to main activity fragment container
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.recipe_list_fragment_container, recipeListFragment)
                                .commit();

                        // Destroy this loader, otherwise is gets called again during onResume
                        getSupportLoaderManager().destroyLoader(LOADER_ID_RECIPE_LIST);
                    }
                }

                @Override
                public void onLoaderReset(@NonNull Loader<AsyncTaskResult<ArrayList<Recipe>>> loader) {
                    // Not used
                }
            };

    /**
     * Recipe list item click callback
     * Bundles clicked Recipe into extras and starts RecipeActivity
     *
     * @param position Clicked Recipe item position in recipe list
     */
    @Override
    public void onRecipeSelected(int position) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(EXTRA_RECIPE, mRecipeList.get(position));
        startActivity(intent);
    }

    /**
     * Recipe list async task loader implementation
     */
    public static class RecipeListLoader
            extends AsyncTaskLoader<AsyncTaskResult<ArrayList<Recipe>>> {

        final PackageManager mPackageManager;
        AsyncTaskResult<ArrayList<Recipe>> mResult;
        final Bundle mArgs;

        private RecipeListLoader(Context context, Bundle args) {
            super(context);
            mPackageManager = getContext().getPackageManager();
            mArgs = args;
        }

        @Override
        protected void onStartLoading() {
            if (mResult != null && (mResult.hasResult() || mResult.hasException())) {
                // If there are already data available, deliver them
                deliverResult(mResult);
            } else {
                // Start loader
                forceLoad();
            }
        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override
        protected void onStopLoading() {
            cancelLoad();
        }

        /**
         * This AsyncTaskLoader method will load and parse the recipe list JSON in the background
         *
         * @return UdacityJsonResult containing either ArrayList of Recipe objects or an exception
         */
        @Override
        public AsyncTaskResult<ArrayList<Recipe>> loadInBackground() {
            try {
                // Example mock request used for debugging to avoid sending network queries
                //String jsonRecipes = MockDataUtils.getMockJson(getContext(), "baking");

                // Load recipe list JSON
                URL recipesUrl = UdacityApi.buildRecipesUrl();
                String jsonRecipes = NetworkUtils.getResponseFromHttpUrl(recipesUrl);

                UdacityApi.UdacityJsonResult<ArrayList<Recipe>> recipeResult =
                        UdacityApi.getRecipesFromJson(jsonRecipes);

                mResult = new AsyncTaskResult<>(recipeResult.getResult(), recipeResult.getException());
            } catch (IOException iex) {
                Log.e(TAG, String.format("IOException when fetching API data: %s", iex.getMessage()));
                mResult = new AsyncTaskResult<>(null, iex);
            }
            return mResult;
        }
    }
}
