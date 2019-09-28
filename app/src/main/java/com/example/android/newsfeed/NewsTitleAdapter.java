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
import androidx.constraintlayout.widget.ConstraintLayout;

public class NewsTitleAdapter extends ArrayAdapter<NewsTitle> {

  private static final int MODULO_PARAMETER = 2;  // For changing list-item colors. Can be >= 2 for
                                                  // having even more colors.
  private static final String GUARDIAN_API_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

  /**
   * Constructor
   *
   * @param context    of the app
   * @param newsTitles List of news titles
   */
  NewsTitleAdapter(Context context, List<NewsTitle> newsTitles) {
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

    // title at given position is the current one
    NewsTitle newsTitleAtPosition = getItem(position);

    if (newsTitleAtPosition != null) {
      // set alternating background color for a slightly better UX
      ConstraintLayout constraintLayout = convertView.findViewById(R.id.item_container);
      if (position % MODULO_PARAMETER == 0) {
        constraintLayout.setBackgroundColor(convertView.getResources().getColor(R.color.colorA400));
      } else {
        constraintLayout.setBackgroundColor(convertView.getResources().getColor(R.color.colorA200));
      }

      // the date and time of a news title
      TextView dateTextView = convertView.findViewById(R.id.dateTextView);
      String dateString = newsTitleAtPosition.getDateString();
      if (dateString != null) {
        // if any, the API datetime is in this exact format
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(GUARDIAN_API_DATETIME_FORMAT);
        try {
          Date date = format.parse(dateString);
          // re-use it for display in the current Local
          if (date != null) {
            dateString = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date);
          }
        } catch (ParseException ignored) {
          // if no success in parsing: date stays = null
        }

        dateTextView.setText(dateString);
      } else {
        dateTextView.setText(R.string.no_data_available);
      }

      // sectionName == topic
      TextView sectionNameTextView = convertView.findViewById(R.id.sectionNameTextView);
      sectionNameTextView.setText(newsTitleAtPosition.getSectionName());

      // contributerWebTitle == author(s), if any
      TextView contributorTextView = convertView.findViewById(R.id.contributorTextView);
      String contributorString = newsTitleAtPosition.getContributorWebTitle();
      if (contributorString != null) {
        contributorTextView.setText(contributorString);
      } else {
        contributorTextView.setText(R.string.no_authors_known);
      }

      // webTitle == title
      TextView webTitleTextView = convertView.findViewById(R.id.webTitleTextView);
      webTitleTextView.setText(newsTitleAtPosition.getWebTitle());
    }

    return convertView;
  }
}

