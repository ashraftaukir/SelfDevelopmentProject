package com.gagagugu.ggservice.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tagview.TagGgs;
import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.asynctasks.CreatePostItemTask;
import com.gagagugu.ggservice.asynctasks.GetModelAsycnTask;
import com.gagagugu.ggservice.asynctasks.UpdateProductAsynTask;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.core.SuggestionApi;
import com.gagagugu.ggservice.interfaces.GetModelDataCallBackGGS;
import com.gagagugu.ggservice.interfaces.PostCreationCallbackListener;
import com.gagagugu.ggservice.models.ConstantsGGS;
import com.gagagugu.ggservice.models.GGProduct;
import com.gagagugu.ggservice.models.SuggestionDataGGS;
import com.gagagugu.ggservice.utils.DialogManager;
import com.gagagugu.ggservice.utils.NetworkUtilGGS;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;
import com.gagagugu.ggservice.view.customviews.ThemeSwitchGGS;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by taukir on 2/6/17.
 */

public class CreateProductOptionalGGS extends Fragment implements View.OnClickListener, PostCreationCallbackListener, CompoundButton.OnCheckedChangeListener, GetModelDataCallBackGGS {
    public static final int TAG_REQUEST_CODE = 1000;
    public static final int FEATURE_REQUEST_CODE = 2000;

    LinearLayout conditionLayout, brand_layout,
            model_layout, features_layout, tag_layout;
    // ConditionFragmentGGS condition;
    BottomSheetDialogFragment condition;


    private final static int BRAND_SELECT_REQUEST_CODE = 9882;
    private final static int MODEL_SELECT_REQUEST_CODE = 8923;
    public static final int CONDITION_REQUEST_CODE = 2001;
    public final static int QUANTITY_REQUEST_CODE = 2048;

    Drawable themePlusIcon;
    TextView tagTv, featureTv;
    //  Context context;
    Context context;
    TextView postButton;
    DialogManager dialogManager;
    private SuggestionApi suggestionApi;

    int quantityVal;
    private ProfileColorsGGS theme;
    private ArrayList<SuggestionDataGGS> conditionlist;
    private ThemeSwitchGGS audioCallSwitch, videoCallSwitch, cellularCallSwitch, chatSwitch;
    private TextView quantityText, seledtedBrandTextView, selectedModelTextView, tv_condition_value;
    //   private ImageView quantityMinus, quantityPlus;
    private ArrayList<TagGgs> selectedTagList, selectedFeaturesList;
    private LinearLayout quantity_layout;
    private String errorMessage;
    private GGProduct.PRODUCT_TYPE productType;
    private FrameLayout appBarLayout;


    public CreateProductOptionalGGS() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        suggestionApi = new SuggestionApi();
        theme = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        postButton = (TextView) getActivity().findViewById(R.id.post_upper);
        dialogManager = new DialogManager(getActivity());

        setPostButtonText();
    }


    private void setPostButtonText() {
        if (UtilsGGS.getGgProductEditFlag(getContext())) {
            postButton.setText(getResources().getString(R.string.update_ggs));
        } else {
            postButton.setText(context.getResources().getString(R.string.post_service_text_ggs));
        }
        validateAndChangePostbutton();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_product_optional_ggs, container, false);
        productType = UtilsGGS.getGgProduct(getContext()).getProduct_type();

        initializeView(view);
        initializeLists();
        initListeners();
        setupViewWithPreviousData();
        setquantity();
        setupModelForEdit();
        validateAndChangePostbutton();
        return view;
    }

    private void setupModelForEdit() {
        if (UtilsGGS.getGgProductEditFlag(getContext()) && productType != GGProduct.PRODUCT_TYPE.RENTAL && UtilsGGS.getGgProduct(getContext()).getBrand().getId()!=-1) {
            if (NetworkUtilGGS.isInternetAvailable(getActivity())) {
                new GetModelAsycnTask(this, UtilsGGS.getGgProduct(getContext()).getBrand().getId(), getActivity()).execute();
            } else {
                dialogManager.showDialog(getString(R.string.failed_to_load_data_ggs), getString(R.string.text_no_internet_connection_ggs), false);

            }

        }
    }

    private void initializeView(View view) {

        conditionLayout = (LinearLayout) view.findViewById(R.id.conditionLayout);
        brand_layout = (LinearLayout) view.findViewById(R.id.brand_layout);
        model_layout = (LinearLayout) view.findViewById(R.id.model_layout);
        features_layout = (LinearLayout) view.findViewById(R.id.features_layout);
        tag_layout = (LinearLayout) view.findViewById(R.id.tag_layout);
        seledtedBrandTextView = (TextView) view.findViewById(R.id.selected_brand_text_view);


        tagTv = (TextView) view.findViewById(R.id.tag_text);
        featureTv = (TextView) view.findViewById(R.id.feature_text);
        tv_condition_value = (TextView) view.findViewById(R.id.tv_condition_value);
        audioCallSwitch = (ThemeSwitchGGS) view.findViewById(R.id.audio_call_swicth);
        videoCallSwitch = (ThemeSwitchGGS) view.findViewById(R.id.video_call_switch);
        cellularCallSwitch = (ThemeSwitchGGS) view.findViewById(R.id.cellular_call_switch);
        chatSwitch = (ThemeSwitchGGS) view.findViewById(R.id.chat_switch);
        quantity_layout = (LinearLayout) view.findViewById(R.id.quantity_layout);

        //  quantityMinus = (ImageView) view.findViewById(R.id.quantity_minus);
        //  quantityPlus = (ImageView) view.findViewById(R.id.quantity_plus);
        quantityText = (TextView) view.findViewById(R.id.quantity_value);
        selectedModelTextView = (TextView) view.findViewById(R.id.selectd_model_text_view);

        if (productType == GGProduct.PRODUCT_TYPE.RENTAL) {
            conditionLayout.setVisibility(View.GONE);
            brand_layout.setVisibility(View.GONE);
            model_layout.setVisibility(View.GONE);
            quantity_layout.setVisibility(View.GONE);

        } else {

            if (UtilsGGS.getGgProduct(getActivity()).getBrand().getId() != -1) {
                seledtedBrandTextView.setText(UtilsGGS.getGgProduct(getContext()).getBrand().getName());


            }

            if (UtilsGGS.getGgProduct(getActivity()).getModel().getId() != -1) {
                selectedModelTextView.setText(UtilsGGS.getGgProduct(getContext()).getModel().getName());
            }

            if (UtilsGGS.getGgProduct(getActivity()).getCondition().getId() != -1) {
                tv_condition_value.setText(UtilsGGS.getGgProduct(getContext()).getCondition().getName());
            }

            if (seledtedBrandTextView.getText().toString().trim().length() == 0) {

                model_layout.setEnabled(false);
                model_layout.setAlpha(.5f);

            }
        }

        appBarLayout = (FrameLayout) getActivity().findViewById(R.id.app_bar_layout_frame_layout);
        appBarLayout.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        ViewCompat.setElevation(appBarLayout, 0f);

    }


    private void initListeners() {
        conditionLayout.setOnClickListener(this);
        features_layout.setOnClickListener(this);
        tag_layout.setOnClickListener(this);
        postButton.setOnClickListener(this);
        brand_layout.setOnClickListener(this);

        audioCallSwitch.setOnCheckedChangeListener(this);
        videoCallSwitch.setOnCheckedChangeListener(this);
        cellularCallSwitch.setOnCheckedChangeListener(this);
        chatSwitch.setOnCheckedChangeListener(this);

        //  quantityMinus.setOnClickListener(this);
        //quantityPlus.setOnClickListener(this);
        quantity_layout.setOnClickListener(this);
        model_layout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int viewId = v.getId();


        if (viewId == R.id.quantity_layout) {
            quantityBottomSheet();
        }

        if (viewId == R.id.conditionLayout) {

            conditionbottomsheet();

        } else if (viewId == R.id.features_layout) {
            showFeatureDialog();
        } else if (viewId == R.id.tag_layout) {
            showTagDialog();
        } else if (viewId == R.id.post_upper) {

            // if (UtilsGGS.getGgProduct(context).getTester() == 1) {
            if (isValidated()) {
                if (UtilsGGS.getGgProductEditFlag(getContext())) {
                    callItemUpdateService();
                } else {
                    callPostService();

                }

            } else {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
            /*} else {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }*/
        } else if (v == brand_layout) {

            showBrandBottomSheet();


        } else if (v == model_layout) {
            if (UtilsGGS.getProductParentActivity(getContext()).models != null) {
                showModelFragment();

            }
        }

    }

    private void callItemUpdateService() {
        if (NetworkUtilGGS.isInternetAvailable(getContext())) {
            Log.d("ggproducts", new Gson().toJson(UtilsGGS.getGgProduct(getActivity())));
            new UpdateProductAsynTask(getActivity(), UtilsGGS.getGgProduct(getContext()), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            dialogManager.showDialog(context.getString(R.string.failed_to_load_data_ggs), context.getString(R.string.text_no_internet_connection_ggs), false);

        }
    }

    private void callPostService() {
        String profileId = ServicePreference.getInstance(context).getConnectProfileId();  // no need, can be removed
        String neighborHoodprofileId = ServicePreference.getInstance(context).getNeighbourhoodProfileId();
        if (NetworkUtilGGS.isInternetAvailable(getContext())) {
            Log.d("ggproducts", new Gson().toJson(UtilsGGS.getGgProduct(getActivity())));
            new CreatePostItemTask(getActivity(), UtilsGGS.getGgProduct(getContext()), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            dialogManager.showDialog(context.getString(R.string.failed_to_load_data_ggs), context.getString(R.string.text_no_internet_connection_ggs), false);

        }
    }

    private void quantityBottomSheet() {


        BottomSheetDialogFragment quantityfragment = CreateProductQuantityFragmentGGS.newInstance();
        quantityfragment.setTargetFragment(this, QUANTITY_REQUEST_CODE);
        quantityfragment.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), this.getClass().getName());

    }

    private void showModelFragment() {
        CreateProductModelFragment createProductModelFragment = CreateProductModelFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putSerializable("model_list", UtilsGGS.getProductParentActivity(getContext()).models);
        createProductModelFragment.setArguments(bundle);
        createProductModelFragment.setTargetFragment(this, MODEL_SELECT_REQUEST_CODE);
        createProductModelFragment.show(getFragmentManager(), createProductModelFragment.getTag());

    }


    private void showBrandBottomSheet() {
        CreateProductBrandFragmentGGS createProductBrandFragmentGGS = CreateProductBrandFragmentGGS.newInstance();
        createProductBrandFragmentGGS.setTargetFragment(this, BRAND_SELECT_REQUEST_CODE);
        createProductBrandFragmentGGS.show(getFragmentManager(), "brand_fragment");
    }

    private void conditionbottomsheet() {

        ConditionFragmentGGS conditionFragmentGGS = ConditionFragmentGGS.newInstance();

        Bundle bundle = new Bundle();
        bundle.putSerializable("condition_list", UtilsGGS.getProductParentActivity(getContext()).conditions);
        conditionFragmentGGS.setTargetFragment(this, CONDITION_REQUEST_CODE);
        if (conditionFragmentGGS.getArguments() != null) {
            conditionFragmentGGS.getArguments().putBundle("bundle", bundle);

        } else {
            conditionFragmentGGS.setArguments(bundle);
        }

        //  conditionlist = new ArrayList<>(UtilsGGS.getProductParentActivity(getContext()).conditions);

        conditionFragmentGGS.show(getFragmentManager(), conditionFragmentGGS.getTag());

    }


    private void showFeatureDialog() {
        CustomBottomSheetGgs featureFragment = new CustomBottomSheetGgs();


        Bundle featureViewBundle = new Bundle();
        featureViewBundle.putSerializable("tag_list", UtilsGGS.getProductParentActivity(context).features);
        // ArrayList<TagGgs> selectedSkills = new ArrayList<>(UtilsGGS.getGgService(context).getSkills());
        selectedFeaturesList = new ArrayList<>(UtilsGGS.getGgProduct(context).getFeatures());
        featureViewBundle.putSerializable("selected_tag_list", selectedFeaturesList);
        featureViewBundle.putString("tag_type", "feature");
        featureFragment.setTargetFragment(this, FEATURE_REQUEST_CODE);
        if (featureFragment.getArguments() != null) {
            featureFragment.getArguments().putBundle("bundle", featureViewBundle);

        } else {
            featureFragment.setArguments(featureViewBundle);
        }
        featureFragment.show(getFragmentManager(), "feature_fragment");


    }

    private void showTagDialog() {
        CustomBottomSheetGgs tagFragment = new CustomBottomSheetGgs();
        Bundle tagViewBundle = new Bundle();
        tagViewBundle.putSerializable("tag_list", UtilsGGS.getProductParentActivity(getContext()).tags);
        selectedTagList = new ArrayList<>(UtilsGGS.getGgProduct(context).getTags());
        tagViewBundle.putSerializable("selected_tag_list", selectedTagList);
        tagViewBundle.putString("tag_type", "tag");
        tagFragment.setTargetFragment(this, TAG_REQUEST_CODE);
        if (tagFragment.getArguments() != null) {
            tagFragment.getArguments().putBundle("bundle", tagViewBundle);
        } else {
            tagFragment.setArguments(tagViewBundle);
        }

        tagFragment.show(getFragmentManager(), "target_fragment");


    }

    private void initializeLists() {

        selectedTagList = new ArrayList<>(UtilsGGS.getGgProduct(context).getTags());
        setTagsTextInTv(selectedTagList, tagTv);

        selectedFeaturesList = new ArrayList<>(UtilsGGS.getGgProduct(context).getFeatures());
        setTagsTextInTv(selectedFeaturesList, featureTv);
    }

    private void setupTheme() {
        ProfileColorsGGS theme = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        audioCallSwitch.changeTheme(theme.getColorCodeLight());
        videoCallSwitch.changeTheme(theme.getColorCodeLight());
        chatSwitch.changeTheme(theme.getColorCodeLight());
        cellularCallSwitch.changeTheme(theme.getColorCodeLight());
    }

    @Override
    public void onResume() {
        super.onResume();

        setupTheme();
    }

    private void setupViewWithPreviousData() {

        audioCallSwitch.setChecked(UtilsGGS.getGgProduct(context).getContact_preference().contains(ConstantsGGS.AUDIO_CALL));
        videoCallSwitch.setChecked(UtilsGGS.getGgProduct(context).getContact_preference().contains(ConstantsGGS.VIDEO_CALL));
        cellularCallSwitch.setChecked(UtilsGGS.getGgProduct(context).getContact_preference().contains(ConstantsGGS.CELLULAR_CALL));
    }


    private void setTagsTextInTv(ArrayList<TagGgs> selectedList, TextView textView) {
        StringBuilder tagStringBuilder = new StringBuilder();
        for (TagGgs tagGgs : selectedList) {
            tagStringBuilder.append(tagGgs.getName());

            if (selectedList.indexOf(tagGgs) != selectedList.size() - 1) {
                tagStringBuilder.append(",");
            }
        }

        textView.setText(tagStringBuilder.toString());
    }

    public boolean isValidated() {
        // boolean isValidated = false;
        if (productType == GGProduct.PRODUCT_TYPE.RENTAL) {
            return true;
        }

        int modelId, brandId, conditionId;
        modelId = UtilsGGS.getGgProduct(context).getModel().getId();
        brandId = UtilsGGS.getGgProduct(context).getBrand().getId();
        conditionId = UtilsGGS.getGgProduct(context).getCondition().getId();

        if (conditionId == -1) {
            errorMessage = getString(R.string.condition_is_required_ggs);
        }/*else if (brandId==-1){
            errorMessage=getString(R.string.brand_is_required_ggs);
        }else if (modelId==-1){
            errorMessage=getString(R.string.model_is_required_ggs);
        }*/
        //return (modelId != -1) && (brandId != -1) && (conditionId != -1);
        return (conditionId != -1);


    }

    public void validateAndChangePostbutton() {
        if (isValidated()) {
            postButton.setTextColor(ContextCompat.getColor(context, R.color.white_ggs));
        } else {
            postButton.setTextColor(ContextCompat.getColor(context, R.color.next_button_opac_color_ggs));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FEATURE_REQUEST_CODE:
                UtilsGGS.getGgProduct(getContext()).setFeatures(selectedFeaturesList);
                setTagsTextInTv(selectedFeaturesList, featureTv);
                break;

            case TAG_REQUEST_CODE:
                UtilsGGS.getGgProduct(getContext()).setTags(selectedTagList);
                setTagsTextInTv(selectedTagList, tagTv);
                break;

            case BRAND_SELECT_REQUEST_CODE:

                if (UtilsGGS.getGgProduct(getContext()).getBrand().getId() > -1) {

                    seledtedBrandTextView.setText(UtilsGGS.getGgProduct(getContext()).getBrand().getName());

                }

                if (seledtedBrandTextView.getText().toString().trim().length() != 0) {

                    model_layout.setAlpha(1f);
                    model_layout.setEnabled(true);
                } else {

                    model_layout.setAlpha(.5f);

                }

                if (resultCode == 1) {
                    selectedModelTextView.setText("");
                    UtilsGGS.getGgProduct(getContext()).getModel().setId(-1);
                    UtilsGGS.getGgProduct(getContext()).getModel().setName(null);
                    if (NetworkUtilGGS.isInternetAvailable(getActivity())) {
                        new GetModelAsycnTask(this, UtilsGGS.getGgProduct(getContext()).getBrand().getId(), getActivity()).execute();
                    } else {
                        dialogManager.showDialog(getString(R.string.failed_to_load_data_ggs), getString(R.string.text_no_internet_connection_ggs), false);

                    }

                }

                validateAndChangePostbutton();
                break;

            case MODEL_SELECT_REQUEST_CODE:
                if (UtilsGGS.getGgProduct(getContext()).getModel().getId() > -1) {
                    Log.d("hey", UtilsGGS.getGgProduct(getContext()).getModel().getName());
                    selectedModelTextView.setText(UtilsGGS.getGgProduct(getContext()).getModel().getName());
                }
                validateAndChangePostbutton();

                break;
            case CONDITION_REQUEST_CODE:
                if (UtilsGGS.getGgProduct(getContext()).getCondition().getId() > -1) {
                    tv_condition_value.setText(UtilsGGS.getGgProduct(getContext()).getCondition().getName());
                }
                validateAndChangePostbutton();

                break;

            case QUANTITY_REQUEST_CODE:
                setquantity();
                // if (UtilsGGS.getGgProduct(getContext()).getQuantity() > 0) {
                // quantityText.setText(String.valueOf(UtilsGGS.getGgProduct(getContext()).getQuantity()));
                // }
                //   validateAndChangePostbutton();
                break;


        }


    }

    private void setquantity() {
        if (UtilsGGS.getGgProduct(getContext()).getQuantity() > 0) {
            quantityText.setText(String.valueOf(UtilsGGS.getGgProduct(getContext()).getQuantity()));
        }
    }

    @Override
    public void onSuccess(String createdId) {
        if (UtilsGGS.getGgProductEditFlag(getContext())) {
            Intent intent = new Intent();
            intent.putExtra("id", String.valueOf(UtilsGGS.getGgProduct(getContext()).getId()));
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        } else {
            Intent intent = new Intent();
            intent.putExtra("created_id", createdId);
            intent.putExtra("created_product", UtilsGGS.getGgProduct(getActivity()));
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();

        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == audioCallSwitch) {
            if (isChecked) {
                UtilsGGS.getGgProduct(context).getContact_preference().add(ConstantsGGS.AUDIO_CALL);

            } else {
                UtilsGGS.getGgProduct(context).getContact_preference().remove(ConstantsGGS.AUDIO_CALL);

            }

            //  UtilsGGS.getGgProduct(getActivity()).setModel();

        } else if (buttonView == videoCallSwitch) {
            if (isChecked) {
                UtilsGGS.getGgProduct(context).getContact_preference().add(ConstantsGGS.VIDEO_CALL);
            } else {
                UtilsGGS.getGgProduct(context).getContact_preference().remove(ConstantsGGS.VIDEO_CALL);

            }
            //UtilsGGS.getGgProduct(context).getContact_preference().add(ConstantsGGS.VIDEO_CALL);
        } else if (buttonView == cellularCallSwitch) {
            if (isChecked) {
                UtilsGGS.getGgProduct(context).getContact_preference().add(ConstantsGGS.CELLULAR_CALL);
            } else {
                UtilsGGS.getGgProduct(context).getContact_preference().remove(ConstantsGGS.CELLULAR_CALL);

            }
        } else if (buttonView == chatSwitch) {
            UtilsGGS.getGgProduct(context).getContact_preference().add(ConstantsGGS.CHAT);
            chatSwitch.setChecked(true);
        }
    }

    @Override
    public void onModelDataCallBack(ArrayList<SuggestionDataGGS> suggestionDataGGSes) {
        UtilsGGS.getProductParentActivity(getContext()).models = suggestionDataGGSes;


    }
}
