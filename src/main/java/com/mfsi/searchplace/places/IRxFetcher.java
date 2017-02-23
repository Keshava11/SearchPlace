package com.mfsi.searchplace.places;

import java.util.ArrayList;

import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Bhaskar Pande on 2/23/2017.
 */
public interface IRxFetcher extends ObservableOnSubscribe<ArrayList<? super IPlaceResult>>{
}
