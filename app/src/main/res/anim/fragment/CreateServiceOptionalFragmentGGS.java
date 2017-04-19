package com.gagagugu.ggservice.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tagview.TagGgs;
import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.asynctasks.CreatePostServiceTask;
import com.gagagugu.ggservice.asynctasks.UpdateServiceAsynTask;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.core.SuggestionApi;
import com.gagagugu.ggservice.interfaces.PostCreationCallbackListener;
import com.gagagugu.ggservice.models.ConstantsGGS;
import com.gagagugu.ggservice.utils.ConstantGGS;
import com.gagagugu.ggservice.utils.DialogManager;
import com.gagagugu.ggservice.utils.MixpanelUtils;
import com.gagagugu.ggservice.utils.NetworkUtilGGS;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;
import com.gagagugu.ggservice.view.customviews.ThemeSwitchGGS;

import java.util.ArrayList;


public class CreateServiceOptionalFragmentGGS extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, PostCreationCallbackListener {

    public static final int TAG_REQUEST_CODE = 1000;
    public static final int SKILL_REQUEST_CODE = 2000;
    public static final int EXPERIENCE_REQUEST_CODE = 2001;
    com.gagagugu.ggservice.fragment.AddServiceExperienceFragmentGGS addServiceExperienceFragment;
    Context context;
    LinearLayout fragmentContainer;
    TextView postButton;
    RelativeLayout rootLayout;
    DialogManager dialogManager;
    private LinearLayout experienceLayout, skillLayout, tagLayout;
    private TextView skillTv, tagTv, experienceTv;
    private SuggestionApi suggestionApi;
    private ArrayList<TagGgs> selectedTagList, selectedSkillList;
    //Bundle tagViewBundle, skillViewBundle;
    private ThemeSwitchGGS audioCallSwitch, videoCallSwitch, cellularCallSwitch, chatSwitch;
    // CustomBottomSheetGgs tagFragment, skillFragment;
    private Animation animationUp, animationDown;
    private FrameLayout appBarLayout;

    public CreateServiceOptionalFragmentGGS() {


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        suggestionApi = new SuggestionApi();
        postButton = (TextView) getActivity().findViewById(R.id.post_upper);
        dialogManager = new DialogManager(getActivity());
        setPostButtonText();
    }

    private void setPostButtonText() {
        if (UtilsGGS.getGgServiceEditFlag(getContext())) {
            postButton.setText(getString(R.string.update_ggs));
        } else {
            postButton.setText(context.getResources().getString(R.string.post_service_text_ggs));
        }
        postButton.setTextColor(ContextCompat.getColor(context, R.color.toolbar_next_text_color_active_ggs));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_service_optional_fragment_ggs, container, false);

        initializeView(view);
        initializeLists();
        initializeBottomFragments();
        setupViewWithPreviousData();
        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        setupTheme();
    }


    private void setupTheme() {
        ProfileColorsGGS theme = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        audioCallSwitch.changeTheme(theme.getColorCodeLight());
        videoCallSwitch.changeTheme(theme.getColorCodeLight());
        chatSwitch.changeTheme(theme.getColorCodeLight());
        cellularCallSwitch.changeTheme(theme.getColorCodeLight());
    }

    private void setupViewWithPreviousData() {
        audioCallSwitch.setChecked(UtilsGGS.getGgService(context).getContact_preference().contains(ConstantsGGS.AUDIO_CALL));
        videoCallSwitch.setChecked(UtilsGGS.getGgService(context).getContact_preference().contains(ConstantsGGS.VIDEO_CALL));
        cellularCallSwitch.setChecked(UtilsGGS.getGgService(context).getContact_preference().contains(ConstantsGGS.CELLULAR_CALL));
    }


    private void initializeLists() {
        /*if (UtilsGGS.getGgService(context).getTags() == null) {
            selectedTagList = new ArrayList<>();

        } else {*/
        selectedTagList = new ArrayList<>(UtilsGGS.getGgService(context).getTags());
        setTagsTextInTv(selectedTagList, tagTv);
        /*}

        if (UtilsGGS.getGgService(context).getSkills() == null) {
            selectedSkillList = new ArrayList<>();

        } else {
        */
        selectedSkillList = new ArrayList<>(UtilsGGS.getGgService(context).getSkills());
        setTagsTextInTv(selectedSkillList, skillTv);
        //}
    }

    private void setTagsTextInTv(ArrayList<TagGgs> selectedList, TextView textView) {
        StringBuilder tagStringBuilder = new StringBuilder();
        for (TagGgs tagGgs : selectedList) {
            tagStringBuilder.append(tagGgs.getName());

            if (selectedList.indexOf(tagGgs) != selectedList.size() - 1) {
                tagStringBuilder.append(",");
            }
        }

        textView.setText(tagStringBuilder.toString());
    }


    private void initializeBottomFragments() {
        addServiceExperienceFragment = new com.gagagugu.ggservice.fragment.AddServiceExperienceFragmentGGS();
    }

    private void initializeView(View view) {
        ConstantsGGS.isBack = true;
        experienceLayout = (LinearLayout) view.findViewById(R.id.experienceLayout);
        skillLayout = (LinearLayout) view.findViewById(R.id.skill_layout);
        tagLayout = (LinearLayout) view.findViewById(R.id.tag_layout);
        skillTv = (TextView) view.findViewById(R.id.skill_text);
        tagTv = (TextView) view.findViewById(R.id.tag_text);
        experienceTv = (TextView) view.findViewById(R.id.experience_tv);
        rootLayout = (RelativeLayout) view.findViewById(R.id.root_layout_optional);

        experienceTv.setText(UtilsGGS.getGgService(context).getExperience());

        audioCallSwitch = (ThemeSwitchGGS) view.findViewById(R.id.audio_call_swicth);
        videoCallSwitch = (ThemeSwitchGGS) view.findViewById(R.id.video_call_switch);
        cellularCallSwitch = (ThemeSwitchGGS) view.findViewById(R.id.cellular_call_switch);
        chatSwitch = (ThemeSwitchGGS) view.findViewById(R.id.chat_switch);


        fragmentContainer = (LinearLayout) view.findViewById(R.id.container_for_bottom_view);
        appBarLayout= (FrameLayout) getActivity().findViewById(R.id.app_bar_layout_frame_layout);
        appBarLayout.setBackgroundColor(ContextCompat.getColor(getContext(),android.R.color.transparent));
        ViewCompat.setElevation(appBarLayout,0f);

        initListeners();

    }

    private void initListeners() {
        experienceLayout.setOnClickListener(this);
        skillLayout.setOnClickListener(this);
        tagLayout.setOnClickListener(this);

        audioCallSwitch.setOnCheckedChangeListener(this);
        videoCallSwitch.setOnCheckedChangeListener(this);
        cellularCallSwitch.setOnCheckedChangeListener(this);
        chatSwitch.setOnCheckedChangeListener(this);
        postButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.experienceLayout) {
            showExperienceDialog();
        } else if (viewId == R.id.skill_layout) {
            showSkillDialog();
        } else if (viewId == R.id.tag_layout) {
            showTagDialog();
        } else if (viewId == R.id.post_upper) {

            if (UtilsGGS.getGgServiceEditFlag(getContext())) {
                    callUpdateService();
                } else {
                    callPostService();
                }

        }
    }

    private void callUpdateService() {
        if (NetworkUtilGGS.isInternetAvailable(getContext())) {
            new UpdateServiceAsynTask(getActivity(), UtilsGGS.getGgService(getContext()), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            dialogManager.showDialog(context.getString(R.string.failed_to_load_data_ggs), context.getString(R.string.text_no_internet_connection_ggs), false);

        }
    }

    private void callPostService() {
        if (NetworkUtilGGS.isInternetAvailable(getContext())) {
            MixpanelUtils mixpanelUtils = new MixpanelUtils(getActivity());
            mixpanelUtils.mpTrackKeyword("ServiceCategory", ConstantGGS.MOST_USED_CATEGORY, UtilsGGS.getGgService(getContext()).getCategory_name());
//            UtilsGGS.getGgService(getContext()).getCategory_name();
            new CreatePostServiceTask(getActivity(), UtilsGGS.getGgService(getContext()), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            dialogManager.showDialog(context.getString(R.string.failed_to_load_data_ggs), context.getString(R.string.text_no_internet_connection_ggs), false);

        }
    }


    private void showSkillDialog() {
        com.gagagugu.ggservice.fragment.CustomBottomSheetGgs skillFragment = new com.gagagugu.ggservice.fragment.CustomBottomSheetGgs();

        Bundle skillViewBundle = new Bundle();
        skillViewBundle.putSerializable("tag_list", UtilsGGS.getParentActivity(context).skillList);
        // ArrayList<TagGgs> selectedSkills = new ArrayList<>(UtilsGGS.getGgService(context).getSkills());
        selectedSkillList = new ArrayList<>(UtilsGGS.getGgService(context).getSkills());
        skillViewBundle.putSerializable("selected_tag_list", selectedSkillList);
        skillViewBundle.putString("tag_type", "skill");
        skillFragment.setTargetFragment(this, SKILL_REQUEST_CODE);
        if (skillFragment.getArguments() != null) {
            skillFragment.getArguments().putBundle("bundle", skillViewBundle);

        } else {
            skillFragment.setArguments(skillViewBundle);
        }
        skillFragment.show(getFragmentManager(), "skill_fragment");


    }

    private void showTagDialog() {
        com.gagagugu.ggservice.fragment.CustomBottomSheetGgs tagFragment = new com.gagagugu.ggservice.fragment.CustomBottomSheetGgs();
        Bundle tagViewBundle = new Bundle();
        tagViewBundle.putSerializable("tag_list", UtilsGGS.getParentActivity(getContext()).tagLists);
        selectedTagList = new ArrayList<>(UtilsGGS.getGgService(context).getTags());
        tagViewBundle.putSerializable("selected_tag_list", selectedTagList);
        tagViewBundle.putString("tag_type", "tag");
        tagFragment.setTargetFragment(this, TAG_REQUEST_CODE);
        if (tagFragment.getArguments() != null) {
            tagFragment.getArguments().putBundle("bundle", tagViewBundle);
        } else {
            tagFragment.setArguments(tagViewBundle);
        }
        tagFragment.show(getFragmentManager(), "tag_dailog");

        /*getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_up_ggs, R.anim.slide_out_up_ggs,R.anim.slide_in_down_ggs,R.anim.slide_out_down_ggs)
                .replace(R.id.container_for_bottom_view, tagFragment).addToBackStack("tag_dialog").commit();
*/

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SKILL_REQUEST_CODE:
                Log.d("skillrequest", "skilerwa");
                UtilsGGS.getGgService(getContext()).setSkills(selectedSkillList);
                setTagsTextInTv(selectedSkillList, skillTv);
                break;

            case TAG_REQUEST_CODE:
                UtilsGGS.getGgService(getContext()).setTags(selectedTagList);
                setTagsTextInTv(selectedTagList, tagTv);
                break;
            case EXPERIENCE_REQUEST_CODE:
                String experienceText = data.getStringExtra("experience_text");

                UtilsGGS.getGgService(getContext()).setExperience(experienceText);
                experienceTv.setText(experienceText);
                break;


        }
    }

    private void showExperienceDialog() {
        addServiceExperienceFragment.setTargetFragment(this, EXPERIENCE_REQUEST_CODE);
        Bundle bundle = new Bundle();
        bundle.putString("experience_text", UtilsGGS.getGgService(getContext()).getExperience());
        addServiceExperienceFragment.setArguments(bundle);
        addServiceExperienceFragment.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "");

    }

/*    @Override
    public void onResponseTag(ArrayList<TagGgs> tagList) {
        Log.d("taglistresponse", String.valueOf(tagList.size()));
        getParentActivity().tagLists = tagList;
    }

    @Override
    public void onResponseSkill(ArrayList<TagGgs> skillList) {
        Log.d("taglistresponse", String.valueOf(skillList.size()));
        getParentActivity().skillList = skillList;

    }*/


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView == audioCallSwitch) {
            if (isChecked) {
                UtilsGGS.getGgService(context).getContact_preference().add(ConstantsGGS.AUDIO_CALL);
            } else {
                UtilsGGS.getGgService(context).getContact_preference().remove(ConstantsGGS.AUDIO_CALL);

            }

        } else if (buttonView == videoCallSwitch) {
            if (isChecked) {
                UtilsGGS.getGgService(context).getContact_preference().add(ConstantsGGS.VIDEO_CALL);
            } else {
                UtilsGGS.getGgService(context).getContact_preference().remove(ConstantsGGS.VIDEO_CALL);

            }
            // UtilsGGS.getGgService(context).getContact_preference().add(ConstantsGGS.VIDEO_CALL);
        } else if (buttonView == cellularCallSwitch) {
            if (isChecked) {
                UtilsGGS.getGgService(context).getContact_preference().add(ConstantsGGS.CELLULAR_CALL);
            } else {
                UtilsGGS.getGgService(context).getContact_preference().remove(ConstantsGGS.CELLULAR_CALL);

            }
        } else if (buttonView == chatSwitch) {
            UtilsGGS.getGgService(context).getContact_preference().add(ConstantsGGS.CHAT);
            chatSwitch.setChecked(true);
        }


    }


    /**
     * called when creating a new post is successful
     */
    @Override
    public void onSuccess(String createdId) {
        if (UtilsGGS.getGgServiceEditFlag(getContext())) {
            Intent intent = new Intent();
            intent.putExtra("id", String.valueOf(UtilsGGS.getGgService(getContext()).getId()));
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();

        } else {
            Intent intent = new Intent();
            intent.putExtra("created_id",createdId);
            intent.putExtra("created_service",UtilsGGS.getGgService(getActivity()));
            getActivity().setResult(Activity.RESULT_OK,intent);
            getActivity().finish();
        }
    }
}
