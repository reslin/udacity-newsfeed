package com.example.android.newsfeed;

class NewsTitle {

  /**
   * Date and time of the news, named as in the API
   */
  private String webPublicationDate;

  /**
   * The topic category, named as in the API
   */
  private String sectionName;

  /**
   * The author(s), named *like* in the API
   */
  private String contributorWebTitle;

  /**
   * The headline, named as in the API
   */
  private String webTitle;

  /**
   * The URL of the title, named as in the API.
   * Used for implicit Intent.
   */
  private String webUrl;

  /**
   * Constructor
   *
   * @param webPublicationDate  date and time, may be null
   * @param sectionName         topic, not null
   * @param contributorWebTitle author(s), may be null
   * @param webTitle            title, not null
   * @param webUrl              URL of title, not null
   */
  NewsTitle(String webPublicationDate, String sectionName, String contributorWebTitle,
            String webTitle, String webUrl) {
    this.webPublicationDate = webPublicationDate;
    this.sectionName = sectionName;
    this.contributorWebTitle = contributorWebTitle;
    this.webTitle = webTitle;
    this.webUrl = webUrl;
  }

  /**
   * Only getters needed, the Constructor sets every values.
   */

  String getDateString() {
    return webPublicationDate;
  }

  String getSectionName() {
    return sectionName;
  }

  String getContributorWebTitle() {
    return contributorWebTitle;
  }

  String getWebTitle() {
    return webTitle;
  }

  String getWebUrl() {
    return webUrl;
  }
}
