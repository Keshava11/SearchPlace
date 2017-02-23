package com.mfsi.searchplace.places;

/**
 * Implement this interface for classes that encapsulate the search queries made
 * Created by Bhaskar Pande on 2/21/2017.
 */
public interface ISearchQuery {

    String getSearchString();
    double getLatitude();
    double getLongitude();
    float getSearchRadius();





}
