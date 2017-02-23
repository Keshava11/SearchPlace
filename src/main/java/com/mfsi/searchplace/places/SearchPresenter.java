package com.mfsi.searchplace.places;

import android.util.Log;

import com.mfsi.searchplace.utils.Validator;

import java.util.ArrayList;

/**
 * The Search presenter.
 * Created by Bhaskar Pande on 2/21/2017.
 */
public class SearchPresenter implements IPlacesFetcher.PlacesFetcherListener {

    private ISearchView mSearchView;
    private IPlacesModel mPlaceModel;
    private IPlacesFetcher mPlacesFetcher;


    /***
     * constructor
     */
    public SearchPresenter(){

    }

    @Override
    public void error() {

    }

    /***
     * Set The View That receives user Input To search plaes
     * @param view
     */
    public void setSearchView(ISearchView view){
        mSearchView = view;
    }

    /***
     * Set The Model To use with the presenter
     * @param model
     */
    public void setPlacesModel(IPlacesModel model){
        mPlaceModel = model;
    }

    /***
     * Set the reference to the class that fetches locations
     * @param placeFetcher
     */
    public void setPlaceFetcher(IPlacesFetcher placeFetcher){
        mPlacesFetcher = placeFetcher;
    }


    /***
     * initialize the presenter here
     */
    public void initialize(){
        mPlacesFetcher.initialize(this);
    }

    @Override
    public void placesFetched(ArrayList<? super IPlaceResult> result) {
        if(result != null && result.size() > 0) {
            mSearchView.displayPlaces(result);
        }else{
            mSearchView.noPlaceDetected();
        }
    }


    private ISearchQuery createQuery(){

        double latitude = mSearchView.getLatitudeSelected();
        double longitude = mSearchView.getLongitudeSelected();
        float radius = mSearchView.getSearchMeterRadiusSelected();
        String queryString = mSearchView.getSearchStringEntered();

        ISearchQuery searchQuery = null;

        if(!Validator.isAnyNull(queryString)){

         searchQuery =   mPlacesFetcher.frameSearchQuery(latitude,longitude,
                    radius, queryString);

        }

        return searchQuery;
    }



    public void searchButtonClicked(){

        ISearchQuery searchQuery = createQuery();
        if(searchQuery != null){
            mPlacesFetcher.fetch(searchQuery);
        }
    }

    /**
     * Call this method when a presenter is no longer needed and therefore must ensure that it is
     * removed from wherever its being referenced.
     */
    public void cleanUp(){

        if(mPlacesFetcher != null){
            mPlacesFetcher.deregister(this);
        }

    }



}
