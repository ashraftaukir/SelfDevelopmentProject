package com.gagagugu.ggservice.fragment;


import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;
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
 * A simple {@link Fragment} subclass.
 */
public class CreateProductExchangeDetailsFragmentGGS extends BottomSheetDialogFragment implements View.OnFocusChangeListener, View.OnClickListener,TextWatcherOnTextChanged {
    private BottomSheetBehavior bottomSheetBehavior;
    private EditText addExchangeEditText;
    private TextView exchangeDoneButton;
    private ProfileColorsGGS profileColorsGGS;
    private TextView textCountExchangeTextView;
    private static final int PEEK_HEIGHT = 700;

    private static final int TEXT_WATCHER_FOR_EXCHANGE_ET = 4;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                dismiss();
            }else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {


        }
    };

    public CreateProductExchangeDetailsFragmentGGS() {
        // Required empty public constructor
    }


    public static com.gagagugu.ggservice.fragment.CreateProductExchangeDetailsFragmentGGS newInstance() {
        com.gagagugu.ggservice.fragment.CreateProductExchangeDetailsFragmentGGS fragment = new com.gagagugu.ggservice.fragment.CreateProductExchangeDetailsFragmentGGS();


        return fragment;
    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_create_product_exchange_details_fragment_gg, null);
        profileColorsGGS = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        dialog.setContentView(contentView);

        initialize(contentView);
        initializeListner(contentView);


        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }


    private void initialize(View contentView) {
        addExchangeEditText=(EditText)contentView.findViewById(R.id.exchange_description_edit_text);
        exchangeDoneButton=(TextView) contentView.findViewById(R.id.exchange_done_button);
        textCountExchangeTextView=(TextView)contentView.findViewById(R.id.text_count_exchange_text_view);

        exchangeDoneButton.setTextColor(profileColorsGGS.getColorCodeLight());

    }

    private void initializeListner(View contentView) {
        addExchangeEditText.setOnFocusChangeListener(this);
        exchangeDoneButton.setOnClickListener(this);

        addExchangeEditText.setText(UtilsGGS.getGgProduct(getActivity()).getExchange());
        addExchangeEditText.setSelection(addExchangeEditText.getText().length());
        textCountExchangeTextView.setText(UtilsGGS.getGgProduct(getActivity()).getExchange().length()+"");

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        final CoordinatorLayout.Behavior behavior = params.getBehavior();


        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            bottomSheetBehavior = (BottomSheetBehavior) behavior;
            bottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        addExchangeEditText.addTextChangedListener(new CustomTextWatcher(TEXT_WATCHER_FOR_EXCHANGE_ET,this));


    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == addExchangeEditText) {
            if (hasFocus) {
                bottomSheetBehavior.setPeekHeight(PEEK_HEIGHT);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v==exchangeDoneButton){
            UtilsGGS.getGgProduct(getActivity()).setExchange(addExchangeEditText.getText().toString());
            getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);
            SearchViewSystemHelperGGS.hideKeyboard(getContext(),addExchangeEditText);
            dismiss();
        }
    }

    @Override
    public void onTextChanged(int requestCode, CharSequence s) {
        switch (requestCode){
            case TEXT_WATCHER_FOR_EXCHANGE_ET:
                textCountExchangeTextView.setText(String.valueOf(s.length()));
                break;
        }
    }
}
