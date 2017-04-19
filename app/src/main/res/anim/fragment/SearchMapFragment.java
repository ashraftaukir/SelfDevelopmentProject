package com.gagagugu.ggservice.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.models.Result;
import com.gagagugu.ggservice.utils.DialogManager;
import com.gagagugu.ggservice.utils.NetworkUtilGGS;
import com.gagagugu.ggservice.utils.PermissionHandler;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;
import com.gagagugu.ggservice.view.activity.ClusterItemGridViewActivity;
import com.gagagugu.ggservice.view.activity.ViewDetailsActivityGGS;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;

import java.util.ArrayList;


/**
 * Created by Moniruzzaman monir 4/march/2017
 */
public class SearchMapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnCameraIdleListener, ClusterManager.OnClusterItemInfoWindowClickListener<Result>, ClusterManager.OnClusterClickListener<Result>, ClusterManager.OnClusterInfoWindowClickListener<Result>, View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String accessToken;
    private String topLeft;
    private String bottomRight;
    private String mParam2;
    Handler handler;
    ArrayList<String> fields;
    String TAG = "MAPFragmentGGS";
    private DialogManager dialogManager;

    private ArrayList<Result> nhFeedResultList;

    public static final int REQUEST_ACCESS_FINE_LOCATION = 100;
    private Context mContext;
    String lat = "23.8103", lon = "90.4125";
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    // This is where the View is being Created!!!
    protected LocationRequest locationRequest;
    TextView addressTextView, locationDoneTextViewGGS;
    ImageView crossImageViewGGS;
    private GoogleMap mMap;
    boolean hasLocationPeermission = false;
    boolean isMarkerClicked = false;
    String address = "";
    SupportMapFragment fragment;
    SupportMapFragment mapFragment;
    private ClusterManager<Result> mClusterManager;
    private ClusterManager<Result> mClusterManager1;
    private ArrayList<Result> mProductResult;
    private ArrayList<Result> mServiceResult;
    public Result clickedClusterItem;
    private final int DELETE_REQUEST_CODE = 10092;
    FloatingActionButton myLocationFAB;
    Cluster<Result> cluster;
    String cProduct = "";
    String cService = "";
    private CameraPosition cp;
    private LinearLayout rootLayout;
    private ImageView imgBack;
    private View toolbar;
    public boolean isService;
    private PermissionHandler permissionHandler;
    String cNames = "";
    String pNames = "";

    public static com.gagagugu.ggservice.fragment.SearchMapFragment newInstance(Context context, String param1, String param2) {
        com.gagagugu.ggservice.fragment.SearchMapFragment fragment = new com.gagagugu.ggservice.fragment.SearchMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

//    private NeighborhoodFeedActivityGGS getParentActivity() {
//        return ((NeighborhoodFeedActivityGGS) getActivity());
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        if (getArguments() != null) {
            accessToken = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    /**
     * Method to initialize toolbar
     */
    public void setupToolbar(String title, View view) {
        //Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
       /* if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_ggs);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        }*/

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated");
        mContext = getActivity();
        permissionHandler = new PermissionHandler(getActivity());
        buildGoogleApiClient();
//        if (NeighborhoodFeedActivityGGS.isCrossLoggedDone)
        startMapFragment();
        myLocationFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyLocation();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG, "onAttach");
    }

    public void startMapFragment() {
        Log.e(TAG, "startMapFragment");
        accessToken = ServicePreference.getInstance(mContext).getServiceAccessToken();
        dialogManager = new DialogManager(getActivity());
        createMap();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.e(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_map_serach, container, false);
        setupToolbar("Location", rootView);
        initView(rootView);
        setThemeColor();
        myLocationFAB = (FloatingActionButton) rootView.findViewById(R.id.myLocationFAB);
        //getActivity().findViewById(R.id.search_view).setVisibility(View.GONE);
        return rootView;
    }

    private void setThemeColor() {
        String themeColor = ServicePreference.getInstance(getContext()).getThemeColor();
        ProfileColorsGGS profileColorsGGS = UtilsGGS.getProfileColor(themeColor);
        rootLayout.setBackgroundResource(profileColorsGGS.getBackground());
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated");
        //initView(view);

    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        MenuItem item = menu.findItem(R.id.filter_by);
//        item.setVisible(false);
//    }


    @Override
    public void onStart() {
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
        if (cp != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        if (mMap != null)
            cp = mMap.getCameraPosition();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "onDetach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView");
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION) {
            Log.d("Request Permissions ", " Accepted");
            hasLocationPeermission = false;
            Log.d("setMyLocationEnabled", "Called 3");
            setMyLocationButtonEnable();
        } else {
            Log.d("Request Permissions ", " Rejected");
            hasLocationPeermission = true;
        }

        for (int i = 0, len = permissions.length; i < len; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                // user rejected the permission
                boolean showRationale = shouldShowRequestPermissionRationale(permission);
                if (!showRationale) {
                    permissionHandler.showSettingsSnackbar(getResources().getString(R.string.permission_msg_location_ggs));
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "Connection to Location Service - onConnected");
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());
            Log.e(TAG, "onConnected LAT ___ LON : " + lat + "____" + lon);
        }
        if (cp == null) {
            setCameraToCurrentLocation();
        }

    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Connection to Location Service - onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection to Location Service - onConnectionFailed");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.i(TAG, "onMapReady > Map is ready");
        setMap();
    }

    @Override
    public void onMapLoaded() {
        Log.e(TAG, " onMapLoaded");
        setCameraToCurrentLocation();
        try {
            if (NetworkUtilGGS.isInternetAvailable(mContext)) {
//                getNHFeedData();
                getSarchResult();
            } else {
                dialogManager.showDialog(getString(R.string.failed_to_load_data_ggs), getString(R.string.text_no_internet_connection_ggs), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCameraIdle() {
        Log.e(TAG, "onCameraIdle > Camera is Idle now");
        if (!isMarkerClicked) {
            if (mMap != null) {
                if (NetworkUtilGGS.isInternetAvailable(mContext)) {
                } else {
                    dialogManager.showDialog(getString(R.string.failed_to_load_data_ggs), getString(R.string.text_no_internet_connection_ggs), true);
                }
            }
        } else {
            isMarkerClicked = false;
        }

    }


    private void getSarchResult() {
        if (UtilsGGS.resultArrayList == null || UtilsGGS.resultArrayList.size() <= 0) {
            dialogManager.showDialog(getString(R.string.failed_to_load_data_ggs), getString(R.string.text_no_internet_connection_ggs), true);

        } else {
            nhFeedResultList = (ArrayList<Result>) UtilsGGS.resultArrayList.clone();
            loadDataOnMap();
        }
    }

    GoogleMap.OnCameraIdleListener onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
        @Override
        public void onCameraIdle() {
            Log.e(TAG, "onCameraIdle > Camera is Idle now");
            if (mMap != null) {
                if (NetworkUtilGGS.isInternetAvailable(mContext)) {
                    getSarchResult();
                } else {
                    dialogManager.showDialog(getString(R.string.failed_to_load_data_ggs), getString(R.string.text_no_internet_connection_ggs), true);

                }
            }
        }
    };


    private void initView(View view) {
        rootLayout = (LinearLayout) view.findViewById(R.id.root_layout);
        imgBack = (ImageView) view.findViewById(R.id.imgBack);
        toolbar = view.findViewById(R.id.toolbarsearchmap);

        imgBack.setOnClickListener(this);
        toolbar.setOnClickListener(this);

    }

    private void createMap() {
        Log.e(TAG, " createMap");
        if (mMap == null) {
//            mMap = ((MapView) inflatedView.findViewById(R.id.mapView)).getMap();
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
            if (mapFragment == null) {
                mapFragment = SupportMapFragment.newInstance();
                getChildFragmentManager().beginTransaction().replace(R.id.map_container, mapFragment).commit();
            }
            mapFragment.getMapAsync(com.gagagugu.ggservice.fragment.SearchMapFragment.this);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void setCameraToCurrentLocation() {
        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)), 10));
        }
    }


    private void setMap() {
        mMap.setOnMapLoadedCallback(com.gagagugu.ggservice.fragment.SearchMapFragment.this);

        mClusterManager1 = new ClusterManager<Result>(mContext, mMap);
        mClusterManager = new ClusterManager<Result>(mContext, mMap);


        mClusterManager.setOnClusterItemInfoWindowClickListener(com.gagagugu.ggservice.fragment.SearchMapFragment.this);
        mClusterManager1.setOnClusterItemInfoWindowClickListener(com.gagagugu.ggservice.fragment.SearchMapFragment.this);

        mClusterManager.setOnClusterInfoWindowClickListener(com.gagagugu.ggservice.fragment.SearchMapFragment.this);
        mClusterManager1.setOnClusterInfoWindowClickListener(com.gagagugu.ggservice.fragment.SearchMapFragment.this);

//        mMap.setOnMarkerClickListener(mClusterManager);
//        mMap.setOnMarkerClickListener(mClusterManager1);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mClusterManager.onMarkerClick(marker);
                mClusterManager1.onMarkerClick(marker);
                return false;
            }
        });


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                mClusterManager.onInfoWindowClick(marker);
                mClusterManager1.onInfoWindowClick(marker);
            }
        });


        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mClusterManager1.onCameraChange(cameraPosition);
                mClusterManager.onCameraChange(cameraPosition);
            }
        });

        mClusterManager1.setRenderer(new com.gagagugu.ggservice.fragment.SearchMapFragment.CustomClusterRenderer(mContext, mMap, mClusterManager1));
        mClusterManager.setRenderer(new com.gagagugu.ggservice.fragment.SearchMapFragment.CustomClusterRenderer(mContext, mMap, mClusterManager));

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Result>() {
            @Override
            public boolean onClusterItemClick(Result item) {
                Log.e(TAG, "Type " + item.getType());
                mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
                clickedClusterItem = item;
                return false;
            }
        });

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Result>() {
            @Override
            public boolean onClusterClick(final Cluster<Result> cluster) {
                Log.e(TAG, "C Size " + cluster.getItems().size());
                for (Result result : cluster.getItems()) {
                    int i = 0;
                    if (i < 6) {
                        cNames += result.getTitle();
                        i++;
                    }
                }
                cService = cluster.getItems().size() + "";
                isService = true;
                mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
                return false;
            }
        });

        mClusterManager1.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Result>() {
            @Override
            public boolean onClusterItemClick(Result item) {
                Log.e(TAG, "Type " + item.getType());
                clickedClusterItem = item;
                mMap.setInfoWindowAdapter(mClusterManager1.getMarkerManager());
                return false;
            }
        });

        mClusterManager1.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Result>() {
            @Override
            public boolean onClusterClick(final Cluster<Result> cluster) {
                Log.e(TAG, "C Size " + cluster.getItems().size());
                for (Result result : cluster.getItems()) {
                    int i = 0;
                    if (i < 6) {
                        pNames += result.getTitle();
                        i++;
                    }
                }
                cProduct = cluster.getItems().size() + "";
                isService = false;
                mMap.setInfoWindowAdapter(mClusterManager1.getMarkerManager());
                return false;
            }
        });


        mClusterManager1.getMarkerCollection().setOnInfoWindowAdapter(new com.gagagugu.ggservice.fragment.SearchMapFragment.CustomMarkerInfoWindowAdapter(mContext));
        mClusterManager1.getClusterMarkerCollection().setOnInfoWindowAdapter(new com.gagagugu.ggservice.fragment.SearchMapFragment.CustomClusterInfoWindowAdapter(mContext));

        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new com.gagagugu.ggservice.fragment.SearchMapFragment.CustomMarkerInfoWindowAdapter(mContext));
        mClusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(new com.gagagugu.ggservice.fragment.SearchMapFragment.CustomClusterInfoWindowAdapter(mContext));

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
                    setMyLocationButtonEnable();
                }
                return;
            } else {
                Log.d("setMyLocationEnabled", "Called 2");
                setMyLocationButtonEnable();
            }
        }
    }


    private void loadDataOnMap() {
        if (nhFeedResultList != null && nhFeedResultList.size() > 0) {
//            ArrayList<Result> results = (ArrayList<Result>) nhFeedResultList;
            mServiceResult = new ArrayList<>();
            mProductResult = new ArrayList<>();
            for (Result result : nhFeedResultList) {
                if (result.getType().equalsIgnoreCase("service")) {
                    mServiceResult.add(result);
                } else {
                    mProductResult.add(result);
                }
            }

            mClusterManager.addItems(mServiceResult);
            mClusterManager1.addItems(mProductResult);


            mClusterManager.cluster();
            mClusterManager1.cluster();
        }
    }


    private void setMyLocationButtonEnable() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    private void getMyLocation() {
        LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onClusterItemInfoWindowClick(Result result) {
        String type = result.getType();
        String id = result.getId() + "";
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("type", type);

        Intent intent = new Intent(getContext(), ViewDetailsActivityGGS.class);
        intent.putExtra("bundle", bundle);
        startActivityForResult(intent, DELETE_REQUEST_CODE);
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Result> cluster) {
        UtilsGGS.resultArrayList = (ArrayList<Result>) cluster.getItems();
        Intent intent = new Intent(getContext(), ClusterItemGridViewActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onClusterClick(Cluster<Result> cluster) {
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == toolbar) {

        } else if (v == imgBack) {
            getActivity().onBackPressed();
        }
    }


    public class CustomClusterRenderer extends DefaultClusterRenderer<Result> {

        private final IconGenerator mIconGenerator;
        private ShapeDrawable mColoredCircleBackground;
        private SparseArray<BitmapDescriptor> mIcons = new SparseArray();
        private final float mDensity;
        private Context mContext;

        public CustomClusterRenderer(Context context, GoogleMap map,
                                     ClusterManager<Result> clusterManager) {
            super(context, map, clusterManager);


            this.mContext = context;
            this.mDensity = context.getResources().getDisplayMetrics().density;
            this.mIconGenerator = new IconGenerator(context);
            this.mIconGenerator.setContentView(this.makeSquareTextView(context));
            this.mIconGenerator.setTextAppearance(
                    com.google.maps.android.R.style.ClusterIcon_TextAppearance);
            this.mIconGenerator.setBackground(this.makeClusterBackground());
        }

        @Override
        protected void onBeforeClusterItemRendered(Result item, MarkerOptions markerOptions) {
            BitmapDescriptor markerDescriptor;
            if (item.getType().equalsIgnoreCase("Service")) {
                markerDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.service_map_pin_icon_ggs);
            } else {
                markerDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.product_map_pin_icon_ggs);
            }


            markerOptions.title(item.getTitle());
            super.onBeforeClusterItemRendered(item, markerOptions);
            markerOptions.icon(markerDescriptor);
        }

        @Override
        protected void onBeforeClusterRendered(final Cluster<Result> cluster,
                                               MarkerOptions markerOptions) {

//            int counterProduct = 0;
//            int serviceProduct = 0;
            int clusterColor = 0;
            ArrayList<Result> results = (ArrayList<Result>) cluster.getItems();
            for (Result reslt : results) {
                if (reslt.getType().equalsIgnoreCase("Service")) {
                    clusterColor = mContext.getResources().getColor(R.color.color_service);
//                    serviceProduct++;
                } else {
//                    counterProduct++;
                    clusterColor = mContext.getResources().getColor(R.color.color_product);

                }
            }

//            int clusterColor = 0;
//            if (counterProduct >= serviceProduct) {
//                clusterColor = mContext.getResources().getColor(R.color.color_product);
////                Log.e("RED", "productCount: " + counterProduct + " serviceCounter: " + serviceProduct);
//            }
//            if (counterProduct <= serviceProduct) {
//                clusterColor = mContext.getResources().getColor(R.color.color_service);
////                Log.e("BLUE", "Else productCount: " + counterProduct + " serviceCounter: " + serviceProduct);
//            }


            int bucket = this.getBucket(cluster);
            BitmapDescriptor descriptor = this.mIcons.get(bucket);
            if (descriptor == null) {
                this.mColoredCircleBackground.getPaint().setColor(clusterColor);
                descriptor = BitmapDescriptorFactory.fromBitmap(
                        this.mIconGenerator.makeIcon(this.getClusterText(bucket)));
                this.mIcons.put(bucket, descriptor);
            }

            markerOptions.icon(descriptor);
        }

        private SquareTextView makeSquareTextView(Context context) {
            SquareTextView squareTextView = new SquareTextView(context);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-2, -2);
            squareTextView.setLayoutParams(layoutParams);
            squareTextView.setId(com.google.maps.android.R.id.text);
            int twelveDpi = (int) (12.0F * this.mDensity);
            squareTextView.setPadding(twelveDpi, twelveDpi, twelveDpi, twelveDpi);
            return squareTextView;
        }

        private LayerDrawable makeClusterBackground() {
            // Outline color
            int clusterOutlineColor = mContext.getResources().getColor(R.color.white);

            this.mColoredCircleBackground = new ShapeDrawable(new OvalShape());
            ShapeDrawable outline = new ShapeDrawable(new OvalShape());
            outline.getPaint().setColor(clusterOutlineColor);
            LayerDrawable background = new LayerDrawable(
                    new Drawable[]{outline, this.mColoredCircleBackground});
            int strokeWidth = (int) (this.mDensity * 3.0F);
            background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth);
            return background;
        }

    }

    private class CustomMarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;
        private Context context;

        public CustomMarkerInfoWindowAdapter(Context context) {
            this.context = context;
            view = getActivity().getLayoutInflater().inflate(R.layout.info_window_layout, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            return null;
        }

        @Override
        public View getInfoWindow(final Marker marker) {

            final TextView title = ((TextView) view.findViewById(R.id.txt_title));
            final TextView snippet = ((TextView) view.findViewById(R.id.txt_price));

            if (clickedClusterItem.getType().equalsIgnoreCase("service")) {
                snippet.setTextColor(getResources().getColor(R.color.color_service));
            } else {
                snippet.setTextColor(getResources().getColor(R.color.color_product));
            }
            title.setText(clickedClusterItem.getTitle());
            if (clickedClusterItem.getType().toLowerCase().equals("item") && clickedClusterItem.getmTtemType().toLowerCase().equals("exchange"))
                snippet.setText("exchange");
            else
                snippet.setText(clickedClusterItem.getCurrency() + " " + clickedClusterItem.getmPrice());
            return view;
        }
    }

    private class CustomClusterInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;
        private Context context;

        public CustomClusterInfoWindowAdapter(Context context) {
            this.context = context;
            view = getActivity().getLayoutInflater().inflate(R.layout.infolayout_cluster, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            return null;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            final TextView namesTextView = ((TextView) view.findViewById(R.id.namesTextView));
            final TextView productTextView = ((TextView) view.findViewById(R.id.productTextView));
            final TextView serviceTextView = ((TextView) view.findViewById(R.id.serviceTextView));

            if (isService) {
                serviceTextView.setVisibility(View.VISIBLE);
                productTextView.setVisibility(View.GONE);
                namesTextView.setText(cNames);
                serviceTextView.setText(cService + " Services");

            } else {
                serviceTextView.setVisibility(View.GONE);
                productTextView.setVisibility(View.VISIBLE);
                namesTextView.setText(pNames);
                productTextView.setText(cProduct + " Products");
            }
            return view;
        }
    }

    private void getCurrentRadius() {
        Projection projection = mMap.getProjection();
        VisibleRegion currentView = projection.getVisibleRegion();


        //top-left corner
        double topleftlatitude = currentView.latLngBounds.northeast.latitude;
        double topleftlongitude = currentView.latLngBounds.southwest.longitude;
        topLeft = topleftlatitude + "," + topleftlongitude;
        Log.e("Cluster marker left: ", topLeft);


        //bottom-right corner
        double bottomrightlatitude = currentView.latLngBounds.southwest.latitude;
        double bottomrightlongitude = currentView.latLngBounds.northeast.longitude;
        bottomRight = bottomrightlatitude + "," + bottomrightlongitude;
        Log.e("Cluster marker right: ", bottomRight);


        LatLng cameraCenter = mMap.getCameraPosition().target;
        float[] projectionRadius = new float[1];
        Location.distanceBetween(currentView.farLeft.latitude, currentView.farLeft.longitude, cameraCenter.latitude, cameraCenter.longitude, projectionRadius);
        int viewRadius = Math.round((projectionRadius[0] / 1000.0f) + 0.5f);
        Log.e("Cluster marker RA: ", String.valueOf(viewRadius));
    }


}
