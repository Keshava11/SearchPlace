package com.mfsi.searchplace;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mfsi.searchplace.placedisplayer.PlaceListDialog;
import com.mfsi.searchplace.places.GooglePlaces.GooglePlayServicesActivity;
import com.mfsi.searchplace.places.IPlaceResult;
import com.mfsi.searchplace.places.ISearchView;
import com.mfsi.searchplace.places.SearchPresenter;
import com.mfsi.searchplace.utils.UserAlerts;
import java.util.ArrayList;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;



public class MainActivity extends GooglePlayServicesActivity implements ISearchView,
        OnMapReadyCallback, DialogInterface.OnDismissListener, GoogleMap.OnMapClickListener, SeekBar.OnSeekBarChangeListener {

    private static final int MAX_PROGRESS = 100;
    private static final int DEFAULT_PROGRESS = 1;
    private SearchView mSearchView;
    private GoogleMap mMap;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
    private boolean mLocationEnabled;
    private Location mLastKnownLocation;
    private SeekBar mKMProgressBar;
    private LatLng mCurrentPosition;
    private String mQueryText;
    private SearchPresenter mSearchPresenter;
    private static final int DEFAULT_ZOOM_LEVEL = 13;
    private int mZoomLevel = DEFAULT_ZOOM_LEVEL;
    private QueryChangeListener mQueryChangeListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeWidgets();
        addListeners();
        configureMvp();

    }

    @Override
    public void googleLocationHelperInitialized() {

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_map_currentLocation);
        supportMapFragment.getMapAsync(this);


    }

    @Override
    public void initializeWidgets() {

        mSearchView = (SearchView) findViewById(R.id.activity_sv_main);
        mKMProgressBar = (SeekBar) findViewById(R.id.activity_skb_radius);
        mKMProgressBar.setMax(MAX_PROGRESS);
        mKMProgressBar.setProgress(DEFAULT_PROGRESS);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        initializeMapWidget(googleMap);
        verifyLocationPermissions();
    }


    private void initializeMapWidget(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            mMap.setOnMapClickListener(this);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

        mCurrentPosition = latLng;
        mZoomLevel = (int) mMap.getCameraPosition().zoom;
        locateLocationInMap(latLng, mMap, mZoomLevel, getProgressInMeters());

    }

    private Location getDeviceLocation() throws SecurityException {

        return LocationServices.FusedLocationApi.
                getLastLocation(getGoogleApiInstance());


    }

    private void locateLocationInMap(LatLng location, GoogleMap map, int defaultZoom,
                                     int meterRadius) {

        if (location != null) {

            map.clear();
            map.addMarker(new MarkerOptions().position(location));
            CircleOptions options = new CircleOptions().radius(meterRadius).center(location);
            map.addCircle(options);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    location, defaultZoom));

        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(0, 0), defaultZoom));
            map.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private void enableLocationView(GoogleMap map, boolean locationEnabled) throws SecurityException {

        map.setMyLocationEnabled(locationEnabled);
        map.getUiSettings().setMyLocationButtonEnabled(locationEnabled);

    }

    private void verifyLocationPermissions() {

        mLocationEnabled = false;

        if (mMap == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLocationEnabled = true;
            configureMapForLocation();

        } else {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                UserAlerts.makeSimpleDialog(this, "The application requires you to access your Current location", this);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    public void configureMapForLocation() {

        mLastKnownLocation = getDeviceLocation();
        if (mLastKnownLocation != null) {
            mCurrentPosition = new LatLng(mLastKnownLocation.getLatitude(),
                    mLastKnownLocation.getLongitude());
        }
        enableLocationView(mMap, mLocationEnabled);
        locateLocationInMap(mCurrentPosition, mMap, mZoomLevel, getProgressInMeters());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLocationEnabled = true;
            configureMapForLocation();

        } else {
            UserAlerts.makeSimpleDialog(this, "The application requires you to access your Current location", this);
        }
    }

    @Override
    public void addListeners() {


        mQueryChangeListener = new QueryChangeListener();
        mSearchView.setOnQueryTextListener(mQueryChangeListener);
        mKMProgressBar.setOnSeekBarChangeListener(this);

    }

    @Override
    public Observable<String> getQueryTextObservable() {
        return Observable.create(mQueryChangeListener);
    }

    class QueryChangeListener implements SearchView.OnQueryTextListener,
            ObservableOnSubscribe<String> {

        private ObservableEmitter<String> mEmitter;

        @Override
        public void subscribe(ObservableEmitter<String> e) throws Exception {
            mEmitter = e;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(mEmitter != null){
                mEmitter.onNext(newText);
            }
            return true;
        }
    }


    @Override
    public double getLatitudeSelected() {
        return mCurrentPosition != null ? mCurrentPosition.latitude : 0;
    }

    @Override
    public double getLongitudeSelected() {
        return mCurrentPosition != null ? mCurrentPosition.longitude : 0;
    }


    @Override
    public float getSearchMeterRadiusSelected() {
        return mKMProgressBar != null ? getProgressInMeters() : 0;
    }

    @Override
    public void displayPlaces(ArrayList<? super IPlaceResult> result) {
        ArrayList<String> places = new ArrayList<>();
        inflateDataToDisplay(places, result);
        PlaceListDialog.showPlaceDialog(this, places);
    }

    private void inflateDataToDisplay(ArrayList<String> places, ArrayList<? super IPlaceResult> result) {

        for (Object place : result) {
            String placeName = ((IPlaceResult) place).getPlaceName();
            places.add(placeName);
        }
    }

    @Override
    public void noPlaceDetected(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void configureMvp() {

        mSearchPresenter = new SearchPresenter();
        mSearchPresenter.setSearchView(this);

        Log.i("TAG", "SEARCH QUERY: " + getGooglePlacesFetcher());

        if (getGooglePlacesFetcher() != null) {
            mSearchPresenter.setPlaceFetcher(getGooglePlacesFetcher());
            mSearchPresenter.initializeForRx();
        } else {
            Toast.makeText(this, "MVP failed", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
    }


    public int getProgressInMeters() {
        return mKMProgressBar.getProgress() * 1000;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        mZoomLevel = (int) mMap.getCameraPosition().zoom;

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        locateLocationInMap(mCurrentPosition, mMap, mZoomLevel, getProgressInMeters());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSearchPresenter != null){
            mSearchPresenter.cleanUp();
        }


    }
}
