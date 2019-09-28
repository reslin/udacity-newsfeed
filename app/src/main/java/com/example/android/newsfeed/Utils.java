package com.example.android.newsfeed;

import android.text.TextUtils;
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
import java.util.ArrayList;
import java.util.List;


/**
 * Helper class with static methods for JSON and Networking
 */

public final class Utils {

  private static final String LOG_TAG = "UTILS";
  private static final int NET_READ_TIMEOUT = 10000;  //milliseconds --> 10 secs
  private static final int NET_CONNECT_TIMEOUT = 15000;  //milliseconds --> 15 secs
  private static final String NET_REQUEST_METHOD = "GET";
  private static final String INPUTSTREAM_CHARSET = "UTF-8";

  // do not construct, this is a static class
  private Utils() {
  }

  // one public method

  /**
   * Fetch a List of news
   *
   * @param urlString queries the API
   * @return a List of news
   */
  public static List<NewsTitle> fetchNewsList(String urlString) {

/*        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

 */

    URL url = createUrl(urlString);

    String jsonResponse = null;
    try {
      jsonResponse = makeHttpRequest(url);
    } catch (IOException e) {
      Log.e(LOG_TAG, "Problem making the HTTP request.", e);
    }

    return extractNewsTitleListFromJson(jsonResponse);
  }

  // four private methods

  /**
   * Convert JSON answer to a List of news
   *
   * @param newsTitleJSON the complete API answer
   * @return the gathered news
   */
  private static List<NewsTitle> extractNewsTitleListFromJson(String newsTitleJSON) {

    if (TextUtils.isEmpty(newsTitleJSON)) {
      return null;  // nothing to do
    }

    List<NewsTitle> newsTitles = new ArrayList<>();

    try {
      // cascade through JSON info
      JSONObject rootLevelJsonResponse = new JSONObject(newsTitleJSON);
      JSONObject responseLevelJsonResponse = rootLevelJsonResponse.getJSONObject("response");
      JSONArray resultsJsonArray = responseLevelJsonResponse.getJSONArray("results");

      // get a NewsTitle
      for (int i = 0; i < resultsJsonArray.length(); i++) {

        JSONObject currentNewsTitle = resultsJsonArray.getJSONObject(i);

        // fields of NewsTitle in order of appearance
        String webPublicationDate = currentNewsTitle.getString("webPublicationDate");
        String sectionName = currentNewsTitle.getString("sectionName");

        // 0 .. many
        JSONArray tagsJsonArray = currentNewsTitle.getJSONArray("tags");
        StringBuilder contributor = new StringBuilder();
        for (int j = 0; j < tagsJsonArray.length(); j++) {
          JSONObject tag = tagsJsonArray.getJSONObject(i);
          contributor.append(tag.getString("webTitle"));  //the authors name
          if (j < (tagsJsonArray.length() - 1)) { //e.g. 3 contributors, 2 commas => "length - 1"
            contributor.append(", ");
          }
        }

        String webTitle = currentNewsTitle.getString("webTitle");
        String webUrl = currentNewsTitle.getString("webUrl");
        // fields of Newstitle

        newsTitles.add(new NewsTitle(webPublicationDate, sectionName,
            contributor.toString(), webTitle, webUrl));
      }

    } catch (JSONException ignored) {
      // on error: nothing is added to the List
    }

    return newsTitles;
  }

  /**
   * Convert a String to a URL
   *
   * @param stringUrl to be converted
   * @return converted URL
   */
  private static URL createUrl(String stringUrl) {
    URL url = null;
    try {
      url = new URL(stringUrl);
    } catch (MalformedURLException e) {
      Log.e(LOG_TAG, "Problem building the URL ", e);
    }
    return url;
  }

  /**
   * Make HTTP request
   *
   * @param url of the API
   * @return JSON String
   * @throws IOException and leaves handling to calling method
   */
  private static String makeHttpRequest(URL url) throws IOException {
    String jsonResponse = "";

    if (null == url) {
      return jsonResponse;
    }

    HttpURLConnection urlConnection = null;
    InputStream inputStream = null;
    try {
      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setReadTimeout(NET_READ_TIMEOUT);
      urlConnection.setConnectTimeout(NET_CONNECT_TIMEOUT);
      urlConnection.setRequestMethod(NET_REQUEST_METHOD);
      urlConnection.connect();

      if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
        inputStream = urlConnection.getInputStream();
        jsonResponse = readFromStream(inputStream);
      } else {
        Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
      }
    } catch (IOException e) {
      Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
    } finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
      if (inputStream != null) {
        inputStream.close();
      }
    }
    return jsonResponse;
  }

  /**
   * Convert InputStream to String
   *
   * @param inputStream to be converted
   * @return resulting String
   * @throws IOException and leaves handling to calling method
   */
  private static String readFromStream(InputStream inputStream) throws IOException {
    StringBuilder output = new StringBuilder();
    if (inputStream != null) {
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName(INPUTSTREAM_CHARSET));
      BufferedReader reader = new BufferedReader(inputStreamReader);
      String line = reader.readLine();
      while (line != null) {
        output.append(line);
        line = reader.readLine();
      }
    }
    return output.toString();
  }
}
