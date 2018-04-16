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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import cz.jtek.bakingapp.R;
import cz.jtek.bakingapp.model.Recipe;

public class RecipeListFragment extends Fragment
        implements RecipeListAdapter.RecipeListOnClickListener {

    @SuppressWarnings("unused")
    private static final String TAG = RecipeListFragment.class.getSimpleName();

    // Custom OnRecipeClickListener interface, must be implemented by container activity
    public interface OnRecipeClickListener {
        void onRecipeSelected(int position);
    }

    // This is a callback to onRecipeSelected in container activity
    OnRecipeClickListener mRecipeClickListenerCallback;

    private Context mContext;
    private boolean mIsTabletLayout;
    private ArrayList<Recipe> mRecipeList;
    private RecyclerView mRecipeListRecyclerView;

    private GridLayoutManager mLayoutManager;
    private static final int COLUMNS_LAYOUT_PHONE = 1;
    private static final int COLUMNS_LAYOUT_TABLET = 3;

    // Instance State bundle keys
    private static final String KEY_RECIPE_LIST = "recipe-list";

    public RecipeListFragment() {}

    // Overriding onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mRecipeClickListenerCallback = (OnRecipeClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnRecipeClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Activity activity = getActivity();
        if (null == activity) { return null; }

        mContext = activity.getApplicationContext();

        final View rootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        if (savedInstanceState != null) {
            // Restoring recipe list from saved instance state
            mRecipeList = savedInstanceState.getParcelableArrayList(KEY_RECIPE_LIST);
        }
        else {
            // Get recipe list from passed arguments
            Bundle args = getArguments();
            if (args != null && args.containsKey(MainActivity.BUNDLE_RECIPE_LIST)) {
                mRecipeList = args.getParcelableArrayList(MainActivity.BUNDLE_RECIPE_LIST);
            }
        }

        // Detecting tablet layout based on value from dimens.xml
        mIsTabletLayout = getResources().getBoolean(R.bool.is_tablet_layout);

        int layoutColumns = (mIsTabletLayout) ? COLUMNS_LAYOUT_TABLET : COLUMNS_LAYOUT_PHONE;

        mRecipeListRecyclerView = rootView.findViewById(R.id.rv_recipe_list);

        mLayoutManager = new GridLayoutManager(mContext, layoutColumns);
        mRecipeListRecyclerView.setLayoutManager(mLayoutManager);
        mRecipeListRecyclerView.setHasFixedSize(true);

        // Create the adapter
        RecipeListAdapter mRecipeListAdapter = new RecipeListAdapter(mContext, mRecipeList, this );
        mRecipeListRecyclerView.setAdapter(mRecipeListAdapter);

        // Return the root view
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // Store recipe list
        outState.putParcelableArrayList(KEY_RECIPE_LIST, mRecipeList);

        super.onSaveInstanceState(outState);
    }

    /**
     * Recipe list item click listener
     *
     * @param position Id of the clicked list item
     */
    @Override
    public void onClick(int position) {
        mRecipeClickListenerCallback.onRecipeSelected(position);
    }
}
