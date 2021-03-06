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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import cz.jtek.bakingapp.R;
import cz.jtek.bakingapp.model.Recipe;

public class RecipeOverviewFragment extends Fragment
        implements RecipeOverviewAdapter.RecipeOverviewOnClickListener {

    @SuppressWarnings("unused")
    private static final String TAG = RecipeOverviewFragment.class.getSimpleName();

    // Custom click listener interface, must be implemented by container activity
    public interface OnRecipeStepClickListener {
        void onRecipeStepSelected(int position);
    }

    // This is a callback to onRecipeStepSelected in container activity
    OnRecipeStepClickListener mRecipeStepClickListenerCallback;

    private Context mContext;
    private Recipe mRecipe;
    private RecyclerView.LayoutManager mLayoutManager;
    private int mLastClickedStepPosition;

    // Instance State bundle keys
    private static final String KEY_RECIPE = "recipe";
    private static final String KEY_STEP_POSITION = "step-position";

    public RecipeOverviewFragment() {}

    // Overriding onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mRecipeStepClickListenerCallback = (OnRecipeStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnRecipeStepClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        Activity activity = getActivity();
        if (activity == null) { return null; }

        mContext = activity.getApplicationContext();

        final View rootView = inflater.inflate(R.layout.fragment_recipe_overview, container, false);

        if (savedInstanceState != null) {
            // Restoring saved instance state
            mRecipe = savedInstanceState.getParcelable(KEY_RECIPE);
            mLastClickedStepPosition = savedInstanceState.getInt(KEY_STEP_POSITION);
        }
        else {
            // Get recipe from passed arguments
            Bundle args = getArguments();
            if (args != null && args.containsKey(RecipeActivity.BUNDLE_RECIPE)) {
                mRecipe = args.getParcelable(RecipeActivity.BUNDLE_RECIPE);
            }

            // Reset last clicked step position
            mLastClickedStepPosition = 0;
        }

        // Detecting tablet layout based on value from dimens.xml
        Boolean isTabletLayout = getResources().getBoolean(R.bool.is_tablet_layout);

        // Recipe overview Recycler View
        RecyclerView overviewRecyclerView = rootView.findViewById(R.id.rv_recipe_overview);
        overviewRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(mContext);
        overviewRecyclerView.setLayoutManager(mLayoutManager);

        // Do not highlight clicked steps on phone layout
        RecipeOverviewAdapter overviewAdapter =
                new RecipeOverviewAdapter(mRecipe, this,
                        mLastClickedStepPosition, isTabletLayout);
        overviewRecyclerView.setAdapter(overviewAdapter);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // Store recipe
        outState.putParcelable(KEY_RECIPE, mRecipe);

        // Store last clicked step position
        outState.putInt(KEY_STEP_POSITION, mLastClickedStepPosition);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStepClick(int position) {
        mLastClickedStepPosition = position;
        mRecipeStepClickListenerCallback.onRecipeStepSelected(position);
    }
}
