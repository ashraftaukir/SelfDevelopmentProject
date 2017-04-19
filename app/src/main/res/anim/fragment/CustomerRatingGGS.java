package com.gagagugu.ggservice.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.adapter.PostReviewAdapter;
import com.gagagugu.ggservice.asynctasks.GetReviewsTask;
import com.gagagugu.ggservice.asynctasks.PostReviewTask;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.interfaces.CallbackForReviewFetch;
import com.gagagugu.ggservice.interfaces.OnPostReviewTaskCallback;
import com.gagagugu.ggservice.interfaces.OnRatingChangeListener;
import com.gagagugu.ggservice.models.MyReviewGGS;
import com.gagagugu.ggservice.models.NeighbourHoodProfileGGS;
import com.gagagugu.ggservice.models.PostReviewResponse;
import com.gagagugu.ggservice.models.ReviewGGS;
import com.gagagugu.ggservice.models.ReviewResponse;
import com.gagagugu.ggservice.utils.NetworkUtilGGS;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerRatingGGS extends Fragment implements CallbackForReviewFetch, View.OnClickListener, OnPostReviewTaskCallback, RatingBar.OnRatingBarChangeListener {

    private LinearLayout rootLayout, rateLayout;
    private int page = 1;
    private String perPage = "20";
    private String TAG = "CUSTOMER_RATING";
    private RecyclerView recyclerView;
    private ArrayList<ReviewGGS> reviews;
    private PostReviewAdapter postReviewAdapter;
    private String title, createDate;
    private double avgRatingValue;
    private ImageView userImage, imgBack;

    private MyReviewGGS myReviewGGS;
    private CardView reviewListCard;
    private String postid;
    private TextView titleTv, createDateTv, ratingTv, ratingMessageText, toolbarTitle;
    private AppCompatRatingBar ratingBar, userRatingBar;
    private String nhProfileId;
    private boolean ratingPosted = false;
    private OnRatingChangeListener onRatingChangeListener;
    private boolean isItem;
    private LinearLayoutManager linearLayoutManager;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean moreItem, loading;
    private boolean fromPagination;
    private long totalReviews;
    private boolean ownProduct;
    private View userRatingLayout;
    private View emptyRatingView;
    private float newAvgRating;
    private boolean hasOptionsMenu;
    private Toolbar toolbar;

    public CustomerRatingGGS() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_rating_ggs, container, false);
        nhProfileId = ServicePreference.getInstance(getActivity()).getNeighbourhoodProfileId();
        reviews = new ArrayList<>();

      //  ((ViewDetailsActivityGGS) getActivity()).setupToolbar(getString(R.string.customer_rating_ggs));

        initViews(view);
        initListeners();
        setThemeColor();

        return view;
    }



    private void getBundles() {
        Bundle bundle = getArguments();

        title = bundle.getString("title");
        createDate = bundle.getString("create_date");
        avgRatingValue = bundle.getDouble("rating");
        totalReviews = bundle.getLong("total_reviews");
        postid = bundle.getString("postid");
        isItem = bundle.getBoolean("isitem");
        hasOptionsMenu = bundle.getBoolean("has_option_menu");


        myReviewGGS = (MyReviewGGS) bundle.getSerializable("my_review");
        ownProduct = bundle.getBoolean("is_own_product");
        if (ownProduct) {
            userRatingLayout.setVisibility(View.GONE);
        }
        if (myReviewGGS == null) {

        } else {
            userRatingBar.setRating(myReviewGGS.getRating());
            userRatingBar.setIsIndicator(true);
            ratingMessageText.setText(getString(R.string.already_rated_ggs));
        }

        Picasso.with(getContext()).load(ServicePreference.getInstance(getContext()).getServiceProfileImageUrlThumb()).fit().centerCrop().into(userImage);


        if (bundle.getString("user_image") != null && !bundle.getString("user_image").isEmpty()) {
            Picasso.with(getContext()).load(bundle.getString("user_image")).into(userImage);
        }
        setValues();
        callRateApi();

        

    }

    private void callRateApi() {
        loading = true;
        new GetReviewsTask(getActivity(),postid, String.valueOf(page), perPage, this, isItem).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setValues() {
        titleTv.setText(title);
        createDateTv.setText(UtilsGGS.getFormattedDate(createDate));
        if(avgRatingValue==0.0) {
            ratingTv.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
        }else {
            ratingTv.setText(removeTrailingZeroFromFloat((float) avgRatingValue));
            ratingBar.setRating((float) avgRatingValue);
        }
    }

    private void initViews(View view) {
        rootLayout = (LinearLayout) view.findViewById(R.id.root_layout);
        titleTv = (TextView) view.findViewById(R.id.title);
        createDateTv = (TextView) view.findViewById(R.id.create_date);
        ratingBar = (AppCompatRatingBar) view.findViewById(R.id.rating_bar);
        userRatingBar = (AppCompatRatingBar) view.findViewById(R.id.rating_bar_top);
        ratingTv = (TextView) view.findViewById(R.id.rating_value);
        ratingMessageText = (TextView) view.findViewById(R.id.rating_review_text);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        userImage = (ImageView) view.findViewById(R.id.user_image);
        postReviewAdapter = new PostReviewAdapter(reviews, getContext());
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(postReviewAdapter);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        imgBack = (ImageView) view.findViewById(R.id.imgBack);
        rateLayout = (LinearLayout) view.findViewById(R.id.rate_layout);
        userRatingLayout = view.findViewById(R.id.user_rating_layout);
        emptyRatingView = view.findViewById(R.id.rating_emptyview);
        reviewListCard = (CardView) view.findViewById(R.id.review_list_card);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        setupToolbar();


    }

    @Override
    public Animation onCreateAnimation(int transit, final boolean enter, int nextAnim) {

        Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);

        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Log.d(TAG, "Animation started.");
                // additional functionality
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d(TAG, "Animation repeating.");
                // additional functionality
            }

            @Override
            public void onAnimationEnd(Animation animation) {
               if (enter){
                   getBundles();

               }
            }
        });

        return anim;
    }

    private void setupToolbar() {
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_ggs);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.customer_rating_ggs));
        }
    }
    private void setThemeColor() {
        String themeColor = ServicePreference.getInstance(getContext()).getThemeColor();
        ProfileColorsGGS profileColorsGGS = UtilsGGS.getProfileColor(themeColor);
        rootLayout.setBackgroundResource(profileColorsGGS.getBackground());

    }

    @Override
    public void onCallback(ReviewResponse reviewResponse) {
        if (reviewResponse != null) {
            //Log.d(TAG, "onCallback: message " + reviewResponse.getMessage());
//            Log.d(TAG, "onCallback: size " + reviewResponse.getReviews().size());


            if (fromPagination) {
                int previous_size = reviews.size();
                if(reviewResponse.getReviews()!=null) {
                    reviews.addAll(reviewResponse.getReviews());
                }
                postReviewAdapter.notifyItemRangeInserted(previous_size, reviews.size());
                fromPagination = false;
            } else {
                reviews.clear();
                if (reviewResponse.getReviews() != null) {
                    reviews.addAll(reviewResponse.getReviews());
                }
                postReviewAdapter.notifyDataSetChanged();

                if (reviews.size() == 0) {
                    emptyRatingView.setVisibility(View.VISIBLE);
                }
            }


            rateLayout.setVisibility(View.VISIBLE);
            moreItem = !(reviews.size() == reviewResponse.getTotalCount());

        } else {
            moreItem = false;
        }
        loading = false;
        if (moreItem) page++;
    }

    private void initListeners() {
        rootLayout.setOnClickListener(this);
        userRatingBar.setOnRatingBarChangeListener(this);
        imgBack.setOnClickListener(this);
        addRecyclerViewListener();
    }

    private void addRecyclerViewListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {

                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();



                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && !loading && moreItem) {

                        if (NetworkUtilGGS.isInternetAvailable(getContext())) {
                            fromPagination = true;

                            callRateApi();
                        }


                    } else {
                        //Toast.makeText(SearchFilterActivityGGS.this, "No more item", Toast.LENGTH_SHORT).show();
                    }


                }


            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == rootLayout) {
            //
        } else if (v == imgBack) {
            getActivity().onBackPressed();
        }
    }


    @Override
    public void onCallback(PostReviewResponse postReviewResponse) {
        if (postReviewResponse != null) {
            // Toast.makeText(getContext(), postReviewResponse.getMessage(), Toast.LENGTH_SHORT).show();
            if (postReviewResponse.isSuccess()) {
                userRatingBar.setIsIndicator(true);
                ratingPosted = true;
                calculateNewRating();

                addDataToAdapter();
            }
            ratingMessageText.setText(getString(R.string.thanks_for_rating_ggs));

        }
    }

    private void addDataToAdapter() {
        ReviewGGS reviewGGS = new ReviewGGS();
        reviewGGS.setRating(userRatingBar.getRating());
        NeighbourHoodProfileGGS nh = new NeighbourHoodProfileGGS();
        nh.setName(ServicePreference.getInstance(getContext()).getServiceProfileDisplayName());
        nh.setThumbUrl(ServicePreference.getInstance(getContext()).getServiceProfileImageUrlThumb());
        reviewGGS.setNeighbourHoodProfileGGS(nh);

        reviewGGS.setPostedAtTime(getCurrentDate());


        reviews.add(reviewGGS);
        postReviewAdapter.notifyItemInserted(reviews.size() - 1);

        if (postReviewAdapter.getItemCount() == 1) {
            emptyRatingView.setVisibility(View.GONE);
        }
    }

    private String getCurrentDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateobj = new Date();
        return df.format(dateobj);
    }

    private void calculateNewRating() {
        float newRating = userRatingBar.getRating();
        double previousTotalRating = avgRatingValue * totalReviews;
        newAvgRating = (float) ((newRating + previousTotalRating) / (++totalReviews));
        ratingBar.setVisibility(View.VISIBLE);
        ratingTv.setVisibility(View.VISIBLE);
        ratingBar.setRating(newAvgRating);

        /*DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.format
        ratingTv.setText(String.valueOf(decimalFormat.format(currentAvgRating)));*/
        ratingTv.setText(removeTrailingZeroFromFloat(newAvgRating));
    }

    private String removeTrailingZeroFromFloat(float value) {
        //String val = String.format("%.2f", value);
        DecimalFormat decimalFormat = new DecimalFormat("#.##",new DecimalFormatSymbols(Locale.ENGLISH));
        String val = decimalFormat.format(value);
        if (val.endsWith("0")) {
            return val.substring(0, val.length() - 1);
        }
        return val;

    }


    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        Log.d(TAG, "onRatingChanged: " + fromUser + "," + rating);
        if (fromUser) {
            new PostReviewTask(postid, String.valueOf(Math.round(rating)), getActivity(), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: " + hasOptionsMenu);
        if (ratingPosted) {
            MyReviewGGS myReviewGGS = new MyReviewGGS();
            myReviewGGS.setRating((long) userRatingBar.getRating());
            myReviewGGS.setPostedAt(getCurrentDate());
            if(onRatingChangeListener!=null) {
                onRatingChangeListener.onRatingChange(newAvgRating, myReviewGGS);

                onRatingChangeListener = null;
            }


        }

        /*if (isItem) {
            ((ViewDetailsActivityGGS) getActivity()).setupToolbar(getString(R.string.product_details_ggs));

        } else {
            ((ViewDetailsActivityGGS) getActivity()).setupToolbar(getString(R.string.service_details_ggs));

        }*/
//        setHasOptionsMenu(hasOptionsMenu);
    }

    public void setOnRatingChangeListener(OnRatingChangeListener onRatingChangeListener) {
        this.onRatingChangeListener = onRatingChangeListener;

    }
}
