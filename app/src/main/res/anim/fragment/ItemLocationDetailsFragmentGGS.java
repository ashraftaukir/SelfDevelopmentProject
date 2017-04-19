package com.gagagugu.ggservice.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ItemLocationDetailsFragmentGGS extends Fragment implements OnMapReadyCallback {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_TITLE = "title";
    private static final String ARG_TYPE = "type";

    Double lat = 23.8103, lon = 90.4125;
    //    String lat = "", lon = "";
    private GoogleMap mMap;
    String title = "";
    String type = "";
    String TAG = "ItemLocationFragmentGGS";
    SupportMapFragment fragment;

    private Toolbar toolbar;

    public static com.gagagugu.ggservice.fragment.ItemLocationDetailsFragmentGGS newInstance(Double param1, Double param2, String title, String type) {
        com.gagagugu.ggservice.fragment.ItemLocationDetailsFragmentGGS fragment = new com.gagagugu.ggservice.fragment.ItemLocationDetailsFragmentGGS();
        Bundle args = new Bundle();
        args.putDouble(ARG_PARAM1, param1);
        args.putDouble(ARG_PARAM2, param2);
        args.putString(ARG_TITLE, title);
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
    public void onPause() {
        super.onPause();
//        setHasOptionsMenu(true);
    /*    if(type.equalsIgnoreCase("service")){
            ((ViewDetailsActivityGGS) getActivity()).setupToolbar(getString(R.string.service_details_ggs));
        }else {
            ((ViewDetailsActivityGGS) getActivity()).setupToolbar(getString(R.string.product_details_ggs));
        }*/
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
            lat = getArguments().getDouble(ARG_PARAM1);
            lon = getArguments().getDouble(ARG_PARAM2);
            title = getArguments().getString(ARG_TITLE);
            type = getArguments().getString(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_item_location_details_ggs, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        LinearLayout linearLayout= (LinearLayout) view.findViewById(R.id.root_layout);
        ProfileColorsGGS profileColorsGGS = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        linearLayout.setBackgroundResource(profileColorsGGS.getBackground());

        setupToolbar();
    }

    private void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_icon_ggs);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.location_details_ggs));
        }

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated");

    /*    ProfileColorsGGS profileColorsGGS = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if(type.equalsIgnoreCase("service")){
            ((ViewDetailsActivityGGS) getActivity()).setupToolbar(getString(R.string.service_details_ggs));

        }else {
            ((ViewDetailsActivityGGS) getActivity()).setupToolbar(getString(R.string.product_details_ggs));
        }*/
//        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), profileColorsGGS.getColorCodeLight()));
//        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), profileColorsGGS.getColorCodeLight()));
        ViewCompat.setElevation(toolbar, 0f);

        createMap();
    }

    private void setCameraToItemLocation() {
        if (mMap!= null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 16));
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.glocation_icon_ggs))
                    .title(title));
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        Log.i(TAG, "onMapReady > Map is ready");
        setCameraToItemLocation();
    }

    private void createMap() {

        if (mMap == null) {
            fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
            if (fragment == null) {
                fragment = SupportMapFragment.newInstance();
                getChildFragmentManager().beginTransaction().replace(R.id.map_container, fragment).commit();
            }
            fragment.getMapAsync(com.gagagugu.ggservice.fragment.ItemLocationDetailsFragmentGGS.this);
        }
    }
}
