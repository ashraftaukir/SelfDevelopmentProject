package com.gagagugu.ggservice.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.interfaces.TextWatcherOnTextChanged;
import com.gagagugu.ggservice.utils.CustomTextWatcher;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.SearchViewSystemHelperGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;


/**
 * A simple subclass.
 */
public class AddServiceExperienceFragmentGGS extends BottomSheetDialogFragment implements View.OnFocusChangeListener, View.OnClickListener, TextWatcherOnTextChanged {


    private static final int PEEK_HEIGHT = 700;
    private static final String BUNDLE_EXTRA_EXPERIENCE_TEXT = "experience_text";
    private static final int TEXT_WATCHER_FOR_EXPERIENCE_ET = 3;
    private EditText addEexperineceEditText;
    private TextView experienceDoneButton;
    private BottomSheetBehavior bottomSheetBehavior;
    private ProfileColorsGGS profileColorsGGS;
    private TextView textCountExperienceTextView;



    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                dismiss();
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {


        }
    };

    public AddServiceExperienceFragmentGGS() {
        // Required empty public constructor
    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_add_service_experience_ggs, null);
        profileColorsGGS = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        dialog.setContentView(contentView);
        //  setCancelable(false);
        initializeView(contentView);
        getBundleAndUpdatePreviousData();
        initializeListener(contentView);
        experienceEditTextValidation();
    }

    private void getBundleAndUpdatePreviousData() {
        if (getArguments()!=null && getArguments().containsKey(BUNDLE_EXTRA_EXPERIENCE_TEXT)) {
            String experienceText = getArguments().getString(BUNDLE_EXTRA_EXPERIENCE_TEXT);
            addEexperineceEditText.setText(experienceText);
            addEexperineceEditText.setSelection(addEexperineceEditText.getText().length());
            textCountExperienceTextView.setText(String.valueOf(experienceText.length()));
        }
    }


    private void initializeView(View contentView) {

        experienceDoneButton = (TextView) contentView.findViewById(R.id.experience_dialog_done_button);
        addEexperineceEditText = (EditText) contentView.findViewById(R.id.add_experinece_edit_text);
        experienceDoneButton.setTextColor(profileColorsGGS.getColorCodeLight());
        textCountExperienceTextView = (TextView) contentView.findViewById(R.id.text_count_experience_text_view);


    }

    private void initializeListener(View contentView) {
        addEexperineceEditText.setOnFocusChangeListener(this);
        experienceDoneButton.setOnClickListener(this);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        final CoordinatorLayout.Behavior behavior = params.getBehavior();


        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            bottomSheetBehavior = (BottomSheetBehavior) behavior;
            bottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }


    private void experienceEditTextValidation() {
        final int experienceEditTextCharLimit = 5;
        // getActivity().getResources().getInteger(R.integer.descr_edittext_max_length_ggs);
        addEexperineceEditText.addTextChangedListener(new CustomTextWatcher(TEXT_WATCHER_FOR_EXPERIENCE_ET,this));



    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == addEexperineceEditText) {
            if (hasFocus) {
                bottomSheetBehavior.setPeekHeight(PEEK_HEIGHT);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == experienceDoneButton) {
            SearchViewSystemHelperGGS.hideKeyboard(getActivity(), addEexperineceEditText);
            dismiss();
            Intent intent = new Intent();
            intent.putExtra("experience_text", addEexperineceEditText.getText().toString());
            getTargetFragment().onActivityResult(getTargetRequestCode(), 0, intent);

        }
    }

    @Override
    public void onTextChanged(int requestCode, CharSequence s) {
        switch (requestCode){
            case TEXT_WATCHER_FOR_EXPERIENCE_ET:
                textCountExperienceTextView.setText(String.valueOf(s.length()));
                break;
        }
    }
}
