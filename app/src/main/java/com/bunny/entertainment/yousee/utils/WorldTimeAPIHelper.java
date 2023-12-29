package com.bunny.entertainment.yousee.utils;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorldTimeAPIHelper {

    private static final String API_URL = "http://worldtimeapi.org/api/ip";

    public interface WorldTimeListener {
        void onWorldTimeFetched(Date currentDate);
    }

    public static void fetchWorldTime(WorldTimeListener listener) {
        new FetchWorldTimeTask(listener).execute(API_URL);
    }

    private static class FetchWorldTimeTask extends AsyncTask<String, Void, String> {

        private final WorldTimeListener listener;

        FetchWorldTimeTask(WorldTimeListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                return result.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String dateTimeString = jsonObject.getString("datetime");

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.getDefault());
                    Date currentDate = dateFormat.parse(dateTimeString);

                    listener.onWorldTimeFetched(currentDate);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


/*public class WorldTimeAPIHelper {

    private static final String API_URL = "http://worldtimeapi.org/api/ip";

    public interface WorldTimeListener {
        void onWorldTimeFetched(Date currentDate);
    }

    public static void fetchWorldTime(WorldTimeListener listener) {
        new FetchWorldTimeTask(listener).execute(API_URL);
    }

    private static class FetchWorldTimeTask extends AsyncTask<String, Void, String> {

        private final WorldTimeListener listener;

        FetchWorldTimeTask(WorldTimeListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                return result.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String dateTimeString = jsonObject.getString("datetime");

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.getDefault());
                    Date currentDate = dateFormat.parse(dateTimeString);

                    listener.onWorldTimeFetched(currentDate);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}*/
