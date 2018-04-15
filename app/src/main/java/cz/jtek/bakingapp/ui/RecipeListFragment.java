package cz.jtek.bakingapp.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import cz.jtek.bakingapp.R;
import cz.jtek.bakingapp.model.Recipe;

public class RecipeListFragment extends Fragment
        implements RecipeListAdapter.RecipeGridOnClickHandler
{

    private Context mContext;
    private ArrayList<Recipe> mRecipeList;


    // Instance State bundle keys
    private static final String KEY_RECIPE_LIST = "recipe-list";

    public RecipeListFragment() {

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

        // Get a reference to the GridView in the fragment_recipe_list xml layout file
        GridView gridView = rootView.findViewById(R.id.gv_recipe_grid);

        // Create the adapter
        RecipeListAdapter mAdapter = new RecipeListAdapter(mContext,  );

        // Set the adapter on the GridView
        gridView.setAdapter(mAdapter);

        // Set a click listener on the gridView and trigger the callback onImageSelected when an item is clicked
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Trigger the callback method and pass in the position that was clicked
                mCallback.onImageSelected(position);
            }
        });

        // Return the root view
        return rootView;

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onClick(int itemId) {

    }
}
