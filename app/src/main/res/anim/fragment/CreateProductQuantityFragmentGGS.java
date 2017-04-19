package com.gagagugu.ggservice.fragment;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.SearchViewSystemHelperGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;

/**
 * Created by taukir on 2/19/17.
 */

public class CreateProductQuantityFragmentGGS extends BottomSheetDialogFragment implements View.OnClickListener {

    private ProfileColorsGGS profileColorsGGS;
    private TextView doneButton;
    private EditText quantityEditText;
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


    public CreateProductQuantityFragmentGGS() {
    }


    public static com.gagagugu.ggservice.fragment.CreateProductQuantityFragmentGGS newInstance() {


        return new com.gagagugu.ggservice.fragment.CreateProductQuantityFragmentGGS();
    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(getContext(), R.layout.fragment_quantity_fragment_ggs, null);
        dialog.setContentView(view);
        profileColorsGGS = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        initializeView(view);
        initlistener();

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    private void initlistener() {

        doneButton.setOnClickListener(this);

    }

    private void initializeView(View view) {

        quantityEditText = (EditText) view.findViewById(R.id.quantity_edit_text);
        //  quantityEditText.setFilters(new InputFilter[]{new MoneyValueFilterGGS()});
        doneButton = (TextView) view.findViewById(R.id.quantity_done_button);
        doneButton.setTextColor(profileColorsGGS.getColorCodeLight());

        if (UtilsGGS.getGgProduct(getContext()).getQuantity()>0) {
            int quantityValue = UtilsGGS.getGgProduct(getContext()).getQuantity();
            quantityEditText.setText(String.valueOf(quantityValue));
            quantityEditText.setSelection(quantityEditText.getText().length());
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
            if (valuecheck()) {
                UtilsGGS.getGgProduct(getActivity()).setQuantity(Integer.valueOf(quantityEditText.getText().toString()));
                getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);
                SearchViewSystemHelperGGS.hideKeyboard(getActivity(), quantityEditText);
                dismiss();
            }
        }
    }


    private boolean valuecheck() {


        if (quantityEditText.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), (getString(R.string.please_set_the_value_ggs)), Toast.LENGTH_SHORT).show();
            return false;
        } else if (Integer.valueOf(quantityEditText.getText().toString())<1) {
            Toast.makeText(getContext(), (getString(R.string.quantity_value_cannot_be_zero_ggs)), Toast.LENGTH_SHORT).show();

            return false;

        } else if (quantityEditText.getText().length() > 9) {
            Toast.makeText(getContext(), getString(R.string.quantity_is_too_big_ggs), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
