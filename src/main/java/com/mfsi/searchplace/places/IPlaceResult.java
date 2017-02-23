package com.mfsi.searchplace.places;

/**
 * the result of fetching places based on a query must implement this interface
 * Created by Bhaskar Pande on 2/21/2017.
 */
public interface IPlaceResult {

    /**
     * the class representing location address must extend this interface
     */
    interface IPlaceAddress{

        String getPinCode();
        String getCity();
        String getState();
    }

    /**
     * @returns place Name
     */
    String getPlaceName();

    /**
     *
     * @return the Address of the Place as an IPlaceAddress object
     */
    IPlaceAddress getPlaceAddress();


}
