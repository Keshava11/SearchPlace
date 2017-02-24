package com.mfsi.searchplace.places;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * UI that accepts input to frame a Search Query and displays the appropriate results must implement
 * this interface
 * Created by Bhaskar Pande on 2/21/2017.
 */
public interface ISearchView {


    void initializeWidgets();
    void addListeners();
    void configureMvp();

    double getLatitudeSelected();
    double getLongitudeSelected();
    Observable<String> getQueryTextObservable();
    float getSearchMeterRadiusSelected();

    void displayPlaces(ArrayList<? super IPlaceResult> result);

    void noPlaceDetected(String errorMessage);
}
