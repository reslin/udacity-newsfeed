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

  //Loader-ID, unique
  private static final int NEWSTITLE_LOADER_ID = 1;

  //show busy state
  private View loadingIndicator;

  //show helping text if no data available
  private TextView noData;

  //Adapter for news titles
  private NewsTitleAdapter newsTitleAdapter;

  @NonNull
  @Override
  // onCreateLoader instantiates and returns a new Loader for the given ID
  public Loader<List<NewsTitle>> onCreateLoader(int i, Bundle bundle) {

    Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
    Uri.Builder uriBuilder = baseUri.buildUpon();

    uriBuilder.appendQueryParameter("q", "brexit");
    uriBuilder.appendQueryParameter("show-tags", "contributor");
    uriBuilder.appendQueryParameter("order-by", "newest");
    uriBuilder.appendQueryParameter("api-key", "test");

    return new NewsTitleLoader(this, uriBuilder.toString());
  }

  @Override
  public void onLoadFinished(Loader<List<NewsTitle>> loader, List<NewsTitle> newsTitles) {
    // if finished, hide progress
    loadingIndicator.setVisibility(View.GONE);

    // wipe the Adapter for new items to come
    newsTitleAdapter.clear();

    // set the default "No data" text. Will be hidden immediately if data arrived.
    noData.setText(R.string.no_date_available);

    if (newsTitles != null && !newsTitles.isEmpty()) {
      newsTitleAdapter.addAll(newsTitles);
    }
  }

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

    newsTitleAdapter = new EarthquakeAdapter(this, new ArrayList<NewsTitle>());

    list.setAdapter(newsTitleAdapter);

    list.setEmptyView(noData);

    // Set an item click listener on the ListView, which sends an intent to a web browser
    // to open a website with more information about the selected earthquake.
    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        NewsTitle newsTitle = newsTitleAdapter.getItem(position);

        // Convert the String URL into a URI object (to pass into the Intent constructor)
        Uri newsTitleUri = null;
        if (newsTitle != null) {
          newsTitleUri = Uri.parse(newsTitle.getWebUrl());
        }

        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsTitleUri);
        startActivity(websiteIntent);
      }
    });

    // Get a reference to the ConnectivityManager to check state of network connectivity
    ConnectivityManager connMgr = (ConnectivityManager)
        getSystemService(Context.CONNECTIVITY_SERVICE);

    // Get details on the currently active default data network
    NetworkInfo networkInfo = null;
    if (connMgr != null) {
      networkInfo = connMgr.getActiveNetworkInfo();
    }

    // If there is a network connection, fetch data
    if (networkInfo != null && networkInfo.isConnected()) {

      // Get a reference to the LoaderManager, in order to interact with loaders.
      LoaderManager loaderManager = getLoaderManager();

      // Initialize the loader. Pass in the int ID constant defined above and pass in null for
      // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
      // because this activity implements the LoaderCallbacks interface).
      loaderManager.initLoader(NEWSTITLE_LOADER_ID, null, this);
    } else {
      loadingIndicator.setVisibility(View.GONE);
      noData.setText(R.string.no_internet);
    }
  }
}
