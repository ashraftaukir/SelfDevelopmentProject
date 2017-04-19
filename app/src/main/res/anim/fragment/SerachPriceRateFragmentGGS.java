package com.gagagugu.ggservice.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.models.GGSearch;
import com.gagagugu.ggservice.utils.DecimalDigitsInputFilterGGS;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.SearchViewSystemHelperGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;

/**
 * Created by taukir on 2/20/17.
 */
public class SerachPriceRateFragmentGGS extends BottomSheetDialogFragment implements View.OnClickListener {


    private final static int BOTTOMSHEET_PEEK_HEIGHT = 800;
    private static final String BUDGET = "Budget";
    GGSearch search;
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView price_range_done_button, tv_name;
    private EditText minimum_ggs, maximum_ggs;
    private ProfileColorsGGS themecolor;
    private String itemtype;
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
    private String selectedMinPrice;
    private String selectedMaxPrice;

    public SerachPriceRateFragmentGGS() {

    }


    public static com.gagagugu.ggservice.fragment.SerachPriceRateFragmentGGS newInstance() {

        return new com.gagagugu.ggservice.fragment.SerachPriceRateFragmentGGS();

    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
       // super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.search_price_rate_fragment_gg, null);
        dialog.setContentView(contentView);
        search = new GGSearch();

       // getBundle();
        initialize(contentView);
        initListener();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

//    private void getBundle() {
//
//        Bundle bundle = getArguments();
//        itemtype = bundle.getString("type");
//    }

    private void initListener() {

        price_range_done_button.setOnClickListener(this);

    }

    private void initialize(View view) {

        themecolor = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        price_range_done_button = (TextView) view.findViewById(R.id.price_range_done_button);
        tv_name = (TextView) view.findViewById(R.id.tv_name);


        price_range_done_button.setTextColor(themecolor.getColorCodeLight());

        minimum_ggs = (EditText) view.findViewById(R.id.minimum_ggs);
        maximum_ggs = (EditText) view.findViewById(R.id.maximum_ggs);

        minimum_ggs.setText(selectedMinPrice);
        maximum_ggs.setText(selectedMaxPrice);
        minimum_ggs.setSelection(minimum_ggs.getText().length());
        minimum_ggs.setFilters(new InputFilter[]{new DecimalDigitsInputFilterGGS(8,2)});
        maximum_ggs.setFilters(new InputFilter[]{new DecimalDigitsInputFilterGGS(8,2)});

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        final CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            bottomSheetBehavior = (BottomSheetBehavior) behavior;
            bottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
            bottomSheetBehavior.setPeekHeight(BOTTOMSHEET_PEEK_HEIGHT);

        }

    }

    @Override
    public void onClick(View v) {


        if (v == price_range_done_button) {

            if (valuecheck()) {
                String minValue = minimum_ggs.getText().toString();
                String maxValue = maximum_ggs.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("min_value",minValue.replaceAll("^0*",""));
                intent.putExtra("max_value",maxValue.replaceAll("^0*",""));

                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);

               /* UtilsGGS.getGGSearch(getActivity()).setMinvalue(minValue.replaceAll("^0*",""));
                UtilsGGS.getGGSearch(getActivity()).setMaxvalue(maxValue.replaceAll("^0*",""));
               */
               // getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);
                SearchViewSystemHelperGGS.hideKeyboard(getActivity(), maximum_ggs);
                dismiss();
            }
        }
    }


    private boolean valuecheck() {
        double maxAmount,minAmount;
        String maxAmountText=maximum_ggs.getText().toString();
        String minAmountText=minimum_ggs.getText().toString();


        if (minAmountText.isEmpty() ){

            Toast.makeText(getContext(), (getString(R.string.please_set_the_minimum_value_ggs)), Toast.LENGTH_SHORT).show();

            return false;
        }

        if (maxAmountText.isEmpty()) {

            Toast.makeText(getContext(), (getString(R.string.please_set_the_maximum_value_ggs)), Toast.LENGTH_SHORT).show();

            return false;
        }



        try {
            minAmount=Double.parseDouble(minAmountText);
            maxAmount=Double.parseDouble(maxAmountText);

        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(getActivity(),getString(R.string.invalid_number_format_ggs), Toast.LENGTH_SHORT).show();
            return false;
        }

        String maxSub=maxAmountText.substring(maxAmountText.length()-1);
        if (maxSub.equals(".")){
            Toast.makeText(getActivity(), getString(R.string.invalid_number_format_ggs), Toast.LENGTH_SHORT).show();
            return false;
        }

        String minSub=minAmountText.substring(minAmountText.length()-1);
        if (minSub.equals(".")){
            Toast.makeText(getActivity(),getString(R.string.invalid_number_format_ggs), Toast.LENGTH_SHORT).show();
            return false;
        }



        if (minAmount>= maxAmount) {
            Toast.makeText(getContext(), (getString(R.string.maximum_should_be_greater_than_minimum_ggs)), Toast.LENGTH_SHORT).show();
            return false;
        }


        if (minAmount <= 0) {
            Toast.makeText(getContext(), (getString(R.string.minimum_value_cannot_be_zero_ggs)), Toast.LENGTH_SHORT).show();
            return false;
        }



        return true;
    }


    public void setMinPrice(String selectedMinPrice) {
        this.selectedMinPrice = selectedMinPrice;
    }

    public void setMaxPrice(String selectedMaxPrice) {
        this.selectedMaxPrice = selectedMaxPrice;
    }
}
