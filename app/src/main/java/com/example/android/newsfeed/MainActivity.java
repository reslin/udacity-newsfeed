package com.example.android.newsfeed;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsTitle>> {

  // URL-string for API request
  private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search";

  // Loader-ID, unique
  private static final int NEWSTITLE_LOADER_ID = 1;

  // show busy state
  private View loadingIndicator;

  // show helping text if no data available
  private TextView noData;

  // Adapter for news titles
  private NewsTitleAdapter newsTitleAdapter;

  /**
   * Get a Loader to load data from
   *
   * @param i      unique ID of the Loader
   * @param bundle "could be" extra data to be used by the Loader (not used here)
   * @return a NewsTitleLoader
   */
  @NonNull
  @Override
  public Loader<List<NewsTitle>> onCreateLoader(int i, Bundle bundle) {

    Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
    Uri.Builder uriBuilder = baseUri.buildUpon();

    // the main topic: "Brexit"
    uriBuilder.appendQueryParameter("q", "brexit");

    // get author(s)
    uriBuilder.appendQueryParameter("show-tags", "contributor");

    // only the newest news, please
    uriBuilder.appendQueryParameter("order-by", "newest");

    // no own key
    uriBuilder.appendQueryParameter("api-key", "test");

    return new NewsTitleLoader(this, uriBuilder.toString());
  }

  /**
   * React on the finish of the loading process
   *
   * @param loader     a previously created Loader instance
   * @param newsTitles List of news objects
   */
  @Override
  public void onLoadFinished(Loader<List<NewsTitle>> loader, List<NewsTitle> newsTitles) {
    loadingIndicator.setVisibility(View.GONE);  // if loading has finished

    newsTitleAdapter.clear(); // make room for new news

    noData.setText(R.string.no_data_available); // if no data, show this message

    if (newsTitles != null && !newsTitles.isEmpty()) {
      newsTitleAdapter.addAll(newsTitles);
    }
  }

  /**
   * Clear the Loader contents, for reload e.g.
   *
   * @param loader a previously created Loader instance
   */
  @Override
  public void onLoaderReset(Loader<List<NewsTitle>> loader) {
    newsTitleAdapter.clear();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    noData = findViewById(R.id.no_data);
    loadingIndicator = findViewById(R.id.loading_indicator);

    ListView list = findViewById(R.id.list);

    newsTitleAdapter = new NewsTitleAdapter(this, new ArrayList<NewsTitle>());

    list.setAdapter(newsTitleAdapter);

    list.setEmptyView(noData);  // if no data: only show a message, not a list

    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        NewsTitle newsTitle = newsTitleAdapter.getItem(position);

        Uri newsTitleUri = null;
        if (newsTitle != null) {
          newsTitleUri = Uri.parse(newsTitle.getWebUrl());
        }

        // if clicked: open a browser
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsTitleUri);
        startActivity(websiteIntent);
      }
    });

    // get a service for checking connectivity
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo networkInfo = null;
    if (connectivityManager != null) {
      networkInfo = connectivityManager.getActiveNetworkInfo();
    }

    if (networkInfo != null && networkInfo.isConnected()) {
      LoaderManager loaderManager = getLoaderManager();
      loaderManager.initLoader(NEWSTITLE_LOADER_ID, null, this);
    } else {
      loadingIndicator.setVisibility(View.GONE);
      noData.setText(R.string.no_internet);
    }
  }
}
