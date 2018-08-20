package com.example.nikosz.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<NewsItem>{
    private static final String TAG = "NewsAdapter";

    private static class ViewHolder {
        TextView sectionTextView;
        TextView authorTextView;
        TextView titleTextView;
        TextView dateTextView;
    }

    public NewsAdapter(@NonNull Context context, @NonNull List<NewsItem> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newsItemView = convertView;
        ViewHolder viewHolder;
        if (newsItemView == null) {
            newsItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent,false);

            viewHolder = new ViewHolder();
            viewHolder.sectionTextView = newsItemView.findViewById(R.id.section_textview);
            viewHolder.authorTextView = newsItemView.findViewById(R.id.author_textview);
            viewHolder.titleTextView = newsItemView.findViewById(R.id.title_textview);
            viewHolder.dateTextView = newsItemView.findViewById(R.id.date_textview);
            newsItemView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) newsItemView.getTag();
        }

        final NewsItem currentNewsItem = getItem(position);

        // Section
        String section = currentNewsItem.getSection();
        viewHolder.sectionTextView.setText(section);

        // Author
        String authors = currentNewsItem.getAuthor();
        viewHolder.authorTextView.setText(authors);

        // Title
        String title = currentNewsItem.getTitle();
        viewHolder.titleTextView.setText(title);

        // Date
        String date = formatDate(currentNewsItem.getPublishDate());
        viewHolder.dateTextView.setText(date);

        // Click listener
        newsItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl(currentNewsItem.getStringUrl());
            }
        });

        return newsItemView;
    }

    private String formatDate(Date date) {
        // Return empty string if there was no date in the JSON response
        if (date == null) return "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, ''yy");
        return simpleDateFormat.format(date);
    }

    private void openUrl(String stringUrl) {
        Uri newsSite = Uri.parse(stringUrl);
        Intent openSiteIntent = new Intent(Intent.ACTION_VIEW, newsSite);
        getContext().startActivity(openSiteIntent);
    }
}
