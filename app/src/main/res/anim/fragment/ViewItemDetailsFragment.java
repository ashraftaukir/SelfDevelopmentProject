package com.gagagugu.ggservice.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tagview.TagGgs;
import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.asynctasks.GetProductDetailsTask;
import com.gagagugu.ggservice.asynctasks.ItemPostDeleteAsynTask;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.interfaces.CallBackFromDeleteItemPost;
import com.gagagugu.ggservice.interfaces.CallBackFromItemDetailsTaskGGS;
import com.gagagugu.ggservice.interfaces.OnRatingChangeListener;
import com.gagagugu.ggservice.models.GGProduct;
import com.gagagugu.ggservice.models.MyReviewGGS;
import com.gagagugu.ggservice.utils.ConstantGGS;
import com.gagagugu.ggservice.utils.DialogManager;
import com.gagagugu.ggservice.utils.MixpanelUtils;
import com.gagagugu.ggservice.utils.NetworkUtilGGS;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;
import com.gagagugu.ggservice.view.activity.CreateProductPostActivityGGS;
import com.gagagugu.ggservice.view.activity.OtherProfileActivityGGS;
import com.gagagugu.ggservice.view.activity.ProductItemActivityGGS;
import com.gagagugu.ggservice.view.activity.ViewDetailsActivityGGS;
import com.gagagugu.ggservice.view.customviews.RoundedImageViewGGS;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewItemDetailsFragment extends Fragment implements CallBackFromItemDetailsTaskGGS, View.OnClickListener, CallBackFromDeleteItemPost, OnRatingChangeListener {
    public static final String EXTRA_CONNECT_THEME_COLOR = "theme_color";
    private static final int EDIT_REQUEST_CODE = 8661;
    private static final String EXTRA_EDIT_PRODUCT_MODEL = "product_model";
    private static final String IS_OTHER_PROFILE = "is_other_profile";
    private static final String NH_PROFILE_ID = "nh_profile_id";
    private static final int DIRECTION_UP = -1;


    private String nhProfileId;
    private RelativeLayout parentView;
    private ImageView productImage;
    private TextView userName, rateValue, negotiableValue, createDate, ratingValue, title, description, itemTypeTv, locationText;
    private RoundedImageViewGGS userImage;
    private AppCompatRatingBar ratingBar;
    private LinearLayout conditionLayout, brandLayout, modelLayout, tagLayout, featureLayout, quantityLayout, exchangeLayout, rateLayout, locationLayout;
    private ImageView ribbonEndLayout;
    private TextView conditionText, brandText, modelText, tagText, featureText, quantityText, exchangeText, rateType, totalReviews, itemCountDetailsTV;
    private TextView contactWithTv;
    private CardView contactWithCard;

    private String contactWithTvText;
    private ArrayList<String> contactPref;
    private ScrollView scrollView;
    private DialogManager dialogManager;
    private String itemId;
    private GGProduct ggProduct;
    private RelativeLayout customerRatingLayout, otherProductsLayout;
    private boolean fromOtherProfile;
    private TextView countTitleTextView;
    private Toolbar toolbar;
    private ProfileColorsGGS profileColorsGGS;
    private boolean hasOptionsMenu;
    private boolean isShadowExist;
    MixpanelUtils mixpanelUtils;
    public static final String CLASS_NAME = "ViewItemDetailsFragment";
    private CardView productRatingCardView, productDetailsCardView, productDetailsBasicInforCardView;

    public ViewItemDetailsFragment() {
        // Required empty public constructor
    }

    public static com.gagagugu.ggservice.fragment.ViewItemDetailsFragment newInstance() {
        com.gagagugu.ggservice.fragment.ViewItemDetailsFragment fragment = new com.gagagugu.ggservice.fragment.ViewItemDetailsFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_product_details, container, false);
        nhProfileId = ServicePreference.getInstance(getActivity()).getNeighbourhoodProfileId();
        dialogManager = new DialogManager(getActivity());
        initView(view);
        getBundleData();
        getDataFromNetwork();
        addScrollListner();
        addBackStackChangeListner();
        return view;
    }


    private void initView(View view) {

        rateType = (TextView) view.findViewById(R.id.rate_type);
        totalReviews = (TextView) view.findViewById(R.id.total_reviews);
        parentView = (RelativeLayout) view.findViewById(R.id.parent_view);
        customerRatingLayout = (RelativeLayout) view.findViewById(R.id.customer_rating_layout);
        otherProductsLayout = (RelativeLayout) view.findViewById(R.id.other_product_layout);
        productImage = (ImageView) view.findViewById(R.id.product_image);
        userImage = (RoundedImageViewGGS) view.findViewById(R.id.user_image);
        userName = (TextView) view.findViewById(R.id.user_name);
        rateValue = (TextView) view.findViewById(R.id.rate_value);
        negotiableValue = (TextView) view.findViewById(R.id.negotiable_value);
        createDate = (TextView) view.findViewById(R.id.create_date);
        ratingBar = (AppCompatRatingBar) view.findViewById(R.id.rating_bar);
        ratingValue = (TextView) view.findViewById(R.id.rating_value);
        title = (TextView) view.findViewById(R.id.title);
        description = (TextView) view.findViewById(R.id.description);
        itemTypeTv = (TextView) view.findViewById(R.id.item_type_tv);
        locationText = (TextView) view.findViewById(R.id.location_text);

        rateLayout = (LinearLayout) view.findViewById(R.id.rate_layout);
        conditionLayout = (LinearLayout) view.findViewById(R.id.condition_layout);
        brandLayout = (LinearLayout) view.findViewById(R.id.brand_layout);
        modelLayout = (LinearLayout) view.findViewById(R.id.model_layout);
        tagLayout = (LinearLayout) view.findViewById(R.id.tag_layout);
        featureLayout = (LinearLayout) view.findViewById(R.id.features_layout);
        quantityLayout = (LinearLayout) view.findViewById(R.id.quantity_layout);
        exchangeLayout = (LinearLayout) view.findViewById(R.id.exchange_layout);
        ribbonEndLayout = (ImageView) view.findViewById(R.id.ribbon_end_layout);


        conditionText = (TextView) view.findViewById(R.id.condition_name);
        brandText = (TextView) view.findViewById(R.id.brand_tv);
        modelText = (TextView) view.findViewById(R.id.model_tv);
        tagText = (TextView) view.findViewById(R.id.tag_tv);
        featureText = (TextView) view.findViewById(R.id.feature_tv);
        quantityText = (TextView) view.findViewById(R.id.quantity_tv);
        exchangeText = (TextView) view.findViewById(R.id.exchange_details_tv);

        contactWithTv = (TextView) view.findViewById(R.id.contact_with_text);

        contactWithCard = (CardView) view.findViewById(R.id.contact_with_cardview);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        itemCountDetailsTV = (TextView) view.findViewById(R.id.item_count_details_text_view);
        countTitleTextView = (TextView) view.findViewById(R.id.count_title_text_view);
        countTitleTextView.setText(getString(R.string.other_products_ggs));

        contactWithCard.setOnClickListener(this);
        customerRatingLayout.setOnClickListener(this);
        userImage.setOnClickListener(this);
        otherProductsLayout.setOnClickListener(this);

        //     ratingBar.setEnabled(false);
        changeRatingBarColor();

        profileColorsGGS = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        rateLayout.setBackgroundColor(profileColorsGGS.getColorCodeLight());
        ribbonEndLayout.setColorFilter(profileColorsGGS.getColorCodeLight(), PorterDuff.Mode.SRC_ATOP);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mixpanelUtils = new MixpanelUtils(getActivity().getApplicationContext());
        ConstantGGS.CURRENT_TIME = mixpanelUtils.getCurrentTime();

        productRatingCardView = (CardView) view.findViewById(R.id.product_rating_details_card_view);
        productDetailsCardView = (CardView) view.findViewById(R.id.product_details_card_view);
        productDetailsBasicInforCardView = (CardView) view.findViewById(R.id.product_details_basic_info_card_view);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        setupToolbar();

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            viewSetupForLowerSdk();
        }

    }

    private void setupToolbar() {
        toolbar.getOverflowIcon().setColorFilter(ContextCompat.getColor(getActivity(),R.color.white_ggs), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_ggs);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.service_details_ggs));
        }
    }

    private void viewSetupForLowerSdk() {

        productRatingCardView.setPreventCornerOverlap(false);
        productRatingCardView.setUseCompatPadding(true);

        productDetailsCardView.setPreventCornerOverlap(false);
        productDetailsCardView.setUseCompatPadding(true);

        productDetailsBasicInforCardView.setPreventCornerOverlap(false);
        productDetailsBasicInforCardView.setUseCompatPadding(true);

    }

    private void addScrollListner() {
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollView.canScrollVertically(DIRECTION_UP)) {
                    if (!isShadowExist) {
                        toolbar.setBackgroundColor(profileColorsGGS.getColorCodeLight());
                        ViewCompat.setElevation(toolbar, 10f);
                        isShadowExist = true;
                    }

                } else {
                    if (isShadowExist) {
                        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
                        ViewCompat.setElevation(toolbar, 0f);
                        isShadowExist = false;
                    }
                }
            }
        });
    }


    private void addBackStackChangeListner() {
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getFragmentManager() != null) {
                    int size = getFragmentManager().getBackStackEntryCount();
                    if (size == 0) {
                        setHasOptionsMenu(hasOptionsMenu);
                        //((ViewDetailsActivityGGS) getActivity()).setupToolbar(getString(R.string.product_details_ggs));

                        if (isShadowExist) {
                            toolbar.setBackgroundColor(profileColorsGGS.getColorCodeLight());
                            ViewCompat.setElevation(toolbar, 10f);
                        }

                    }
                }
            }
        });

    }

    private void changeRatingBarColor() {
       /* LayerDrawable layerDrawable = (LayerDrawable) ratingBar.getProgressDrawable();
        int fullcolor = ContextCompat.getColor(getContext(), R.color.rating_star_full_color_ggs);
        int emptyColor = ContextCompat.getColor(getContext(), R.color.rating_star_half_color_ggs);
        layerDrawable.getDrawable(2).setColorFilter(fullcolor, PorterDuff.Mode.SRC_ATOP);
        layerDrawable.getDrawable(0).setColorFilter(emptyColor, PorterDuff.Mode.SRC_ATOP);
        layerDrawable.getDrawable(1).setColorFilter(emptyColor, PorterDuff.Mode.SRC_ATOP);*/

    }

    private void getBundleData() {
        Bundle bundle = getArguments();
        itemId = bundle.getString("id");
        fromOtherProfile = bundle.getBoolean(IS_OTHER_PROFILE, false);
        if (fromOtherProfile) {
            otherProductsLayout.setVisibility(View.GONE);

        }


    }

    private void getDataFromNetwork() {
        if (NetworkUtilGGS.isConnectedToNetwork(getContext())) {
            new GetProductDetailsTask(getActivity(), itemId, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            dialogManager.showDialog(getString(R.string.failed_to_load_data_ggs), getString(R.string.text_no_internet_connection_ggs), true);

        }
    }

    @Override
    public void onCallback(GGProduct ggProduct) {
        parentView.setVisibility(View.VISIBLE);
        if (String.valueOf(ggProduct.getUserInfo().getNeighbourhoodProfileId()).equals(ServicePreference.getInstance(getContext()).getNeighbourhoodProfileId())) {
            if (fromOtherProfile) {
                hasOptionsMenu = false;
            } else {
                hasOptionsMenu = true;

            }
            setHasOptionsMenu(hasOptionsMenu);


            otherProductsLayout.setVisibility(View.GONE);
            contactWithCard.setVisibility(View.GONE);


            RelativeLayout.LayoutParams llp = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
            llp.bottomMargin = 0;
            llp.setMargins(0, 0, 0, 0);

            scrollView.setLayoutParams(llp);
        }
        this.ggProduct = ggProduct;
        if (((ViewDetailsActivityGGS) getActivity()).isEdit) {
            ((ViewDetailsActivityGGS) getActivity()).editIntent.putExtra("price", ggProduct.getPrice());
            if (ggProduct.getMedia().size() > 0) {
                ((ViewDetailsActivityGGS) getActivity()).editIntent.putExtra("image", ggProduct.getMedia().get(0).getThumb_url_medium());
            }
            ((ViewDetailsActivityGGS) getActivity()).editIntent.putExtra("id", itemId);

        }
        setValues();
    }

    private void setValues() {

        userName.setText(ggProduct.getUserInfo().getName());
        createDate.setText(UtilsGGS.getFormattedDate(ggProduct.getDateCreated()));
        ratingValue.setText(String.valueOf(ggProduct.getRatingValue()));
        ratingBar.setRating((float) ggProduct.getRatingValue());
        // ratingBar.setRating((float) 2.5);
        title.setText(ggProduct.getTitle());
        description.setText(ggProduct.getDescription());
        UtilsGGS.makeTextViewResizable(description, 3, getString(R.string.see_more_ggs), true, getActivity());
        itemTypeTv.setText(getItemTypeText(ggProduct.getProduct_type()));

        if (ggProduct.getMedia().size() > 0) {
            String url = ggProduct.getMedia().get(0).getThumb_url_medium();
            Log.d("imageurl", url);
            setImage(url, productImage, R.drawable.image_placeholder_mega_2x_ggs);
        } else {
            productImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.image_placeholder_mega_2x_ggs));
        }
        setImage(ggProduct.getUserInfo().getThumbUrlMedium(), userImage, R.drawable.avatar_placeholder_ggs);

        conditionText.setText(ggProduct.getCondition().getName());
        if (ggProduct.getBrand().getName() != null) {
            brandLayout.setVisibility(View.VISIBLE);
            brandText.setText(ggProduct.getBrand().getName());

        } else {
            brandLayout.setVisibility(View.GONE);
        }
        if (ggProduct.getModel().getName() != null) {
            modelLayout.setVisibility(View.VISIBLE);
            modelText.setText(ggProduct.getModel().getName());
        } else {
            modelLayout.setVisibility(View.GONE);
        }
        totalReviews.setText(String.valueOf(ggProduct.getTotalReviews()));
        locationText.setText(ggProduct.getAddress());
        setTagDatas(ggProduct.getTags(), tagText, tagLayout);
        setTagDatas(ggProduct.getFeatures(), featureText, featureLayout);
        if (ggProduct.getQuantity() > 0) {
            quantityLayout.setVisibility(View.VISIBLE);
            quantityText.setText(String.valueOf(ggProduct.getQuantity()));
        } else {
            quantityLayout.setVisibility(View.GONE);
        }

        Log.d("formattedprice", UtilsGGS.getFormattedPriceValue(ggProduct.getPrice()));
        if (ggProduct.getProduct_type() == GGProduct.PRODUCT_TYPE.EXCHANGE) {
            exchangeLayout.setVisibility(View.VISIBLE);
            exchangeText.setText(ggProduct.getExchange());
            rateValue.setVisibility(View.GONE);
            rateType.setVisibility(View.GONE);
            negotiableValue.setVisibility(View.GONE);
            ribbonEndLayout.setVisibility(View.GONE);
            rateLayout.setVisibility(View.GONE);
        } else if (ggProduct.getProduct_type() == GGProduct.PRODUCT_TYPE.RENTAL) {
            brandLayout.setVisibility(View.GONE);
            modelLayout.setVisibility(View.GONE);
            conditionLayout.setVisibility(View.GONE);
            quantityLayout.setVisibility(View.GONE);
            quantityLayout.setVisibility(View.GONE);
            rateType.setText(ggProduct.getCurrency());
            setRateValue(ggProduct.getPrice(), ggProduct.getPrice_unit());
            setNegotiableValue();

        } else {
            rateType.setText(ggProduct.getCurrency());
            String p = ggProduct.getPrice();
            rateValue.setText(UtilsGGS.makeCommaSeparatedPriceFormat(Double.parseDouble(ggProduct.getPrice())));
            setNegotiableValue();

        }


        getFragmentManager().beginTransaction().replace(R.id.map_container, com.gagagugu.ggservice.fragment.ItemLocationFragmentGGS.newInstance(ggProduct.getLat(), ggProduct.getLng(), "item"), "itemLocation").commit();

        contactPref = ggProduct.getContact_preference();

        itemCountDetailsTV.setText(String.valueOf(ggProduct.getPostCount().getItems()));
        itemCountDetailsTV.setText(String.valueOf(ggProduct.getPostCount().getItems()));

      /*  if (ggProduct.getPostCount().getItems()!=0){
            itemCountDetailsTV.setText(String.valueOf(ggProduct.getPostCount().getItems()));
        }*/

    }

    private void setNegotiableValue() {
        if (ggProduct.getNegotiable() == 1) {
            negotiableValue.setText(getString(R.string.negotiable_ggs));
            negotiableValue.setVisibility(View.VISIBLE);
        } else {
            negotiableValue.setVisibility(View.GONE);
        }
    }


    private void setRateValue(String price, String price_unit) {
        StringBuilder string = new StringBuilder();
        string.append(UtilsGGS.makeCommaSeparatedPriceFormat(Double.parseDouble(price)));
        string.append("/");
        string.append(UtilsGGS.getUnitValue(price_unit));
        rateValue.setText(string.toString());

    }

    private void setTagDatas(ArrayList<TagGgs> tags, TextView tagTv, LinearLayout tagLayout) {
        if (tags.size() > 0) {
            tagLayout.setVisibility(View.VISIBLE);
            tagTv.setText(getStringFromList(tags));

        } else {
            tagLayout.setVisibility(View.GONE);
        }
    }

    private String getStringFromList(ArrayList<TagGgs> tags) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            stringBuilder.append(tags.get(i).getName());
            if (i == tags.size() - 1) break;
            stringBuilder.append(", ");
        }

        return stringBuilder.toString();
    }


    private String getItemTypeText(GGProduct.PRODUCT_TYPE productTypeName) {
        switch (productTypeName) {
            case BUY:
                setContactCardView(getString(R.string.buyer_ggs));
                return getString(R.string.for_buy_ggs);
            case SELL:
                setContactCardView(getString(R.string.seller_ggs));
                return getString(R.string.for_sell_ggs);
            case EXCHANGE:
                setContactCardView(getString(R.string.exchanger_ggs));
                return getString(R.string.for_exchange_ggs);
            case RENTAL:
                setContactCardView(getString(R.string.renter_ggs));
                return getString(R.string.is_renting_ggs);
            default:
                return "";
        }
    }

    private void setContactCardView(String string) {
        contactWithTvText = String.format(getString(R.string.contact_with_text_format_ggs), string);
        contactWithTv.setText(contactWithTvText);
    }

    private void setImage(String url, ImageView imageview, int placeholder) {
        if (url != null && !url.isEmpty()) {
            Picasso.with(getContext())
                    .load(url)
                    .stableKey(url)
                    .placeholder(placeholder)
                    .fit()
                    .centerCrop()
                    .noFade()
                    .into(imageview);
        }
    }


    @Override
    public void onClick(View v) {
        if (v == contactWithCard) {
            com.gagagugu.ggservice.fragment.ContactChooseDailogFragmentGGS contactChooseDailogFragmentGGS = com.gagagugu.ggservice.fragment.ContactChooseDailogFragmentGGS.newInstance(contactWithTvText, contactPref, ggProduct.getUserInfo().getProfileId(), ggProduct.getUserInfo().getThumbUrl()
                    , ggProduct.getUserInfo().getName(), ggProduct.getUserInfo().getPhoneNumber());
            contactChooseDailogFragmentGGS.show(getFragmentManager(), contactChooseDailogFragmentGGS.getTag());
        } else if (v == customerRatingLayout) {
            setHasOptionsMenu(false);

            com.gagagugu.ggservice.fragment.CustomerRatingGGS customerRatingGGS = new com.gagagugu.ggservice.fragment.CustomerRatingGGS();
            Bundle bundle = new Bundle();
            bundle.putString("postid", itemId);
            bundle.putBoolean("isitem", true);
            bundle.putString("title", ggProduct.getTitle());
            bundle.putString("create_date", ggProduct.getDateCreated());
            bundle.putDouble("rating", ggProduct.getRatingValue());
            bundle.putLong("total_reviews", ggProduct.getTotalReviews());
            bundle.putBoolean("is_own_product", String.valueOf(ggProduct.getUserInfo().getNeighbourhoodProfileId()).equals(ServicePreference.getInstance(getContext()).getNeighbourhoodProfileId()));
            bundle.putBoolean("has_option_menu", hasOptionsMenu);
            bundle.putSerializable("my_review", ggProduct.getMyReviews());
            customerRatingGGS.setOnRatingChangeListener(this);
            customerRatingGGS.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.activity_transition_slide_in_ggs, R.anim.activity_transition_slide_out_ggs, R.anim.activity_transition_slide_back_to_screen_ggs, R.anim.activity_transition_slide_back_ggs)
                    .add(R.id.fragment_container, customerRatingGGS)
                    .addToBackStack("customer_rating")
                    .commit();
        } else if (v == userImage) {

            if (String.valueOf(ggProduct.getUserInfo().getNeighbourhoodProfileId()).equals(ServicePreference.getInstance(getContext()).getNeighbourhoodProfileId())) {
                return;
            }
            if (!fromOtherProfile) {
                Intent intent = new Intent(getActivity(), OtherProfileActivityGGS.class);
                intent.putExtra("nh_profile_id", String.valueOf(ggProduct.getUserInfo().getNeighbourhoodProfileId()));
                startActivity(intent);
            }
        } else if (v == otherProductsLayout) {
            if (!fromOtherProfile) {
                if (ggProduct.getPostCount().getItems() != 0) {
                    Intent intent = new Intent(getActivity(), ProductItemActivityGGS.class);
                    intent.putExtra(NH_PROFILE_ID, String.valueOf(ggProduct.getUserInfo().getNeighbourhoodProfileId()));
                    intent.putExtra("post_count", ggProduct.getPostCount());
                    startActivity(intent);
                }
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_edit_item) {

            goToItemEdit();
        } else if (item.getItemId() == R.id.menu_delete_item) {
            showAlertDailog();


        }

        return false;

    }

    private void goToItemEdit() {

        Intent intent = new Intent(getActivity(), CreateProductPostActivityGGS.class);
        intent.putExtra(EXTRA_EDIT_PRODUCT_MODEL, ggProduct);
        intent.putExtra(EXTRA_CONNECT_THEME_COLOR, ServicePreference.getInstance(getContext()).getThemeColor());
        intent.putExtra("type", ggProduct.getProduct_type());
        Log.d("type", ggProduct.getProduct_type().name());

        startActivityForResult(intent, EDIT_REQUEST_CODE);

    }

    private void showAlertDailog() {
        AlertDialog.Builder alertDailogBuilder = new AlertDialog.Builder(getContext());
        alertDailogBuilder.setTitle(getString(R.string.delete_post_ggs));
        alertDailogBuilder.setMessage(getString(R.string.delete_post_confirmation_message_ggs));
        alertDailogBuilder.setCancelable(false)
                .setPositiveButton(getString(R.string.Delete_ggs), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (NetworkUtilGGS.isConnectedToNetwork(getContext())) {

                            new ItemPostDeleteAsynTask(getContext(), itemId, com.gagagugu.ggservice.fragment.ViewItemDetailsFragment.this, "item").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                        } else {
                            dialogManager.showDialog(getString(R.string.failed_to_load_data_ggs), getString(R.string.text_no_internet_connection_ggs), false);

                        }


                    }
                }).setNegativeButton(getString(R.string.cancel_ggs), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do Nothing

            }
        }).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.product_details_over_flow_menu, menu);


    }

    @Override
    public void onItemDelete(Map<String, String> map) {
        if (map != null) {
            if (map.get("status").equals("success")) {
                Toast.makeText(getContext(), map.get("msg"), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("id", itemId);
                intent.putExtra("isDelete", true);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();


            } else {
                dialogManager.showFailedDailog(map.get("msg"), false);
            }
        } else {
            dialogManager.showFailedDailog(getString(R.string.unable_to_delete), false);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            itemId = data.getStringExtra("id");
            ((ViewDetailsActivityGGS) getActivity()).isEdit = true;
            getDataFromNetwork();

        }
    }

    @Override
    public void onRatingChange(float newRating, MyReviewGGS myReviewGGS) {
        //getDataFromNetwork();
        ratingBar.setRating(newRating);
        ratingValue.setText(removeTrailingZeroFromFloat(newRating));
        ggProduct.setRatingValue(newRating);
        ggProduct.setMyReviews(myReviewGGS);
    }

    private String removeTrailingZeroFromFloat(float value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));
        String val = decimalFormat.format(value);
        if (val.endsWith("0")) {
            return val.substring(0, val.length() - 1);
        }
        return val;

    }

    public void showLocationDetails() {
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.push_up_in_ggs, R.anim.push_up_out_ggs, R.anim.push_down_in_ggs, R.anim.push_down_out_ggs)
                .add(R.id.fragment_container,
                        ItemLocationDetailsFragmentGGS.newInstance(ggProduct.getLat(), ggProduct.getLng(), ggProduct.getAddress(), "item"))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
       // ((ViewDetailsActivityGGS) getActivity()).setupToolbar(getString(R.string.product_details_ggs));

    }

    @Override
    public void onStop() {
        ConstantGGS.END_TIME = mixpanelUtils.getCurrentTime();
        ConstantGGS.STAY_TIME = mixpanelUtils.getDateDiff(ConstantGGS.CURRENT_TIME, ConstantGGS.END_TIME);
        mixpanelUtils.mixPanelReport(CLASS_NAME, ConstantGGS.CURRENT_TIME, ConstantGGS.END_TIME, ConstantGGS.STAY_TIME);
        mixpanelUtils.mixpanel.flush();
        super.onStop();
    }
}
