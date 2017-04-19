package com.gagagugu.ggservice.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.adapter.NavGridViewRecycleViewAdapter;
import com.gagagugu.ggservice.asynctasks.GetNHFeedForGridAsynTask;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.interfaces.CallBackFromGridAysnTask;
import com.gagagugu.ggservice.models.GridViewItemData;
import com.gagagugu.ggservice.utils.ConstantGGS;
import com.gagagugu.ggservice.utils.DialogManager;
import com.gagagugu.ggservice.utils.GridLayoutItemDecoration;
import com.gagagugu.ggservice.utils.MixpanelUtils;
import com.gagagugu.ggservice.utils.NetworkUtilGGS;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.RecyclerTouchListenerGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;
import com.gagagugu.ggservice.view.activity.SearchFilterActivityGGS;
import com.gagagugu.ggservice.view.activity.ViewDetailsActivityGGS;

import java.util.ArrayList;

import static com.gagagugu.ggservice.utils.UtilsGGS.EDIT_DELETE_REQUEST_CODE;

public class NAVGridFragment extends Fragment implements CallBackFromGridAysnTask, View.OnClickListener {
    public static final String CLASS_NAME = "NAVGridFragment";
    private static final int GRID_ITEM_COUNT = 2;
    private RecyclerView mRvGgServiceList;
    private NavGridViewRecycleViewAdapter navGridViewRecycleViewAdapter;
    private int page;
    private GridLayoutManager gridLayoutManager;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading, moreItem, isRefreshing;
    private ArrayList<GridViewItemData> items = new ArrayList<>();
    private DialogManager dialogManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final int LIMIT = 20;
    MixpanelUtils mixpanelUtils;
    private static final String SERVICE = "service";
    private static final String BUY = "buy";
    private static final String SELL = "sell";
    private static final String EXCHANGE = "exchange";
    private static final String RENTAL = "rental";
    private static final String ITEM = "item";
    private TextView allSearchButton, serviceButton, buyButton, sellButton, exchangeButton, rentalButton, prevSelectedButton;
    private HorizontalScrollView horizontalScrollView;
    private ProfileColorsGGS theme;
    private String paramOfferType = "", paramType = "";


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            Intent intent = new Intent(getContext(), SearchFilterActivityGGS.class);
            Bundle bundle = new Bundle();
            bundle.putString("type", paramType);
            bundle.putString("offer_type", paramOfferType);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
            //Toast.makeText(context, "Search coming soon", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_navgrid, container, false);
        dialogManager = new DialogManager(getActivity());
        setHasOptionsMenu(true);
        page = 1;

        loading = false;
        moreItem = true;
        isRefreshing = false;

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        addOnScrollListner();
        theme = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        changeButtonState(allSearchButton, "", "");
        addRecycleViewOnClickListner();
        addSwipRefreshLayout();
        initListeners();
        /*if (items.size() == 0) {
            if (NetworkUtilGGS.isInternetAvailable(getContext())) {
                callNHFeedApi();
            } else {
                //dialogManager.showDialog(getString(R.string.failed_to_load_data_ggs), getString(R.string.text_no_internet_connection_ggs), false);
                Toast.makeText(getContext(), getString(R.string.text_no_internet_connection_ggs), Toast.LENGTH_SHORT).show();

            }
        }*/

    }

    private void initListeners() {
        allSearchButton.setOnClickListener(this);
        serviceButton.setOnClickListener(this);
        buyButton.setOnClickListener(this);
        sellButton.setOnClickListener(this);
        exchangeButton.setOnClickListener(this);
        rentalButton.setOnClickListener(this);
    }

    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && items.size() == 0) {
            if (NetworkUtilGGS.isInternetAvailable(getContext())) {
                callNHFeedApi();
            } else {
                dialogManager.showDialog(getString(R.string.failed_to_load_data_ggs), getString(R.string.text_no_internet_connection_ggs), false);

            }
        }

    }*/

    private void initView(View view) {
        mRvGgServiceList = (RecyclerView) view.findViewById(R.id.grid_recycle_view);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_top_ggs);
        mRvGgServiceList.addItemDecoration(new GridLayoutItemDecoration(spacingInPixels));

        navGridViewRecycleViewAdapter = new NavGridViewRecycleViewAdapter(items);
        gridLayoutManager = new GridLayoutManager(getActivity(), GRID_ITEM_COUNT);
        mRvGgServiceList.setLayoutManager(gridLayoutManager);
        mRvGgServiceList.setAdapter(navGridViewRecycleViewAdapter);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mixpanelUtils = new MixpanelUtils(getActivity().getApplicationContext());
        ConstantGGS.CURRENT_TIME = mixpanelUtils.getCurrentTime();

        allSearchButton = (TextView) getActivity().findViewById(R.id.all_search_button);
        serviceButton = (TextView) getActivity().findViewById(R.id.serivce_search_button);
        buyButton = (TextView) getActivity().findViewById(R.id.buy_search_button);
        sellButton = (TextView) getActivity().findViewById(R.id.sell_search_button);
        exchangeButton = (TextView) getActivity().findViewById(R.id.exchange_search_button);
        rentalButton = (TextView) getActivity().findViewById(R.id.rental_search_button);

        horizontalScrollView = (HorizontalScrollView) getActivity().findViewById(R.id.search_horizontal_scroll_view);
    }

    @Override
    public void onResume() {
        super.onResume();
        theme = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());

        if (prevSelectedButton != null) {
            prevSelectedButton.setTextColor(theme.getColorCodeLight());
        } else {
            allSearchButton.setTextColor(theme.getColorCodeLight());
        }
    }

    private void addSwipRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!loading && !isRefreshing) {
                    if (NetworkUtilGGS.isInternetAvailable(getContext())) {
                        isRefreshing = true;
                        page = 1;
                        moreItem = true;
                        callNHFeedApi();
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                        dialogManager.showDialog(getString(R.string.failed_to_load_data_ggs), getString(R.string.text_no_internet_connection_ggs), false);
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void addOnScrollListner() {
        mRvGgServiceList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && !loading && moreItem) {

                        if (NetworkUtilGGS.isInternetAvailable(getContext())) {
                            callNHFeedApi();
                        }


                    }
                }
            }
        });
    }


    private void addRecycleViewOnClickListner() {
        mRvGgServiceList.addOnItemTouchListener(new RecyclerTouchListenerGGS(getActivity(), new RecyclerTouchListenerGGS.ClickListener() {
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

    private void callNHFeedApi() {
        loading = true;
        swipeRefreshLayout.setRefreshing(true);
        String fields = UtilsGGS.getFieldsForSearch();
        new GetNHFeedForGridAsynTask(getActivity(), fields, String.valueOf(page), String.valueOf(LIMIT), this, paramType, paramOfferType).
                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void doneLoadingData(ArrayList<GridViewItemData> gridViewItemDatas) {

        if (gridViewItemDatas != null) {
            if (gridViewItemDatas.size() == 0) {
                moreItem = false;
                if (page == 1) {
                    items.clear();
                    navGridViewRecycleViewAdapter.notifyDataSetChanged();
                }
            } else {

                if (page == 1) {
                    items.clear();
                    items.addAll(gridViewItemDatas);
                    navGridViewRecycleViewAdapter.notifyDataSetChanged();
                    isRefreshing = false;

                } else {
                    int previous_size = items.size();
                    items.addAll(gridViewItemDatas);
                    navGridViewRecycleViewAdapter.notifyItemRangeInserted(previous_size, items.size());
                }


                page++;
            }


        }

        isRefreshing = false;
        loading = false;

        swipeRefreshLayout.setRefreshing(false);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
    public void onStop() {
        ConstantGGS.END_TIME = mixpanelUtils.getCurrentTime();
        ConstantGGS.STAY_TIME = mixpanelUtils.getDateDiff(ConstantGGS.CURRENT_TIME, ConstantGGS.END_TIME);
        mixpanelUtils.mixPanelReport(CLASS_NAME, ConstantGGS.CURRENT_TIME, ConstantGGS.END_TIME, ConstantGGS.STAY_TIME);
        mixpanelUtils.mixpanel.flush();
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        if (v == allSearchButton) {
            int scrollX = (allSearchButton.getLeft() - (width / 2)) + (allSearchButton.getWidth() / 2);
            horizontalScrollView.smoothScrollTo(scrollX, 0);
            // horizontalScrollView.smoothScrollTo(0, 0);
            changeButtonState(allSearchButton, "", "");
        } else if (v == serviceButton) {
            int scrollX = (serviceButton.getLeft() - (width / 2)) + (serviceButton.getWidth() / 2);
            horizontalScrollView.smoothScrollTo(scrollX, 0);
            // horizontalScrollView.smoothScrollTo(0, 0);
            changeButtonState(serviceButton, SERVICE, "");
        } else if (v == buyButton) {
            int scrollX = (buyButton.getLeft() - (width / 2)) + (buyButton.getWidth() / 2);
            horizontalScrollView.smoothScrollTo(scrollX, 0);
            //horizontalScrollView.smoothScrollTo((int) buyButton.getX(), (int) buyButton.getY());
            changeButtonState(buyButton, ITEM, BUY);
        } else if (v == sellButton) {
            int scrollX = (sellButton.getLeft() - (width / 2)) + (sellButton.getWidth() / 2);
            horizontalScrollView.smoothScrollTo(scrollX, 0);
            //horizontalScrollView.smoothScrollTo((int) sellButton.getX(), (int) sellButton.getY());
            changeButtonState(sellButton, ITEM, SELL);
        } else if (v == exchangeButton) {
            int scrollX = (exchangeButton.getLeft() - (width / 2)) + (exchangeButton.getWidth() / 2);
            horizontalScrollView.smoothScrollTo(scrollX, 0);
            // horizontalScrollView.smoothScrollTo((int) exchangeButton.getX(), (int) exchangeButton.getY());
            changeButtonState(exchangeButton, ITEM, EXCHANGE);
        } else if (v == rentalButton) {
            int scrollX = (rentalButton.getLeft() - (width / 2)) + (rentalButton.getWidth() / 2);
            horizontalScrollView.smoothScrollTo(scrollX, 0);
            //horizontalScrollView.smoothScrollTo((int) rentalButton.getX(), (int) rentalButton.getY());
            changeButtonState(rentalButton, ITEM, RENTAL);
        }
    }

    private void changeButtonState(TextView clickedButton, String paramType, String paramOfferType) {
        if (prevSelectedButton == clickedButton) {
            /*changeToUnclickState(clickedButton);
            prevSelectedButton = null;
            setParam("", "");*/

        } else {
            changeToClickState(clickedButton);
            if (prevSelectedButton != null) {
                changeToUnclickState(prevSelectedButton);
            }
            prevSelectedButton = clickedButton;
            page = 1;
            this.paramType = paramType;
            this.paramOfferType = paramOfferType;
            if (NetworkUtilGGS.isInternetAvailable(getContext())) {
                callNHFeedApi();
            } else {

                //dialogManager.showDialog(getString(R.string.failed_to_load_data_ggs), getString(R.string.text_no_internet_connection_ggs), false);
                Toast.makeText(getContext(), getString(R.string.text_no_internet_connection_ggs), Toast.LENGTH_SHORT).show();
                items.clear();
                navGridViewRecycleViewAdapter.notifyDataSetChanged();

            }
        }
        //isTypeChange = true;

        //clickingtypeValue = paramOfferType;
    }

    private void changeToUnclickState(TextView textView) {

        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white_ggs));
        textView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        ViewCompat.setElevation(textView, 0f);

    }

    private void changeToClickState(TextView textView) {

        textView.setTextColor(theme.getColorCodeLight());

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.search_textview_back_ggs);
        drawable.setColorFilter(ContextCompat.getColor(getContext(), R.color.white_ggs), PorterDuff.Mode.SRC_ATOP);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            textView.setBackgroundDrawable(drawable);

        } else {
            textView.setBackground(drawable);

        }

        ViewCompat.setElevation(textView, 10f);


    }
}

