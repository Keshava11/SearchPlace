package com.mfsi.searchplace.places;

import java.util.ArrayList;

import io.reactivex.ObservableOnSubscribe;

/**
 * Class that fetches locations according to a Query must implement this interface
 * Created by Bhaskar Pande on 2/21/2017.
 */
public interface IPlacesFetcher{

    /**
     * requests The Observable that will initiate fetch locations according to a given Query. The caller
     * must subscribe to it to be notified the results.
     * @param searchQuery the Search Query to be used.
     */
    ObservableOnSubscribe<ArrayList<? super IPlaceResult>> getObservableForFetch(ISearchQuery searchQuery);

    /**
     * creates a Query based on the given Parameters
     * @param latitude
     * @param longitude
     * @param radius in meters identifying the region in which to search for.
     * @param queryString
     * @return an ISearchQuery reference
     */
    ISearchQuery frameSearchQuery(double latitude, double longitude, float radius, String queryString);




}
