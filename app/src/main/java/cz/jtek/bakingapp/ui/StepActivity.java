package cz.jtek.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import cz.jtek.bakingapp.R;
import cz.jtek.bakingapp.model.Recipe.Step;

/**
 * This activity is used only when using phone layout. It displays single step detailed view
 * using StepFragment.
 */
public class StepActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    private static final String TAG = StepActivity.class.getSimpleName();

    private ArrayList<Step> mSteps;
    private int mCurrentStepId;

    // Fragment bundle keys
    public static final String BUNDLE_STEP = "step";

    // Instance state bundle keys
    private static final String KEY_STEP_LIST = "step-list";
    private static final String KEY_STEP_ID = "step-id";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        if (savedInstanceState == null) {
            Intent startingIntent = getIntent();
            if (startingIntent == null) { return; }

            // Retrieve intent extras
            if (startingIntent.hasExtra(RecipeActivity.EXTRA_STEPS)) {
                mSteps = startingIntent.getParcelableArrayListExtra(RecipeActivity.EXTRA_STEPS);
            }

            if (startingIntent.hasExtra(RecipeActivity.EXTRA_STEP_ID)) {
                mCurrentStepId = startingIntent.getIntExtra(RecipeActivity.EXTRA_STEP_ID, 0);
            }
        }
        else {
            // Retrieve list of steps and selected step id from savedInstanceState
            mSteps = savedInstanceState.getParcelableArrayList(KEY_STEP_LIST);
            mCurrentStepId = savedInstanceState.getInt(KEY_STEP_ID);
        }

        // Set title

        // Create step fragment
        StepFragment stepFragment = new StepFragment();
        // StepFragment receives single Recipe.Step object as an argument
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putParcelable(BUNDLE_STEP, mSteps.get(mCurrentStepId));

        stepFragment.setArguments(fragmentBundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.step_fragment_container, stepFragment)
                .commit();

        // Set navigation click listeners
        TextView navPrevious = findViewById(R.id.tv_step_navigation_prev);
        navPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to previous step
                if (mCurrentStepId > 0) {
                    mCurrentStepId--;
                    replaceStepFragment(mCurrentStepId);
                }
            }
        });

        TextView navNext = findViewById(R.id.tv_step_navigation_next);
        navNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to next step
                if (mCurrentStepId < mSteps.size() - 1) {
                    mCurrentStepId++;
                    replaceStepFragment(mCurrentStepId);
                }
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Store step list
        outState.putParcelableArrayList(KEY_STEP_LIST, mSteps);

        // Store current step id
        outState.putInt(KEY_STEP_ID, mCurrentStepId);

        super.onSaveInstanceState(outState);
    }

    private void replaceStepFragment(int stepId) {
        // Create step fragment
        StepFragment stepFragment = new StepFragment();
        // StepFragment receives single Recipe.Step object as an argument
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putParcelable(BUNDLE_STEP, mSteps.get(stepId));

        stepFragment.setArguments(fragmentBundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.step_fragment_container, stepFragment)
                .commit();
    }
}
