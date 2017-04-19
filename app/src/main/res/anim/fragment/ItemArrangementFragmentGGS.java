package com.gagagugu.ggservice.fragment;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.adapter.ItemAdapterGGS;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.interfaces.OnItemSelectedListenerForItemArrangement;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.RecyclerTouchListenerGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by taukir on 2/23/17.
 */

public class ItemArrangementFragmentGGS extends Fragment implements View.OnClickListener {


    ArrayList<String> sortinglist;
    RecyclerView itemrecylerview;
    ItemAdapterGGS itemAdapterGGS;
    ProfileColorsGGS profileColorsGGS;
    ImageView iv_cross;
    LinearLayout layoutCross;

    private LinearLayout rootLayout;

    OnItemSelectedListenerForItemArrangement onItemSelectedListenerForItemArrangement;
    private boolean isFromExchange;


    public ItemArrangementFragmentGGS() {

    }

    public static com.gagagugu.ggservice.fragment.ItemArrangementFragmentGGS newInstance() {
        return new com.gagagugu.ggservice.fragment.ItemArrangementFragmentGGS();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_item_arrangement_ggs, container, false);
        getActivity().getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getContext(),R.color.white_ggs));
        profileColorsGGS = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        getBundlesExtras();
        initViews(view);
        initlist();
        initializeRecycleViewAdapter(view);
        initlistener();
        return view;

    }

    private void getBundlesExtras() {
        Bundle bundle = getArguments();
        if(bundle.containsKey("is_exchange")){
            isFromExchange = bundle.getBoolean("is_exchange");
        }
    }

    private void initViews(View view) {

        iv_cross = (ImageView) view.findViewById(R.id.iv_cross);
        iv_cross.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        rootLayout = (LinearLayout) view.findViewById(R.id.root_layout);
        layoutCross = (LinearLayout) view.findViewById(R.id.layout_cross);
    }

    private void initlistener() {


        itemrecylerview.addOnItemTouchListener(new RecyclerTouchListenerGGS(getContext(), new RecyclerTouchListenerGGS.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                UtilsGGS.getGGSearch(getContext()).setSortingid(position);
                UtilsGGS.getGGSearch(getContext()).setSortingname(sortinglist.get(position));
                onItemSelectedListenerForItemArrangement.onItemSelected();
                closeFragment();


            }

        }));

        layoutCross.setOnClickListener(this);
        rootLayout.setOnClickListener(this);

    }

    private void initlist() {

        sortinglist = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.item_arrangement_string_array_ggs)));
        if(isFromExchange){
            // removing lessexpensive and muchexpensive
            sortinglist.remove(6);
            sortinglist.remove(6);
        }
    }

    private void initializeRecycleViewAdapter(View view) {


        itemrecylerview = (RecyclerView) view.findViewById(R.id.sorting_recylerview);
        itemrecylerview.setLayoutManager(new LinearLayoutManager(getContext()));
        itemAdapterGGS = new ItemAdapterGGS(sortinglist, UtilsGGS.getGGSearch(getContext()).getSortingid(), profileColorsGGS);
        itemrecylerview.setAdapter(itemAdapterGGS);

    }

    @Override
    public void onClick(View v) {
        if (v == layoutCross) {
            closeFragment();

        }else if(v==rootLayout){
            //do nothing
        }

    }


    @Override
    public void onDetach() {
        super.onDetach();
        rebuildThemeBasedUI(ServicePreference.getInstance(getContext()).getThemeColor());
    }

    private void closeFragment() {
        getActivity().onBackPressed();
    }

    public void setOnItemSelectedListener(OnItemSelectedListenerForItemArrangement onItemSelectedListener) {
        this.onItemSelectedListenerForItemArrangement = onItemSelectedListener;
    }

    private void rebuildThemeBasedUI(String color) {
        ProfileColorsGGS mProfileTheme = UtilsGGS.getProfileColor(color);
        getActivity().getWindow().getDecorView().setBackgroundResource(mProfileTheme.getBackground());
    }
}
