package com.example.nikosz.newsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper methods for querying the Guardian API
 */
public class QueryUtils{
    private static final String TAG = "QueryUtils";

    public static List<NewsItem> fetchNews(String stringUrl) {

        URL url = createUrl(stringUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to API", e);
        }

        List<NewsItem> news = extractNewsItemsFromJson(jsonResponse);
        return news;
    }

    private static List<NewsItem> extractNewsItemsFromJson(String jsonResponse) {
        ArrayList<NewsItem> news = new ArrayList<NewsItem>();

        // parse jsonReponse String
        try {
            JSONArray resultsArray = new JSONObject(jsonResponse)
                    .getJSONObject("response")
                    .getJSONArray("results");
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject article = resultsArray.getJSONObject(i);
                String title = article.getString("webTitle");
                String section = article.getString("sectionName");
                Date date = formatDate(article.getString("webPublicationDate"));
                String authors = getTag(article.getJSONArray("tags"), "webTitle");
                String url = article.getString("webUrl");
                NewsItem newsItem = new NewsItem(title, section, date, authors, url);
                news.add(newsItem);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error while parsing the news JSON results.", e);
        }


        return news;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // Check input validity
        if (url == null) return jsonResponse;

        HttpURLConnection httpURLConnection= null;
        InputStream inputStream = null;

        try {
            // Establish http connection
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == 200) {
                // get input stream in String format
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "Error response code: " + httpURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Can't retrieve news json from Guardian API", e);
        } finally {
            // close connection and stream
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // Read input stream into string
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    // create URL from String
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error creating URL", e);
        }
        return url;
    }

    private static Date formatDate(String stringDate) {
        if (stringDate == null || stringDate == "") return null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = null;
        try
        {
            date = format.parse(stringDate);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date.", e);
        }
        return date;
    }

    private static String getTag(JSONArray tags, String tag) throws JSONException {
        StringBuilder authors = new StringBuilder();
        for (int i = 0; i < tags.length(); i++) {
            if (i > 0) authors.append(", ");
            String author = tags.getJSONObject(i).getString(tag);
            authors.append(author);
        }
        return authors.toString();
    }
}
