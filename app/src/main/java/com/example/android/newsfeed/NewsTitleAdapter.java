package com.example.android.newsfeed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;

public class NewsTitleAdapter extends ArrayAdapter<NewsTitle> {

  /**
   * Constructor
   *
   * @param context    of the app
   * @param newsTitles List of news titles
   */
  public NewsTitleAdapter(Context context, List<NewsTitle> newsTitles) {
    super(context, 0, newsTitles);
  }

  /**
   * @param position    in list
   * @param convertView View for re-use or for new creation
   * @param parent      ViewGroup
   * @return View with content
   */
  @NonNull
  @Override
  public View getView(int position, View convertView, @NonNull ViewGroup parent) {

    // Check if an existing view is being reused, otherwise inflate the view
    if (null == convertView) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
    }

    //title at given position is current
    NewsTitle newsTitleAtPosition = getItem(position);

    if (newsTitleAtPosition != null) {

      TextView dateTextView = convertView.findViewById(R.id.dateTextView);
      String dateString = newsTitleAtPosition.getDateString();
      if (dateString != null) {
        //if any, the API datetime is in this exact format
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
          Date date = format.parse(dateString);
          //re-use it for display in the current Local
          if (date != null) {
            dateString = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date);
          }
        } catch (ParseException ignored) {
        }

        dateTextView.setText(dateString);
      } else {
        dateTextView.setText(R.string.no_date_available);
      }

      //sectionName == topic
      TextView sectionNameTextView = convertView.findViewById(R.id.sectionNameTextView);
      sectionNameTextView.setText(newsTitleAtPosition.getSectionName());

      //contributerWebTitle == author(s), if any
      TextView contributorTextView = convertView.findViewById(R.id.contributorTextView);
      String contributorString = newsTitleAtPosition.getContributorWebTitle();
      if (contributorString != null) {
        contributorTextView.setText(contributorString);
      } else {
        contributorTextView.setText(R.string.no_authors_known);
      }

      //webTitle == title
      TextView webTitleTextView = convertView.findViewById(R.id.webTitleTextView);
      webTitleTextView.setText(newsTitleAtPosition.getWebTitle());
    }

    return convertView;
  }
}

