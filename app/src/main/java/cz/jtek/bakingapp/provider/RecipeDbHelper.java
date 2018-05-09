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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cz.jtek.bakingapp.provider.RecipeContract.RecipeEntry;
import cz.jtek.bakingapp.provider.RecipeContract.IngredientEntry;

public class RecipeDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "baking_app.db";

    // Increment db version if changing db schema
    private static final int DB_VERSION = 1;

    RecipeDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create recipe table
        final String SQL_CREATE_RECIPE_TABLE =
                "CREATE TABLE " + RecipeEntry.TABLE_NAME + " (" +
                        RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        RecipeEntry.COL_RECIPE_ID + " INTEGER NOT NULL, " +
                        RecipeEntry.COL_NAME + " TEXT NOT NULL, " +
                        RecipeEntry.COL_SERVINGS + " INTEGER NOT NULL, " +
                        RecipeEntry.COL_IMAGE + " TEXT NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_RECIPE_TABLE);

        // Create ingredients table
        // It is used to hold currently selected recipe ingredients for app widget list view
        final String SQL_CREATE_INGREDIENTS_TABLE =
                "CREATE TABLE " + IngredientEntry.TABLE_NAME + " (" +
                IngredientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                IngredientEntry.COL_QUANTITY + " REAL NOT NULL, " +
                IngredientEntry.COL_MEASURE + " TEXT NOT NULL, " +
                IngredientEntry.COL_INGREDIENT + " TEXT NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_INGREDIENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // On upgrade just recreate tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RecipeEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + IngredientEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
