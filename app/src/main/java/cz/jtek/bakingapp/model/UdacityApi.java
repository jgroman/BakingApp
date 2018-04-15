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

package cz.jtek.bakingapp.model;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class UdacityApi {

    @SuppressWarnings("unused")
    private static final String TAG = UdacityApi.class.getSimpleName();


    private static final String API_SCHEME = "http";
    private static final String API_AUTHORITY = "go.udacity.com";
    private static final String API_PATH_RECIPES = "android-baking-app-json";

    public static URL buildRecipesUrl() {

        // Build Udacity Baking App recipes Uri
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme(API_SCHEME).authority(API_AUTHORITY).appendPath(API_PATH_RECIPES);

        Uri uri = uriBuilder.build();

        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static UdacityJsonResult<ArrayList<Recipe>> getRecipesFromJson(String recipesJsonString) {
        try {
            ArrayList<Recipe> recipes = Recipe.fromJson(new JSONArray(recipesJsonString));
            return new UdacityJsonResult<>(recipes, null);
        } catch (JSONException jex) {
            Log.e(TAG, String.format("JSON Exception parsing Udacity reply: %s", jex.getMessage()));
            return new UdacityJsonResult<>(null, jex);
        }
    }

    /**
     * JSON result wrapper
     * Allows returning either result or exception
     *
     * @param <T> Result type
     */
    public static class UdacityJsonResult<T> {
        private final T result;
        private final Exception exception;

        UdacityJsonResult(T result, Exception exception) {
            this.result = result;
            this.exception = exception;
        }

        public T getResult() { return result; }

        public Exception getException() { return exception; }

        // Checks whether instance contains an exception
        public boolean hasException() { return exception != null; }
    }

}
