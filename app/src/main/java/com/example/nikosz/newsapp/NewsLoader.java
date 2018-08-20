package com.example.nikosz.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<NewsItem>> {
    private static final String TAG = "NewsLoader";

    private String mStringUrl;

    public NewsLoader(Context context, String stringUrl) {
        super(context);
        mStringUrl = stringUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<NewsItem> loadInBackground() {
        if (mStringUrl == null) return null;
        return QueryUtils.fetchNews(mStringUrl);
    }
}
