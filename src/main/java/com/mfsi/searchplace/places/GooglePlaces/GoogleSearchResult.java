package com.mfsi.searchplace.places.GooglePlaces;

import com.mfsi.searchplace.places.IPlaceResult;

/**
 * Created by Bhaskar Pande on 2/22/2017.
 */
public class GoogleSearchResult implements IPlaceResult {


    private String mPlaceName;

    @Override
    public String getPlaceName() {
        return mPlaceName;
    }

    public void setPlaceName(String placeName){
        mPlaceName = placeName;
    }

    @Override
    public IPlaceAddress getPlaceAddress() {
        return null;
    }
}
