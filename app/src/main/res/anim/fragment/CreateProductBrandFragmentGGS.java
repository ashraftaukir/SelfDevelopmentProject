package com.gagagugu.ggservice.fragment;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.adapter.SelectBrandRecycleViewAdapter;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.interfaces.EditTextChangeTextListener;
import com.gagagugu.ggservice.models.SuggestionDataGGS;
import com.gagagugu.ggservice.utils.EditTextTextWatcherGgs;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.RecyclerTouchListenerGGS;
import com.gagagugu.ggservice.utils.SearchTextViewGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;

import java.util.ArrayList;
import java.util.Collections;


public class CreateProductBrandFragmentGGS extends DialogFragment implements View.OnClickListener {
    private ArrayList<SuggestionDataGGS> brandsArrayList;
    private ArrayList<SuggestionDataGGS> adapterArrayList = new ArrayList<>();
    private ProfileColorsGGS profileColorsGGS;
    private RecyclerView brandRecycleView;


    private SelectBrandRecycleViewAdapter selectBrandRecycleViewAdapter;
    private SearchTextViewGGS searchTextViewGGS;
    private int resultCode;
    //  private LinearLayout parentLayout;
    private RelativeLayout parentLayout;
    private LinearLayout childLayout;
    private TextView addItemTv,addDummyTv;
    private CardView noResultCardView;


    public CreateProductBrandFragmentGGS() {
        // Required empty public constructor
    }


    public static com.gagagugu.ggservice.fragment.CreateProductBrandFragmentGGS newInstance() {


        com.gagagugu.ggservice.fragment.CreateProductBrandFragmentGGS fragment = new com.gagagugu.ggservice.fragment.CreateProductBrandFragmentGGS();


        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_product_brand, container, false);
        profileColorsGGS = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());

        brandsArrayList = UtilsGGS.getProductParentActivity(getContext()).brands;
        Collections.sort(brandsArrayList);

        initialize(view);
        initizeListner();
        addTextListener();

        brandRecycleView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                return false;
            }
        });
        return view;


    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity(), R.style.CustomDialog);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);



        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }


    private void addTextListener() {
        searchTextViewGGS.getSearchEditText().addTextChangedListener(new EditTextTextWatcherGgs(new EditTextChangeTextListener() {
            @Override
            public void onChange(String s) {
                if (s.length() != 0) {

                    changeListItem(searchInLocalList(s));

                } else {
                    changeListItem(brandsArrayList);
                }
            }
        }));
    }


    private void initialize(View contentView) {
        brandRecycleView = (RecyclerView) contentView.findViewById(R.id.select_brand_recycle_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        brandRecycleView.setLayoutManager(layoutManager);
        this.brandRecycleView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        selectBrandRecycleViewAdapter = new SelectBrandRecycleViewAdapter(adapterArrayList, UtilsGGS.getGgProduct(getContext()).getBrand().getId(),
                profileColorsGGS.getColorCodeLight());
        brandRecycleView.setAdapter(selectBrandRecycleViewAdapter);
        adapterArrayList.addAll(brandsArrayList);
        selectBrandRecycleViewAdapter.notifyDataSetChanged();
        searchTextViewGGS = (SearchTextViewGGS) contentView.findViewById(R.id.search_edit_text);
        // parentLayout=(LinearLayout)contentView.findViewById(R.id.parent_layout);
        parentLayout = (RelativeLayout) contentView.findViewById(R.id.parent_layout);
        childLayout = (LinearLayout) contentView.findViewById(R.id.child_layout);
        parentLayout.setOnClickListener(this);
        childLayout.setOnClickListener(this);
        addItemTv=(TextView)contentView.findViewById(R.id.tv_add_item);
        addItemTv.setText(getString(R.string.search_result_not_found_ggs));

        addDummyTv=(TextView)contentView.findViewById(R.id.tv_dummy);
        addDummyTv.setVisibility(View.GONE);
        noResultCardView = (CardView) contentView.findViewById(R.id.add_dummy_text);


    }

    private void changeListItem(ArrayList<SuggestionDataGGS> suggestionDataGGSes) {
        adapterArrayList.clear();
        adapterArrayList.addAll(suggestionDataGGSes);
        selectBrandRecycleViewAdapter.notifyDataSetChanged();
        if (suggestionDataGGSes.size()==0){
            noResultCardView.setVisibility(View.VISIBLE);
        }else {
            noResultCardView.setVisibility(View.GONE);
        }

    }

    private void initizeListner() {

        brandRecycleView.addOnItemTouchListener(new RecyclerTouchListenerGGS(getActivity(), new RecyclerTouchListenerGGS.ClickListener() {
            @Override
            public void onClick(View view, final int position) {


                SuggestionDataGGS brand;
                brand = adapterArrayList.get(position);


                if (UtilsGGS.getGgProduct(getContext()) != null && UtilsGGS.getGgProduct(getContext()).getBrand().getId() == brand.getId())
                    resultCode = 0;
                else
                    resultCode = 1;
                UtilsGGS.getGgProduct(getContext()).setBrand(brand);


                getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, null);
                dismiss();
            }

        }));


    }

    private ArrayList<SuggestionDataGGS> searchInLocalList(String s) {
        ArrayList<SuggestionDataGGS> list = new ArrayList<>();
        for (SuggestionDataGGS suggestionDataGGS : brandsArrayList) {
            if (suggestionDataGGS.getName().toLowerCase().contains(s.toLowerCase())) {
                list.add(suggestionDataGGS);
            }
        }
        return list;
    }


    @Override
    public void onClick(View v) {
        if (v == parentLayout) {
            dismiss();
        } else if (v == childLayout) {
            //Do Nothing
        }
    }
}
