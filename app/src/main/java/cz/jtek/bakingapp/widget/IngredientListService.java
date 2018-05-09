package cz.jtek.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.Locale;

import cz.jtek.bakingapp.R;
import cz.jtek.bakingapp.provider.RecipeContract;

import static cz.jtek.bakingapp.provider.RecipeContract.BASE_CONTENT_URI;
import static cz.jtek.bakingapp.provider.RecipeContract.PATH_INGREDIENTS;

public class IngredientListService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Cursor mCursor;

    ListRemoteViewsFactory(Context appContext) {
        mContext = appContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Uri INGREDIENTS_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();

        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = mContext.getContentResolver().query(INGREDIENTS_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (mCursor == null || mCursor.getCount() == 0) {
            return null;
        }

        if (position == AdapterView.INVALID_POSITION || !mCursor.moveToPosition(position)) {
            return null;
        }

        int indexQuantity = mCursor.getColumnIndex(RecipeContract.IngredientEntry.COL_QUANTITY);
        int indexMeasure = mCursor.getColumnIndex(RecipeContract.IngredientEntry.COL_MEASURE);
        int indexIngredient = mCursor.getColumnIndex(RecipeContract.IngredientEntry.COL_INGREDIENT);

        double quantity = mCursor.getDouble(indexQuantity);
        String measure = mCursor.getString(indexMeasure);
        String ingredient = mCursor.getString(indexIngredient);

        RemoteViews rvs = new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredient_list_item);

        String quantityString;
        // Do not display trailing zeros on "integer" quantity
        if (quantity % 1.0 != 0) {
            quantityString = String.format(Locale.getDefault(), "%s", quantity);
        }
        else {
            quantityString = String.format(Locale.getDefault(), "%.0f", quantity);
        }

        quantityString += " " + measure;

        rvs.setTextViewText(R.id.tv_widget_ingredient_quantity, quantityString);
        rvs.setTextViewText(R.id.tv_widget_ingredient_name, ingredient);

        return rvs;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return mCursor.moveToPosition(position) ? mCursor.getLong(0) : position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}