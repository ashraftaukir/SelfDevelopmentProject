package com.gagagugu.ggservice.fragment;


import android.app.Activity;
import android.content.Context;
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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tagview.TagGgs;
import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.asynctasks.GetServiceDetailsTask;
import com.gagagugu.ggservice.asynctasks.ItemPostDeleteAsynTask;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.interfaces.CallBackFromDeleteItemPost;
import com.gagagugu.ggservice.interfaces.CallBackFromServiceDetailsTaskGGS;
import com.gagagugu.ggservice.interfaces.OnRatingChangeListener;
import com.gagagugu.ggservice.models.GGService;
import com.gagagugu.ggservice.models.MyReviewGGS;
import com.gagagugu.ggservice.utils.ConstantGGS;
import com.gagagugu.ggservice.utils.DialogManager;
import com.gagagugu.ggservice.utils.MixpanelUtils;
import com.gagagugu.ggservice.utils.NetworkUtilGGS;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;
import com.gagagugu.ggservice.view.activity.CreateServicePostActivityGGS;
import com.gagagugu.ggservice.view.activity.NavgridProfileItemActivityGGS;
import com.gagagugu.ggservice.view.activity.OtherProfileActivityGGS;
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
public class ViewServiceDetailsFragment extends Fragment implements CallBackFromServiceDetailsTaskGGS, View.OnClickListener, CallBackFromDeleteItemPost, OnRatingChangeListener {
    private static final int EDIT_REQUEST_CODE = 8761;
    private static final int DIRECTION_UP = -1;
    private String nhProfileId;
    private RelativeLayout parentView;
    public static final String EXTRA_CONNECT_THEME_COLOR = "theme_color";
    public static final String EXTRA_EDIT_SERVICE_MODEL = "service_model";
    private static final String IS_OTHER_PROFILE = "is_other_profile";
    private static final String NH_PROFILE_ID = "nh_profile_id";


    private ImageView productImage;
    private TextView userName, rateValue, negotiableValue, createDate, ratingValue, title, description, experienceTv, skillTv, tagTv, rateType, totalReviews, locationText;
    private RoundedImageViewGGS userImage;
    private RatingBar ratingBar;
    private CardView contactWithCard;
    private String contactWithTvText;
    private ArrayList<String> contactPref;
    private ScrollView scrollView;
    private DialogManager dialogManager;
    private GGService ggService;
    private ImageView ribbonEndLayout;

    LinearLayout skillLayout, tagLayout, experienceLayout, rateLayout;
    private String servideId;
    private RelativeLayout customerRatingLayout, otherProductsLayout;
    private boolean fromOtherProfile;
    private TextView itemCountDetailsTV;
    private TextView countTitleTextView;
    private Toolbar toolbar;
    private ProfileColorsGGS profileColorsGGS;
    private boolean hasOptionsMenu;
    private boolean isShadowExist;
    MixpanelUtils mixpanelUtils;
    public static final String CLASS_NAME = "ViewServiceDetailsFragment";
    private CardView productRatingCardView, serviceDetailsCardView, productDetailsBasicInforCardView;

    public ViewServiceDetailsFragment() {
        // Required empty public constructor
    }


    public static com.gagagugu.ggservice.fragment.ViewServiceDetailsFragment newInstance() {
        com.gagagugu.ggservice.fragment.ViewServiceDetailsFragment fragment = new com.gagagugu.ggservice.fragment.ViewServiceDetailsFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_service_details, container, false);
        nhProfileId = ServicePreference.getInstance(getActivity()).getNeighbourhoodProfileId();
        dialogManager = new DialogManager(getActivity());
        initView(view);
        getBundleData();
        getDataFromNetwork();
        addScrollListner();
        addBackStackChangeListner();

        return view;
    }

    private void addBackStackChangeListner() {
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getFragmentManager() != null) {
                    int size = getFragmentManager().getBackStackEntryCount();
                    if (size == 0) {
                        setHasOptionsMenu(hasOptionsMenu);
                        //((ViewDetailsActivityGGS) getActivity()).setupToolbar(getString(R.string.service_details_ggs));
                        if (isShadowExist) {
                            toolbar.setBackgroundColor(profileColorsGGS.getColorCodeLight());
                            ViewCompat.setElevation(toolbar, 10f);
                        }
                    }
                }
            }
        });

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


    private void initView(View view) {
        rateType = (TextView) view.findViewById(R.id.rate_type);
        totalReviews = (TextView) view.findViewById(R.id.total_reviews);
        parentView = (RelativeLayout) view.findViewById(R.id.parent_view);
        locationText = (TextView) view.findViewById(R.id.location_text);
        customerRatingLayout = (RelativeLayout) view.findViewById(R.id.customer_rating_layout);
        otherProductsLayout = (RelativeLayout) view.findViewById(R.id.other_product_layout);
        productImage = (ImageView) view.findViewById(R.id.product_image);
        userImage = (RoundedImageViewGGS) view.findViewById(R.id.user_image);
        userName = (TextView) view.findViewById(R.id.user_name);
        rateValue = (TextView) view.findViewById(R.id.rate_value);
        negotiableValue = (TextView) view.findViewById(R.id.negotiable_value);
        createDate = (TextView) view.findViewById(R.id.create_date);
        ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
        ratingValue = (TextView) view.findViewById(R.id.rating_value);
        title = (TextView) view.findViewById(R.id.title);
        description = (TextView) view.findViewById(R.id.description);
        experienceLayout = (LinearLayout) view.findViewById(R.id.experience_layout);
        tagLayout = (LinearLayout) view.findViewById(R.id.tag_layout);
        skillLayout = (LinearLayout) view.findViewById(R.id.skill_layout);
        experienceTv = (TextView) view.findViewById(R.id.experience_tv);
        tagTv = (TextView) view.findViewById(R.id.tags_tv);
        skillTv = (TextView) view.findViewById(R.id.skill_tv);
        contactWithCard = (CardView) view.findViewById(R.id.contact_with_cardview);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        ribbonEndLayout = (ImageView) view.findViewById(R.id.ribbon_end_layout);
        rateLayout = (LinearLayout) view.findViewById(R.id.rate_layout);
        itemCountDetailsTV = (TextView) view.findViewById(R.id.item_count_details_text_view);
        countTitleTextView = (TextView) view.findViewById(R.id.count_title_text_view);
        countTitleTextView.setText(getString(R.string.other_service_ggs));


        contactWithCard.setOnClickListener(this);
        customerRatingLayout.setOnClickListener(this);
        userImage.setOnClickListener(this);
        otherProductsLayout.setOnClickListener(this);


        //String themeColor = ServicePreference.getInstance(getContext()).getThemeColor();
        profileColorsGGS = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        rateLayout.setBackgroundColor(profileColorsGGS.getColorCodeLight());
        ribbonEndLayout.setColorFilter(profileColorsGGS.getColorCodeLight(), PorterDuff.Mode.SRC_ATOP);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        setupToolbar();

        mixpanelUtils = new MixpanelUtils(getActivity().getApplicationContext());
        ConstantGGS.CURRENT_TIME = mixpanelUtils.getCurrentTime();

        productRatingCardView = (CardView) view.findViewById(R.id.product_rating_details_card_view);
        serviceDetailsCardView = (CardView) view.findViewById(R.id.service_details_card_view);
        productDetailsBasicInforCardView = (CardView) view.findViewById(R.id.product_details_basic_info_card_view);

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

        serviceDetailsCardView.setPreventCornerOverlap(false);
        serviceDetailsCardView.setUseCompatPadding(true);

        productDetailsBasicInforCardView.setPreventCornerOverlap(false);
        productDetailsBasicInforCardView.setUseCompatPadding(true);

    }


    private void getBundleData() {

        Bundle bundle = getArguments();
        servideId = bundle.getString("id");
        fromOtherProfile = bundle.getBoolean(IS_OTHER_PROFILE, false);
        if (fromOtherProfile) {
            otherProductsLayout.setVisibility(View.GONE);
        }


    }


    private void getDataFromNetwork() {
        if (NetworkUtilGGS.isConnectedToNetwork(getContext())) {
            new GetServiceDetailsTask(servideId, getActivity(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            dialogManager.showDialog(getString(R.string.failed_to_load_data_ggs), getString(R.string.text_no_internet_connection_ggs), true);

        }
    }

    @Override
    public void onCallback(GGService ggService) {
        parentView.setVisibility(View.VISIBLE);
        setValues(ggService);
        this.ggService = ggService;
        if (String.valueOf(ggService.getUserInfo().getNeighbourhoodProfileId()).equals(ServicePreference.getInstance(getContext()).getNeighbourhoodProfileId())) {
            if (fromOtherProfile) {
                hasOptionsMenu = false;
            } else {
                hasOptionsMenu = true;

            }
            setHasOptionsMenu(hasOptionsMenu);
            contactWithCard.setVisibility(View.GONE);
            otherProductsLayout.setVisibility(View.GONE);

            RelativeLayout.LayoutParams llp = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
            llp.bottomMargin = 0;
            llp.setMargins(0, 0, 0, 0);
            if (((ViewDetailsActivityGGS) getActivity()).isEdit) {
                ((ViewDetailsActivityGGS) getActivity()).editIntent.putExtra("price", ggService.getPrice());
                if (ggService.getMedia().size() > 0) {
                    ((ViewDetailsActivityGGS) getActivity()).editIntent.putExtra("image", ggService.getMedia().get(0).getThumb_url_medium());
                }
                ((ViewDetailsActivityGGS) getActivity()).editIntent.putExtra("id", servideId);
            }
            scrollView.setLayoutParams(llp);


        }
    }


    private void setValues(GGService ggService) {
        userName.setText(ggService.getUserInfo().getName());
        createDate.setText(UtilsGGS.getFormattedDate(ggService.getDateCreated()));
        rateType.setText(ggService.getCurrency());
        totalReviews.setText(String.valueOf(ggService.getTotalReviews()));

        setRateValue(ggService.getPrice(), ggService.getPrice_unit());
        if (ggService.getNegotiable() == 1) {
            negotiableValue.setText(getString(R.string.negotiable_ggs));
            negotiableValue.setVisibility(View.VISIBLE);
        } else {
            negotiableValue.setVisibility(View.GONE);
        }
        ratingValue.setText(String.valueOf(ggService.getRatingValue()));
        ratingBar.setRating((float) ggService.getRatingValue());


        title.setText(ggService.getTitle());
        description.setText(ggService.getDescription());
        Log.d("descritpiyo", "Outside: " + description.getLineCount() + "," + ggService.getDescription());
        UtilsGGS.makeTextViewResizable(description, 3, getString(R.string.see_more_ggs), true, getActivity());

        Log.d("ServiceDetails", "phonenumber: " + ggService.getUserInfo().getPhoneNumber());
        if (ggService.getMedia().size() > 0) {
            setImage(ggService.getMedia().get(0).getThumb_url_medium(), productImage, R.drawable.image_placeholder_mega_2x_ggs);
        } else {
            productImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.image_placeholder_mega_2x_ggs));
        }
        setImage(ggService.getUserInfo().getThumbUrlMedium(), userImage, R.drawable.avatar_placeholder_ggs);
        if (!ggService.getExperience().isEmpty()) {
            experienceLayout.setVisibility(View.VISIBLE);
            experienceTv.setText(ggService.getExperience());

        } else {
            experienceLayout.setVisibility(View.GONE);
        }
        Log.d("formattedprice", UtilsGGS.getFormattedPriceValue(ggService.getPrice()));


        setTagDatas(ggService.getTags(), tagTv, tagLayout);
        setTagDatas(ggService.getSkills(), skillTv, skillLayout);

        locationText.setText(ggService.getAddress());
        itemCountDetailsTV.setText(String.valueOf(ggService.getPostCount().getServices()));
        contactPref = ggService.getContact_preference();
     /*   if (ggService.getPostCount().getServices()!=0){
            itemCountDetailsTV.setText(String.valueOf(ggService.getPostCount().getServices()));
        }*/

        getFragmentManager().beginTransaction().replace(R.id.map_container, com.gagagugu.ggservice.fragment.ItemLocationFragmentGGS.newInstance(ggService.getLat(), ggService.getLng(), "service")).commit();
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


    private void setRateValue(String price, String price_unit) {
        StringBuilder string = new StringBuilder();
        string.append(UtilsGGS.makeCommaSeparatedPriceFormat(Double.parseDouble(price)));
        string.append("/");
        string.append(UtilsGGS.getUnitValue(price_unit));
        rateValue.setText(string.toString());

    }

    private void setImage(String url, ImageView imageview, int placeholder) {
        if (url != null && !url.isEmpty()) {
            Picasso.with(getContext()).load(url)
                    .stableKey(url)
                    .placeholder(placeholder)
                    .noFade()
                    .fit()
                    .centerCrop()
                    .into(imageview);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == contactWithCard) {
            com.gagagugu.ggservice.fragment.ContactChooseDailogFragmentGGS contactChooseDailogFragmentGGS = com.gagagugu.ggservice.fragment.ContactChooseDailogFragmentGGS.newInstance(getString(R.string.contact_with_service_provider_ggs), contactPref, ggService.getUserInfo().getProfileId(), ggService.getUserInfo().getThumbUrl()
                    , ggService.getUserInfo().getName(), ggService.getUserInfo().getPhoneNumber());
            contactChooseDailogFragmentGGS.show(getFragmentManager(), contactChooseDailogFragmentGGS.getTag());
        } else if (v == customerRatingLayout) {
            setHasOptionsMenu(false);

            com.gagagugu.ggservice.fragment.CustomerRatingGGS customerRatingGGS = new com.gagagugu.ggservice.fragment.CustomerRatingGGS();
            Bundle bundle = new Bundle();
            bundle.putString("postid", servideId);
            bundle.putBoolean("isitem", false);
            bundle.putString("title", ggService.getTitle());
            bundle.putString("create_date", ggService.getDateCreated());
            bundle.putLong("total_reviews", ggService.getTotalReviews());
            bundle.putDouble("rating", ggService.getRatingValue());
            bundle.putBoolean("is_own_product", String.valueOf(ggService.getUserInfo().getNeighbourhoodProfileId()).equals(ServicePreference.getInstance(getContext()).getNeighbourhoodProfileId()));
            bundle.putSerializable("my_review", ggService.getMyReviews());
            bundle.putBoolean("has_option_menu", hasOptionsMenu);
            customerRatingGGS.setOnRatingChangeListener(this);
            customerRatingGGS.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.activity_transition_slide_in_ggs, R.anim.activity_transition_slide_out_ggs, R.anim.activity_transition_slide_back_to_screen_ggs, R.anim.activity_transition_slide_back_ggs)
                    .add(R.id.fragment_container, customerRatingGGS)
                    .addToBackStack("customer_rating")
                    .commit();
        } else if (v == userImage) {
            if (String.valueOf(ggService.getUserInfo().getNeighbourhoodProfileId()).equals(ServicePreference.getInstance(getContext()).getNeighbourhoodProfileId())) {
                return;
            }
            if (!fromOtherProfile) {
                Intent intent = new Intent(getActivity(), OtherProfileActivityGGS.class);
                intent.putExtra(NH_PROFILE_ID, String.valueOf(ggService.getUserInfo().getNeighbourhoodProfileId()));
                startActivity(intent);
            }
        } else if (v == otherProductsLayout) {
            if (!fromOtherProfile) {
                if (ggService.getPostCount().getServices() != 0) {
                    Intent intent = new Intent(getActivity(), NavgridProfileItemActivityGGS.class);
                    intent.putExtra(NH_PROFILE_ID, String.valueOf(ggService.getUserInfo().getNeighbourhoodProfileId()));
                    intent.putExtra("type", "service");
                    startActivity(intent);
                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_edit_item) {
            goToEditPart();
            //Toast.makeText(getContext(), "Edit Coming Soon", Toast.LENGTH_SHORT).show();

        } else if (item.getItemId() == R.id.menu_delete_item) {
            showDeleteDailog();
            //Toast.makeText(getContext(), "Delete Coming Soon", Toast.LENGTH_SHORT).show();

        }

        return false;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("aattachagain", "onAttach: ");
    }

    private void showDeleteDailog() {
        AlertDialog.Builder alertDailogBuilder = new AlertDialog.Builder(getContext());
        alertDailogBuilder.setTitle(getString(R.string.delete_post_ggs));
        alertDailogBuilder.setMessage(getString(R.string.delete_post_confirmation_message_ggs));
        alertDailogBuilder.setCancelable(false)
                .setPositiveButton(getString(R.string.Delete_ggs), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (NetworkUtilGGS.isConnectedToNetwork(getContext())) {

                            new ItemPostDeleteAsynTask(getContext(), servideId, com.gagagugu.ggservice.fragment.ViewServiceDetailsFragment.this, "service").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                        } else {
                            dialogManager.showDialog(getString(R.string.failed_to_load_data_ggs), getString(R.string.text_no_internet_connection_ggs), false);

                        }
                    }
                }).setNegativeButton(getString(R.string.cancel_ggs), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }


    private void goToEditPart() {
        Intent intent = new Intent(getActivity(), CreateServicePostActivityGGS.class);
        intent.putExtra(EXTRA_EDIT_SERVICE_MODEL, ggService);
        intent.putExtra(EXTRA_CONNECT_THEME_COLOR, ServicePreference.getInstance(getContext()).getThemeColor());
        startActivityForResult(intent, EDIT_REQUEST_CODE);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.product_details_over_flow_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);


    }

    @Override
    public void onItemDelete(Map<String, String> map) {
        if (map != null) {
            if (map.get("status").equals("success")) {
                Toast.makeText(getContext(), map.get("msg"), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("id", servideId);
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
            servideId = data.getStringExtra("id");
            ((ViewDetailsActivityGGS) getActivity()).isEdit = true;
            getDataFromNetwork();

        }
    }

    @Override
    public void onRatingChange(float newRating, MyReviewGGS myReviewGGS) {
        //getDataFromNetwork();
        ratingBar.setRating(newRating);
        ratingValue.setText(removeTrailingZeroFromFloat(newRating));
        ggService.setRatingValue(newRating);
        ggService.setMyReviews(myReviewGGS);


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
        setHasOptionsMenu(false);
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.push_up_in_ggs, R.anim.push_up_out_ggs, R.anim.push_down_in_ggs, R.anim.push_down_out_ggs)
                .add(R.id.fragment_container,
                        com.gagagugu.ggservice.fragment.ItemLocationDetailsFragmentGGS.newInstance(ggService.getLat(), ggService.getLng(), ggService.getAddress(), "service"))
                .addToBackStack("location")
                .commit();
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
