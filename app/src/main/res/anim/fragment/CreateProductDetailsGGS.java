package com.gagagugu.ggservice.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.adapter.UploadMediaRecyclerViewAdapterGGS;
import com.gagagugu.ggservice.asynctasks.GetAddressTask;
import com.gagagugu.ggservice.asynctasks.UploadMediaAsyncTask;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.interfaces.GetAddressCallBackGGS;
import com.gagagugu.ggservice.interfaces.MediaUploadCallback;
import com.gagagugu.ggservice.interfaces.OnStartDragListenerGGS;
import com.gagagugu.ggservice.interfaces.TextWatcherOnTextChanged;
import com.gagagugu.ggservice.media.BitmapFileCreatorTask;
import com.gagagugu.ggservice.media.MediaCreator;
import com.gagagugu.ggservice.models.CurrencyGGS;
import com.gagagugu.ggservice.models.GGProduct;
import com.gagagugu.ggservice.models.MediaGgs;
import com.gagagugu.ggservice.models.MediaImageGgs;
import com.gagagugu.ggservice.models.UploadMediaGGS;
import com.gagagugu.ggservice.utils.CustomTextWatcher;
import com.gagagugu.ggservice.utils.DialogManager;
import com.gagagugu.ggservice.utils.PermissionHandler;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.SimpleItemTouchHelperCallbackGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;
import com.gagagugu.ggservice.view.activity.CreateProductPostActivityGGS;
import com.gagagugu.ggservice.view.customviews.ThemeSwitchGGS;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateProductDetailsGGS extends Fragment implements GetAddressCallBackGGS,View.OnClickListener, MediaCreator.OnMediaCreated, BitmapFileCreatorTask.FileCreationListener, OnStartDragListenerGGS, UploadMediaRecyclerViewAdapterGGS.MediaUploadOnClickCallBack, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, CompoundButton.OnCheckedChangeListener, View.OnTouchListener, TextWatcherOnTextChanged,MediaUploadCallback {
    private static final int DIRECTION_UP = -1;
    String category;
    // Context context;

    private static final int TEXT_WATCHER_CODE_FOR_TITLE_ET = 1;
    private static final int TEXT_WATCHER_CODE_FOR_DESCRIPTION_ET = 2;
    Context context;
    private static final int REQUEST_CAMERA_PERMISSION = 110;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int REQUEST_CAMERA_AND_STORAGE = 158;
    public static final int IMAGE_COMPRESS_QUALITY = 100;
    private static final int EXCHANGE_DETAILS_REQUEST_CODE = 29102;
    private ScrollView createServiceDetailsScrollView;

    public static final int RATE_BOTTOM_REQUEST_CODE = 9901;
    private final static int CURRENCY_REQUEST_CODE = 1001;

    String lat = "23.8103", lon = "90.4125";

    private DialogManager dialogManager;

    Button nextBottom;
    boolean hasLocationPeermission = false;


    UploadMediaRecyclerViewAdapterGGS adapter;
    RecyclerView uploadMediaRecyclerView;
   // ArrayList<MediaImageGgs> mediaImageGgsArrayList = new ArrayList<>();
    //ArrayList<MediaGgs> mediaList;
    ArrayList<MediaGgs> mediaList;


    private EditText titleEt, descriptionEt;
    private ThemeSwitchGGS negotiationAllowSwitch;
    private boolean updateCurrencyFlag;
    private TextView categoryNameTv, productIntroText, productTitleTv, productDescTv, addressTextView, createServiceCurrencyTextView, rateTv, ratePerValueTextView, rateTextTv;
    private MediaCreator mMediaCreator;
    private MediaCreator.Option mOption;
    private ItemTouchHelper mItemTouchHelper;
    String address = "";
    private RelativeLayout layout_category_title;

    public static final int REQUEST_ACCESS_FINE_LOCATION = 100;
    private Bundle currencyBundle;
    private String mSelectedImagePath;
    private ProfileColorsGGS theme;
    String TAG = "LocationTAG";

    LinearLayout editCategory, count_product_charecter;
    TextView postButton;

    public static String useraddress = "";
    public static String userLat = "";
    public static String userLon = "";
    public static String userCountry = "";

    RelativeLayout currencyLayout, locationLayout, exchangeLayout;
    private LinearLayout negotiationLayout, rateLayout;
    private TextView currentCountTitle, totalCountTitle, currentCountDescription, totalCountDescription, exchangeDetails;
    private String accessToken;
    private PermissionHandler permissionHandler;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    // This is where the View is being Created!!!
    protected LocationRequest locationRequest;
//    GetAddressTask getAddressTask;
    private String errorMessage;
    private FrameLayout appBarLayout;
    private boolean isShadowExist;


    public CreateProductDetailsGGS() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_product_details_ggs, container, false);
        accessToken = ServicePreference.getInstance(getActivity()).getServiceAccessToken();
        category = getArguments().getString("category");
        theme = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        currencyBundle = new Bundle();
        context = getActivity();
        dialogManager=new DialogManager(getActivity());

        initializeMediaList();
        initViews(view);
        initMediaCreator();
        setupViewWithValue();
        setupForEditProduct();
        addScrollListner();
        addBackStackListner();
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                View view = getActivity().getCurrentFocus();
                if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
                    int scrcoords[] = new int[2];
                    view.getLocationOnScreen(scrcoords);
                    float x = ev.getRawX() + view.getLeft() - scrcoords[0];
                    float y = ev.getRawY() + view.getTop() - scrcoords[1];
                    if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((getActivity().getWindow().getDecorView().getApplicationWindowToken()), 0);
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        buildGoogleApiClient();
        createLocationRequest();

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            } else {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                updateLocationUI();
            }
            return;
        }
    }

    private void addBackStackListner() {
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

                if (getFragmentManager() != null) {
                    int size = getFragmentManager().getBackStackEntryCount();
                    if (size == 1) {
                        if (isShadowExist) {
                            appBarLayout.setBackgroundColor(theme.getColorCodeLight());
                            ViewCompat.setElevation(appBarLayout, 10f);
                        }
                    }
                }
            }
        });
    }


    private void addScrollListner() {
        createServiceDetailsScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (getActivity() == null) {
                    return;
                }


                if (createServiceDetailsScrollView.canScrollVertically(DIRECTION_UP)) {
                    if (!isShadowExist) {
                        appBarLayout.setBackgroundColor(theme.getColorCodeLight());
                        ViewCompat.setElevation(appBarLayout, 10f);
                        isShadowExist = true;
                    }
                } else {
                    if (isShadowExist) {
                        appBarLayout.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
                        ViewCompat.setElevation(appBarLayout, 0f);
                        isShadowExist = false;
                    }
                }

            }
        });


    }

    private void updateLocationUI() {
        if (mLastLocation != null && !UtilsGGS.getGgProductEditFlag(getActivity())) {
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());
            Log.e(TAG, "updateLocationUI LAT ___ LON : " + lat + "____" + lon);

//            getAddressTask = new GetAddressTask(getActivity());
//            getAddressTask.execute(mLastLocation);

            new GetAddressTask(getActivity(), com.gagagugu.ggservice.fragment.CreateProductDetailsGGS.this, mLastLocation).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    }

    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000000);
        locationRequest.setFastestInterval(100);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void initViews(View view) {
        permissionHandler = new PermissionHandler(getActivity());

        uploadMediaRecyclerView = (RecyclerView) view.findViewById(R.id.upload_media_recycler_view);
        adapter = new UploadMediaRecyclerViewAdapterGGS(UtilsGGS.getGgProduct(getActivity()).getMedia(), theme.getColorCodeLight(), this, UtilsGGS.getGgProductEditFlag(getContext()));
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallbackGGS(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(uploadMediaRecyclerView);

        editCategory = (LinearLayout) view.findViewById(R.id.edit_category);
        categoryNameTv = (TextView) view.findViewById(R.id.ggs_service_category);
        addressTextView = (TextView) view.findViewById(R.id.addressTextView);
        rateTv = (TextView) view.findViewById(R.id.rate_value);
        productIntroText = (TextView) view.findViewById(R.id.textView);
        productTitleTv = (TextView) view.findViewById(R.id.service_title_tv);
        productDescTv = (TextView) view.findViewById(R.id.service_desc_tv);
        titleEt = (EditText) view.findViewById(R.id.title);
        descriptionEt = (EditText) view.findViewById(R.id.description);

        titleEt.setText(UtilsGGS.getGgProduct(getContext()).getTitle());
        descriptionEt.setText(UtilsGGS.getGgProduct(getContext()).getDescription());


        negotiationAllowSwitch = (ThemeSwitchGGS) view.findViewById(R.id.negotiation_allow_switch);
        negotiationAllowSwitch.setOnCheckedChangeListener(this);

        nextBottom = (Button) view.findViewById(R.id.next_bottom);
        layout_category_title = (RelativeLayout) view.findViewById(R.id.layout_category_title);
        createServiceCurrencyTextView = (TextView) view.findViewById(R.id.create_service_currency_text_view);

        currencyLayout = (RelativeLayout) view.findViewById(R.id.currency_layout);
        rateLayout = (LinearLayout) view.findViewById(R.id.rate_layout);
        locationLayout = (RelativeLayout) view.findViewById(R.id.location_layout);
        negotiationLayout = (LinearLayout) view.findViewById(R.id.nagotiation_layout);
        exchangeLayout = (RelativeLayout) view.findViewById(R.id.exchange_layout);
        count_product_charecter = (LinearLayout) view.findViewById(R.id.count_product_charecter);


        currentCountTitle = (TextView) view.findViewById(R.id.current_count_title);
        totalCountTitle = (TextView) view.findViewById(R.id.total_count_title);
        currentCountDescription = (TextView) view.findViewById(R.id.current_count_description);
        totalCountDescription = (TextView) view.findViewById(R.id.total_count_description);
        exchangeDetails = (TextView) view.findViewById(R.id.exchange_details);
        ratePerValueTextView = (TextView) view.findViewById(R.id.rate_per_value);
        rateTextTv = (TextView) view.findViewById(R.id.rate_text_tv);

        setTextIntoTv();

        postButton = (TextView) getActivity().findViewById(R.id.post_upper);
        postButton.setVisibility(View.VISIBLE);
        setPostButtonText();
        setValidationOfEditTexts();
        currentCountTitle.setText(UtilsGGS.getGgProduct(getContext()).getTitle().length() + "");
        currentCountDescription.setText(UtilsGGS.getGgProduct(getContext()).getDescription().length() + "");


        uploadMediaRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        uploadMediaRecyclerView.setItemAnimator(new DefaultItemAnimator());
        uploadMediaRecyclerView.setAdapter(adapter);

        initListeners();

        if (UtilsGGS.getGgProduct(getContext()).getProduct_type() == GGProduct.PRODUCT_TYPE.EXCHANGE) {
            rateLayout.setVisibility(View.GONE);
            currencyLayout.setVisibility(View.GONE);
            negotiationLayout.setVisibility(View.GONE);
            exchangeLayout.setVisibility(View.VISIBLE);
        } else {
            rateLayout.setVisibility(View.VISIBLE);
            currencyLayout.setVisibility(View.VISIBLE);
            negotiationLayout.setVisibility(View.VISIBLE);
            exchangeLayout.setVisibility(View.GONE);
        }

        appBarLayout = (FrameLayout) getActivity().findViewById(R.id.app_bar_layout_frame_layout);
        createServiceDetailsScrollView = (ScrollView) view.findViewById(R.id.create_product_details_scroll_view);

    }


    private void setValidationOfEditTexts() {
        final int titleEditTextCharLimit = getActivity().getResources().getInteger(R.integer.title_edittext_max_length_ggs);
        final int descrEditTextCharLimit = getActivity().getResources().getInteger(R.integer.descr_edittext_max_length_ggs);

        String slashChar = "/";
        currentCountTitle.setText("0");
        StringBuilder slashBuilder = new StringBuilder();
        slashBuilder.append(slashChar);
        slashBuilder.append(titleEditTextCharLimit);
        totalCountTitle.setText(slashBuilder.toString());
        currentCountDescription.setText("0");

        slashBuilder = new StringBuilder();
        slashBuilder.append(slashChar);
        slashBuilder.append(descrEditTextCharLimit);
        totalCountDescription.setText(String.valueOf(slashBuilder.toString()));
        titleEt.addTextChangedListener(new CustomTextWatcher(TEXT_WATCHER_CODE_FOR_TITLE_ET, this));
        descriptionEt.addTextChangedListener(new CustomTextWatcher(TEXT_WATCHER_CODE_FOR_DESCRIPTION_ET, this));


    }

    @Override
    public void onTextChanged(int requestCode, CharSequence s) {
        switch (requestCode) {
            case TEXT_WATCHER_CODE_FOR_TITLE_ET:
                UtilsGGS.getGgProduct(getContext()).setTitle(s.toString());
                nextPageValidation();
                currentCountTitle.setText(String.valueOf(s.length()));
                break;
            case TEXT_WATCHER_CODE_FOR_DESCRIPTION_ET:
                UtilsGGS.getGgProduct(getContext()).setDescription(s.toString());
                nextPageValidation();

                if (descriptionEt.getLineCount() > 4)
                    descriptionEt.setOnTouchListener(com.gagagugu.ggservice.fragment.CreateProductDetailsGGS.this);
                else {
                    descriptionEt.setOnTouchListener(null);
                }

                currentCountDescription.setText(String.valueOf(s.length()));

                break;

        }
    }

    private void setPostButtonText() {
        postButton.setText(getContext().getResources().getString(R.string.next_ggs));
    }

    private void initListeners() {
        postButton.setOnClickListener(this);
        rateLayout.setOnClickListener(this);
        editCategory.setOnClickListener(this);
        currencyLayout.setOnClickListener(this);
        //nextUpper.setOnClickListener(this);
        nextBottom.setOnClickListener(this);
        locationLayout.setOnClickListener(this);
        exchangeLayout.setOnClickListener(this);
    }

    private void setTextIntoTv() {
        setTextInIntroTv();
        categoryNameTv.setText(category);

        String type = UtilsGGS.getGgProduct(getContext()).getProduct_type().name();

        if (type.equals(GGProduct.PRODUCT_TYPE.RENTAL.name())) {
            productTitleTv.setText(getString(R.string.rental_title_ggs));
            productDescTv.setText(getString(R.string.rental_description_ggs));
            titleEt.setHint(getString(R.string.your_rental_title_here_ggs));
            descriptionEt.setHint(R.string.your_rental_description_here_ggs);
        } else {
            productTitleTv.setText(getResources().getString(R.string.product_title_ggs));
            productDescTv.setText(getResources().getString(R.string.product_desc_ggs));
            titleEt.setHint(getResources().getString(R.string.product_title_hint_ggs));
            descriptionEt.setHint(getResources().getString(R.string.product_desc_hint_ggs));
        }


    }

    private void setTextInIntroTv() {
        String type = UtilsGGS.getGgProduct(getContext()).getProduct_type().name();
        Resources resources = getResources();

        if (type.equals(GGProduct.PRODUCT_TYPE.BUY.name())) {
            productIntroText.setText(resources.getString(R.string.product_buy_intro_ggs));
            rateTextTv.setText(getContext().getResources().getString(R.string.budget_ggs));
        } else if (type.equals(GGProduct.PRODUCT_TYPE.SELL.name())) {
            productIntroText.setText(resources.getString(R.string.product_sell_intro_ggs));
            rateTextTv.setText(getContext().getResources().getString(R.string.price_ggs));


        } else if (type.equals(GGProduct.PRODUCT_TYPE.EXCHANGE.name())) {
            productIntroText.setText(resources.getString(R.string.product_exchange_intro_ggs));

        } else if (type.equals(GGProduct.PRODUCT_TYPE.RENTAL.name())) {
            productIntroText.setText(getString(R.string.which_rental_are_you_providing_ggs));
            rateTextTv.setText(getString(R.string.rate_ggs));
        }
    }


    private void setupForEditProduct() {
        if (UtilsGGS.getGgProductEditFlag(getContext())) {
            count_product_charecter.setVisibility(View.GONE);
            titleEt.setEnabled(false);
            titleEt.setTextColor(getResources().getColor(R.color.currency_value_text_color_ggs));
            addressTextView.setText(UtilsGGS.getGgProduct(getContext()).getAddress());
        }
    }

    private void initializeMediaList() {
        if (UtilsGGS.getGgProduct(getActivity()).getMedia().size()==0 || UtilsGGS.getGgProduct(getActivity()).getMedia().get(0).getItem_type()!=1) {
            MediaImageGgs mediaGgs = new MediaImageGgs();
            mediaGgs.setItem_type(1);
            UtilsGGS.getGgProduct(getContext()).getMedia().add(0, mediaGgs);
        }
    }

    private void initMediaCreator() {
        mMediaCreator = MediaCreator.create(getActivity())
                .addOption(MediaCreator.Option.PHOTOS)
                .addOption(MediaCreator.Option.CAMERA)
                .setOnMediaCreatedListener(MediaCreator.Option.PHOTOS, this)
                .setOnMediaCreatedListener(MediaCreator.Option.CAMERA, this);
    }


    private void setupViewWithValue() {

        if (UtilsGGS.getGgProduct(getContext()).getPrice().length() > 0) {
            rateTv.setText(UtilsGGS.makeCommaSeparatedPriceFormat(Double.parseDouble(UtilsGGS.getGgProduct(getContext()).getPrice())));
        } else {
            rateTv.setText("");
        }
        if (UtilsGGS.getGgProduct(getContext()).getPrice_unit().equals("")) {
            ratePerValueTextView.setText("");
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("/ ");
            stringBuilder.append(UtilsGGS.getGgProduct(getContext()).getPrice_unit());
            ratePerValueTextView.setText(stringBuilder);
        }


        negotiationAllowSwitch.setChecked(UtilsGGS.getGgProduct(getContext()).getNegotiable() == 1);
        if (!UtilsGGS.getGgProduct(getContext()).getCurrency().equals("")) {
            createServiceCurrencyTextView.setText(String.valueOf(UtilsGGS.getGgProduct(getContext()).getCurrency()));
        }

        if (!UtilsGGS.getGgProduct(getContext()).getExchange().isEmpty()) {
            exchangeDetails.setText(UtilsGGS.getGgProduct(getContext()).getExchange());
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && (requestCode == MediaCreator.getResultCode(MediaCreator.Option.PHOTOS) || requestCode == MediaCreator.getResultCode(MediaCreator.Option.CAMERA))) {
            mMediaCreator.onActivityResult(requestCode, data);
            mOption = MediaCreator.Option.values()[requestCode];
        } else if (requestCode == CURRENCY_REQUEST_CODE) {
            if (data != null) {
                int position = data.getIntExtra("position", -1);
                System.out.println(position + "");
            }
        } else if (requestCode == RATE_BOTTOM_REQUEST_CODE) {
            nextPageValidation();
            setupViewWithValue();
        } else if (requestCode == EXCHANGE_DETAILS_REQUEST_CODE) {
            nextPageValidation();
            setupViewWithValue();
        } else if (requestCode == RATE_BOTTOM_REQUEST_CODE) {
            nextPageValidation();
            setupViewWithValue();
        }
    }

    private boolean nextPageValidation() {
        boolean flag = true;

        if (UtilsGGS.getGgProduct(getContext()).getTitle().trim().length() < 1) {
            flag = false;
            errorMessage = getString(R.string.product_title_is_required_ggs);

        } else if (UtilsGGS.getGgProduct(getContext()).getLat() == 0 || UtilsGGS.getGgProduct(getContext()).getLng() == 0.0) {
            flag = false;
            errorMessage = getString(R.string.location_is_required_ggs);
        } else {

            GGProduct.PRODUCT_TYPE product_type = UtilsGGS.getGgProduct(getContext()).getProduct_type();

            if (product_type == GGProduct.PRODUCT_TYPE.BUY || product_type == GGProduct.PRODUCT_TYPE.SELL) {
                if (UtilsGGS.getGgProduct(getContext()).getPrice().trim().length() < 1) {
                    flag = false;
                    if (product_type == GGProduct.PRODUCT_TYPE.BUY) {
                        errorMessage = getString(R.string.budget_is_required_ggs);
                    } else if (product_type == GGProduct.PRODUCT_TYPE.SELL) {
                        errorMessage = getString(R.string.price_is_required_ggs);
                    }

                }
            } else if (product_type == GGProduct.PRODUCT_TYPE.EXCHANGE) {
                if (UtilsGGS.getGgProduct(getContext()).getExchange().isEmpty()) {
                    flag = false;
                    errorMessage = getString(R.string.exchange_details_is_required_ggs);
                }
            } else if (product_type == GGProduct.PRODUCT_TYPE.RENTAL) {

                if (UtilsGGS.getGgProduct(getContext()).getPrice().trim().length() < 1) {
                    flag = false;
                    errorMessage = getString(R.string.rental_rate_is_required_ggs);

                } else if (UtilsGGS.getGgProduct(getContext()).getPrice_unit().trim().length() < 1) {
                    flag = false;


                }
            }

        }

        if (UtilsGGS.getGgProduct(getContext()).getImageCounter() != 0) {
            errorMessage = getString(R.string.image_is_uploading_ggs);
            flag = false;
        }
        if (flag) {


            postButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white_ggs));
            nextBottom.setAlpha(1f);
        } else {
            postButton.setTextColor(ContextCompat.getColor(getContext(), R.color.next_button_opac_color_ggs));
            nextBottom.setAlpha(.5f);
        }

        return flag;
    }


    @Override
    public void onMediaCreated(String mediaPath) {
        ArrayList<MediaImageGgs>mediaImageGgsArrayList=UtilsGGS.getGgProduct(getActivity()).getMedia();
        mSelectedImagePath = mediaPath;
        MediaImageGgs mediaGgs = new MediaImageGgs();
        mediaGgs.setUri_local(mSelectedImagePath);
        if (mediaImageGgsArrayList.contains(mediaGgs)) {
            Toast.makeText(getActivity(), getString(R.string.already_contains_ggs), Toast.LENGTH_SHORT).show();
        } else {
            mediaGgs.setItem_type(2);
            mediaGgs.setUploaded(false);
            mediaImageGgsArrayList.add(mediaGgs);
            adapter.notifyItemInserted(mediaImageGgsArrayList.size());
            uploadMediaRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
            UtilsGGS.getGgProduct(getActivity()).setImageCounter(UtilsGGS.getGgProduct(getActivity()).getImageCounter() + 1);
            new UploadMediaAsyncTask(getActivity(), mediaGgs,this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            nextPageValidation();
        }
    }




    @Override
    public void onMediaCreated(Uri uri) {
        BitmapFileCreatorTask creatorTask = new BitmapFileCreatorTask(getActivity(), uri);
        creatorTask.setFileCreationListener(this);
        creatorTask.execute();
    }

    @Override
    public void onFileCreated(File file) {

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);

    }

    @Override
    public void onClipClicked() {
        handleMediaCreator();

    }




    private void handleMediaCreator() {
        if (!hasStoragePermission() && !hasCameraPermission()) {
            requestCameraAndStoragePermission();
        } else if (!hasStoragePermission() && hasCameraPermission()) {
            requestStoragePermission();
        } else if (hasStoragePermission() && !hasCameraPermission()) {
            requestCameraPermission();
        } else {
            mMediaCreator.show(getString(R.string.upload_media_dialog_title_ggs), getResources().getStringArray(R.array.select_media_for_create_ggs));
        }
    }

    public boolean hasStoragePermission() {
        return ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraAndStoragePermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CAMERA_AND_STORAGE);
            } else {

            }
            return;
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);
            } else {

            }
            return;
        }

//        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                Manifest.permission.CAMERA)) {
//
//            permissionHandler.showSettingsSnackbar(getResources().getString(R.string.permission_msg_camera));
//
//        } else {
//
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[]{Manifest.permission.CAMERA},
//                    REQUEST_CAMERA_PERMISSION);
//        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);
            } else {

            }
            return;
        }

//        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//
//            permissionHandler.showSettingsSnackbar(getResources().getString(R.string.permission_msg_write_external_storage));
//
//        } else {
//
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    REQUEST_WRITE_STORAGE);
//        }
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

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (useraddress != null && useraddress.length() > 0) {
            address = useraddress;
            lat = userLat;
            lon = userLon;
            addressTextView.setText(useraddress);
        } else {
            updateLocationUI();
        }
    }

    protected void startLocationUpdates() {
        if (!UtilsGGS.getGgProductEditFlag(getActivity())) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            if (useraddress != null && useraddress.length() > 0) {
                address = useraddress;
                lat = userLat;
                lon = userLon;
                addressTextView.setText(useraddress);
            } else {
                updateLocationUI();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMediaCreator.show(getString(R.string.upload_media_dialog_title_ggs), getResources().getStringArray(R.array.select_media_for_create_ggs));
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_camera_permission_denied_ggs), Toast.LENGTH_LONG).show();
                }

                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        // user rejected the permission
                        boolean showRationale = shouldShowRequestPermissionRationale(permission);
                        if (!showRationale) {
                            permissionHandler.showSettingsSnackbar(getResources().getString(R.string.permission_msg_camera_storage_ggs));
                        }
                    }
                }
            }
            break;
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMediaCreator.show(getString(R.string.upload_media_dialog_title_ggs), getResources().getStringArray(R.array.select_media_for_create_ggs));
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_storage_permission_denied_ggs), Toast.LENGTH_LONG).show();
                }

                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        // user rejected the permission
                        boolean showRationale = shouldShowRequestPermissionRationale(permission);
                        if (!showRationale) {
                            permissionHandler.showSettingsSnackbar(getResources().getString(R.string.permission_msg_write_external_storage_ggs));
                        }
                    }
                }
            }
            break;
            case REQUEST_CAMERA_AND_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    mMediaCreator.show(getString(R.string.upload_media_dialog_title_ggs), getResources().getStringArray(R.array.select_media_for_create_ggs));
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_camera_storage_permission_denied_ggs), Toast.LENGTH_LONG).show();
                }

                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        // user rejected the permission
                        boolean showRationale = shouldShowRequestPermissionRationale(permission);
                        if (!showRationale) {
                            permissionHandler.showSettingsSnackbar(getResources().getString(R.string.permission_msg_camera_storage_ggs));
                        }
                    }
                }

            }
            break;
            case REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Request Permissions ", " Accepted");
                    hasLocationPeermission = false;
                    startLocationUpdates();
                } else {
                    updateCurrency(ServicePreference.getInstance(getActivity()).getCountryOfConnectProfile());
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
            break;
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
    public void onLocationChanged(Location location) {
        Log.e(TAG, "Connection to Location Service - onLocationChanged");
        Log.e(TAG, "onLocationChanged" + location.getLatitude() + "----" + location.getLongitude());
//        (new GetAddressTask(mContext)).execute(mLastLocation);
//        try {
//            getAddress(location.getLatitude(), location.getLongitude());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        mLastLocation = location;
        if (useraddress != null && useraddress.length() > 0) {
            address = useraddress;
            lat = userLat;
            lon = userLon;
            addressTextView.setText(useraddress);
        } else {
            updateLocationUI();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!UtilsGGS.getGgProductEditFlag(getContext())) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupTheme();
        if (!UtilsGGS.getGgProductEditFlag(getContext())) {
            updateCurrencyFlag = true;

//        if (mGoogleApiClient.isConnected()) {
//            startLocationUpdates();
//        }

            if (userCountry != null && userCountry.length() > 0)
                setCurrency();

            if (useraddress != null && useraddress.length() > 0) {
                address = useraddress;
                lat = userLat;
                lon = userLon;
                try {
                    UtilsGGS.getGgProduct(getContext()).setLat(Double.parseDouble(lat));
                    UtilsGGS.getGgProduct(getContext()).setLng(Double.parseDouble(lon));
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }

                addressTextView.setText(useraddress);

                if (mGoogleApiClient.isConnected()) {
                    stopLocationUpdates();
                }
            } else {
                if (mGoogleApiClient.isConnected()) {
                    startLocationUpdates();
                }
            }

        }
        nextPageValidation();

    }

    private void setupTheme() {

        negotiationAllowSwitch.changeTheme(theme.getColorCodeLight());

        GradientDrawable shapeNext = (GradientDrawable) nextBottom.getBackground();
        shapeNext.setColor(theme.getColorCodeLight());

        ColorDrawable shapeCategory = (ColorDrawable) layout_category_title.getBackground();
        shapeCategory.setColor(theme.getColorCodeLight());
    }

    protected void stopLocationUpdates() {

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onPause() {
        super.onPause();
//        if (getAddressTask != null && getAddressTask.getStatus() == AsyncTask.Status.RUNNING)
//            getAddressTask.cancel(true);


        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == negotiationAllowSwitch) {
            UtilsGGS.getGgProduct(getContext()).setNegotiable(isChecked ? 1 : 0);
        }
    }

    private void setCurrency() {
        if (updateCurrencyFlag) {
            if (userCountry.equals(""))
                updateCurrency(ServicePreference.getInstance(getActivity()).getCountryOfConnectProfile());
            else
                updateCurrency(userCountry);

            updateCurrencyFlag = false;
        }
    }

    private void updateCurrency(String country) {
        Log.v("tomal update", country);
      /*  if(UtilsGGS.getProductParentActivity(getContext()).currencyGGSes==null){
            return;
        }*/
        if (getActivity() == null) {
            return;
        }

        if (((CreateProductPostActivityGGS) getActivity()).currencyGGSes == null || ((CreateProductPostActivityGGS) getActivity()).currencyGGSes.size() == 0) {
            return;
        }

        ArrayList<CurrencyGGS> currencyGGSes = UtilsGGS.getProductParentActivity(getContext()).currencyGGSes;
        int index = -1, currencyId = 0;
        for (int i = 0; i < currencyGGSes.size(); i++) {
            Log.v("mycon", currencyGGSes.get(i).getCountry_name().toLowerCase() + " " + country.toLowerCase());
            if (currencyGGSes.get(i).getCountry_name().toLowerCase().equals(country.toLowerCase())) {

                index = i;
                break;
            }
        }

        Log.v("index", index + "");
        String curency = null;
        String selectedUsd = getActivity().getString(R.string.usd_ggs);
        if (index != -1) {
            curency = currencyGGSes.get(index).getCode();
            currencyId = currencyGGSes.get(index).getId();
        } else {
            for (CurrencyGGS c : currencyGGSes) {
                if (c.getCode().toUpperCase().equals(selectedUsd.toUpperCase())) {
                    curency = c.getCode();
                    currencyId = c.getId();
                    break;
                }
            }
        }


        if (curency != null) {
            createServiceCurrencyTextView.setText(curency);
            UtilsGGS.getGgProduct(getContext()).setCurrncyId(currencyId);
            UtilsGGS.getGgProduct(getContext()).setCurrency(curency);
        }

    }

    @Override
    public void onDestroy() {
//        getAddressTask.cancel(true);
        super.onDestroy();
    }



    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        /*if (viewId == R.id.next_upper) {
            goToNextFragment();

        }*/
        if (viewId == R.id.next_bottom) {

            if (nextPageValidation()) {
                goToNextFragment();
            } else {
                makeToast(errorMessage);
            }

        } else if (viewId == R.id.exchange_layout) {
            showExchangeDialog();

        } else if (viewId == R.id.rate_layout) {
            if (UtilsGGS.getGgProduct(getContext()).getProduct_type().name().equals(GGProduct.PRODUCT_TYPE.RENTAL.name())) {
                showRateDailog();
            } else {
                showBudgetDialog();

            }
            //showToast();

        } else if (viewId == R.id.currency_layout) {
            //showCurrencyDialog();
            //showToast();
        } else if (viewId == R.id.location_layout) {
            if (!UtilsGGS.getGgProductEditFlag(getContext())) {
                goToLocationPickerFragment();
            }
        } else if (viewId == R.id.edit_category) {
            if (UtilsGGS.getGgProduct(getActivity()).getImageCounter() == 0) {
                goToEditCategoryFragment();
            } else {
                makeToast(getString(R.string.image_is_uploading_ggs));
            }
        } else if (viewId == R.id.post_upper) {
            if (nextPageValidation()) {
                goToNextFragment();
            } else {
                makeToast(errorMessage);
            }
        }
    }

    private void showRateDailog() {
        com.gagagugu.ggservice.fragment.CreateServiceRateFragmentGGS rateBottomDialogFragment = com.gagagugu.ggservice.fragment.CreateServiceRateFragmentGGS.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("price", UtilsGGS.getGgProduct(getContext()).getPrice());
        bundle.putString("price_unit", UtilsGGS.getGgProduct(getContext()).getPrice_unit());
        bundle.putString("type", "rental");
        rateBottomDialogFragment.setArguments(bundle);
        rateBottomDialogFragment.setTargetFragment(this, RATE_BOTTOM_REQUEST_CODE);
        rateBottomDialogFragment.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "");
    }


    private void showExchangeDialog() {
        BottomSheetDialogFragment bottomSheetDialogFragment = com.gagagugu.ggservice.fragment.CreateProductExchangeDetailsFragmentGGS.newInstance();
        bottomSheetDialogFragment.setTargetFragment(this, EXCHANGE_DETAILS_REQUEST_CODE);
        bottomSheetDialogFragment.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), this.getClass().getName());


    }

    private void goToLocationPickerFragment() {
        LocationPickerFragmentGGS locationPickerFragmentGGS = new LocationPickerFragmentGGS();
        Bundle bundle = new Bundle();
        bundle.putBoolean("from_service", false);
        locationPickerFragmentGGS.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()

                //   setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up,R.anim.slide_in_down,R.anim.slide_out_down)
                .replace(R.id.fragment_container, locationPickerFragmentGGS).addToBackStack("locationPicker").commit();
    }

    private void goToEditCategoryFragment() {

//        ((AppCompatActivity) getActivity()).getSupportFragmentManager().beginTransaction().
//                setCustomAnimations(R.anim.slide_back_to_screen_ggs,R.anim.slide_back_ggs).
//                replace(R.id.fragment_container, new CreateServiceCategoryFragmentGGS()).addToBackStack(null).commit();

        getActivity().onBackPressed();


    }

    private void goToNextFragment() {
        // UtilsGGS.getGgService(context).setProfile_id("1");
        //UtilsGGS.getGgService(context).setTitle(titleEt.getText().toString());
        //UtilsGGS.getGgService(context).setDescription(descriptionEt.getText().toString());
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((getActivity().getWindow().getDecorView().getApplicationWindowToken()), 0);
        getFragmentManager().beginTransaction().
                setCustomAnimations(R.anim.slide_in_ggs, R.anim.slide_out_ggs, R.anim.slide_back_to_screen_ggs, R.anim.slide_back_ggs).
                replace(R.id.fragment_container, new com.gagagugu.ggservice.fragment.CreateProductOptionalGGS()).addToBackStack("optionalgs").commit();

    }

    private void showBudgetDialog() {
        BottomSheetDialogFragment budgetDailogFragment = com.gagagugu.ggservice.fragment.CreateItemBudgetFragmentGGS.newInstance();
/*        Bundle bundle = new Bundle();
        bundle.putString("price", UtilsGGS.getGgProduct(context).getPrice());
      //  bundle.putString("price_unit", UtilsGGS.getGgProduct(context).getPrice_unit());
        //bundle.putSerializable("service_rate", getGgProduct(context).getServiceRateGgs());
        rateBottomDialogFragment.setArguments(bundle);
        rateBottomDialogFragment.setTargetFragment(this, RATE_BOTTOM_REQUEST_CODE);*/
        budgetDailogFragment.setTargetFragment(this, RATE_BOTTOM_REQUEST_CODE);
        budgetDailogFragment.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), this.getClass().getName());
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == descriptionEt) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
        }
        return false;
    }

    private void makeToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMediaUpload(MediaImageGgs mediaGgs, UploadMediaGGS uploadMediaGGS) {

        Log.d("onMediaUpload","onMediaUpload");


        ArrayList<MediaImageGgs>mediaImageGgsArrayList=UtilsGGS.getGgProduct(getActivity()).getMedia();
        UtilsGGS.getGgProduct(getActivity()).setMedia(mediaImageGgsArrayList);
        UtilsGGS.getGgProduct(getActivity()).setImageCounter(UtilsGGS.getGgProduct(getActivity()).getImageCounter() - 1);
        nextPageValidation();




        if (uploadMediaGGS != null && uploadMediaGGS.getSuccess() != null) {
            if (mediaImageGgsArrayList.contains(mediaGgs)) {
                int i = mediaImageGgsArrayList.indexOf(mediaGgs);
                mediaImageGgsArrayList.get(i).setUploaded(true);
                mediaImageGgsArrayList.get(i).setThumb_url(uploadMediaGGS.getSuccess().getFile().getThumbUrl());
                mediaImageGgsArrayList.get(i).setThumb_url_large(uploadMediaGGS.getSuccess().getFile().getThumbUrlLarge());
                mediaImageGgsArrayList.get(i).setThumb_url_medium(uploadMediaGGS.getSuccess().getFile().getThumbUrlMedium());
                mediaImageGgsArrayList.get(i).setUrl(uploadMediaGGS.getSuccess().getFile().getUrl());
                mediaImageGgsArrayList.get(i).setType("image");
                mediaImageGgsArrayList.get(i).setItem_type(2);
                adapter.notifyItemChanged(i);

            }

            Log.d("thumURL : ", uploadMediaGGS.getSuccess().getFile().getThumbUrl());
            Log.d("largeURL : ", uploadMediaGGS.getSuccess().getFile().getThumbUrlLarge());
            Log.d("url medium  : ", uploadMediaGGS.getSuccess().getFile().getThumbUrlMedium());
            Log.d("url ORIGINAL  : ", uploadMediaGGS.getSuccess().getFile().getUrl());
        } else {

            BitmapFactory.Options options = new BitmapFactory.Options();
            //by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
            //you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(mediaGgs.getUri_local(), options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;


            if (uploadMediaGGS.isLocalError()) {

                dialogManager.showDialogForInvalidImage(actualHeight, actualWidth);
            } else {
                dialogManager.showDialogForNoPost(getActivity().getString(R.string.failed_to_upload_media_image_ggs));

            }
            int sizeMediaList = mediaImageGgsArrayList.size();
            for (int i = 1; i < sizeMediaList; i++) {
                if (mediaImageGgsArrayList.get(i).getUri_local() != null && mediaImageGgsArrayList.get(i).getUri_local().equals(mediaGgs.getUri_local())) {
                    mediaImageGgsArrayList.remove(i);
                    adapter.notifyItemRemoved(i);
                    break;
                }
            }
        }
    }


    @Override
    public void onGetAddressCallback(HashMap<String, String> resultMap) {
        address = (String)resultMap.get("address");

        userCountry = (String)resultMap.get("country");
        useraddress = address;
        userLat = lat;
        userLon = lon;

        addressTextView.setText(address);

        if (mGoogleApiClient.isConnected())
            stopLocationUpdates();

        setCurrency();
        if (!address.contains("No address found")) {
            try {
                UtilsGGS.getGgProduct(context).setLat(Double.parseDouble(lat));
                UtilsGGS.getGgProduct(context).setLng(Double.parseDouble(lon));
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
    }



//    private class GetAddressTask extends AsyncTask<Location, Void, String> {
//        Context mContext;
//
//        public GetAddressTask(Context context) {
//            super();
//            mContext = context;
//        }
//
//        @Override
//        protected String doInBackground(Location... params) {
//            Geocoder geocoder =
//                    new Geocoder(mContext, Locale.getDefault());
//            // Get the current location from the input parameter list
//            Location loc = params[0];
//            // Create a list to contain the result address
//            List<Address> addresses = null;
//            try {
//                addresses = geocoder.getFromLocation(loc.getLatitude(),
//                        loc.getLongitude(), 1);
//            } catch (IOException e1) {
//                Log.e("Location ", "IO Exception in getFromLocation()");
//                e1.printStackTrace();
//                return ("No address found");
//            } catch (IllegalArgumentException e2) {
//                // Error message to post in the log
//                String errorString = "Illegal arguments " +
//                        Double.toString(loc.getLatitude()) +
//                        " , " +
//                        Double.toString(loc.getLongitude()) +
//                        " passed to address service";
//                Log.e("Location ", errorString);
//                e2.printStackTrace();
//                return "No address found";
//            }
//            // If the reverse geocode returned an address
//            if (addresses != null && addresses.size() > 0) {
//                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                String city = addresses.get(0).getLocality();
//                if (city == null || city.equalsIgnoreCase("null"))
//                    city = "";
//                else city += ",";
//
//                String state = addresses.get(0).getAdminArea();
//                if (state == null || state.equalsIgnoreCase("null"))
//                    state = "";
//                else state += ",";
//
//                String country = addresses.get(0).getCountryName();
//                userCountry = country;
//                if (country == null || country.equalsIgnoreCase("null"))
//                    country = "";
//                else country += ",";
//
//                String postalCode = addresses.get(0).getPostalCode();
//                if (postalCode == null || postalCode.equalsIgnoreCase("null"))
//                    postalCode = "";
//                else postalCode += ",";
//
//                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
//                if (knownName == null || knownName.equalsIgnoreCase("null"))
//                    knownName = "";
//                else knownName += ",";
//
//                Log.e(TAG, "Address : " + address);
//                Log.e(TAG, "KnownName : " + knownName);
//                Log.e(TAG, "City : " + city);
//                Log.e(TAG, "State : " + state);
//                Log.e(TAG, "Country : " + country);
//                Log.e(TAG, "PostalCode : " + postalCode);
////        address = latitude+","+longitude+"\n" + knownName + "," + city + "," + state + "," + country + "," + postalCode;
//                address = knownName + city + state + country + postalCode;
//                if (address.substring(address.length() - 1).equalsIgnoreCase(",")) {
//                    address = address.substring(0, address.length() - 1);
//                }
//                useraddress = address;
//                userLat = lat;
//                userLon = lon;
//                return address.toString();
//            } else {
//                return "No address found";
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String address) {
//            if (!isCancelled()) {
////                Toast.makeText(mContext, address, Toast.LENGTH_SHORT).show();
//                addressTextView.setText(address);
//
//                if (mGoogleApiClient.isConnected())
//                    stopLocationUpdates();
//
//                setCurrency();
//                if (!address.contains("No address found")) {
//                    try {
//                        UtilsGGS.getGgProduct(mContext).setLat(Double.parseDouble(lat));
//                        UtilsGGS.getGgProduct(mContext).setLng(Double.parseDouble(lon));
//                    } catch (NumberFormatException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//
//        }
//    }


}
