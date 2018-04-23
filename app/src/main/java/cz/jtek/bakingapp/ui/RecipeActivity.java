package cz.jtek.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cz.jtek.bakingapp.R;
import cz.jtek.bakingapp.model.Recipe;

public class RecipeActivity extends AppCompatActivity
        implements RecipeOverviewFragment.OnRecipeStepClickListener {

    @SuppressWarnings("unused")
    private static final String TAG = RecipeActivity.class.getSimpleName();

    private boolean mIsTabletLayout;

    private Recipe mRecipe;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // Detecting tablet layout based on value from dimens.xml
        mIsTabletLayout = getResources().getBoolean(R.bool.is_tablet_layout);

        if (savedInstanceState == null) {

            Intent startingIntent = getIntent();
            if (startingIntent == null) { return; }

            //if (startingIntent.hasExtra(MainActivity.EXTRA_RECIPE)) {
            //    Recipe recipe = startingIntent.getParcelableExtra(MainActivity.EXTRA_RECIPE);
            //}

            // Create recipe overview fragment
            RecipeOverviewFragment overviewFragment = new RecipeOverviewFragment();
            overviewFragment.setArguments(startingIntent.getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_overview_fragment_container, overviewFragment)
                    .commit();

            if (mIsTabletLayout) {
                // Create step detail fragment
            }

        }
    }

    @Override
    public void onRecipeStepSelected(int position) {
        Log.d(TAG, "onRecipeStepSelected: " + position);
    }
}
