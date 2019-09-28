package com.example.android.newsfeed;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsTitleLoader extends AsyncTaskLoader<List<NewsTitle>> {

  // query url
  private String url;

  /**
   * Constructor
   *
   * @param context of the app
   * @param url     to query
   */
  public NewsTitleLoader(Context context, String url) {
    super(context);
    this.url = url;
  }

  @Override
  protected void onStartLoading() {
    forceLoad();
  }

  @Override
  public List<NewsTitle> loadInBackground() {
    if (url == null) {
      return null;
    }

    // Perform the network request, parse the response, and extract a list of earthquakes.
    return QueryUtils.fetchEarthquakeData(mUrl);
  }
}
