package com.gagagugu.ggservice.fragment;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tagview.TagGgs;
import com.example.tagview.TagLayoutContainer;
import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.adapter.SelectedTagViewAdapterGGS;
import com.gagagugu.ggservice.asynctasks.GetSkillQueryTaskGgs;
import com.gagagugu.ggservice.asynctasks.GetTagQueryTaskGgs;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.interfaces.EditTextChangeTextListener;
import com.gagagugu.ggservice.interfaces.GetTagQueryCallback;
import com.gagagugu.ggservice.utils.EditTextTextWatcherGgs;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.SearchTextViewGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;

import java.util.ArrayList;

/**
 * A simple subclass.
 */
public class CrateServiceTagViewDialogGGS extends BottomSheetDialogFragment implements TagLayoutContainer.OnTagClickListener, View.OnClickListener, GetTagQueryCallback {
    LinearLayout searchResultTagContainer;
    String tag_type, queryvalue;
    SelectedTagViewAdapterGGS selectedTagViewAdapter;
    RecyclerView selectedTagsList;
    TextView noSkillSelectedText, doneText, addTagText, tv_dummy;
    int normalFontColor = Color.parseColor("#797979");
    SearchTextViewGGS searchView;
    LinearLayout tagLayout, parentView, add_dummy_text;
    long idle_min = 1000; // 4 seconds after user stops typing
    long last_text_edit = 0;
    Handler h = new Handler();
    boolean already_queried = false;
    Runnable input_finish_checker;
    BottomSheetBehavior bottomSheetBehavior;
    View rootView;
    private TagLayoutContainer tagLayoutContainer, searchTagLayoutContainer;
    private ArrayList<TagGgs> tagGgses, selectedTags, searchResultTagGgses;
    private boolean isTag = false;
    private ProfileColorsGGS theme;



    BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };


    public CrateServiceTagViewDialogGGS() {

    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        rootView = View.inflate(getContext(), R.layout.fragment_create_service_tag_view_dialog_ggs, null);
        theme = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());

        setCancelable(false);

        initializeView(rootView);
        dialog.setContentView(rootView);
        bottomSheetBehavior = BottomSheetBehavior.from((View) rootView.getParent());

        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);

        setDialog(rootView);


        return dialog;
    }


    private void setDialog(View contentView) {
        //getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        View parent = (View) contentView.getParent();
        parent.setFitsSystemWindows(true);
        //BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(parent);
        //contentView.measure(0, 0);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        // bottomSheetBehavior.setPeekHeight(screenHeight);

        if (params.getBehavior() instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) params.getBehavior()).setBottomSheetCallback(bottomSheetCallback);
        }

        params.height = screenHeight;
        parent.setLayoutParams(params);
    }

    private void initializeView(View view) {
        tagLayoutContainer = (TagLayoutContainer) view.findViewById(R.id.tagLayoutContainer);
        searchResultTagContainer = (LinearLayout) view.findViewById(R.id.search_result_tag);
        tagLayout = (LinearLayout) view.findViewById(R.id.tag_layout);
        parentView = (LinearLayout) view.findViewById(R.id.parent_view);
        Bundle bundle = getArguments();
        selectedTagsList = (RecyclerView) view.findViewById(R.id.selected_tags);
        noSkillSelectedText = (TextView) view.findViewById(R.id.no_skill_selected_text);
        doneText = (TextView) view.findViewById(R.id.done);
        addTagText = (TextView) view.findViewById(R.id.add_tag_text);
        searchView = (SearchTextViewGGS) view.findViewById(R.id.search_layoyt);
        add_dummy_text = (LinearLayout) view.findViewById(R.id.add_dummy_text);
        tv_dummy = (TextView) view.findViewById(R.id.tv_dummy);

        doneText.setOnClickListener(this);

        initializeList(bundle);
        initializeSelectedTagListAdapter();


        setSearchViewEditTextWatcher();

    }

    private void setSearchViewEditTextWatcher() {

        final EditText searchEditText = searchView.getSearchEditText();
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String queryText = searchEditText.getText().toString();
                    if (!queryText.isEmpty()) {
                        callApi(queryText);
                    }
                    return true;
                }

                return false;
            }
        });


        searchView.setSearchViewOnFocusListner(new SearchTextViewGGS.SearchViewOnFocusListner() {
            @Override
            public void onSearviewFocusListner(View view, boolean hasFocus) {
                if (hasFocus) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        searchEditText.addTextChangedListener(new EditTextTextWatcherGgs(new EditTextChangeTextListener() {
            @Override
            public void onChange(final String s) {
                last_text_edit = System.currentTimeMillis();

                if (s.length() != 0) {
                    input_finish_checker = new Runnable() {

                        public void run() {
                            //  Log.d("stopppeeddddddddd", "new runnable running");

                            if (System.currentTimeMillis() > (last_text_edit + idle_min - 10)) {
                                //    Log.d("stopppeeddddddddd", "idle");

                                if (!already_queried) { // don't do this stuff twice.
                                    already_queried = true;
                                    //      Log.d("stopppeeddddddddd", "stopped");
                                    //Log.d("saskljfd", "Query Text: " + s);
                                    callApi(s);

                                    passperameter(s);

                                } else {
                                    //    Log.d("stopppeeddddddddd", "notquerued");

                                }
                            } else {
                                //Log.d("stopppeeddddddddd", "notidle"+System.currentTimeMillis() +",."+ (last_text_edit + idle_min));


                            }
                        }
                    };
                    h.postDelayed(input_finish_checker, idle_min);
                } else {
                    searchResultTagContainer.setVisibility(View.GONE);
                    tagLayout.setVisibility(View.VISIBLE);
                    Log.d("selectedtagssize", selectedTags.size() + "");
                    setTagsSelected(selectedTags, tagLayoutContainer, tagGgses);
                }
            }
        }));


    }

    private void callApi(String queryText) {
        if (isTag) {
            new GetTagQueryTaskGgs(com.gagagugu.ggservice.fragment.CrateServiceTagViewDialogGGS.this, queryText, getContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else {
            new GetSkillQueryTaskGgs(com.gagagugu.ggservice.fragment.CrateServiceTagViewDialogGGS.this, queryText, getContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

    }


    private void initializeList(Bundle bundle) {
        tag_type = bundle.getString("tag_type");
        setStringsInView(tag_type);
        tagGgses = (ArrayList<TagGgs>) bundle.getSerializable("tag_list");
        tagLayoutContainer.addTags(getActivity(), tagGgses);
        tagLayoutContainer.setOnTagClickListener(this);
        selectedTags = (ArrayList<TagGgs>) bundle.getSerializable("selected_tag_list");
        setTagsSelected(selectedTags, tagLayoutContainer, tagGgses);
    }


    private void setStringsInView(String tag_type) {
        String tagName;
        switch (tag_type) {
            case "tag":
                tagName = "tags";
                setTagNameInTv(tagName);
                isTag = true;
                break;
            case "skill":
                tagName = "skills";
                setTagNameInTv(tagName);
                isTag = false;
                break;
        }

    }

    private void setTagNameInTv(String tagName) {
        String addTag = String.format(getActivity().getResources().getString(R.string.add_skill_text_ggs), tagName);
        addTagText.setText(addTag);

        String nothingSelectedText = String.format(getActivity().getResources().getString(R.string.no_skill_selected_ggs), tagName);
        noSkillSelectedText.setText(nothingSelectedText);
    }


    public void passperameter(String s) {

        queryvalue = s;
    }

    private void setTagsSelected(ArrayList<TagGgs> selectedTags, TagLayoutContainer container, ArrayList<TagGgs> tagGgses) {
        if (selectedTags.size() == 0) {
            noSkillSelectedText.setVisibility(View.VISIBLE);
        } else {
            noSkillSelectedText.setVisibility(View.GONE);

        }
        for (TagGgs s : selectedTags) {
            int index = tagGgses.indexOf(s);
            if (index != -1) {
                View view = container.getChildAt(index);
                TextView tagText = (TextView) view.findViewById(R.id.tag_text);
                view.setBackgroundResource(R.drawable.selected_tag_back);
                tagText.setTextColor(Color.WHITE);
            }

        }

    }


    private void initializeSelectedTagListAdapter() {

        selectedTagViewAdapter = new SelectedTagViewAdapterGGS(selectedTags, getContext(), theme.getColorCodeLight(), new SelectedTagViewAdapterGGS.OnRemovedTag() {
            @Override
            public void onRemoved(TagGgs text) {
                int index = tagGgses.indexOf(text);
                if (index != -1) {
                    View view = tagLayoutContainer.getChildAt(index);
                    view.setBackgroundResource(R.drawable.tag_pressed);
                    ((TextView) view.findViewById(R.id.tag_text)).setTextColor(normalFontColor);

                }

                if (searchResultTagGgses != null) {
                    int selectedListIndex = searchResultTagGgses.indexOf(text);
                    if (selectedListIndex != -1) {
                        View view = searchTagLayoutContainer.getChildAt(selectedListIndex);
                        view.setBackgroundResource(R.drawable.tag_pressed);
                        ((TextView) view.findViewById(R.id.tag_text)).setTextColor(normalFontColor);
                    }
                }

                if (selectedTags.size() == 0) {
                    noSkillSelectedText.setVisibility(View.VISIBLE);
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        selectedTagsList.setLayoutManager(mLayoutManager);
        selectedTagsList.setItemAnimator(new DefaultItemAnimator());
        selectedTagsList.setAdapter(selectedTagViewAdapter);
    }


    @Override
    public void onTagClick(int position) {
        onClickTag(tagGgses, position, tagLayoutContainer);

    }


    private void onClickTag(ArrayList<TagGgs> tags, int position, TagLayoutContainer container) {
        TagGgs text = tags.get(position);
        View view = container.getChildAt(position);
        TextView tagText = (TextView) view.findViewById(R.id.tag_text);
        if (selectedTags.contains(text)) {
            selectedTagViewAdapter.removeData(text);

            Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.tag_pressed);
            drawable.setColorFilter(theme.getColorCodeLight(), PorterDuff.Mode.SRC);
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackgroundDrawable(drawable);

            } else {
                view.setBackground(drawable);

            }

            tagText.setTextColor(normalFontColor);

            if (selectedTags.size() == 0) {
                noSkillSelectedText.setVisibility(View.VISIBLE);
            }

            if (container != tagLayoutContainer) {
                int newPos = tagGgses.indexOf(text);
                if (newPos != -1) {
                    View newView = tagLayoutContainer.getChildAt(newPos);
                    newView.setBackgroundResource(R.drawable.tag_pressed);
                    ((TextView) newView.findViewById(R.id.tag_text)).setTextColor(normalFontColor);
                }

            }


        } else {
            selectedTagViewAdapter.addData(tags.get(position));
            selectedTagsList.scrollToPosition(selectedTags.size() - 1);
            view.setBackgroundResource(R.drawable.selected_tag_back);
            tagText.setTextColor(Color.WHITE);
            noSkillSelectedText.setVisibility(View.GONE);


        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.done) {
            dismiss();
            getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);

        }
    }

    @Override
    public void onTagQueryResponse(ArrayList<TagGgs> tagList) {
        Log.d("saskljfd", tagList.size() + "");
        showSearchResult(tagList);

        already_queried = false;
        h.removeCallbacks(input_finish_checker);

        if (tagList.size() == 0) {
            tv_dummy.setText(Html.fromHtml("&ldquo;" + queryvalue + "&rdquo;"));
            add_dummy_text.setVisibility(View.VISIBLE);
        }

    }

    private void showSearchResult(final ArrayList<TagGgs> tagList) {
        searchResultTagGgses = tagList;
        searchResultTagContainer.setVisibility(View.VISIBLE);
        tagLayout.setVisibility(View.GONE);

        searchResultTagContainer.removeAllViews();
        searchTagLayoutContainer = new TagLayoutContainer(getActivity());
        searchTagLayoutContainer.addTags(getActivity(), tagList);

        searchTagLayoutContainer.setOnTagClickListener(new TagLayoutContainer.OnTagClickListener() {
            @Override
            public void onTagClick(int position) {
                onClickTag(tagList, position, searchTagLayoutContainer);
            }
        });
        setTagsSelected(selectedTags, searchTagLayoutContainer, tagList);


        searchResultTagContainer.addView(searchTagLayoutContainer);


    }
}
