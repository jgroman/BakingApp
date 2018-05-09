package cz.jtek.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;

import cz.jtek.bakingapp.R;
import cz.jtek.bakingapp.provider.RecipeContract;

import static cz.jtek.bakingapp.provider.RecipeContract.BASE_CONTENT_URI;
import static cz.jtek.bakingapp.provider.RecipeContract.PATH_RECIPE;


/**
 * Implementation of App Widget functionality.
 */
public class BakingAppWidgetProvider extends AppWidgetProvider {

    @SuppressWarnings("unused")
    private static final String TAG = BakingAppWidgetProvider.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String widgetTitle = context.getResources().getString(R.string.widget_title_ingredients);

        // Obtain currently selected recipe name and servings
        Cursor cursor;
        Uri RECIPE_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE).build();

        cursor = context.getContentResolver().query(RECIPE_URI,
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            int indexName = cursor.getColumnIndex(RecipeContract.RecipeEntry.COL_NAME);
            int indexServings = cursor.getColumnIndex(RecipeContract.RecipeEntry.COL_SERVINGS);

            String name = cursor.getString(indexName);
            int servings = cursor.getInt(indexServings);

            if (name != null && name.length() > 0) {
                widgetTitle = name + " " + context.getResources().getString(R.string.widget_title_for) + " " + Integer.toString(servings);
            }

            cursor.close();
        }

        RemoteViews rvs = new RemoteViews(context.getPackageName(), R.layout.widget_ingredient_list);
        // Set widget title
        rvs.setTextViewText(R.id.tv_widget_ingredients_title, widgetTitle);

        Intent intent = new Intent(context, IngredientListService.class);
        // Set ingredient list adapter
        rvs.setRemoteAdapter(R.id.lv_widget_ingredients, intent);

        appWidgetManager.updateAppWidget(appWidgetId, rvs);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

