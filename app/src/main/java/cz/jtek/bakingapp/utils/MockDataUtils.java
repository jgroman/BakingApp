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

package cz.jtek.bakingapp.utils;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * These utilities can be used for application testing without the need of network
 * connection.
 */
public class MockDataUtils {

    @SuppressWarnings("unused")
    private static final String TAG = MockDataUtils.class.getSimpleName();

    /**
     * Read text file from input stream
     *
     * @param inputStream    Input stream
     * @return File contents
     */
    private static String readFile(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        int readLen;

        byte[] buffer = new byte[1024];

        try {
            while ((readLen = inputStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, readLen));
            }
            inputStream.close();
        } catch (IOException ex) {
            Log.e(TAG, "IOException reading file.");
            ex.printStackTrace();
            throw ex;
        }

        return sb.toString();
    }

    /**
     * Provides file contents for testing. Mock JSON files are read
     * from /res/raw directory.
     *
     * @param context    Current context
     * @param fileName   Name of file to read WITHOUT EXTENSION
     * @return File contents
     */
    @SuppressWarnings("unused")
    public static String getMockJson(Context context, String fileName) throws IOException {

        InputStream inputStream = context.getResources().openRawResource(
                context.getResources().getIdentifier(fileName, "raw", context.getPackageName())
        );

        return readFile(inputStream);
    }
}
