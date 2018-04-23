package cz.jtek.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import cz.jtek.bakingapp.R;

public class StepActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    private static final String TAG = StepActivity.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        if (savedInstanceState == null) {
            Intent startingIntent = getIntent();
            if (startingIntent == null) { return; }

            // Create step fragment
            RecipeOverviewFragment stepFragment = new RecipeOverviewFragment();
            stepFragment.setArguments(startingIntent.getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.step_fragment_container, stepFragment)
                    .commit();

        }
    }
}
