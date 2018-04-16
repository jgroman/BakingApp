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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import cz.jtek.bakingapp.R;
import cz.jtek.bakingapp.model.Recipe;

public class RecipeOverviewFragment extends Fragment {

    @SuppressWarnings("unused")
    private static final String TAG = RecipeOverviewFragment.class.getSimpleName();

    private Context mContext;
    private Recipe mRecipe;

    private RecyclerView.LayoutManager mLayoutManager;

    // Instance State bundle keys
    private static final String KEY_RECIPE = "recipe";

    public RecipeOverviewFragment() {}

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
            // Restoring recipe from saved instance state
            mRecipe = savedInstanceState.getParcelable(KEY_RECIPE);
        }
        else {
            // Get recipe from passed arguments
            Bundle args = getArguments();
            if (args != null && args.containsKey(MainActivity.EXTRA_RECIPE)) {
                mRecipe = args.getParcelable(MainActivity.EXTRA_RECIPE);
            }
        }

        RecyclerView ingredientsRecyclerView = rootView.findViewById(R.id.rv_recipe_ingredients);
        ingredientsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(mContext);
        ingredientsRecyclerView.setLayoutManager(mLayoutManager);

        IngredientListAdapter adapter = new IngredientListAdapter(mRecipe.getIngredients());
        ingredientsRecyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // Store recipe
        outState.putParcelable(KEY_RECIPE, mRecipe);

        super.onSaveInstanceState(outState);
    }
}
