package com.gagagugu.ggservice.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.adapter.RecylerViewPOstTimeAdapterGGS;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.RecyclerTouchListenerGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by taukir on 2/22/17.
 */

public class PostTimeFragmentGGS extends DialogFragment implements View.OnClickListener {


    private ProfileColorsGGS profileColorsGGS;
    private RecylerViewPOstTimeAdapterGGS mAdapter;
    private ArrayList<String> postTimeArrayList;

    private RecyclerView recyclerView;
    private RelativeLayout parent_relative_layout;
    private ArrayList<String> timeStringForMap;
    private int postTimeSelectedPos;
    private TextView postTimeTv;

    public PostTimeFragmentGGS() {

    }

    public static com.gagagugu.ggservice.fragment.PostTimeFragmentGGS newInstance() {
        return new com.gagagugu.ggservice.fragment.PostTimeFragmentGGS();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeStringForMap = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.post_time_for_map_ggs)));
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.post_time_fragment_gg, null);
        dialog.setContentView(contentView);
        initializeRecycleViewAdapter(contentView);
        initialize();
        initializeList();
        initListener();
        //   dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        //  dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    private void initialize() {

        profileColorsGGS = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
    }


    @NonNull
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

        recyclerView = (RecyclerView) view.findViewById(R.id.posttime_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        parent_relative_layout = (RelativeLayout) view.findViewById(R.id.parent_relative_layout);
        postTimeTv = (TextView) view.findViewById(R.id.tv_post_time);

    }

    private void initializeList() {

        postTimeArrayList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.post_time_string_array_ggs)));
        mAdapter = new RecylerViewPOstTimeAdapterGGS(postTimeArrayList, postTimeSelectedPos, profileColorsGGS);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

    }

    private void initListener() {

        recyclerView.addOnItemTouchListener(new RecyclerTouchListenerGGS(getActivity(), new RecyclerTouchListenerGGS.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                /*UtilsGGS.getGGSearch(getContext()).setId(position);
                UtilsGGS.getGGSearch(getContext()).setItemname(postTimeArrayList.get(position));
                UtilsGGS.getGGSearch(getContext()).setItemMapName(timeStringForMap.get(position));*/
                Intent intent = new Intent();
                intent.putExtra("position", position);
                intent.putExtra("posttimeitemname", postTimeArrayList.get(position));
                intent.putExtra("itemmapname", timeStringForMap.get(position));
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                dismiss();
            }

        }));
        postTimeTv.setOnClickListener(this);

        parent_relative_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v==postTimeTv){

        }else if (v == parent_relative_layout) {
            dismiss();

        }
    }

    public void setSelectedPosition(int postTimeSelectedPos) {

        this.postTimeSelectedPos = postTimeSelectedPos;
    }
}
