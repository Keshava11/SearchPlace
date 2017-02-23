package com.mfsi.searchplace.placedisplayer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mfsi.searchplace.R;

import java.util.ArrayList;

/**
 * Created by Bhaskar Pande on 2/21/2017.
 */
public class PlaceListDialog extends DialogFragment {

    private static final String TAG = "PlacesList";

    public static DialogFragment showPlaceDialog(FragmentActivity hostActivity,
                                                 ArrayList<String> places){

        FragmentManager manager = hostActivity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment fragment = manager.findFragmentByTag(TAG);

        if(fragment != null){
            transaction.remove(fragment).commit();
        }

        PlaceListDialog placeListDialog = new PlaceListDialog();
        placeListDialog.addPlacesList(places);
        placeListDialog.show(manager, TAG);

        return placeListDialog;


    }


    private ListView mListView;
    private ArrayList<String> mPlacesList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void addPlacesList(ArrayList<String> list){
        mPlacesList.addAll(list);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.places_dialog, container, false);

        mListView = (ListView)(view.findViewById(R.id.placeList_lsv_places));
        mListView.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.place_listitem,
                R.id.listItem_txv_place,mPlacesList));

        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
