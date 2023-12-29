package com.bunny.entertainment.yousee.utils;

import android.content.res.Resources;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.Scanner;

public class JsonUtils {
    public static JSONObject readJsonObjectFromRaw(Resources resources, int resourceId) throws JSONException {
        InputStream inputStream = resources.openRawResource(resourceId);
        Scanner scanner = new Scanner(inputStream);
        StringBuilder builder = new StringBuilder();

        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
        }

        scanner.close();

        return new JSONObject(builder.toString());
    }

    public static JSONArray readJsonArrayFromRaw(Resources resources, int resourceId) throws JSONException {
        InputStream inputStream = resources.openRawResource(resourceId);
        Scanner scanner = new Scanner(inputStream);
        StringBuilder builder = new StringBuilder();

        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
        }

        scanner.close();

        return new JSONArray(builder.toString());
    }
}
