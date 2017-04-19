package com.gagagugu.ggservice.fragment;


import android.app.Dialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.utils.DecimalDigitsInputFilterGGS;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.SearchViewSystemHelperGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;


public class CreateServiceRateFragmentGGS extends BottomSheetDialogFragment implements View.OnClickListener, View.OnFocusChangeListener {

    private final static int PEEK_HEIGHT = 800;
    private Button perServiceButton, perHourButton, perDayButton, perWeekButton, perMonthButton, prevSelectedButton;
    private TextView perServicelistDoneButton;
    private EditText perSeriviceRateEditText;
    private BottomSheetBehavior bottomSheetBehavior;
    //private ServiceRateGgs serviceRateGgs;
    private String price_unit;
    private String value;
    private String selectedUnit;
    private ProfileColorsGGS theme;
    private String type;
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


    public CreateServiceRateFragmentGGS() {
        // Required empty public constructor
    }


    public static com.gagagugu.ggservice.fragment.CreateServiceRateFragmentGGS newInstance() {
        com.gagagugu.ggservice.fragment.CreateServiceRateFragmentGGS fragment = new com.gagagugu.ggservice.fragment.CreateServiceRateFragmentGGS();

        return fragment;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        //super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.fragment_create_service_rate_ggs, null);
        theme = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        dialog.setContentView(contentView);

        initiliaze(contentView);
        getBundleExtra();
        initializeListener(contentView);
        backToPreviousSate();


        bottomSheetBehavior.setPeekHeight(PEEK_HEIGHT);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


    }

    private void getBundleExtra() {
        if (!getArguments().isEmpty()) {
            price_unit = getArguments().getString("price_unit");
            value = getArguments().getString("price");
            type = getArguments().getString("type");

            if (type != null && type.equals("rental")) {
                perServiceButton.setText(getString(R.string.rent_ggs));
            }
        }
    }


    private void initiliaze(View root) {
        selectedUnit = "";
        perServiceButton = (Button) root.findViewById(R.id.per_service_button);
        perHourButton = (Button) root.findViewById(R.id.per_hour_button);
        perDayButton = (Button) root.findViewById(R.id.per_day_button);
        perWeekButton = (Button) root.findViewById(R.id.per_week_button);
        perMonthButton = (Button) root.findViewById(R.id.per_month_button);
        perServicelistDoneButton = (TextView) root.findViewById(R.id.per_list_done_button);
        perSeriviceRateEditText = (EditText) root.findViewById(R.id.per_serivice_rate_edit_text);

        perServicelistDoneButton.setTextColor(theme.getColorCodeLight());


    }

    private void initializeListener(View contentView) {
        perServiceButton.setOnClickListener(this);
        perHourButton.setOnClickListener(this);
        perDayButton.setOnClickListener(this);
        perWeekButton.setOnClickListener(this);
        perMonthButton.setOnClickListener(this);
        perServicelistDoneButton.setOnClickListener(this);
        perSeriviceRateEditText.setOnFocusChangeListener(this);
        perSeriviceRateEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilterGGS(8, 2)});

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        final CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            bottomSheetBehavior = (BottomSheetBehavior) behavior;
            bottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == perServiceButton) {
            if (prevSelectedButton == perServiceButton) {
                changeToUnclickState(perServiceButton);
                prevSelectedButton = null;
                selectedUnit = "";
            } else {
                changeToClickState(perServiceButton);
                if (prevSelectedButton != null)
                    changeToUnclickState(prevSelectedButton);
                prevSelectedButton = perServiceButton;
            }

        } else if (v == perHourButton) {
            if (prevSelectedButton == perHourButton) {
                changeToUnclickState(perHourButton);
                prevSelectedButton = null;
                selectedUnit = "";
            } else {
                changeToClickState(perHourButton);
                if (prevSelectedButton != null)
                    changeToUnclickState(prevSelectedButton);
                prevSelectedButton = perHourButton;
            }
        } else if (v == perDayButton) {
            if (prevSelectedButton == perDayButton) {
                changeToUnclickState(perDayButton);
                prevSelectedButton = null;
                selectedUnit = "";
            } else {
                changeToClickState(perDayButton);
                if (prevSelectedButton != null)
                    changeToUnclickState(prevSelectedButton);
                prevSelectedButton = perDayButton;
            }
        } else if (v == perWeekButton) {
            if (prevSelectedButton == perWeekButton) {
                changeToUnclickState(perWeekButton);
                prevSelectedButton = null;
                selectedUnit = "";
            } else {
                changeToClickState(perWeekButton);
                if (prevSelectedButton != null)
                    changeToUnclickState(prevSelectedButton);
                prevSelectedButton = perWeekButton;
            }
        } else if (v == perMonthButton) {

            if (prevSelectedButton == perMonthButton) {
                changeToUnclickState(perMonthButton);
                prevSelectedButton = null;
                selectedUnit = "";
            } else {
                changeToClickState(perMonthButton);
                if (prevSelectedButton != null)
                    changeToUnclickState(prevSelectedButton);
                prevSelectedButton = perMonthButton;
            }
        } else if (v == perServicelistDoneButton) {
            if (validate()) {
                setValue();
                getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);
                SearchViewSystemHelperGGS.hideKeyboard(getActivity(), perSeriviceRateEditText);
                dismiss();
            }

        }
    }

    private void setValue() {
        if (type.equalsIgnoreCase("service")) {
            UtilsGGS.getGgService(getContext()).setPrice(perSeriviceRateEditText.getText().toString());
            UtilsGGS.getGgService(getContext()).setPrice_unit(selectedUnit.toLowerCase());
        } else if (type.equalsIgnoreCase("rental")) {
            UtilsGGS.getGgProduct(getContext()).setPrice(perSeriviceRateEditText.getText().toString());
            UtilsGGS.getGgProduct(getContext()).setPrice_unit(selectedUnit.toLowerCase());

        }
    }


    /**
     * Validate the user input
     *
     * @return
     */
    private boolean validate() {

        Double amount;
        String amountText = perSeriviceRateEditText.getText().toString();
        Log.v("tomav", selectedUnit + " empty");

        if (amountText.isEmpty()) {
            MakeToast(getString(R.string.no_rate_is_given_ggs));
            return false;
        }

        try {
            amount = Double.parseDouble(amountText);
        } catch (Exception ex) {
            ex.printStackTrace();
            MakeToast(getString(R.string.invalid_number_format_ggs));
            return false;
        }

        String subString=amountText.substring(amountText.length()-1);
        if (subString.equals(".")){
            MakeToast(getString(R.string.invalid_number_format_ggs));
            return false;
        }

        if (amount == 0) {
            MakeToast(getString(R.string.price_cannot_be_zero_ggs));
            return false;
        } else if (selectedUnit.isEmpty()) {

            MakeToast(getString(R.string.please_select_your_service_time_ggs));
            return false;
        }


        return true;
    }


    private void MakeToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * change button color to selected state
     *
     * @param button
     */
    private void changeToClickState(Button button) {
        selectedUnit = button.getText().toString();
        button.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_ggs));

        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.tag_back);
        drawable.setColorFilter(theme.getColorCodeLight(), PorterDuff.Mode.SRC_ATOP);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            button.setBackgroundDrawable(drawable);

        } else {
            button.setBackground(drawable);

        }
    }


    /**
     * change button to unselected state
     *
     * @param button
     */
    private void changeToUnclickState(Button button) {

        button.setTextColor(ContextCompat.getColor(getActivity(), R.color.per_list_text_color_ggs));

        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.corner_button_ggs);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            button.setBackgroundDrawable(drawable);

        } else {
            button.setBackground(drawable);

        }
    }


    /**
     * make user input as previously user had given
     */
    private void backToPreviousSate() {
        Button button = null;
        if (value.length() > 0) {
            perSeriviceRateEditText.setText(String.valueOf(value));
            perSeriviceRateEditText.setSelection(perSeriviceRateEditText.getText().length());
        }
        if (!price_unit.isEmpty()) {
            if (price_unit.toLowerCase().equals(perHourButton.getText().toString().toLowerCase())) {
                button = perHourButton;
            } else if (price_unit.toLowerCase().equals(perDayButton.getText().toString().toLowerCase())) {
                button = perDayButton;
            } else if (price_unit.toLowerCase().equals(perServiceButton.getText().toString().toLowerCase())) {
                button = perServiceButton;
            } else if (price_unit.toLowerCase().equals(perWeekButton.getText().toString().toLowerCase())) {
                button = perWeekButton;
            } else if (price_unit.toLowerCase().equals(perMonthButton.getText().toString().toLowerCase())) {
                button = perMonthButton;
            }

            prevSelectedButton = button;
            changeToClickState(button);

        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == perSeriviceRateEditText) {
            if (hasFocus) {
                // bottomSheetBehavior.setPeekHeight(PEEK_HEIGHT);
            }
        }

    }
}
