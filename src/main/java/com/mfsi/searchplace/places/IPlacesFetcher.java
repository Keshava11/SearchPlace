package com.mfsi.searchplace.places;

import java.util.ArrayList;

/**
 * Class that fetches locations according to a Query must implement this interface
 * Created by Bhaskar Pande on 2/21/2017.
 */
public interface IPlacesFetcher extends IRxFetcher {

    /**
     * requests to fetch locations according to a given Query
     * @param searchQuery the Search Query to be used.
     */
    void fetch(ISearchQuery searchQuery);

    /**
     * creates a Query based on the given Parameters
     * @param latitude
     * @param longitude
     * @param radius in meters identifying the region in which to search for.
     * @param queryString
     * @return an ISearchQuery reference
     */
    ISearchQuery frameSearchQuery(double latitude, double longitude, float radius, String queryString);

    /***
     * The Class that wishes to be notified of the Places Fetch results must implement this interface
     */
    interface PlacesFetcherListener{

        /**
         * callback when the Places Fetcher returns with an array of Location Results based on a Search Query.
         * @param result an ArrayList of IPlaceResult objetcs
         */
        void placesFetched(ArrayList<? super IPlaceResult> result);

        /**
         * indicates that a Places Fetch attempt has failed
         */
        void error();
    }

    /**
     * registers to a Place Fetcher a class that must be notified on its results
     * @param listener Listener reference that implements PlacesFetcherListener
     */
    void initialize(PlacesFetcherListener listener);

    /**
     * deregister the given listener from the list the have added themselves to be notified on the Progress.
     * @param listener the registered listener
     */
    void deregister(PlacesFetcherListener listener);

}
