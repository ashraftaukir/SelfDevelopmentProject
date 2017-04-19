package com.gagagugu.ggservice.fragment;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.adapter.PlacesAutoCompleteAdapter;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.utils.CustomFontTextViewGGS;
import com.gagagugu.ggservice.utils.LocationDividerItemDecorationGGS;
import com.gagagugu.ggservice.utils.LocationSearchRecyclerItemClickListenerGGS;
import com.gagagugu.ggservice.utils.PermissionHandler;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.SearchTextViewGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;
import com.gagagugu.ggservice.utils.WeakLocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link LocationPickerFragmentGGS.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link LocationPickerFragmentGGS#newInstance} factory method to
// * create an instance of this fragment.
// */
public class LocationPickerFragmentGGS extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback, GoogleMap.OnCameraIdleListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    Handler handler;
    public static final int REQUEST_ACCESS_FINE_LOCATION = 100;
    private Context mContext;
//    String lat = "23.8103", lon = "90.4125";
    String lat = "", lon = "";
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    protected LocationRequest locationRequest;

    CustomFontTextViewGGS addressTextView;
    TextView locationDoneTextViewGGS;
    ImageView crossImageViewGGS;

    private GoogleMap mMap;
    boolean hasLocationPermission = false;
    String TAG = "LPFragmentGGS";
    SupportMapFragment fragment;
    SearchTextViewGGS locationSearchEditText;
    boolean isUserSelected = false;
    RelativeLayout searchTextViewGGS;
    AutoCompleteTextView searchAutoCompleteTextView;
    private RecyclerView locationRecyclerView;
    private LinearLayout searchCollapsViewLayout, searchExpandViewLayout;
    private RelativeLayout locationAppBarLayout, defaultAppBarLayout;
    private LinearLayoutManager locationLinearLayoutManager;
    private PlacesAutoCompleteAdapter placesAutoCompleteAdapter;
    LatLng currentLatLng;
    String currentAddress;
    String currentCountry;
    BottomSheetBehavior behavior;
    View bottomSheet;
    GetAddressTask getAddressTask;
    boolean fromService;
    private LinearLayout locationPickerLinearLayout;
    private String mLocationSearchString = "";
    private boolean isBottomSheetExpanded = false;
    private boolean isLocationExist = false;
    private PermissionHandler permissionHandler;
    private Handler mHandler;
    WeakLocationListener weakLocationListener;

//    private static final LatLngBounds BOUNDS_BANGLADESH = new LatLngBounds(new LatLng(90, 24), new LatLng(90, 24));

//    public LocationPickerFragmentGGS() {
//        // Required empty public constructor
//    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocationPickerFragmentGGS.
     */
    // TODO: Rename and change types and number of parameters
    public static com.gagagugu.ggservice.fragment.LocationPickerFragmentGGS newInstance(Context context, String param1, String param2) {
        com.gagagugu.ggservice.fragment.LocationPickerFragmentGGS fragment = new com.gagagugu.ggservice.fragment.LocationPickerFragmentGGS();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
//        mContext = context;
        return fragment;
    }

//    @Override
//    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
//        Animation anim;
//        if (enter) {
//            anim = AnimationUtils.loadAnimation(getActivity(), R.anim.down_in);
//        } else {
//            anim = AnimationUtils.loadAnimation(getActivity(), R.anim.down_out);
//        }
//
//        anim.setAnimationListener(new Animation.AnimationListener() {
//            public void onAnimationEnd(Animation animation) {
//                Log.e(TAG, "onAnimationEnd");
//                createMap();
//                mGoogleApiClient.connect();
//            }
//
//            public void onAnimationRepeat(Animation animation) {
//            }
//
//            public void onAnimationStart(Animation animation) {
//                Log.e(TAG, "onAnimationStart");
//            }
//        });
//
//        return anim;
//    }

    @Override
    public void onStart() {
        locationAppBarLayout.setVisibility(View.VISIBLE);
        defaultAppBarLayout.setVisibility(View.GONE);
        Log.e(TAG, "onStart");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        locationAppBarLayout.setVisibility(View.VISIBLE);
        defaultAppBarLayout.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationAppBarLayout.setVisibility(View.GONE);
        defaultAppBarLayout.setVisibility(View.VISIBLE);

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
//            mHandlerThread.quit();
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.unregisterConnectionCallbacks(this);
            mGoogleApiClient.unregisterConnectionFailedListener(this);

            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, weakLocationListener);
            }

            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }

//        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        weakLocationListener = new WeakLocationListener(this);
        mContext = getActivity();
        permissionHandler = new PermissionHandler(getActivity());
        // Create an instance of GoogleAPIClient.

        fromService = getArguments().getBoolean("from_service");
        Log.d("from_service", String.valueOf(fromService));
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();

            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            locationRequest.setInterval(3 * 1000);
//            locationRequest.setFastestInterval(1000);
        }

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            } else {
                Log.d(TAG, "setMyLocationEnabled Called 1");
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    currentAddress = String.valueOf(mLastLocation.getLatitude()) + " --- " + String.valueOf(mLastLocation.getLongitude());
                    Log.d(TAG, "mLastLocation : " + currentAddress);
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                            .title("My Location"));
                }

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                        .title("My Location"));
//                    setCameraToCurrentLocation();
            }
            return;
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                currentAddress = String.valueOf(mLastLocation.getLatitude()) + " --- " + String.valueOf(mLastLocation.getLongitude());
                Log.d(TAG, "mLastLocation : " + currentAddress);
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                        .title("My Location"));
            }
        }



    }

    private void setupTheme() {
        ProfileColorsGGS profileColorsGGS = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.search_controler_rect_ggs);
        drawable.setColorFilter(profileColorsGGS.getColorCodeLight(), PorterDuff.Mode.SRC);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            locationPickerLinearLayout.setBackgroundDrawable(drawable);

        } else {
            locationPickerLinearLayout.setBackground(drawable);

        }

    }


        /**
         * Callback received when a permissions request has been completed.
         */
        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            if (requestCode == REQUEST_ACCESS_FINE_LOCATION) {
                Log.d("Request Permissions ", " Accepted");
                hasLocationPermission = false;
            } else {
                Log.d("Request Permissions ", " Rejected");
                hasLocationPermission = true;
            }

            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    boolean showRationale = shouldShowRequestPermissionRationale( permission );
                    if (! showRationale) {
                        permissionHandler.showSettingsSnackbar(getResources().getString(R.string.permission_msg_location_ggs));
                    }
                }
            }
        }

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState){
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_location_picker_ggs, container, false);
        }

        @Override
        public void onActivityCreated (@Nullable Bundle savedInstanceState){
            super.onActivityCreated(savedInstanceState);
            Log.e(TAG, "onActivityCreated");
            initializeViews();
            setupTheme();

//            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
//                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)
//                    .build();
//            searchAutoCompleteTextView.setAdapter(placesAutoCompleteAdapter);

            locationLinearLayoutManager = new LinearLayoutManager(this.getActivity());
            locationRecyclerView.setLayoutManager(locationLinearLayoutManager);
            placesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this.getActivity(), R.layout.location_searchview_adapter_ggs,
                    mGoogleApiClient, null, null);
            locationRecyclerView.setAdapter(placesAutoCompleteAdapter);
            locationRecyclerView.addItemDecoration(new LocationDividerItemDecorationGGS(mContext));

            createMap();

            locationSearchEditText.getSearchEditText().addTextChangedListener(new TextWatcher() {

                public void onTextChanged(final CharSequence s, int start, int before, int count) {
//                    Log.i("onTextChanged", "String : " + s);
                    mLocationSearchString = s.toString();
                    if (!s.toString().equals("") && s.length() > 0 && mGoogleApiClient.isConnected()) {

                        if (mHandler != null) {
                            mHandler.removeCallbacksAndMessages(null);
                        } else {
                            mHandler = new Handler();
                        }

                        mHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                // Background thread
                                placesAutoCompleteAdapter.getFilter().filter(s.toString());
                                locationRecyclerView.setVisibility(View.VISIBLE);
                                placesAutoCompleteAdapter.notifyDataSetChanged();
                                mLocationSearchString = s.toString();

                                // Post to Main Thread
//                                mHandler.sendEmptyMessage(1);
                            }
                        }, 500);

                    } else if (!mGoogleApiClient.isConnected()) {
//                        Toast.makeText(mContext, "API_NOT_CONNECTED", Toast.LENGTH_SHORT).show();
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                public void afterTextChanged(Editable s) {
                    Log.i("afterTextChanged", "String : " + s);
                }
            });

            locationRecyclerView.addOnItemTouchListener(
                    new LocationSearchRecyclerItemClickListenerGGS(mContext, new LocationSearchRecyclerItemClickListenerGGS.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
//                            if (placesAutoCompleteAdapter != null && placesAutoCompleteAdapter.)
                            final PlacesAutoCompleteAdapter.PlaceAutocomplete item = placesAutoCompleteAdapter.getItem(position);
                            final String placeId = String.valueOf(item.placeId);
                            Log.i("TAG", "Autocomplete item selected: " + item.description);
                            Log.i("TAG", "Called getPlaceById to get Place details for " + item.placeId);
                            currentAddress = String.valueOf(item.description);
                        /*
                             Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
                         */

                            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                    .getPlaceById(mGoogleApiClient, placeId);
                            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                                @Override
                                public void onResult(PlaceBuffer places) {
                                    if (places.getCount() == 1) {
                                        isUserSelected = true;
                                        String placeAddress = String.valueOf(places.get(0).getAddress());
                                        currentLatLng = places.get(0).getLatLng();
                                        lat = String.valueOf(places.get(0).getLatLng().latitude);
                                        lon = String.valueOf(places.get(0).getLatLng().longitude);

                                        if (currentAddress != null && currentAddress.length() > 0) {
                                            if (fromService) {
//                                                CreateServiceDetailsGGS.useraddress = currentAddress;
//                                                CreateServiceDetailsGGS.userLat = lat;
//                                                CreateServiceDetailsGGS.userLon = lon;
                                            } else {
//                                                CreateProductDetailsGGS.useraddress = currentAddress;
//                                                CreateProductDetailsGGS.userLat = lat;
//                                                CreateProductDetailsGGS.userLon = lon;
                                            }
                                        }

                                        getAddressTask = new GetAddressTask(mContext);
                                        getAddressTask.execute(currentLatLng);

                                        addressTextView.setText(currentAddress);

                                        setCameraToCurrentLocation();
                                        locationSearchEditText.clearFocus();
                                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                                    hideKeyboard();
                                        Log.i(TAG, "Place Address : " + placeAddress);

//                                    Toast.makeText(mContext, String.valueOf(places.get(0).getLatLng()), Toast.LENGTH_SHORT).show();
                                    } else {
//                                    Toast.makeText(mContext, "SOMETHING_WENT_WRONG", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    })
            );

            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int screenHeight = displaymetrics.heightPixels;

            bottomSheet = getView().findViewById(R.id.locationBottomSheet);
            ViewGroup.LayoutParams params = bottomSheet.getLayoutParams();
            params.height = screenHeight;
            bottomSheet.setLayoutParams(params);

            behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case BottomSheetBehavior.STATE_DRAGGING:
                            Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGING");
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:
                            Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
                            if (!isBottomSheetExpanded){
                                mLocationSearchString = currentAddress;
                            }
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:
                            isBottomSheetExpanded = true;
                            Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
//                        searchCollapsViewLayout.setVisibility(View.GONE);
//                        searchExpandViewLayout.setVisibility(View.VISIBLE);

//                        searchCollapsViewLayout.setVisibility(View.GONE);
//                        searchExpandViewLayout.setVisibility(View.VISIBLE);

                            locationSearchEditText.getSearchEditText().requestFocus();
                            locationSearchEditText.getSearchEditText().setText(mLocationSearchString);
                            locationSearchEditText.getSearchEditText().setSelection(locationSearchEditText.getSearchEditText().getText().length());

                            searchCollapsViewLayout.setVisibility(View.GONE);
                            searchExpandViewLayout.setVisibility(View.VISIBLE);

//                            searchCollapsViewLayout.setVisibility(View.GONE);
//                            searchExpandViewLayout.setVisibility(View.VISIBLE);
                            //showKeyboard();
                            break;
                        case BottomSheetBehavior.STATE_COLLAPSED:
                            isBottomSheetExpanded = false;
                            hideKeyboard();
                            Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                            searchCollapsViewLayout.setVisibility(View.VISIBLE);
                            searchExpandViewLayout.setVisibility(View.GONE);
//                        isUserSelected = false;
                            changeUserSelectionFlag();
                            break;
                        case BottomSheetBehavior.STATE_HIDDEN:
                            Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_HIDDEN");
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    Log.i("BottomSheetCallback", "slideOffset: " + slideOffset);
                    if (slideOffset > 0.6) {
                        searchCollapsViewLayout.setVisibility(View.GONE);
                        searchExpandViewLayout.setVisibility(View.VISIBLE);
                    } else {

                    }
                }
            });

            searchTextViewGGS.setOnClickListener(searchOnClickListener);
        }

    public void initializeViews() {
//        searchAutoCompleteTextView = (AutoCompleteTextView) getView().findViewById(R.id.searchAutoCompleteTextView);
        searchTextViewGGS = (RelativeLayout) getView().findViewById(R.id.sSearchTextViewGGS);
        searchCollapsViewLayout = (LinearLayout) getView().findViewById(R.id.searchCollapsViewLayout);
        searchExpandViewLayout = (LinearLayout) getView().findViewById(R.id.searchExpandViewLayout);
        locationAppBarLayout = (RelativeLayout) getActivity().findViewById(R.id.locationAppBarLayout);
        defaultAppBarLayout = (RelativeLayout) getActivity().findViewById(R.id.defaultAppBarLayout);

        defaultAppBarLayout = (RelativeLayout) getActivity().findViewById(R.id.defaultAppBarLayout);
        locationDoneTextViewGGS = (TextView) getActivity().findViewById(R.id.locationDoneTextViewGGS);
        crossImageViewGGS = (ImageView) getActivity().findViewById(R.id.crossImageViewGGS);

        locationSearchEditText = (SearchTextViewGGS) getView().findViewById(R.id.searchEditText);
        locationRecyclerView = (RecyclerView) getView().findViewById(R.id.locationRecyclerView);

        locationPickerLinearLayout=(LinearLayout)getView().findViewById(R.id.location_picker_linear_layout);
        addressTextView = (CustomFontTextViewGGS) getView().findViewById(R.id.addressTextView);

        locationAppBarLayout.setVisibility(View.VISIBLE);
        defaultAppBarLayout.setVisibility(View.GONE);

        crossImageViewGGS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((getActivity().getWindow().getDecorView().getApplicationWindowToken()), 0);
                getActivity().getSupportFragmentManager().popBackStack();
//                CreateServiceDetailsGGS.useraddress = currentAddress;
                com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.userLat = lat;
                com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.userLon = lon;
//                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        locationDoneTextViewGGS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((getActivity().getWindow().getDecorView().getApplicationWindowToken()), 0);

                if (currentAddress != null && currentAddress.length() > 0) {
                    if (fromService) {
                        com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.userCountry = currentCountry;
                        com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.useraddress = currentAddress;
                        com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.userLat = lat;
                        com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.userLon = lon;
                    } else {
                        com.gagagugu.ggservice.fragment.CreateProductDetailsGGS.userCountry = currentCountry;
                        com.gagagugu.ggservice.fragment.CreateProductDetailsGGS.useraddress = currentAddress;
                        com.gagagugu.ggservice.fragment.CreateProductDetailsGGS.userLat = lat;
                        com.gagagugu.ggservice.fragment.CreateProductDetailsGGS.userLon = lon;
                    }
                }

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    Runnable changeFlagRunnable = new Runnable() {
        @Override
        public void run() {
            isUserSelected = false;
        }
    };

    private void changeUserSelectionFlag() {
        handler = new Handler();
        handler.postDelayed(changeFlagRunnable, 1000);
    }

    View.OnClickListener searchOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            searchCollapsViewLayout.setVisibility(View.GONE);
            searchExpandViewLayout.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.useraddress.contains("No address")){
            Log.e(TAG, "onLocationChanged" + location.getLatitude() + "----" + location.getLongitude());
            lat = String.valueOf(location.getLatitude());
            lon = String.valueOf(location.getLongitude());
            try {
                getAddress(location.getLatitude(), location.getLongitude());
            } catch (IOException e) {
                e.printStackTrace();
            }

            setCameraToCurrentLocation();
        }
    }

    private void setCameraToCurrentLocation() {
        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)), 14));
        }
    }

    private void setCameraToExistingLocation() {
        Log.e(TAG, "ExistingLocation" + com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.userLat + " -- " + com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.userLon);
        if (mMap != null) {
            if (fromService){
                if (com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.userLat.length() > 3 && com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.userLon.length() >3){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.userLat), Double.parseDouble(com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.userLon)), 14));
                    isUserSelected = false;
                }
            } else {
                if (com.gagagugu.ggservice.fragment.CreateProductDetailsGGS.userLat.length() > 3 && com.gagagugu.ggservice.fragment.CreateProductDetailsGGS.userLon.length() >3){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(com.gagagugu.ggservice.fragment.CreateProductDetailsGGS.userLat), Double.parseDouble(com.gagagugu.ggservice.fragment.CreateProductDetailsGGS.userLon)), 14));
                    isUserSelected = false;
                }
            }

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "Connection to Location Service - onConnected");

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        builder.build()
                );

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_ACCESS_FINE_LOCATION);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (fromService){
            if (com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.useraddress.contains("No address") || com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.useraddress.contains("No Location") || com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.useraddress.equalsIgnoreCase("")){
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    lat = String.valueOf(mLastLocation.getLatitude());
                    lon = String.valueOf(mLastLocation.getLongitude());
                    try {
                        getAddress(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                isUserSelected = true;
                addressTextView.setText(com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.useraddress);
                setCameraToExistingLocation();
            }
        } else {
            if (com.gagagugu.ggservice.fragment.CreateProductDetailsGGS.useraddress.contains("No address") || com.gagagugu.ggservice.fragment.CreateProductDetailsGGS.useraddress.contains("No Location") || com.gagagugu.ggservice.fragment.CreateProductDetailsGGS.useraddress.equalsIgnoreCase("")){
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    lat = String.valueOf(mLastLocation.getLatitude());
                    lon = String.valueOf(mLastLocation.getLongitude());
                    try {
                        getAddress(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                isUserSelected = true;
                addressTextView.setText(com.gagagugu.ggservice.fragment.CreateProductDetailsGGS.useraddress);
                setCameraToExistingLocation();
            }
        }


    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, weakLocationListener);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, weakLocationListener);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Connection to Location Service - onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection to Location Service - onConnectionFailed");
    }

    public void getAddress(double latitude, double longitude) throws IOException {
        if (latitude == 0.0 || longitude == 0.0) {
            currentAddress = "No address found";
        } else {
            Log.e(TAG, "Getting Address of : " + latitude + "---" + longitude);
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(mContext, Locale.getDefault());

            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses != null && addresses.size() > 0) {
                currentAddress = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                if (city == null || city.equalsIgnoreCase("null"))
                    city = "";
                else city += ", ";

                String state = addresses.get(0).getAdminArea();
                if (state == null || state.equalsIgnoreCase("null"))
                    state = "";
                else state += ", ";

                String country = addresses.get(0).getCountryName();
                if (country == null || country.equalsIgnoreCase("null"))
                    country = "";
                else country += ", ";

                String postalCode = addresses.get(0).getPostalCode();
                if (postalCode == null || postalCode.equalsIgnoreCase("null"))
                    postalCode = "";
                else postalCode += ", ";

                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                if (knownName == null || knownName.equalsIgnoreCase("null"))
                    knownName = "";
                else knownName += ", ";

                Log.e(TAG, "Address : " + currentAddress);
                Log.e(TAG, "KnownName : " + knownName);
                Log.e(TAG, "City : " + city);
                Log.e(TAG, "State : " + state);
                Log.e(TAG, "Country : " + country);
                Log.e(TAG, "PostalCode : " + postalCode);
//        address = latitude+","+longitude+"\n" + knownName + "," + city + "," + state + "," + country + "," + postalCode;
                currentAddress = knownName +  city +  state +  country + postalCode;
                if (currentAddress.substring(currentAddress.length() - 1).equalsIgnoreCase(",")){
                    currentAddress = currentAddress.substring(0, currentAddress.length()-1);
                }
                if (fromService) {
//                    CreateServiceDetailsGGS.userCountry = addresses.get(0).getCountryName();
                    currentCountry = addresses.get(0).getCountryName();
                } else {
//                    CreateProductDetailsGGS.userCountry = addresses.get(0).getCountryName();
                    currentCountry = addresses.get(0).getCountryName();

                }
            } else {
                currentAddress = "No address found";
            }
        }

        addressTextView.setText("");
        addressTextView.setText(currentAddress);
//        CreateServiceDetailsGGS.useraddress = currentAddress;
        isUserSelected = false;
    }

    private class GetAddressTask extends AsyncTask<LatLng, Void, String> {
        Context mContext;

        public GetAddressTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected String doInBackground(LatLng... params) {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            // Get the current location from the input parameter list
            LatLng latLng = params[0];
            // Create a list to contain the result address
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            } catch (IOException e1) {
                Log.e("Location ", "IO Exception in getFromLocation()");
                e1.printStackTrace();
                return ("No address found");
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Illegal arguments " +
                        Double.toString(latLng.latitude) +
                        " , " +
                        Double.toString(latLng.longitude) +
                        " passed to address service";
                Log.e("Location ", errorString);
                e2.printStackTrace();
                return "No address found";
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                String country = addresses.get(0).getCountryName();
                currentAddress = addresses.get(0).getAddressLine(0);

                if(fromService) {
//                    CreateServiceDetailsGGS.userCountry = country;
                    currentCountry = country;
                } else {
//                    CreateProductDetailsGGS.userCountry = country;
                    currentCountry = country;

                }
                Log.i(TAG, "Place Address from task: " + currentAddress.toString() + " Country : " + country);
//                CreateServiceDetailsGGS.useraddress = currentAddress;
                return currentAddress.toString();
            } else {
                return "No address found";
            }
        }

        @Override
        protected void onPostExecute(String address) {
            if (!isCancelled()) {
//                Toast.makeText(mContext, address, Toast.LENGTH_SHORT).show();
//                addressTextView.setText(address);
//
//                if (mGoogleApiClient.isConnected())
//                    stopLocationUpdates();
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.i(TAG, "onMapReady > Map is ready");
        setMap();
    }

    private void createMap() {

        if (mMap == null) {
//            mMap = ((MapView) inflatedView.findViewById(R.id.mapView)).getMap();
            fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
            if (fragment == null) {
                fragment = SupportMapFragment.newInstance();
                getChildFragmentManager().beginTransaction().replace(R.id.map_container, fragment).commit();
            }
            fragment.getMapAsync(com.gagagugu.ggservice.fragment.LocationPickerFragmentGGS.this);
        }
    }

    private void setMap() {
        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.d(TAG, "requestPermissions Called 1");
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                } else {
                    Log.d(TAG, "setMyLocationEnabled Called 1");
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.setOnCameraIdleListener(com.gagagugu.ggservice.fragment.LocationPickerFragmentGGS.this);
//                    setCameraToCurrentLocation();
                }
                return;
            }

            Log.d("setMyLocationEnabled", "Called 2");
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setOnCameraIdleListener(com.gagagugu.ggservice.fragment.LocationPickerFragmentGGS.this);
        }

    }

    @Override
    public void onCameraIdle() {
        Log.e(TAG, "onCameraIdle > Camera is Idle now");
        if (mMap != null) {
//            if (!isUserSelected){
            LatLng centerOfMap = mMap.getCameraPosition().target;
            // Update your Marker's position to the center of the Map.
//            marker.setPosition(centerOfMap);
            try {
                if (!isUserSelected) {
                    Log.e(TAG, "onCameraIdle > getting address");
                    lat = centerOfMap.latitude+"";
                    lon = centerOfMap.longitude+"";
                    getAddress(centerOfMap.latitude, centerOfMap.longitude);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
//            }
        }
    }

    private void showKeyboard() {
        locationSearchEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(locationSearchEditText, InputMethodManager.SHOW_IMPLICIT);
//        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    private void hideKeyboard() {
        locationSearchEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInput(locationSearchEditText, InputMethodManager.SHOW_IMPLICIT);
//
//        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        imm.hideSoftInputFromWindow(locationSearchEditText.getWindowToken(), 0);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
