package com.mfsi.searchplace.places;

import io.reactivex.Observable;

/**
 * Created by Bhaskar Pande on 2/23/2017.
 */
public interface IRxView {

    Observable<String> getQueryTextObservable();

}
