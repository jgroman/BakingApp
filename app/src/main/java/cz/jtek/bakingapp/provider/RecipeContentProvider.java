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

package cz.jtek.bakingapp.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cz.jtek.bakingapp.provider.RecipeContract.RecipeEntry;
import cz.jtek.bakingapp.provider.RecipeContract.IngredientEntry;

public class RecipeContentProvider extends ContentProvider {

    @SuppressWarnings("unused")
    private static final String TAG = RecipeContentProvider.class.getName();

    public static final int RECIPE = 100;
    public static final int INGREDIENTS = 200;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_RECIPE, RECIPE);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_INGREDIENTS, INGREDIENTS);

        return uriMatcher;
    }

    private RecipeDbHelper mRecipeDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mRecipeDbHelper = new RecipeDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Context context = getContext();

        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        Cursor cursor;
        final SQLiteDatabase db = mRecipeDbHelper.getReadableDatabase();

        switch (sUriMatcher.match(uri)) {
            case RECIPE:
                cursor = db.query(RecipeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case INGREDIENTS:
                cursor = db.query(IngredientEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(context.getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        Context context = getContext();

        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        final SQLiteDatabase db = mRecipeDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case INGREDIENTS:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(IngredientEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    context.getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        Context context = getContext();

        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        if (contentValues == null) {
            throw new IllegalArgumentException("ContentValues cannot be null");
        }

        final SQLiteDatabase db = mRecipeDbHelper.getWritableDatabase();
        long rowId;

        switch (sUriMatcher.match(uri)) {
            case RECIPE:
                rowId = db.insert(RecipeEntry.TABLE_NAME, null, contentValues);
                if (rowId > 0) {
                    context.getContentResolver().notifyChange(uri, null);
                    return ContentUris.withAppendedId(RecipeEntry.CONTENT_URI, rowId);
                }
                break;

            case INGREDIENTS:
                rowId = db.insert(IngredientEntry.TABLE_NAME, null, contentValues);
                if (rowId > 0) {
                    context.getContentResolver().notifyChange(uri, null);
                    return ContentUris.withAppendedId(IngredientEntry.CONTENT_URI, rowId);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return null;
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        Context context = getContext();

        if (context == null) {
            throw new NullPointerException("Context cannot be null");
        }

        // Users of the delete method will expect the number of rows deleted to be returned.
        int numRowsDeleted;

        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        if (null == selection) {
            selection = "1";
        }

        switch (sUriMatcher.match(uri)) {
            case RECIPE:
                numRowsDeleted = mRecipeDbHelper.getWritableDatabase().delete(
                        RecipeEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case INGREDIENTS:
                numRowsDeleted = mRecipeDbHelper.getWritableDatabase().delete(
                        IngredientEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // If we actually deleted any rows, notify that a change has occurred to this URI
        if (numRowsDeleted != 0) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new RuntimeException("Update not implemented.");
    }
}
