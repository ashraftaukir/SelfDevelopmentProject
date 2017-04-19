package com.gagagugu.ggservice.fragment;


import android.app.Dialog;
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
import com.gagagugu.ggservice.models.GGProduct;
import com.gagagugu.ggservice.utils.DecimalDigitsInputFilterGGS;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.SearchViewSystemHelperGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;


public class CreateItemBudgetFragmentGGS extends BottomSheetDialogFragment implements View.OnClickListener {
    private ProfileColorsGGS theme;
    private TextView doneButton;
    private EditText budgetEditText;
    private BottomSheetBehavior bottomSheetBehavior;


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

    public CreateItemBudgetFragmentGGS() {
        // Required empty public constructor
    }


    public static com.gagagugu.ggservice.fragment.CreateItemBudgetFragmentGGS newInstance() {
        com.gagagugu.ggservice.fragment.CreateItemBudgetFragmentGGS fragment = new com.gagagugu.ggservice.fragment.CreateItemBudgetFragmentGGS();


        return fragment;
    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
        //super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_create_item_budget_fragment_gg, null);
        TextView rateTv = (TextView) contentView.findViewById(R.id.textView);
        dialog.setContentView(contentView);
        theme = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        initialize(contentView);

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        if(UtilsGGS.getGgProduct(getActivity()).getProduct_type()== GGProduct.PRODUCT_TYPE.BUY){
            rateTv.setText(getContext().getString(R.string.budget_ggs));
            budgetEditText.setHint(getContext().getString(R.string.your_budget_here_ggs));
        }else {
            rateTv.setText(getContext().getString(R.string.price_ggs));
            budgetEditText.setHint(getContext().getString(R.string.your_price_here_ggs));

        }

    }


     private void initialize(View view) {
        doneButton = (TextView) view.findViewById(R.id.budget_done_button);
        budgetEditText = (EditText) view.findViewById(R.id.budget_edit_text);
        budgetEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilterGGS(8,2)});
        doneButton.setTextColor(theme.getColorCodeLight());

        doneButton.setOnClickListener(this);

        String savedPrice = UtilsGGS.getGgProduct(getContext()).getPrice();
        if (!savedPrice.isEmpty()) {
            budgetEditText.setText(savedPrice);
            budgetEditText.setSelection(budgetEditText.getText().length());
        }

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        final CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            bottomSheetBehavior = (BottomSheetBehavior) behavior;
            bottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

    }


    @Override
    public void onClick(View v) {
        if (v == doneButton) {
            if (validate()) {
                UtilsGGS.getGgProduct(getActivity()).setPrice(budgetEditText.getText().toString());
                getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);
                SearchViewSystemHelperGGS.hideKeyboard(getActivity(), budgetEditText);
                dismiss();
            }
        }

    }

    private boolean validate() {

        Double amount;
        String amountText = budgetEditText.getText().toString();

        if (amountText.isEmpty()) {
            if(UtilsGGS.getGgProduct(getActivity()).getProduct_type()== GGProduct.PRODUCT_TYPE.BUY) {
                Toast.makeText(getContext(), getString(R.string.no_budget_is_given_ggs), Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getContext(), getString(R.string.no_price_is_given_ggs), Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        try {
            amount=Double.parseDouble(amountText);
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(getActivity(), getString(R.string.invalid_number_format_ggs), Toast.LENGTH_SHORT).show();
            return false;
        }

        String subString=amountText.substring(amountText.length()-1);
        if (subString.equals(".")){
            Toast.makeText(getActivity(), getString(R.string.invalid_number_format_ggs), Toast.LENGTH_SHORT).show();
            return false;
        }


        if (amount==0){
            if(UtilsGGS.getGgProduct(getActivity()).getProduct_type()== GGProduct.PRODUCT_TYPE.BUY) {
                Toast.makeText(getContext(), getString(R.string.budget_cannot_be_zero_ggs), Toast.LENGTH_SHORT).show();
                return false;
            }else {
                Toast.makeText(getContext(), getString(R.string.price_cannot_be_zero_ggs), Toast.LENGTH_SHORT).show();
                return false;
            }
        }


        return true;
    }

}
