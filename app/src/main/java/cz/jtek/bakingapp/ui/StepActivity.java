package cz.jtek.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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

    private View mPrevNavButton;
    private View mNextNavButton;

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

        // Set navigation buttons' click listeners
        mPrevNavButton = findViewById(R.id.btn_step_nav_previous);
        if (mPrevNavButton != null) {
            // On first recipe step hide button
            if (mCurrentStepId == 0) {
                mPrevNavButton.setVisibility(View.INVISIBLE);
            }

            mPrevNavButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Navigate to previous step
                    if (mCurrentStepId > 0) {
                        mCurrentStepId--;
                        replaceStepFragment(mCurrentStepId);
                    }

                    // Make sure Next button is visible
                    mNextNavButton.setVisibility(View.VISIBLE);

                    // On first recipe step hide button
                    if (mCurrentStepId == 0) {
                        mPrevNavButton.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }

        mNextNavButton = findViewById(R.id.btn_step_nav_next);
        if (mNextNavButton != null) {
            // On last recipe step hide button
            if (mCurrentStepId == mSteps.size() - 1) {
                mNextNavButton.setVisibility(View.INVISIBLE);
            }

            mNextNavButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Navigate to next step
                    if (mCurrentStepId < mSteps.size() - 1) {
                        mCurrentStepId++;
                        replaceStepFragment(mCurrentStepId);
                    }

                    // Make sure Prev button is visible
                    mPrevNavButton.setVisibility(View.VISIBLE);

                    // On last recipe step hide button
                    if (mCurrentStepId == mSteps.size() - 1) {
                        mNextNavButton.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }
}
