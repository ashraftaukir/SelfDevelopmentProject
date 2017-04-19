package com.gagagugu.ggservice.fragment;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.interfaces.OnApplyFilterClick;
import com.gagagugu.ggservice.models.SearchLocationGGS;
import com.gagagugu.ggservice.utils.LocationHandler;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;
import com.gagagugu.ggservice.view.customviews.ThemeSwitchGGS;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by taukir on 2/20/17.
 */
public class SearchFilterFragmentGGS extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, LocationHandler.OnLocationUpdateListener {


    public static final int POST_TIME_REQUEST_CODE = 2410;
    public static final int DISTANCE_REQUEST_CODE = 2510;
    public static final int SEARCH_PRICE_RATE_REQUEST_CODE = 2742;
    private static final String EXCHANGE = "exchange";
    private ProfileColorsGGS theme;
    private ThemeSwitchGGS negotiationAllowSwitch;
    private TextView apply_filter;
    private LinearLayout pricerangeLayout;
    private LinearLayout posttimeLayout;
    private LinearLayout distanceLayout;
    private CardView price_range_cardview;
    private TextView tv_distance_value, tv_clear, tv_post_time_value, price_range_text;
    private OnApplyFilterClick onApplyfilterClick;
    private HashMap<String, String> map;
    private LocationHandler locationHandler;
    Location mLocation;
    private String TAG = "SearchFilterFragment";
    private String itemtype;
    private ImageView ivCross;
    LinearLayout rootLayout;
    private boolean filterChanged = false, cleared = true, checkedChanged = false;


    private int postTimeSelectedPos = -1, distanceSelectedPoition = -1;
    private String selectedMinPrice = "", selectedMaxPrice = "";
    private String selectedDistanceName = "";
    private SearchLocationGGS selectedSearchLocationGGS;
    private String postTimeSelectedItemName = "", postTimeSelectedItemMapName = "";
    private String lastLon, lastLat;


    public SearchFilterFragmentGGS() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        map = new HashMap<>();
        locationHandler = new LocationHandler(getContext());
        locationHandler.setOnLocationUpdateListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_filter_ggs, container, false);
        theme = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        getBundle();


        getSavedValues();


        initViews(view);
        apply_filter.setTextColor(ContextCompat.getColor(getContext(), R.color.next_button_opac_color_ggs));
        initListiner();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        return view;
    }

    private void getSavedValues() {
        selectedMinPrice = UtilsGGS.getGGSearch(getActivity()).getMinvalue();
        selectedMaxPrice = UtilsGGS.getGGSearch(getActivity()).getMaxvalue();

        distanceSelectedPoition = UtilsGGS.getGGSearch(getContext()).getDistanceid();
        postTimeSelectedPos = UtilsGGS.getGGSearch(getContext()).getId();

        postTimeSelectedItemName = UtilsGGS.getGGSearch(getActivity()).getItemname();
        postTimeSelectedItemMapName = UtilsGGS.getGGSearch(getActivity()).getItemMapName();
        selectedDistanceName = UtilsGGS.getGGSearch(getActivity()).getDistancename();
        selectedSearchLocationGGS = UtilsGGS.getGGSearch(getActivity()).getSelectedSearchLocationGGS();
    }

    private void getBundle() {

        Bundle bundle = getArguments();
        itemtype = bundle.getString("clickingtypeValue");


    }


    private void initViews(View view) {

        price_range_cardview = (CardView) view.findViewById(R.id.price_range_cardview);
        tv_post_time_value = (TextView) view.findViewById(R.id.tv_post_time_value);
        price_range_text = (TextView) view.findViewById(R.id.price_range_text);
        posttimeLayout = (LinearLayout) view.findViewById(R.id.posttimeLayout);
        distanceLayout = (LinearLayout) view.findViewById(R.id.distanceLayout);
        tv_distance_value = (TextView) view.findViewById(R.id.tv_distance_value);
        tv_clear = (TextView) view.findViewById(R.id.tv_clear);
        //tv_clear.setEnabled(false);
        negotiationAllowSwitch = (ThemeSwitchGGS) view.findViewById(R.id.negotiation_switch);
        apply_filter = (TextView) view.findViewById(R.id.apply_filter);
        pricerangeLayout = (LinearLayout) view.findViewById(R.id.pricerangeLayout);
        rootLayout = (LinearLayout) view.findViewById(R.id.root_layout_search);
        ivCross = (ImageView) view.findViewById(R.id.iv_cross);

        setThemeColor();


        if (itemtype.equals(EXCHANGE)) {

            pricerangeLayout.setVisibility(View.GONE);
            price_range_cardview.setVisibility(View.GONE);
        }


        negotiationAllowSwitch.setChecked(UtilsGGS.getGGSearch(getActivity()).isNegotiable() == 1);

        if (vauleChecker()) {

            tv_clear.setTextColor(ContextCompat.getColor(getContext(), R.color.white_ggs));
            cleared = false;

        }
    }

    private void setThemeColor() {
        String themeColor = ServicePreference.getInstance(getContext()).getThemeColor();
        ProfileColorsGGS profileColorsGGS = UtilsGGS.getProfileColor(themeColor);
        rootLayout.setBackgroundResource(profileColorsGGS.getBackground());
    }

    private boolean vauleChecker() {

        boolean flag = false;


        if (UtilsGGS.getGGSearch(getContext()).getId() > -1) {
            tv_post_time_value.setText(postTimeSelectedItemName);
            Log.d(TAG, "vauleChecker: setting itemname " + UtilsGGS.getGGSearch(getContext()).getItemname());

            flag = true;
        }

        if (UtilsGGS.getGGSearch(getContext()).getDistanceid() > -1) {
            tv_distance_value.setText(selectedDistanceName);
            Log.d(TAG, "vauleChecker: setting distancename " + UtilsGGS.getGGSearch(getContext()).getDistancename());
            flag = true;
        }

        if (!selectedMinPrice.equals("") && !selectedMaxPrice.equals("")) {
            //tv_price_minimum_range_value.setText(UtilsGGS.getGGSearch(getContext()).getMinvalue());
            price_range_text.setText(getPriceMinMaxValueString(selectedMinPrice, selectedMaxPrice));
            flag = true;
        }

        Log.d(TAG, "vauleChecker: " + UtilsGGS.getGGSearch(getActivity()).isNegotiable());

        if (UtilsGGS.getGGSearch(getActivity()).isNegotiable() >= 0) {
            flag = true;
        }
        /*if(map.containsKey("negotiable") && !map.get("negotiable").isEmpty()){
            flag = true;
        }*/
        //flag = map.containsKey("negotiable") && !map.get("negotiable").isEmpty();


        return flag;
    }

    private void initListiner() {

        pricerangeLayout.setOnClickListener(this);
        posttimeLayout.setOnClickListener(this);
        distanceLayout.setOnClickListener(this);
        apply_filter.setOnClickListener(this);
        negotiationAllowSwitch.setOnCheckedChangeListener(this);
        tv_clear.setOnClickListener(this);
        rootLayout.setOnClickListener(this);
        ivCross.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        themeSettings();

    }

    private void themeSettings() {

        negotiationAllowSwitch.changeTheme(theme.getColorCodeLight());
        GradientDrawable apply_filter_obj = (GradientDrawable) apply_filter.getBackground();
        apply_filter_obj.setColor(theme.getColorCodeLight());

    }

    @Override
    public void onClick(View v) {

        if (v == pricerangeLayout) {
            openPriceRange();
        } else if (v == posttimeLayout) {
            openPostTime();
        } else if (v == distanceLayout) {
            openDistance();
        } else if (v == apply_filter) {
            if (filterChanged) {
                saveValueInMap();
                onApplyfilterClick.onClick(map);
                Log.d(TAG, "onClick: " + new Gson().toJson(map));
                getActivity().onBackPressed();
            }
        } else if (v == tv_clear) {
            if (!cleared) {
                clearAllValue();
            }
        } else if (v == rootLayout) {
            //do nothing
        } else if (v == ivCross) {
            getActivity().onBackPressed();
        }


    }

    private void saveValueInMap() {
        if (!selectedMinPrice.isEmpty() && !selectedMaxPrice.isEmpty()) {
            map.put("price_range", selectedMinPrice + "," + selectedMaxPrice);

        }


        if (distanceSelectedPoition > -1) {

            if (selectedDistanceName.equals(getString(R.string.nearby_ggs))) {
                if (mLocation != null) {
                    Log.d(TAG, "distanceValueSet: lat" + mLocation.getLatitude() + "lng" + mLocation.getLongitude());
                    map.put("lat", String.valueOf(mLocation.getLatitude()));
                    map.put("lon", String.valueOf(mLocation.getLongitude()));
                    map.put("city", "");
                    map.put("country", "");
                } else {
                    map.put("lat", lastLat);
                    map.put("lon", lastLon);
                    map.put("city", "");
                    map.put("country", "");
                }
            } else if (selectedDistanceName.equals(getString(R.string.my_city_ggs))) {
                Log.d(TAG, "distanceValueSet: city" + selectedSearchLocationGGS.getCity());
                map.put("city", selectedSearchLocationGGS.getCity());
                map.put("country", "");
                map.put("lat", "");
                map.put("lon", "");

            } else if (selectedDistanceName.equals(getString(R.string.my_country_ggs))) {
                Log.d(TAG, "distanceValueSet: country" + selectedSearchLocationGGS.getCountry());
                map.put("city", "");
                map.put("lat", "");
                map.put("lon", "");
                map.put("country", selectedSearchLocationGGS.getCountry());
            }
        }

        if (postTimeSelectedPos > -1) {
            map.put("time", postTimeSelectedItemMapName);


        }

        UtilsGGS.getGGSearch(getActivity()).setMinvalue(selectedMinPrice);
        UtilsGGS.getGGSearch(getActivity()).setMaxvalue(selectedMaxPrice);
        UtilsGGS.getGGSearch(getActivity()).setDistanceid(distanceSelectedPoition);
        UtilsGGS.getGGSearch(getActivity()).setDistancename(selectedDistanceName);
        UtilsGGS.getGGSearch(getActivity()).setId(postTimeSelectedPos);
        UtilsGGS.getGGSearch(getActivity()).setItemname(postTimeSelectedItemName);
        UtilsGGS.getGGSearch(getActivity()).setItemMapName(postTimeSelectedItemMapName);
        UtilsGGS.getGGSearch(getActivity()).setSelectedSearchLocation(selectedSearchLocationGGS);


        if (checkedChanged) {
            UtilsGGS.getGGSearch(getActivity()).setNegotiable(negotiationAllowSwitch.isChecked() ? 1 : 0);
            map.put("negotiable", String.valueOf(negotiationAllowSwitch.isChecked()?1:0));
        }
        if (cleared) {
            map.put("negotiable", "");
            UtilsGGS.getGGSearch(getActivity()).setNegotiable(-1);

        }
    }

    private void clearAllValue() {


        /*UtilsGGS.getGGSearch(getContext()).setItemname("");
        UtilsGGS.getGGSearch(getContext()).setDistancename("");
        UtilsGGS.getGGSearch(getContext()).setMaxvalue("");
        UtilsGGS.getGGSearch(getContext()).setMinvalue("");
        UtilsGGS.getGGSearch(getContext()).setId(-1);
        UtilsGGS.getGGSearch(getContext()).setDistanceid(-1);*/

        selectedMaxPrice = "";
        selectedMinPrice = "";
        selectedDistanceName = "";
        selectedSearchLocationGGS = null;
        postTimeSelectedPos = -1;
        distanceSelectedPoition = -1;
        postTimeSelectedItemMapName = "";
        postTimeSelectedItemName = "";

        tv_post_time_value.setText("");
        tv_distance_value.setText("");
        price_range_text.setText("");
        negotiationAllowSwitch.setChecked(false);
        tv_clear.setTextColor(ContextCompat.getColor(getContext(), R.color.next_button_opac_color_ggs));

        clearMap();
        filterChanged = true;
        cleared = true;
        checkedChanged = false;
        changeApplyFilterColorToWhite();
    }

    private void changeApplyFilterColorToWhite() {
        apply_filter.setTextColor(ContextCompat.getColor(getContext(), R.color.white_ggs));
    }

    private void clearMap() {
        map.put("price_range", "");
        map.put("time", "");
        map.put("city", "");
        map.put("country", "");
        map.put("lat", "");
        map.put("lon", "");
        map.put("negotiable", "");
    }

    private void openDistance() {

        com.gagagugu.ggservice.fragment.DistanceFragmentGGS distanceFragment = com.gagagugu.ggservice.fragment.DistanceFragmentGGS.newInstance();
        distanceFragment.setTargetFragment(this, DISTANCE_REQUEST_CODE);
        distanceFragment.setSelectedPosition(distanceSelectedPoition);
        distanceFragment.show(getFragmentManager(), distanceFragment.getTag());

    }

    private void openPostTime() {

        com.gagagugu.ggservice.fragment.PostTimeFragmentGGS posttimeFragmentGGS = com.gagagugu.ggservice.fragment.PostTimeFragmentGGS.newInstance();
        posttimeFragmentGGS.setSelectedPosition(postTimeSelectedPos);
        posttimeFragmentGGS.setTargetFragment(this, POST_TIME_REQUEST_CODE);
        posttimeFragmentGGS.show(getFragmentManager(), posttimeFragmentGGS.getTag());

    }

    private void openPriceRange() {

        com.gagagugu.ggservice.fragment.SerachPriceRateFragmentGGS budgetDailogFragment = com.gagagugu.ggservice.fragment.SerachPriceRateFragmentGGS.newInstance();
        budgetDailogFragment.setMinPrice(selectedMinPrice);
        budgetDailogFragment.setMaxPrice(selectedMaxPrice);
        budgetDailogFragment.setTargetFragment(this, SEARCH_PRICE_RATE_REQUEST_CODE);
        budgetDailogFragment.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), this.getClass().getName());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SEARCH_PRICE_RATE_REQUEST_CODE:

                searchPriceRateValueSet(data);
                break;

            case POST_TIME_REQUEST_CODE:

                postTimeValueSet(data);
                break;

            case DISTANCE_REQUEST_CODE:

                distanceValueSet(data);
                break;

        }
    }

    private void searchPriceRateValueSet(Intent data) {

        selectedMinPrice = data.getStringExtra("min_value");
        selectedMaxPrice = data.getStringExtra("max_value");

        if (selectedMinPrice.length() > 0 && selectedMaxPrice.length() > 0) {
            price_range_text.setText(getPriceMinMaxValueString(selectedMinPrice, selectedMaxPrice));
            enableClearButton();

        } else {
            price_range_text.setText("");
        }
    }

    private String getPriceMinMaxValueString(String minPrice, String maxPrice) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(minPrice).append(" ").append(getString(R.string.hiphane_ggs)).append(" ").append(maxPrice);
        return stringBuilder.toString();
    }

    private void postTimeValueSet(Intent data) {
        Log.d(TAG, "postTimeValueSet: " + data.getIntExtra("position", -1));
        postTimeSelectedPos = data.getIntExtra("position", -1);
        if (postTimeSelectedPos > -1) {
            Log.d(TAG, "postTimeValueSet: ");
            postTimeSelectedItemName = data.getStringExtra("posttimeitemname");
            postTimeSelectedItemMapName = data.getStringExtra("itemmapname");
            tv_post_time_value.setText(postTimeSelectedItemName);
            enableClearButton();
        }
        if (UtilsGGS.getGGSearch(getContext()).getId() > -1) {


        }


    }

    private void distanceValueSet(Intent data) {
        distanceSelectedPoition = data.getIntExtra("distanceposition", -1);

        if (data.hasExtra("lastlat") && data.hasExtra("lastlon")) {
            lastLat = data.getStringExtra("lastlat");
            lastLon = data.getStringExtra("lastlon");
        }
        if (distanceSelectedPoition > -1) {
            selectedDistanceName = data.getStringExtra("distanceitemname");
            tv_distance_value.setText(selectedDistanceName);
            if (selectedDistanceName.equals(getString(R.string.my_city_ggs)) || selectedDistanceName.equals(getString(R.string.my_country_ggs))) {
                selectedSearchLocationGGS = (SearchLocationGGS) data.getBundleExtra("bundle").getSerializable("searchlocation");

            }
            enableClearButton();


        }
    }

    private void enableClearButton() {
        tv_clear.setTextColor(ContextCompat.getColor(getContext(), R.color.white_ggs));
        changeApplyFilterColorToWhite();
        filterChanged = true;
        cleared = false;
        //tv_clear.setEnabled(true);
    }

    public void setOnApplyFilterClickListener(OnApplyFilterClick onApplyFilterClick) {
        this.onApplyfilterClick = onApplyFilterClick;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        /*if (isChecked) {
            map.put("negotiable", "true");
            UtilsGGS.getGGSearch(getActivity()).setNegotiable(true);
        } else {
            map.put("negotiable", "false");
            UtilsGGS.getGGSearch(getActivity()).setNegotiable(false);

        }*/
        enableClearButton();
        changeApplyFilterColorToWhite();
        filterChanged = true;
        cleared = false;
        checkedChanged = true;
    }


    @Override
    public void onLocationChange(Location location) {
        mLocation = location;
        if (mLocation != null) {
            ServicePreference.getInstance(getContext()).setLastLat(String.valueOf(mLocation.getLatitude()));
            ServicePreference.getInstance(getContext()).setLastLng(String.valueOf(mLocation.getLongitude()));
        }
    }
}
