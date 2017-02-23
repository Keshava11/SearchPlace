package com.mfsi.searchplace.places;

import android.text.TextUtils;
import android.util.Log;

import com.mfsi.searchplace.utils.Validator;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * The Search presenter.
 * Created by Bhaskar Pande on 2/21/2017.
 */
public class SearchPresenter implements IPlacesFetcher.PlacesFetcherListener{


    private ISearchView mSearchView;
    private IPlacesModel mPlaceModel;
    private IPlacesFetcher mPlacesFetcher;
    private Observable<ArrayList<? super IPlaceResult>> mQueryTextObservable;
    private Disposable mQueryTextDisposable;

    private static final int QUERY_STRING_MIN_LENGTH = 3;
    private static final long MIN_INTERVAL_SEARCH_REQ = 4;


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

    public void initializeForRx(){
        initialize();
        mQueryTextObservable = mSearchView.getQueryTextObservable().
                debounce(MIN_INTERVAL_SEARCH_REQ, TimeUnit.MILLISECONDS).
                filter((String queryString)-> !TextUtils.isEmpty(queryString) && queryString.length() > QUERY_STRING_MIN_LENGTH).
                map((String queryString)->createQuery(queryString)).
                flatMap((ISearchQuery query)-> Observable.create(mPlacesFetcher));
        mQueryTextDisposable = mQueryTextObservable.
                subscribe((ArrayList<? super IPlaceResult> result) -> placesRetrieved(result),
                        (Throwable throwable)-> requestFailed(throwable) );
    }


    public void placesRetrieved(ArrayList<? super IPlaceResult> places){

        mSearchView.displayPlaces(places);

    }

    public void requestFailed(Throwable throwable){

    }


    @Override
    public void placesFetched(ArrayList<? super IPlaceResult> result) {
        if(result != null && result.size() > 0) {
            mSearchView.displayPlaces(result);
        }else{
            mSearchView.noPlaceDetected();
        }
    }

    private ISearchQuery createQuery(String queryString){

        double latitude = mSearchView.getLatitudeSelected();
        double longitude = mSearchView.getLongitudeSelected();
        float radius = mSearchView.getSearchMeterRadiusSelected();

        ISearchQuery searchQuery = null;

        if(!Validator.isAnyNull(queryString)){
            searchQuery =   mPlacesFetcher.frameSearchQuery(latitude,longitude,
                    radius, queryString);
        }
        return searchQuery;
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

    private void searchQuery(ISearchQuery searchQuery){

    }

    private void onError(Throwable error){

    }

    /**
     * Call this method when a presenter is no longer needed and therefore must ensure that it is
     * removed from wherever its being referenced.
     */
    public void cleanUp(){

        if(mPlacesFetcher != null){
            mPlacesFetcher.deregister(this);
            mQueryTextDisposable.dispose();


        }

    }



}
