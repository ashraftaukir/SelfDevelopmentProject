package com.gagagugu.ggservice.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.adapter.NavGridViewRecycleViewAdapter;
import com.gagagugu.ggservice.asynctasks.SearchTaskGGS;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.interfaces.CallBackForSearch;
import com.gagagugu.ggservice.interfaces.OnApplyFilterClick;
import com.gagagugu.ggservice.interfaces.OnItemSelectedListenerForItemArrangement;
import com.gagagugu.ggservice.models.GridViewItemData;
import com.gagagugu.ggservice.models.Result;
import com.gagagugu.ggservice.models.SearchPostGGS;
import com.gagagugu.ggservice.utils.ConstantGGS;
import com.gagagugu.ggservice.utils.DialogManager;
import com.gagagugu.ggservice.utils.GridLayoutItemDecoration;
import com.gagagugu.ggservice.utils.LocationHandler;
import com.gagagugu.ggservice.utils.MixpanelUtils;
import com.gagagugu.ggservice.utils.NetworkUtilGGS;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.RecyclerTouchListenerGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;
import com.gagagugu.ggservice.view.activity.ViewDetailsActivityGGS;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static com.gagagugu.ggservice.utils.UtilsGGS.EDIT_DELETE_REQUEST_CODE;


public class SearchFragmentGGS extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener, CallBackForSearch, OnItemSelectedListenerForItemArrangement, OnApplyFilterClick, LocationHandler.OnLocationUpdateListener, View.OnFocusChangeListener, View.OnTouchListener {
    private static final String TAG = "SearchFilterActivityTag";
    private static final String SERVICE = "service";
    private static final String BUY = "buy";
    private static final String SELL = "sell";
    private static final String EXCHANGE = "exchange";
    private static final String RENTAL = "rental";
    private static final String QUERY_STRING_KEY = "q";
    private static final String TYPE_KEY = "type";
    private static final String OFFER_TYPE_KEY = "offer_type";
    private static final String ITEM = "item";
    private static final String SORT_BY = "sort_by";
    private static final String SORT = "sort";
    private static final String ASCENDING = "asc";
    private static final String DESCENDING = "desc";
    private static final String TIME = "time";
    private static final String RATE = "rating";
    private static final String PRICE = "price";
    private static final String DISTANCE = "distance";
    private final int QUERY_MIN_LENGTH = 1;
    private final String limit = "20";
    long idle_min = 500; // 0.5 seconds after user stops typing
    long last_text_edit = 0;
    Handler h = new Handler();
    boolean already_queried = false;
    Runnable input_finish_checker;
    private Toolbar toolbar;
    private SearchView searchView;
    private RelativeLayout mapLayout, filterLayout;
    private ConcurrentHashMap<String, String> params = new ConcurrentHashMap<>();
    private NavGridViewRecycleViewAdapter navGridViewRecycleViewAdapter;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<GridViewItemData> items = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    //variable for pagination
    private boolean loading, moreItem, isSwipeRefresh;
    private DialogManager dialogManager;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private TextView allSearchButton, serviceButton, buyButton, sellButton, exchangeButton, rentalButton, prevSelectedButton;
    private RecyclerView recyclerView;
    private View noSearchResultFoundLayout;
    private int page = 1;
    private ProfileColorsGGS theme;
    private LocationHandler locationHandler;
    private String clickingtypeValue;
    private LinearLayout cardOverlayLayout;

    private CardView searchCard;
    TextView searchQueryText;
    private boolean fromPagination = false;
    private boolean fromSearchFilter = false;
    private View transparentLayoutOnMap;
    private RelativeLayout rootLayout;
    private HorizontalScrollView horizontalScrollView;
    private RelativeLayout appBarParentRelativeLayout;
    private String currentKeyword = "";

    public SearchFragmentGGS() {
        // Required empty public constructor
    }

    private double currentLat, currentLng;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_fragment_gg, container, false);
        setSortValueInParam(TIME, DESCENDING);  //default sorting- newest result
        locationHandler = new LocationHandler(getContext());
        getActivity().overridePendingTransition(R.anim.activity_transition_slide_in_ggs, R.anim.activity_transition_slide_out_ggs);
        dialogManager = new DialogManager(getActivity());
        theme = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        locationHandler.setOnLocationUpdateListener(this);


        initLatLng();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        initViews(view);
        getBundleExtras();
        setupTheme();
        initListner();
        //callApi("");
        return view;
    }

    private void getBundleExtras() {
        Bundle bundle = getArguments();
        setParam(bundle.getString("type"), bundle.getString("offer_type"));
        setInitialSelectedButton(bundle.getString("type"), bundle.getString("offer_type"));

    }

    private void setInitialSelectedButton(String type, String offer_type) {
        if (type.isEmpty() && offer_type.isEmpty()) {
            changeToClickState(allSearchButton);
        } else if (type.equals(SERVICE)) {
            changeToClickState(serviceButton);

        } else if (offer_type.equals(BUY)) {
            changeToClickState(buyButton);

        } else if (offer_type.equals(SELL)) {
            changeToClickState(sellButton);

        } else if (offer_type.equals(EXCHANGE)) {
            changeToClickState(exchangeButton);

        } else if (offer_type.equals(RENTAL)) {
            changeToClickState(rentalButton);

        }
        clickingtypeValue = offer_type;

    }

    private void initLatLng() {
        if (!ServicePreference.getInstance(getContext()).getLastLat().isEmpty()) {
            currentLat = parseDouble(ServicePreference.getInstance(getContext()).getLastLat());
        }
        if (!ServicePreference.getInstance(getContext()).getLastLng().isEmpty()) {
            currentLng = parseDouble(ServicePreference.getInstance(getContext()).getLastLng());
        }
    }

    private double parseDouble(String val) {
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        locationHandler.setOnLocationUpdateListener(null);
    }


    private void initViews(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbarNav);
        setUpToolbar();


        searchView = (SearchView) view.findViewById(R.id.search_view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        addRecycleViewOnClickListner();
        setRecyclerViewAdapter();

        allSearchButton = (TextView) view.findViewById(R.id.all_search_button);
        serviceButton = (TextView) view.findViewById(R.id.serivce_search_button);
        buyButton = (TextView) view.findViewById(R.id.buy_search_button);
        sellButton = (TextView) view.findViewById(R.id.sell_search_button);
        exchangeButton = (TextView) view.findViewById(R.id.exchange_search_button);
        rentalButton = (TextView) view.findViewById(R.id.rental_search_button);
        mapLayout = (RelativeLayout) view.findViewById(R.id.search_map_layout);
        filterLayout = (RelativeLayout) view.findViewById(R.id.search_filter_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        noSearchResultFoundLayout = view.findViewById(R.id.no_search_result_layout);
        searchCard = (CardView) view.findViewById(R.id.search_text_card);
        searchQueryText = (TextView) view.findViewById(R.id.search_query_text);
        cardOverlayLayout = (LinearLayout) view.findViewById(R.id.card_overlay_layout);
        transparentLayoutOnMap = view.findViewById(R.id.transparent_layout_on_map);
        rootLayout = (RelativeLayout) view.findViewById(R.id.root_layout);
        horizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.search_horizontal_scroll_view);
        appBarParentRelativeLayout = (RelativeLayout) view.findViewById(R.id.app_bar_parent_relative_layout);
        getSearchViewFocus();


    }


    private void setUpToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_icon_ggs);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    private void setRecyclerViewAdapter() {
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_top_ggs);
        recyclerView.addItemDecoration(new GridLayoutItemDecoration(spacingInPixels));
        navGridViewRecycleViewAdapter = new NavGridViewRecycleViewAdapter(items);
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(navGridViewRecycleViewAdapter);

    }

    private void getSearchViewFocus() {
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        //searchView.requestFocus();
        //  getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void setupTheme() {
        Drawable mapDrawable = ContextCompat.getDrawable(getContext(), R.drawable.search_bottom_map_drawable_ggs);
        mapDrawable.setColorFilter(theme.getColorCodeLight(), PorterDuff.Mode.SRC_ATOP);

        Drawable filterDrawable = ContextCompat.getDrawable(getContext(), R.drawable.search_bottom_filter_drawable_ggs);
        filterDrawable.setColorFilter(theme.getColorCodeLight(), PorterDuff.Mode.SRC_ATOP);


        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mapLayout.setBackgroundDrawable(mapDrawable);
            filterLayout.setBackgroundDrawable(filterDrawable);

        } else {
            mapLayout.setBackground(mapDrawable);
            filterLayout.setBackground(filterDrawable);

        }

    }

    private void initListner() {
        addSwipeRefreshLayoutListener();
        addOnScrollListner();
        allSearchButton.setOnClickListener(this);
        serviceButton.setOnClickListener(this);
        buyButton.setOnClickListener(this);
        sellButton.setOnClickListener(this);
        exchangeButton.setOnClickListener(this);
        rentalButton.setOnClickListener(this);
        searchView.setOnQueryTextListener(this);
        filterLayout.setOnClickListener(this);
        mapLayout.setOnClickListener(this);
        searchCard.setOnClickListener(this);
        searchView.setOnQueryTextFocusChangeListener(this);
        cardOverlayLayout.setOnClickListener(this);
        rootLayout.setOnTouchListener(this);


    }

    private void addOnScrollListner() {


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                clearKeyboard();

                int SCROLL_DIRECTION_UP = -1;
                if (recyclerView.canScrollVertically(SCROLL_DIRECTION_UP)) {
                    appBarParentRelativeLayout.setBackgroundColor(theme.getColorCodeLight());
                    ViewCompat.setElevation(appBarParentRelativeLayout, 10F);
                } else {
                    appBarParentRelativeLayout.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
                    ViewCompat.setElevation(appBarParentRelativeLayout, 0f);
                }
                // if (searchView.getQuery().length() > 0) {
                if (dy > 0) {
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && !loading && moreItem) {

                        if (NetworkUtilGGS.isInternetAvailable(getContext())) {
                            fromPagination = true;
                            callApi(searchView.getQuery().toString());
                        }


                    } else {
                        //Toast.makeText(SearchFilterActivityGGS.this, "No more item", Toast.LENGTH_SHORT).show();
                    }
                }
                //  }
            }
        });
    }

    private void addSwipeRefreshLayoutListener() {
        swipeRefreshLayout.setEnabled(false);

    }


    private void addRecycleViewOnClickListner() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListenerGGS(getContext(), new RecyclerTouchListenerGGS.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                String type = items.get(position).getType();
                String id = String.valueOf(items.get(position).getId());

                Log.d("navgriditempostion", type + "," + id);

                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                bundle.putString("type", type);

                Intent intent = new Intent(getContext(), ViewDetailsActivityGGS.class);
                intent.putExtra("bundle", bundle);
                startActivityForResult(intent, EDIT_DELETE_REQUEST_CODE);


            }

        }));
    }

    private void callApi(String newText) {
        //  searchView.clearFocus();
        Log.d(TAG, "callApi querystring: " + newText);
        params.put(QUERY_STRING_KEY, newText);
        currentKeyword = newText;
        if (params.containsKey("sort_by") && params.get("sort_by").equals("distance") && params.get("lat").isEmpty() && params.get("lon").isEmpty()) {
            /**
             * for that case when search param contain distance filtering, but lat lon does not exist due to a clear of map while selecting mycountry and mycity


             */
            Log.d(TAG, "callApi: intospecialcase");
            putLocationInParam(currentLat, currentLng);
        }

        //if (newText.length() >= QUERY_MIN_LENGTH) {
        cardOverlayLayout.setVisibility(View.GONE);
        clearKeyboard();
        if (NetworkUtilGGS.isInternetAvailable(getContext())) {
            loading = true;
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });

            if (newText.length() > 0) {
                MixpanelUtils mixpanelUtils = new MixpanelUtils(getActivity());
                mixpanelUtils.mpTrackKeyword("SearchFragmentGGS", ConstantGGS.SEARCH_KEY_WORD, newText);
            }

            new SearchTaskGGS(getActivity(), String.valueOf(page), params, limit, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Toast.makeText(getContext(), getString(R.string.text_no_internet_connection_ggs), Toast.LENGTH_SHORT).show();
            if(page==1){
                items.clear();
                navGridViewRecycleViewAdapter.notifyDataSetChanged();
                transparentLayoutOnMap.setVisibility(View.VISIBLE);
            }
            if (items.size() == 0) {
                if (!currentKeyword.isEmpty()) {
                    noSearchResultFoundLayout.setVisibility(View.VISIBLE);
                    //transparentLayoutOnMap.setVisibility(View.VISIBLE);
                } else {
                    noSearchResultFoundLayout.setVisibility(View.GONE);

                }
            }

            already_queried = false;
            h.removeCallbacks(input_finish_checker);
            //  moreItem = gridViewItemDatas.size()>0;
            loading = false;
        }
    }

    private void clearKeyboard() {
        searchView.clearFocus();
    }


    private void setParam(String type, String offer_type) {
        params.put(TYPE_KEY, type);
        params.put(OFFER_TYPE_KEY, offer_type);
        callApi(searchView.getQuery().toString());

    }

    @Override
    public void doneLoadingData(SearchPostGGS searchPost) {
        if (searchPost != null) {
            ArrayList<GridViewItemData> gridViewItemDatas = searchPost.getGridViewItemDatas();
            Log.d(TAG, "doneLoadingData: " + gridViewItemDatas.size());


            if (fromPagination) {
                int previous_size = items.size();
                items.addAll(gridViewItemDatas);
                navGridViewRecycleViewAdapter.notifyItemRangeInserted(previous_size, items.size());
                fromPagination = false;
            } else {
                items.clear();
                items.addAll(gridViewItemDatas);
                navGridViewRecycleViewAdapter.notifyDataSetChanged();
            }

            moreItem = !(items.size() == searchPost.getTotalResult());

        } else {
            moreItem = false;
            items.clear();
            navGridViewRecycleViewAdapter.notifyDataSetChanged();
            transparentLayoutOnMap.setVisibility(View.VISIBLE);

        }

        if (items.size() == 0) {
            transparentLayoutOnMap.setVisibility(View.VISIBLE);
            if (!currentKeyword.isEmpty()) {
                noSearchResultFoundLayout.setVisibility(View.VISIBLE);
            } else {
                noSearchResultFoundLayout.setVisibility(View.GONE);

            }
        } else {
            noSearchResultFoundLayout.setVisibility(View.GONE);
            transparentLayoutOnMap.setVisibility(View.GONE);
        }

        if (moreItem) page++;
        already_queried = false;
        h.removeCallbacks(input_finish_checker);
        //  moreItem = gridViewItemDatas.size()>0;
        loading = false;
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query.trim().length() >= QUERY_MIN_LENGTH) {
            page = 1;
            search(query);
        }
        return true;
    }


    @Override
    public boolean onQueryTextChange(final String newText) {
        /*if (fromSearchFilter) {
            fromSearchFilter = false;
            if(searchView.hasFocus()){
                clearKeyboard();
            }
            return false;
        }*/
        if (newText.length() == 0) {
            cardOverlayLayout.setVisibility(View.GONE);
            searchView.clearFocus();
            /*items.clear();
            navGridViewRecycleViewAdapter.notifyDataSetChanged();*/
            // noSearchResultFoundLayout.setVisibility(View.GONE);
            //transparentLayoutOnMap.setVisibility(View.VISIBLE);
        } else {
            if (cardOverlayLayout.getVisibility() == View.GONE) {
                cardOverlayLayout.setVisibility(View.VISIBLE);
            }
            searchQueryText.setText(newText);

        }
        return true;


        //Log.d(TAG, "Query Text: " + newText);

        /*last_text_edit = System.currentTimeMillis();
        isTextChanged = true;
        if (newText.length() != 0) {
            input_finish_checker = new Runnable() {
                public void run() {
                    if (System.currentTimeMillis() > (last_text_edit + idle_min - 10)) {
                        search(newText);
                    }
                }
            };
            h.postDelayed(input_finish_checker, idle_min);
            return true;
        }
        return false;*/

    }

    private void search(String newText) {
        if (!already_queried) {
            already_queried = true;
            callApi(newText);
        }


    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onClick(View v) {


        if (v == allSearchButton) {

            changeButtonState(allSearchButton, "", "");
        } else if (v == serviceButton) {

            changeButtonState(serviceButton, SERVICE, "");
        } else if (v == buyButton) {
            changeButtonState(buyButton, ITEM, BUY);
        } else if (v == sellButton) {
            changeButtonState(sellButton, ITEM, SELL);
        } else if (v == exchangeButton) {
            changeButtonState(exchangeButton, ITEM, EXCHANGE);
        } else if (v == rentalButton) {
            changeButtonState(rentalButton, ITEM, RENTAL);
        } else if (v == filterLayout) {
            fromSearchFilter = true;
            openSearchFilter();
        } else if (v == mapLayout) {
            openItemsInMap();
        } else if (v == searchCard) {
            page = 1;
            search(searchView.getQuery().toString());
        } else if (v == cardOverlayLayout) {
            cardOverlayLayout.setVisibility(View.GONE);
            clearKeyboard();
        }
    }

    private void openItemsInMap() {
        if (items.size() > 0) {
            Gson gson = new Gson();
            String itemsJson = gson.toJson(items);
            Type type = new TypeToken<ArrayList<Result>>() {
            }.getType();
            ArrayList<Result> results = gson.fromJson(itemsJson, type);

            Log.d(TAG, "openItemsInMap: " + gson.toJson(results));

            UtilsGGS.resultArrayList = results;
            //appBarLayout.setVisibility(View.GONE);
            com.gagagugu.ggservice.fragment.SearchMapFragment searchMapFragment = new com.gagagugu.ggservice.fragment.SearchMapFragment();
            getFragmentManager().beginTransaction().add(R.id.fragment_search_container_ggs, searchMapFragment).addToBackStack("search_map_fragment").commit();
        }
    }

    private void openSearchFilter() {
        clearKeyboard();
        SearchFilterFragmentGGS searchFilterFragmentGGS = new SearchFilterFragmentGGS();

        searchFilterFragmentGGS.setOnApplyFilterClickListener(this);
        Bundle bundle = new Bundle();
        bundle.putString("clickingtypeValue", clickingtypeValue);
        searchFilterFragmentGGS.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.fragment_search_container_ggs, searchFilterFragmentGGS).addToBackStack("search_filter_fragment").commit();
    }

    private void changeButtonState(TextView clickedButton, String paramType, String paramOfferType) {
        page = 1;
        if (prevSelectedButton == clickedButton) {

        } else {
            if (prevSelectedButton != null) {
                changeToUnclickState(prevSelectedButton);
            }
            changeToClickState(clickedButton);
            prevSelectedButton = clickedButton;
            setParam(paramType, paramOfferType);


            if (clickedButton == exchangeButton) {
                if (params.containsKey("sort_by") && params.get("sort_by").equals("price")) {
                    setSortValueInParam(TIME, DESCENDING);
                    UtilsGGS.getGGSearch(getActivity()).setSortingid(0);
                }
            }
        }
        //isTypeChange = true;

        clickingtypeValue = paramOfferType;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpToolbar();
        /*if (prevSelectedButton != null) {
            setButtonSelectedOnResume(prevSelectedButton.getText().toString());

        }*/ /*else {
            changeButtonState(allSearchButton, "", "");
        }*/
        locationHandler.setOnLocationUpdateListener(this);
        // appBarLayout.setVisibility(View.VISIBLE);

        // searchCard.setVisibility(searchCard.getVisibility());


    }

   /* private void setButtonSelectedOnResume(String s) {
       *//* if (s.equals(getString(R.string.buy_product_ggs))) {
            onResumeButtonChange(buyButton);
        } else if (s.equals(getString(R.string.sell_text_ggs))) {
            onResumeButtonChange(sellButton);

        } else if (s.equals(getString(R.string.exchange_ggs))) {
            onResumeButtonChange(exchangeButton);

        } else if (s.equals(getString(R.string.rental_ggs))) {
            onResumeButtonChange(rentalButton);

        } else if (s.equals(getString(R.string.service_ggs))) {
            onResumeButtonChange(serviceButton);

        } else if (s.equals(getString(R.string.all_search_ggs))) {
            onResumeButtonChange(allSearchButton);
        }*//*
    }

    private void onResumeButtonChange(Button button) {
        changeToClickState(button);
    }*/

    private void changeToUnclickState(TextView textView) {

        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white_ggs));
        textView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        ViewCompat.setElevation(textView, 0f);

    }

    private void changeToClickState(final TextView textView) {
        prevSelectedButton = textView;
        textView.setTextColor(theme.getColorCodeLight());

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.search_textview_back_ggs);
        drawable.setColorFilter(ContextCompat.getColor(getContext(), R.color.white_ggs), PorterDuff.Mode.SRC_ATOP);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            textView.setBackgroundDrawable(drawable);

        } else {
            textView.setBackground(drawable);

        }
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;
        final int width = metrics.widthPixels;

        float left = textView.getX();


        textView.post(new Runnable() {
            @Override
            public void run() {
                int scrollX = ((int) textView.getLeft() - (width / 2)) + (textView.getWidth() / 2);
                horizontalScrollView.smoothScrollTo(scrollX, 0);
                ViewCompat.setElevation(textView, 15f);

            }
        });


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.search_filter_menu_ggs, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.filter_by) {
            fromSearchFilter = true;
            openItemArrangement();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openItemArrangement() {
        clearKeyboard();
        ItemArrangementFragmentGGS itemArrangementFragmentGGS = new ItemArrangementFragmentGGS();
        itemArrangementFragmentGGS.setOnItemSelectedListener(this);
        Bundle bundle = new Bundle();
        if (prevSelectedButton == exchangeButton) {
            bundle.putBoolean("is_exchange", true);
        } else {
            bundle.putBoolean("is_exchange", false);

        }
        itemArrangementFragmentGGS.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.fragment_search_container_ggs, itemArrangementFragmentGGS).addToBackStack("item_arrangement").commit();


    }

    @Override
    public void onItemSelected() {
        Log.d(TAG, "onItemSelected: " + UtilsGGS.getGGSearch(getContext()).getSortingname());
        setParamsForSort(UtilsGGS.getGGSearch(getContext()).getSortingname());
    }

    private void setParamsForSort(String sortString) {
        if (sortString.equals(getString(R.string.newest_result_ggs))) {
            setSortValueInParam(TIME, DESCENDING);
        } else if (sortString.equals(getString(R.string.oldest_result_ggs))) {
            setSortValueInParam(TIME, ASCENDING);

        } else if (sortString.equals(getString(R.string.nearby_location_ggs))) {
            setSortValueInParam(DISTANCE, ASCENDING);
            putLocationInParam(currentLat, currentLng);

        } else if (sortString.equals(getString(R.string.distant_location_ggs))) {
            setSortValueInParam(DISTANCE, ASCENDING);
            putLocationInParam(currentLat, currentLng);

        } else if (sortString.equals(getString(R.string.top_rated_ggs))) {
            setSortValueInParam(RATE, DESCENDING);

        } else if (sortString.equals(getString(R.string.poorly_rated_ggs))) {
            setSortValueInParam(RATE, ASCENDING);

        } else if (sortString.equals(getString(R.string.less_expensive_ggs))) {
            setSortValueInParam(PRICE, ASCENDING);

        } else if (sortString.equals(getString(R.string.much_expensive_ggs))) {
            setSortValueInParam(PRICE, DESCENDING);

        }

        if (!UtilsGGS.getGGSearch(getActivity()).getDistancename().equals(getString(R.string.nearby_ggs)) && !sortString.equals(getString(R.string.nearby_location_ggs)) && !sortString.equals(getString(R.string.distant_location_ggs))) {
            params.put("lat", "");
            params.put("lon", "");
        }

        page = 1;
        Log.d(TAG, "onClickSOrt: " + String.valueOf(searchCard.getVisibility() == View.GONE));

        if (cardOverlayLayout.getVisibility() == View.GONE) {
            callApi(searchView.getQuery().toString());
        }

    }

    private void putLocationInParam(double currentLat, double currentLng) {
        params.put("lat", String.valueOf(currentLat));
        params.put("lon", String.valueOf(currentLng));
    }

    private void setSortValueInParam(String sort_by, String sort) {
        params.put(SORT_BY, sort_by);
        params.put(SORT, sort);
    }


    @Override
    public void onLocationChange(Location location) {


        if (location != null) {
            Log.d("localksjdf", location.getLatitude() + "," + location.getLongitude());
            currentLat = location.getLatitude();
            currentLng = location.getLongitude();
            ServicePreference.getInstance(getContext()).setLastLat(String.valueOf(currentLat));
            ServicePreference.getInstance(getContext()).setLastLng(String.valueOf(currentLng));
        }
    }

    @Override
    public void onClick(HashMap<String, String> map) {
        for (String s : map.keySet()) {
            params.put(s, map.get(s));
        }
        page = 1;
        if (cardOverlayLayout.getVisibility() == View.GONE) {
            callApi(searchView.getQuery().toString());
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            if (searchView.getQuery().toString().trim().length() > 0) {
                searchQueryText.setText(searchView.getQuery().toString());
                cardOverlayLayout.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (requestCode == EDIT_DELETE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            boolean isDelete = data.getBooleanExtra("isDelete", false);
            if (isDelete) {
                String postId = data.getStringExtra("id");
                int position = getPositionFromId(postId);
                if (position != -1) {
                    items.remove(position);
                    navGridViewRecycleViewAdapter.notifyItemRemoved(position);
                }
            } else {

                String postId = data.getStringExtra("id");
                int position = getPositionFromId(postId);
                if (position != -1) {
                    if (data.hasExtra("image")) {
                        String image = data.getStringExtra("image");
                        ArrayList<String> images = new ArrayList<>();
                        images.add(image);
                        items.get(position).setThumbImageUrls(images);
                    } else {
                        items.get(position).setThumbImageUrls(new ArrayList<String>());
                    }

                    String price = data.getStringExtra("price");


                    items.get(position).setPrice(price);
                    navGridViewRecycleViewAdapter.notifyItemChanged(position);
                }

            }
        }
    }


    private int getPositionFromId(String postId) {
        int position = -1;
        for (int i = 0; i < items.size(); i++) {
            if (String.valueOf(items.get(i).getId()).equals(postId)) {
                position = i;
                break;
            }
        }

        return position;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == rootLayout) {
            clearKeyboard();
            return true;
        }
        return false;
    }
}
