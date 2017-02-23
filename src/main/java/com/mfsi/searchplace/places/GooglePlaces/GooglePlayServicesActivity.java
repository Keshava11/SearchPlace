package com.mfsi.searchplace.places.GooglePlaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Bhaskar Pande on 2/21/2017.
 */
public abstract class GooglePlayServicesActivity extends AppCompatActivity {

    private GoogleLocationHelper mGoogleLocationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleLocationHelper = new GoogleLocationHelper(this);

    }

    public GoogleApiClient getGoogleApiInstance() {
        return mGoogleLocationHelper!=null?mGoogleLocationHelper.getGoogleClientApi():null;
    }


    public GoogleLocationHelper getGooglePlacesFetcher(){
        return mGoogleLocationHelper;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGoogleLocationHelper.onGooglePlayResponse(this, requestCode, resultCode);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleLocationHelper.hostActivityOnStart();

    }



    @Override
    protected void onStop() {
        super.onStop();
        mGoogleLocationHelper.hostActivityOnStop();
    }


    public abstract void googleLocationHelperInitialized();
}
