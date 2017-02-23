package com.mfsi.searchplace.places;

import java.util.ArrayList;

/**
 * UI that accepts input to frame a Search Query and displays the appropriate results must implement
 * this interface
 * Created by Bhaskar Pande on 2/21/2017.
 */
public interface ISearchView extends IRxView {


    void initializeWidgets();
    void addListeners();
    void configureMvp();

    double getLatitudeSelected();
    double getLongitudeSelected();
    String getSearchStringEntered();
    float getSearchMeterRadiusSelected();

    void displayPlaces(ArrayList<? super IPlaceResult> result);

    void noPlaceDetected();
}
