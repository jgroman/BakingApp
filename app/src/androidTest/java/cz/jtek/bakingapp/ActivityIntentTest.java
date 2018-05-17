package cz.jtek.bakingapp;

import android.app.Activity;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.v7.widget.RecyclerView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import cz.jtek.bakingapp.ui.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static org.hamcrest.CoreMatchers.allOf;

@RunWith(AndroidJUnit4.class)
public class ActivityIntentTest {

    // MainActivity extra keys
    private static final String EXTRA_RECIPE = "recipe";

    // RecipeActivity extra keys
    private static final String EXTRA_NAME = "name";
    private static final String EXTRA_STEPS = "steps";
    private static final String EXTRA_STEP_ID = "step-id";

    /**
     * This method obtains current activity instance.
     *
     * @return Current activity instance
     */
    private Activity getActivityInstance() {

        final Activity[] currentActivity = {null};

        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection<Activity> resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()) {
                    currentActivity[0] = resumedActivities.iterator().next();
                }
            }
        });

        return currentActivity[0];
    }

    @Rule
    public IntentsTestRule<MainActivity> mIntentsRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void clickRecipeItem_RunsRecipeActivityIntent() {

        onView(withId(R.id.rv_recipe_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        intended(
                hasExtraWithKey(EXTRA_RECIPE)
        );

    }

    @Test
    public void clickthroughToRecipeDetail_RunsStepActivityIntent() {

        // MainActivity: Click first recycler view item
        onView(withId(R.id.rv_recipe_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        Activity activity = getActivityInstance();
        RecyclerView rv = activity.findViewById(R.id.rv_recipe_overview);
        int recyclerViewItemCount = rv.getAdapter().getItemCount();

        boolean isTabletLayout = activity.getResources().getBoolean(R.bool.is_tablet_layout);

        // Intent is used only in phone layout processing
        if (!isTabletLayout) {
            // RecipeActivity: Click last recycler view item
            onView(withId(R.id.rv_recipe_overview))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(recyclerViewItemCount - 1, click()));

            intended(allOf(
                    hasExtraWithKey(EXTRA_NAME),
                    hasExtraWithKey(EXTRA_STEPS),
                    hasExtraWithKey(EXTRA_STEP_ID)
            ));
        }
    }

}
