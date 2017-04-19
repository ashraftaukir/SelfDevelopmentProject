package com.gagagugu.ggservice.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.adapter.RecyclerViewConditionAdapterGGS;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.models.SuggestionDataGGS;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.RecyclerTouchListenerGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;

import java.util.ArrayList;

/**
 * Created by taukir on 2/20/17.
 */
public class ConditionSearchFragmentGGS extends DialogFragment implements View.OnClickListener {


    private ProfileColorsGGS profileColorsGGS;
    private RecyclerViewConditionAdapterGGS mAdapter;
    private ArrayList<SuggestionDataGGS> conditionlist;
    private RecyclerView recyclerView;
    private RelativeLayout parentLayout;



    public ConditionSearchFragmentGGS() {

    }

    public static com.gagagugu.ggservice.fragment.ConditionSearchFragmentGGS newInstance() {


        return new com.gagagugu.ggservice.fragment.ConditionSearchFragmentGGS();
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(getContext(), R.layout.fragment_condition_ggs, null);
        profileColorsGGS = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        dialog.setContentView(view);
        Bundle bundle = getBundle();
        initializeRecycleViewAdapter(view);
        initializeList(bundle);
        initizeListner();


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        final Dialog dialog = new Dialog(getActivity(), R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    private void initializeRecycleViewAdapter(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.condition_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        parentLayout=(RelativeLayout)view.findViewById(R.id.parent_layout);
    }


    private void initizeListner() {


        recyclerView.addOnItemTouchListener(new RecyclerTouchListenerGGS(getActivity(), new RecyclerTouchListenerGGS.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                UtilsGGS.getGgProduct(getContext()).setCondition(conditionlist.get(position));

                getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);
                dismiss();
            }

        }));

        parentLayout.setOnClickListener(this);

    }

    private void initializeList(Bundle bundle) {

        conditionlist = (ArrayList<SuggestionDataGGS>) bundle.getSerializable("condition_list");
        mAdapter = new RecyclerViewConditionAdapterGGS(conditionlist, UtilsGGS.getGgProduct(getContext()).getCondition().getId(),profileColorsGGS);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);


    }

    private Bundle getBundle() {

        if (getArguments().getBundle("bundle") != null) return getArguments().getBundle("bundle");
        return getArguments();

    }

    @Override
    public void onClick(View v) {
        if (v==parentLayout)
            dismiss();
    }



}
