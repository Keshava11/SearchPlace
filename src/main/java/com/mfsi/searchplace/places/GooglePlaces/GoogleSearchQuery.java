package com.mfsi.searchplace.places.GooglePlaces;

import com.google.android.gms.maps.model.LatLngBounds;
import com.mfsi.searchplace.places.ISearchQuery;

/**
 * Created by Bhaskar Pande on 2/22/2017.
 */
public class GoogleSearchQuery implements ISearchQuery{


    private double mLatitude;
    private double mLongitude;
    private float mAreaRadius;
    private String queryString;


    @Override
    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    @Override
    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }

    @Override
    public float getSearchRadius() {
        return mAreaRadius;
    }

    public void setAreaRadius(float areaRadius) {
        this.mAreaRadius = areaRadius;
    }

    @Override
    public String getSearchString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
}
