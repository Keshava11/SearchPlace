package com.mfsi.searchplace.places.GooglePlaces;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.CharacterStyle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.mfsi.searchplace.R;
import com.mfsi.searchplace.places.IPlaceResult;
import com.mfsi.searchplace.places.IPlacesFetcher;
import com.mfsi.searchplace.places.ISearchQuery;

import java.util.ArrayList;
import java.util.Iterator;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Bhaskar Pande on 2/21/2017.
 */
public class GoogleLocationHelper implements IPlacesFetcher,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{


    private GoogleApiClient mGoogleApiClient;
    private GooglePlayServicesActivity mHostActivity;
    private int CONNECT_GOOGLE_API = 100;
    private boolean mResolvingConnectionFailure;


    class PlaceFetchObservable implements ObservableOnSubscribe<ArrayList<? super IPlaceResult>>,
            ResultCallback<AutocompletePredictionBuffer> {

        private ISearchQuery mSearchQuery;
        private ObservableEmitter<ArrayList<? super IPlaceResult>> mObsEmitter;


        public PlaceFetchObservable(ISearchQuery query) {
            mSearchQuery = query;
        }

        private void notifyResults(ArrayList<? super IPlaceResult> results) {

            mObsEmitter.onNext(results);
        }

        private void notifyError(String message) {

            Exception created = new Exception(message);
            mObsEmitter.onError(created);

        }

        @Override
        public void subscribe(ObservableEmitter<ArrayList<? super IPlaceResult>> emitter) throws Exception {
            mObsEmitter = emitter;
            LatLng center = new LatLng(mSearchQuery.getLatitude(), mSearchQuery.getLongitude());
            LatLng latLngSW = SphericalUtil.computeOffset(center, mSearchQuery.getSearchRadius() * Math.sqrt(2.0), 225);
            LatLng latLngNE = SphericalUtil.computeOffset(center, mSearchQuery.getSearchRadius() * Math.sqrt(2.0), 45);

            LatLngBounds latLngBounds = new LatLngBounds(latLngSW, latLngNE);

            PendingResult<AutocompletePredictionBuffer> result = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient,
                    mSearchQuery.getSearchString(),
                    latLngBounds, null);
            result.setResultCallback(this);
        }

        @Override
        public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {

            ArrayList<? super IPlaceResult> results = new ArrayList<>();


            Log.i("tag", "myTag: " + autocompletePredictions);

            if (autocompletePredictions != null) {

                if (autocompletePredictions.getStatus().isSuccess()) {

                    Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
                    while (iterator.hasNext()) {
                        AutocompletePrediction prediction = iterator.next();
                        GoogleSearchResult result = new GoogleSearchResult();
                        String name = prediction.getPrimaryText(null).toString();
                        Log.i("TAG", "SEARCH COMPLETED: " + name);
                        result.setPlaceName(name);
                        results.add(result);
                    }
                    notifyResults(results);
                } else {
                    notifyError(autocompletePredictions.getStatus().getStatusMessage());
                }
            } else {
                notifyError("Null Returned: Reason Unknown");
            }
        }
    }


    @Override
    public ObservableOnSubscribe<ArrayList<? super IPlaceResult>> getObservableForFetch(ISearchQuery searchQuery) {

        PlaceFetchObservable placeFetchObservable = new PlaceFetchObservable(searchQuery);
        return placeFetchObservable;

    }



    @Override
    public ISearchQuery frameSearchQuery(double latitude, double longitude, float radius, String queryString) {

        GoogleSearchQuery googleSearchQuery = new GoogleSearchQuery();

        googleSearchQuery.setAreaRadius(radius);
        googleSearchQuery.setLatitude(latitude);
        googleSearchQuery.setLongitude(longitude);
        googleSearchQuery.setQueryString(queryString);

        return googleSearchQuery;
    }





    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.i("TAG", "CONNECTION FAILED: " + connectionResult);

        if (mResolvingConnectionFailure) {
            return;
        }

        mResolvingConnectionFailure = true;
        boolean isResolutionAttemptInitiated = BaseGameUtils.
                resolveConnectionFailure(mHostActivity, mGoogleApiClient, connectionResult, CONNECT_GOOGLE_API,
                        R.string.connection_failure);
        if (!isResolutionAttemptInitiated) {
            mResolvingConnectionFailure = false;
        }


    }

    public GoogleLocationHelper(GooglePlayServicesActivity context) {


        mHostActivity = context;
        mGoogleApiClient = new GoogleApiClient.Builder(context).
                addApi(LocationServices.API).
                addApi(Places.GEO_DATA_API).
                addApi(Places.PLACE_DETECTION_API).
                addConnectionCallbacks(this).
                enableAutoManage(context, this).
                build();


    }


    public void onGooglePlayResponse(GooglePlayServicesActivity googlePlayServicesActivity, int requestCode, int resultCode) {

        if (requestCode == CONNECT_GOOGLE_API) {
            mResolvingConnectionFailure = false;
            if (resultCode == Activity.RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(googlePlayServicesActivity,
                        requestCode, resultCode, R.string.connection_failure);
            }
        }

    }

    public void hostActivityOnStart() {
    }

    public void hostActivityOnStop() {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mHostActivity.googleLocationHelperInitialized();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    public GoogleApiClient getGoogleClientApi() {
        return mGoogleApiClient;
    }


}
