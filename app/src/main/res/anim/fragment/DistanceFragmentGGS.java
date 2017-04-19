package com.gagagugu.ggservice.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.adapter.RecylerViewPOstTimeAdapterGGS;
import com.gagagugu.ggservice.asynctasks.GetCityAndCountryTask;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.interfaces.CallBackFromCityCountryTask;
import com.gagagugu.ggservice.models.SearchLocationGGS;
import com.gagagugu.ggservice.utils.LocationHandler;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.RecyclerTouchListenerGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by taukir on 2/23/17.
 */

public class DistanceFragmentGGS extends DialogFragment implements View.OnClickListener, LocationHandler.OnLocationUpdateListener, CallBackFromCityCountryTask {

    private ProfileColorsGGS profileColorsGGS;
    private RecylerViewPOstTimeAdapterGGS mAdapter;
    private ArrayList<String> distanceArrayList;
    private RecyclerView recyclerView;
    private RelativeLayout parent_relative_layout;
    LocationHandler locationHandler;
    Location mLocation;
    private int distanceSelectedPoition;
    private TextView distanceTv;

    public DistanceFragmentGGS() {
    }

    public static com.gagagugu.ggservice.fragment.DistanceFragmentGGS newInstance() {
        return new com.gagagugu.ggservice.fragment.DistanceFragmentGGS();

    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.distance_fragment_gg, null);
        dialog.setContentView(contentView);
        locationHandler = new LocationHandler(getContext());
        locationHandler.setOnLocationUpdateListener(this);
        initializeRecycleViewAdapter(contentView);
        initialize();
        initializeList();
        initListener();

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

        distanceTv = (TextView) view.findViewById(R.id.tv_distance);
        recyclerView = (RecyclerView) view.findViewById(R.id.distance_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        parent_relative_layout = (RelativeLayout) view.findViewById(R.id.distance_relative_layout);

    }

    private void initializeList() {

        distanceArrayList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.distance_string_array_ggs)));
        mAdapter = new RecylerViewPOstTimeAdapterGGS(distanceArrayList, distanceSelectedPoition, profileColorsGGS);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

    }

    private void initListener() {


        recyclerView.addOnItemTouchListener(new RecyclerTouchListenerGGS(getActivity(), new RecyclerTouchListenerGGS.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                Intent intent = new Intent();
                intent.putExtra("distanceposition", position);
                distanceSelectedPoition = position;
                intent.putExtra("distanceitemname", distanceArrayList.get(position));

               /* UtilsGGS.getGGSearch(getContext()).setDistanceid(position);
                UtilsGGS.getGGSearch(getContext()).setDistancename(distanceArrayList.get(position));
                */
                String lastLat = ServicePreference.getInstance(getContext()).getLastLat();
                String lastLng = ServicePreference.getInstance(getContext()).getLastLng();

                if (position > 0) {  //selected position is city or country
                    Log.d("GETCITYCOUNTRY", "executing");
                    if (mLocation != null) {
                        new GetCityAndCountryTask(getActivity(), com.gagagugu.ggservice.fragment.DistanceFragmentGGS.this).execute(mLocation);

                    } else {
                        if (TextUtils.isEmpty(lastLat) && TextUtils.isEmpty(lastLng)) {
                            mLocation = new Location("");
                            mLocation.setLatitude(Double.parseDouble(lastLat));
                            mLocation.setLongitude(Double.parseDouble(lastLng));
                            new GetCityAndCountryTask(getActivity(), com.gagagugu.ggservice.fragment.DistanceFragmentGGS.this).execute(mLocation);
                        } else {
                            Log.d("GETCITYCOUNTRY", "mLocation null ");
                            Toast.makeText(getContext(), getString(R.string.no_location_found_try_again_ggs), Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }
                } else {
                    if (mLocation != null) {
                        intent.putExtra("lastlat", String.valueOf(mLocation.getLatitude()));
                        intent.putExtra("lastlon", String.valueOf(mLocation.getLongitude()));
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    } else {
                        if (lastLat != null && lastLng != null) {
                            intent.putExtra("lastlat", lastLat);
                            intent.putExtra("lastlon", lastLng);
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);

                        } else {
                            Toast.makeText(getContext(), getString(R.string.no_location_found_try_again_ggs), Toast.LENGTH_SHORT).show();
                        }
                    }
                    dismiss();
                }


            }

        }));

        parent_relative_layout.setOnClickListener(this);
        distanceTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == distanceTv) {
            //do nothing
        } else if (v == parent_relative_layout) {
            dismiss();

        }


    }


    @Override
    public void onLocationChange(Location location) {
        mLocation = location;
        if (mLocation != null) {
            ServicePreference.getInstance(getContext()).setLastLat(String.valueOf(mLocation.getLatitude()));
            ServicePreference.getInstance(getContext()).setLastLng(String.valueOf(mLocation.getLongitude()));
        }
    }

    @Override
    public void onCityCountryCallBack(SearchLocationGGS searchLocationGGS) {

        dismiss();

        if (searchLocationGGS != null) {
            Intent intent = new Intent();
            intent.putExtra("distanceposition", distanceSelectedPoition);
            intent.putExtra("distanceitemname", distanceArrayList.get(distanceSelectedPoition));
            Bundle bundle = new Bundle();
            bundle.putSerializable("searchlocation", searchLocationGGS);
            intent.putExtra("bundle", bundle);
            getTargetFragment().onActivityResult(getTargetRequestCode(), 0, intent);
        }
    }

    public void setSelectedPosition(int distanceSelectedPoition) {
        this.distanceSelectedPoition = distanceSelectedPoition;
    }

}
