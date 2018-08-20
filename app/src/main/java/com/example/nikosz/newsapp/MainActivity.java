package com.example.nikosz.newsapp;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsItem>>{
    private static final String TAG = "MainActivity";
    private static final String API_QUERY_URL =
            "https://content.guardianapis.com/search?q=mathematics%20OR%20math&show-tags=contributor";
    // &api-key=17f56bb9-dc95-4489-9f4f-9573f5fb70a8
    private static final String API_KEY = BuildConfig.ApiKey;
    private static final int NEWS_LOADER_ID = 0;

    private NewsAdapter mNewsAdapter;
    private ProgressBar mLoadingIndicator;
    private TextView mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Set loading indicator
        mLoadingIndicator = findViewById(R.id.loading_progressbar);

        ListView newsListView = findViewById(R.id.news_listview);

        // Set empty state view
        mEmptyView = findViewById(R.id.empty_state_textview);
        newsListView.setEmptyView(mEmptyView);

        mNewsAdapter = new NewsAdapter(this, new ArrayList<NewsItem>());
        newsListView.setAdapter(mNewsAdapter);


        // Check if connected to Internet
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        if (!isConnected) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mEmptyView.setText(R.string.no_internet_string);
        } else {
            getLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
        }
    }


    @Override
    public Loader<List<NewsItem>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String numberOfHits = sharedPreferences.getString(
                getString(R.string.settings_number_of_hits_key),
                getString(R.string.settings_number_of_hits_default));


        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(API_QUERY_URL);

        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter(getString(R.string.settings_number_of_hits_key), numberOfHits);
        uriBuilder.appendQueryParameter(getString(R.string.settings_order_by_key), orderBy);
        uriBuilder.appendQueryParameter("api-key", API_KEY);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> newsItems) {
        mEmptyView.setText(R.string.no_news_string);
        mNewsAdapter.clear();
        // TODO: set empty view

        // hide loading indicator
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if (newsItems != null && !newsItems.isEmpty()) {
            mNewsAdapter.addAll(newsItems);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        mNewsAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
