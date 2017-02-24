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
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.operators.maybe.MaybeEqualSingle;

/**
 * The Search presenter.
 * Created by Bhaskar Pande on 2/21/2017.
 */
public class SearchPresenter{


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




    public void initializeForRx(){
        mQueryTextObservable = mSearchView.getQueryTextObservable().
                debounce(MIN_INTERVAL_SEARCH_REQ, TimeUnit.MILLISECONDS).
                filter((String queryString)-> !TextUtils.isEmpty(queryString) && queryString.length() > QUERY_STRING_MIN_LENGTH).
                map((String queryString)->createQuery(queryString)).
                flatMap((ISearchQuery query)-> Observable.create(getObservable(query)));
        mQueryTextDisposable = mQueryTextObservable.
                subscribe((ArrayList<? super IPlaceResult> result) -> placesRetrieved(result),
                        (Throwable throwable)-> requestFailed(throwable) );
    }


    private void placesRetrieved(ArrayList<? super IPlaceResult> places){

        mSearchView.displayPlaces(places);

    }

    private void requestFailed(Throwable throwable){

        String message = throwable!= null?throwable.getMessage():"SOMETHING TERRIBLY WENT WRONG";

        mSearchView.noPlaceDetected(message);
    }

    private ObservableOnSubscribe<ArrayList<? super IPlaceResult>> getObservable(ISearchQuery query){

        return mPlacesFetcher.getObservableForFetch(query);
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


    /**
     * Call this method when a presenter is no longer needed and therefore must ensure that it is
     * removed from wherever its being referenced.
     */
    public void cleanUp(){

        if(mPlacesFetcher != null){
            mQueryTextDisposable.dispose();


        }

    }



}
