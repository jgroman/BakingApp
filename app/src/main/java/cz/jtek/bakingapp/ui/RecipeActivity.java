package cz.jtek.bakingapp.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import cz.jtek.bakingapp.R;
import cz.jtek.bakingapp.model.Recipe;
import cz.jtek.bakingapp.provider.RecipeContract;

/**
 * This activity displays recipe overview - ingredient list + step list using RecipeOverviewFragment
 * When using tablet layout there is also single step detail view created by StepFragment
 */
public class RecipeActivity extends AppCompatActivity
        implements RecipeOverviewFragment.OnRecipeStepClickListener {

    @SuppressWarnings("unused")
    private static final String TAG = RecipeActivity.class.getSimpleName();

    private boolean mIsTabletLayout;

    private Recipe mRecipe;
    private int mCurrentStepId;

    private UpdateIngredientProviderTask mUpdateTask;

    // Extras keys
    static final String EXTRA_NAME = "name";
    static final String EXTRA_STEPS = "steps";
    static final String EXTRA_STEP_ID = "step-id";

    // Instance state bundle keys
    private static final String KEY_RECIPE = "recipe";
    private static final String KEY_STEP_ID = "step-id";

    // Fragment bundle keys
    public static final String BUNDLE_RECIPE = "recipe";
    public static final String BUNDLE_STEP = "step";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // Detecting tablet layout based on value from dimens.xml
        mIsTabletLayout = getResources().getBoolean(R.bool.is_tablet_layout);

        if (savedInstanceState == null) {
            Intent startingIntent = getIntent();
            if (startingIntent == null) { return; }

            // Retrieve intent extras
            if (startingIntent.hasExtra(MainActivity.EXTRA_RECIPE)) {
                mRecipe = startingIntent.getParcelableExtra(MainActivity.EXTRA_RECIPE);
            }

            // Select first recipe step
            mCurrentStepId = 0;

            if (mIsTabletLayout) {
                // Tablet layout
                // Create recipe overview fragment
                RecipeOverviewFragment overviewFragment = new RecipeOverviewFragment();
                // RecipeOverviewFragment receives single Recipe object as an argument
                Bundle overviewFragmentBundle = new Bundle();
                overviewFragmentBundle.putParcelable(BUNDLE_RECIPE, mRecipe);

                overviewFragment.setArguments(overviewFragmentBundle);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.tablet_overview_fragment_container, overviewFragment)
                        .commit();

                // Create recipe step fragment
                // Create step fragment
                StepFragment stepFragment = new StepFragment();
                // StepFragment receives single Recipe.Step object as an argument
                Bundle stepFragmentBundle = new Bundle();
                stepFragmentBundle.putParcelable(BUNDLE_STEP, mRecipe.getSteps().get(mCurrentStepId));

                stepFragment.setArguments(stepFragmentBundle);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.tablet_step_fragment_container, stepFragment)
                        .commit();
            }
            else {
                // Phone layout
                // Create recipe overview fragment
                RecipeOverviewFragment overviewFragment = new RecipeOverviewFragment();
                // RecipeOverviewFragment receives single Recipe object as an argument
                Bundle fragmentBundle = new Bundle();
                fragmentBundle.putParcelable(BUNDLE_RECIPE, mRecipe);

                overviewFragment.setArguments(fragmentBundle);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.recipe_overview_fragment_container, overviewFragment)
                        .commit();
            }

            // Store recipe ingredients into database in async task
            mUpdateTask = new UpdateIngredientProviderTask(this);
            mUpdateTask.execute(mRecipe);

        }
        else {
            // Retrieve state from savedInstanceState
            mRecipe = savedInstanceState.getParcelable(KEY_RECIPE);
            mCurrentStepId = savedInstanceState.getInt(KEY_STEP_ID);
        }

        // Set title
        if (mRecipe != null) {
            setTitle(mRecipe.getName());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Prevent memory leaks when laving running async task
        if (mUpdateTask != null && mUpdateTask.getStatus() == AsyncTask.Status.RUNNING) {
            mUpdateTask.cancel(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Store recipe
        outState.putParcelable(KEY_RECIPE, mRecipe);

        // Store currently selected step
        outState.putInt(KEY_STEP_ID, mCurrentStepId);
    }

    /**
     * Recipe step item click callback
     * Bundles all recipe steps and selected step id into extras and starts StepActivity on phone.
     * Replaces recipe step fragment on tablet.
     *
     * @param position Clicked step id
     */
    @Override
    public void onRecipeStepSelected(int position) {

        if (mIsTabletLayout) {
            // In tablet layout replace currently displayed step fragment
            mCurrentStepId = position;

            // Create step fragment
            StepFragment stepFragment = new StepFragment();
            // StepFragment receives single Recipe.Step object as an argument
            Bundle fragmentBundle = new Bundle();
            fragmentBundle.putParcelable(BUNDLE_STEP, mRecipe.getSteps().get(mCurrentStepId));

            stepFragment.setArguments(fragmentBundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.tablet_step_fragment_container, stepFragment)
                    .commit();
        }
        else {
            // When using phone layout, start StepActivity
            Intent intent = new Intent(this, StepActivity.class);
            intent.putExtra(EXTRA_NAME, mRecipe.getName());
            intent.putExtra(EXTRA_STEPS, mRecipe.getSteps());
            intent.putExtra(EXTRA_STEP_ID, position);
            startActivity(intent);
        }
    }

    private class UpdateIngredientProviderTask extends AsyncTask<Recipe, Void, Void> {

        private Context mContext;

        UpdateIngredientProviderTask(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(Recipe... recipes) {

            ArrayList<Recipe.Ingredient> ingredients = recipes[0].getIngredients();

            int ingredientCount = ingredients.size();
            ContentValues[] valuesArray = new ContentValues[ingredientCount];

            for (int i = 0; i < ingredientCount; i++) {
                ContentValues values = new ContentValues();

                values.put(RecipeContract.IngredientEntry.COL_QUANTITY, ingredients.get(i).getQuantity());
                values.put(RecipeContract.IngredientEntry.COL_MEASURE, ingredients.get(i).getMeasure());
                values.put(RecipeContract.IngredientEntry.COL_INGREDIENT, ingredients.get(i).getIngredient());

                valuesArray[i] = values;
            }

            // Delete all previous ingredients
            mContext.getContentResolver().delete(RecipeContract.IngredientEntry.CONTENT_URI, null, null);

            // Insert new ingredients into content provider
            mContext.getContentResolver().bulkInsert(RecipeContract.IngredientEntry.CONTENT_URI, valuesArray);

            return null;
        }
    }
}
