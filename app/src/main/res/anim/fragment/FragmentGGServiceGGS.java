package com.gagagugu.ggservice.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.adapter.GgServiceAdapterGGS;
import com.gagagugu.ggservice.config.GGServiceAPI;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.utils.UtilsGGS;
import com.gagagugu.ggservice.view.customviews.DividerItemDecorationGGS;

import app.com.myfolder.init.OpenMedia;
import app.com.myfolder.utils.MediaType;
import app.com.myfolder.utils.PreferenceUtil;


/**
 * Created by Md. Sifat-Ul Haque on 10/17/2016.
 */

public class FragmentGGServiceGGS extends Fragment implements GgServiceAdapterGGS.onServiceSelectedListener {

    private RecyclerView mRvGgServiceList;
    public static final String TITLE_TAG = "title";
    public static final String URL_TAG = "url";
    public static final String CHANGE_BACK_ICON = "icon";
    public static final String URL_GAMES_NEW = "http://game.ggdev.xyz/?profile_id=";
    // GG dufferent service app package name.
    public static String APP_PACKAGE_GG_POLL;// = "xyz.ggdev.poll";
    public static String APP_PACKAGE_GG_CONTEST;// = "xyz.ggdev.contest";
    PreferenceUtil pref;
    public static final String KEY_APP_ID = "app_id";
    public static final String APP_ID = "405";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        /*getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.gg_service_dialog_background);
        getDialog().getWindow().setWindowAnimations(R.style.GgServiceDialogAnimation);*/
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_gg_service_ggs, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        setupList();

    }

    private void setupList() {

        GgServiceAdapterGGS ggServiceAdapter = new GgServiceAdapterGGS();
        ggServiceAdapter.setServiceSelectedListener(this);

        DividerItemDecorationGGS dividerItemDecoration =
                new DividerItemDecorationGGS(getActivity(),
                        LinearLayoutManager.VERTICAL,
                        ContextCompat.getDrawable(getActivity(), R.drawable.invit_item_divider_drawable_ggs));
        mRvGgServiceList.addItemDecoration(dividerItemDecoration);

        mRvGgServiceList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvGgServiceList.setAdapter(ggServiceAdapter);

    }

    private void initView(View view) {
        mRvGgServiceList = (RecyclerView) view.findViewById(R.id.rv_gg_service);
    }

    @Override
    public void onServiceSelected(String serviceName) {
        if (serviceName.equals(getResources().getString(R.string.text_games_ggs))) {

            Intent intent = new Intent("com.gagagugu.connect.view.activity.WebViewActivity");

            intent.putExtra(TITLE_TAG, getResources().getString(R.string.text_game_zone_ggs));
            //  intent.putExtra(WebViewActivity.URL_TAG, URL_GAMES);
            intent.putExtra(URL_TAG, URL_GAMES_NEW + ServicePreference.getInstance(getContext()).getConnectProfileId());
            intent.putExtra(CHANGE_BACK_ICON, true);
            startActivity(intent);
        } else if (serviceName.equals(getResources().getString(R.string.text_contest_ggs))) {
            if (!UtilsGGS.launceAppForPackage(getActivity(), APP_PACKAGE_GG_CONTEST)) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.toast_coming_soon_ggs), Toast.LENGTH_SHORT).show();
            }
        } else if (serviceName.equals(getResources().getString(R.string.text_poll_ggs))) {
            if (!UtilsGGS.launceAppForPackage(getActivity(), APP_PACKAGE_GG_POLL)) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.toast_coming_soon_ggs), Toast.LENGTH_SHORT).show();
            }
        } else if (serviceName.equals(getResources().getString(R.string.text_gg_folder_ggs))) {
            new OpenMedia(getActivity(), ServicePreference.getInstance(getContext()).getConnectProfileId(), ServicePreference.getInstance(getContext()).getServiceAccessToken(), GGServiceAPI.MEDIA_CLIENT_ID_SERVICE, MediaType.ALL, GGServiceAPI.MEDIA_FOLDER_BASE);
        } else if (serviceName.equals(getResources().getString(R.string.text_gagagugu))) { // start service intent (sajal)
            getActivity().finish();
        }

    }
}
