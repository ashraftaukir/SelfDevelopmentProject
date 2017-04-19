package com.gagagugu.ggservice.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gagagugu.ggservice.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link LocationPickerFragmentGGS.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link LocationPickerFragmentGGS#newInstance} factory method to
// * create an instance of this fragment.
// */
public class ItemLocationFragmentGGS extends Fragment implements OnMapReadyCallback {
    private static final String ARG_LAT = "param1";
    private static final String ARG_LON = "param2";
    private static final String ARG_TYPE = "type";

    private String type;
    private OnItemLocationFragmentInteractionListener mListener;

    // Initially Starting the Map and Everything
    public static final int REQUEST_ACCESS_FINE_LOCATION = 100;
    Double lat = 23.8103, lon = 90.4125;
    //    String lat = "", lon = "";
    private GoogleMap mMap;
    String TAG = "ItemLocationFragmentGGS";
    SupportMapFragment fragment;

    // Container Activity must implement this interface
    public interface OnItemLocationFragmentInteractionListener {
        public void OnItemLocationMarkerClicked(String type);
    }

    public static com.gagagugu.ggservice.fragment.ItemLocationFragmentGGS newInstance(Double lat, Double lon, String type) {
        com.gagagugu.ggservice.fragment.ItemLocationFragmentGGS fragment = new com.gagagugu.ggservice.fragment.ItemLocationFragmentGGS();
        Bundle args = new Bundle();
        args.putDouble(ARG_LAT, lat);
        args.putDouble(ARG_LON, lon);
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        Log.e(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        if (getArguments() != null) {
            lat = getArguments().getDouble(ARG_LAT);
            lon = getArguments().getDouble(ARG_LON);
            type = getArguments().getString(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_location_ggs, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated");

        RelativeLayout mapLayout = (RelativeLayout) getView().findViewById(R.id.map_layout);
        ImageView markerImageView = (ImageView) getView().findViewById(R.id.markerImageView);

        markerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.OnItemLocationMarkerClicked(type);
            }
        });



        createMap();

    }

    private void setCameraToItemLocation() {
        if (mMap!= null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 16));
        }

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnItemLocationFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        Log.i(TAG, "onMapReady > Map is ready");

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mListener.OnItemLocationMarkerClicked(type);
            }
        });
        setCameraToItemLocation();
    }

    private void createMap() {

        if (mMap == null) {
            fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
            if (fragment == null) {
                fragment = SupportMapFragment.newInstance();
                getChildFragmentManager().beginTransaction().replace(R.id.map_container, fragment).commit();
            }
            fragment.getMapAsync(com.gagagugu.ggservice.fragment.ItemLocationFragmentGGS.this);
        }
    }
}
