package com.gagagugu.ggservice.fragment;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tagview.TagGgs;
import com.example.tagview.TagLayoutContainer;
import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.adapter.SelectedTagViewAdapterGGS;
import com.gagagugu.ggservice.asynctasks.GetFeatureQueryTaskGGS;
import com.gagagugu.ggservice.asynctasks.GetSkillQueryTaskGgs;
import com.gagagugu.ggservice.asynctasks.GetTagQueryTaskGgs;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.interfaces.EditTextChangeTextListener;
import com.gagagugu.ggservice.interfaces.GetTagQueryCallback;
import com.gagagugu.ggservice.utils.EditTextTextWatcherGgs;
import com.gagagugu.ggservice.utils.NetworkUtilGGS;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.SearchTextViewGGS;
import com.gagagugu.ggservice.utils.SearchViewSystemHelperGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;
import com.gagagugu.ggservice.view.activity.CreateServicePostActivityGGS;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomBottomSheetGgs extends DialogFragment implements TagLayoutContainer.OnTagClickListener, View.OnClickListener, GetTagQueryCallback, NestedScrollView.OnScrollChangeListener {

    LinearLayout searchResultTagContainer;
    String tag_type, queryvalue;
    SelectedTagViewAdapterGGS selectedTagViewAdapter;
    RecyclerView selectedTagsList;
    TextView noSkillSelectedText, addTagText, tv_dummy, add_tv_item;
    int normalFontColor = Color.parseColor("#797979");
    SearchTextViewGGS searchView;
    LinearLayout tagLayout;
    RelativeLayout parentView;
    CardView add_dummy_text;
    String countOverflowValidationMessage, textLengthValidationMessage;
    long idle_min = 500; // 4 seconds after user stops typing
    long last_text_edit = 0;
    Handler h = new Handler();
    boolean already_queried = false;
    Runnable input_finish_checker;
    LinearLayout transparentLayout;
    Context context;
    int maxAllowedSelectionCount;
    private TagLayoutContainer tagLayoutContainer, searchTagLayoutContainer;
    private ArrayList<TagGgs> tagGgses, selectedTags, searchResultTagGgses;
    private NestedScrollView tagNestedScrollView;
    private TextView doneButton;
    private LinearLayout bottomLayout;
    private ProfileColorsGGS theme;
    private TextView currentCount, totalCount;
    private GetTagQueryTaskGgs getTagQueryTaskGgs;
    private GetSkillQueryTaskGgs getSkillQueryTaskGgs;
    private GetFeatureQueryTaskGGS getFeatureQueryTaskGgs;

    public CustomBottomSheetGgs() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.custom_bottom_sheet_ggs_layout, container, false);
        theme = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        initializeView(view);

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

    private void initializeView(View view) {
        tagLayoutContainer = (TagLayoutContainer) view.findViewById(R.id.tagLayoutContainer);
        searchResultTagContainer = (LinearLayout) view.findViewById(R.id.search_result_tag);
        tagLayout = (LinearLayout) view.findViewById(R.id.tag_layout);
        parentView = (RelativeLayout) view.findViewById(R.id.parent_view);
        selectedTagsList = (RecyclerView) view.findViewById(R.id.selected_tags);
        noSkillSelectedText = (TextView) view.findViewById(R.id.no_skill_selected_text);
        doneButton = (TextView) view.findViewById(R.id.done);
        addTagText = (TextView) view.findViewById(R.id.add_tag_text);
        searchView = (SearchTextViewGGS) view.findViewById(R.id.search_layoyt);
        transparentLayout = (LinearLayout) view.findViewById(R.id.transparent);
        doneButton.setOnClickListener(this);
        parentView.setOnClickListener(this);
        currentCount = (TextView) view.findViewById(R.id.current_count);
        totalCount = (TextView) view.findViewById(R.id.total_count);
        tv_dummy = (TextView) view.findViewById(R.id.tv_dummy);
        add_tv_item = (TextView) view.findViewById(R.id.add_tv_item);
        tv_dummy.setTextColor(theme.getColorCodeLight());
        add_tv_item.setTextColor(theme.getColorCodeLight());

        bottomLayout = (LinearLayout) view.findViewById(R.id.id_layout_bottom);
        add_dummy_text = (CardView) view.findViewById(R.id.add_dummy_text);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            // only for LOLLIPOP and older versions
            cardVieSetupForLowerDevice();
        }


        add_dummy_text.setOnClickListener(this);

        tagNestedScrollView = (NestedScrollView) view.findViewById(R.id.tag_nested_scroll_view);
        tagNestedScrollView.setOnScrollChangeListener(this);

        doneButton.setTextColor(theme.getColorCodeLight());
        Bundle bundle = getBundle();
        initializeList(bundle);
        initializeSelectedTagListAdapter();


        setSearchViewEditTextWatcher();

    }

    private void cardVieSetupForLowerDevice() {
        add_dummy_text.setPreventCornerOverlap(false);
        add_dummy_text.setUseCompatPadding(true);
        add_dummy_text.setMaxCardElevation(0);


    }

    private Bundle getBundle() {
        if (getArguments().getBundle("bundle") != null) return getArguments().getBundle("bundle");

        return getArguments();
    }

    private void setSearchViewEditTextWatcher() {

        final EditText searchEditText = searchView.getSearchEditText();
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String queryText = searchEditText.getText().toString().trim();
                    if (!queryText.isEmpty()) {
                        callApi(queryText);
                    }
                    return true;
                }

                return false;
            }
        });

        transparentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        searchView.setSearchViewOnFocusListner(new SearchTextViewGGS.SearchViewOnFocusListner() {
            @Override
            public void onSearviewFocusListner(View view, boolean hasFocus) {

                if (hasFocus) {

                    transparentLayout.setVisibility(View.GONE);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                    bottomLayout.setLayoutParams(lp);


                } else {

                    transparentLayout.setVisibility(View.VISIBLE);

                    LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    lpp.weight = 70;
                    transparentLayout.setLayoutParams(lpp);

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    lp.weight = 30;
                    bottomLayout.setLayoutParams(lp);

                }


            }
        });

        searchEditText.addTextChangedListener(new EditTextTextWatcherGgs(new EditTextChangeTextListener() {
            @Override
            public void onChange(final String s) {
                add_dummy_text.setVisibility(View.GONE);
                last_text_edit = System.currentTimeMillis();

                if (s.trim().length() != 0) {
                    input_finish_checker = new Runnable() {

                        public void run() {

                            if (System.currentTimeMillis() > (last_text_edit + idle_min - 10)) {
                                Log.d("callingapibottomsheet", "queried" + String.valueOf(already_queried));
                                passPerameter(s.trim());
                                if (!already_queried) {
                                    already_queried = true;
                                    ArrayList<TagGgs> searchResultTag = searchInLocalList(s.trim());
                                    if (searchResultTag.size() != 0) {
                                        showSearchResult(searchResultTag);
                                        changeVisibilityOfAddDummyLayout(searchResultTag, s);
                                    } else {
                                        callApi(s.trim());
                                    }

                                }
                            }
                        }

                    };
                    h.postDelayed(input_finish_checker, idle_min);
                } else {
                    searchResultTagContainer.setVisibility(View.GONE);
                    tagLayout.setVisibility(View.VISIBLE);
                    setTagsSelected(selectedTags, tagLayoutContainer, tagGgses);
                    add_dummy_text.setVisibility(View.GONE);
                    already_queried = false;
                    h.removeCallbacks(input_finish_checker);
                }
            }
        }));


    }

    private void changeVisibilityOfAddDummyLayout(ArrayList<TagGgs> searchResultTag, String s) {
        TagGgs tagGgs = new TagGgs();
        tagGgs.setName(s.trim());
        if (!searchResultTag.contains(tagGgs)) {
            tv_dummy.setText(Html.fromHtml("&ldquo;" + s.trim() + "&rdquo;"));
            add_dummy_text.setVisibility(View.VISIBLE);
        } else {
            add_dummy_text.setVisibility(View.GONE);

        }
    }


    private ArrayList<TagGgs> searchInLocalList(String s) {
        ArrayList<TagGgs> list = new ArrayList<>();
        for (TagGgs tag : tagGgses) {
            if (tag.getName().toLowerCase().contains(s.toLowerCase())) {
                list.add(tag);
            }
        }
        return list;
    }

    public void passPerameter(String s) {

        queryvalue = s;
    }


    private void callApi(String queryText) {
        passPerameter(queryText);
        Log.d("callingapibottomsheet", queryText);
        if (NetworkUtilGGS.isInternetAvailable(context)) {

            switch (tag_type) {
                case "tag":
                    getTagQueryTaskGgs = new GetTagQueryTaskGgs(com.gagagugu.ggservice.fragment.CustomBottomSheetGgs.this, queryText, getActivity());
                    getTagQueryTaskGgs.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                case "skill":
                    getSkillQueryTaskGgs = new GetSkillQueryTaskGgs(com.gagagugu.ggservice.fragment.CustomBottomSheetGgs.this, queryText, getActivity());
                    getSkillQueryTaskGgs.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                case "feature":
                    getFeatureQueryTaskGgs = new GetFeatureQueryTaskGGS(com.gagagugu.ggservice.fragment.CustomBottomSheetGgs.this, queryText, getActivity());
                    getFeatureQueryTaskGgs.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    break;
            }


        } else {
            Toast.makeText(context, context.getString(R.string.text_no_internet_connection_ggs), Toast.LENGTH_SHORT).show();
        }

    }

    private AsyncTask getCurrentAsyncTask() {
        switch (tag_type) {
            case "tag":
                return getTagQueryTaskGgs;
            case "skill":
                return getSkillQueryTaskGgs;
            case "feature":
                return getFeatureQueryTaskGgs;

        }
        return null;
    }


    private void initializeList(Bundle bundle) {
        tag_type = bundle.getString("tag_type");
        setStringsInView(tag_type);

        tagGgses = (ArrayList<TagGgs>) bundle.getSerializable("tag_list");
        tagLayoutContainer.addTags(getActivity(), tagGgses);
        tagLayoutContainer.setOnTagClickListener(this);
        selectedTags = (ArrayList<TagGgs>) bundle.getSerializable("selected_tag_list");
        currentCount.setText(String.valueOf(selectedTags.size()));
        setTagsSelected(selectedTags, tagLayoutContainer, tagGgses);


    }


    private void setStringsInView(String tag_type) {
        String tagName;
        switch (tag_type) {
            case "tag":
                tagName = "Tags";
                setTagNameInTv(tagName);
                textLengthValidationMessage = getString(R.string.max_tag_length_ggs);
                if (getActivity().getClass().getCanonicalName().equals(CreateServicePostActivityGGS.class.getCanonicalName())) {
                    maxAllowedSelectionCount = 10;

                } else {
                    maxAllowedSelectionCount = 20;

                }
                countOverflowValidationMessage = getString(R.string.max_tag_limit_ggs) + maxAllowedSelectionCount;

                //  isTag = true;
                break;
            case "skill":
                tagName = "Skills";
                setTagNameInTv(tagName);
                textLengthValidationMessage = getString(R.string.max_skill_length_ggs);
                // isTag = false;
                maxAllowedSelectionCount = 10;
                countOverflowValidationMessage = getString(R.string.max_skill_limit_ggs) + maxAllowedSelectionCount;

                break;

            case "feature":
                tagName = "Feature";
                setTagNameInTv(tagName);
                textLengthValidationMessage = getString(R.string.max_feature_length_ggs);
                //  isTag = false;
                maxAllowedSelectionCount = 30;
                countOverflowValidationMessage = getString(R.string.max_feature_limit_ggs) + maxAllowedSelectionCount;

                break;
        }

        Log.d("activityname", getActivity().getClass().getCanonicalName() + "," + CreateServicePostActivityGGS.class.getCanonicalName());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/").append(maxAllowedSelectionCount).append(")");
        totalCount.setText(stringBuilder.toString());

    }

    private void setTagNameInTv(String tagName) {
        String addTag = String.format(getActivity().getResources().getString(R.string.add_skill_text_ggs), tagName);
        addTagText.setText(addTag);

        String nothingSelectedText = String.format(getActivity().getResources().getString(R.string.no_skill_selected_ggs), tagName);
        noSkillSelectedText.setText(nothingSelectedText);
    }

    private void setTagsSelected(ArrayList<TagGgs> selectedTags, TagLayoutContainer container, ArrayList<TagGgs> tagGgses) {
        if (selectedTags.size() == 0) {
            noSkillSelectedText.setVisibility(View.VISIBLE);
        } else {
            noSkillSelectedText.setVisibility(View.GONE);

        }
        for (TagGgs s : selectedTags) {
            int index = tagGgses.indexOf(s);
            Log.d("settingselected", s.getName() + " . index: " + index);
            if (index != -1) {
                View view = container.getChildAt(index);
                TextView tagText = (TextView) view.findViewById(R.id.tag_text);
                changeTagsBackgroundColor(view, R.drawable.tag_back, theme.getColorCodeLight(), true);

                tagText.setTextColor(Color.WHITE);
            }/*else {
                View view = container.getChildAt(index);
                TextView tagText = (TextView) view.findViewById(R.id.tag_text);
                view.setBackgroundResource(R.drawable.tag_pressed);
                tagText.setTextColor(normalFontColor);

            }*/

        }

    }


    private void initializeSelectedTagListAdapter() {

        selectedTagViewAdapter = new SelectedTagViewAdapterGGS(selectedTags, getContext(), theme.getColorCodeLight(), new SelectedTagViewAdapterGGS.OnRemovedTag() {
            @Override
            public void onRemoved(TagGgs text) {
                int index = tagGgses.indexOf(text);
                if (index != -1) {
                    View view = tagLayoutContainer.getChildAt(index);
                    view.setBackgroundResource(R.drawable.tag_back);
                    ((TextView) view.findViewById(R.id.tag_text)).setTextColor(normalFontColor);

                }

                if (searchResultTagGgses != null) {
                    int selectedListIndex = searchResultTagGgses.indexOf(text);
                    if (selectedListIndex != -1) {
                        View view = searchTagLayoutContainer.getChildAt(selectedListIndex);
                        view.setBackgroundResource(R.drawable.tag_back);
                        ((TextView) view.findViewById(R.id.tag_text)).setTextColor(normalFontColor);
                    }
                }

                if (selectedTags.size() == 0) {
                    noSkillSelectedText.setVisibility(View.VISIBLE);
                }
                currentCount.setText(String.valueOf(selectedTagViewAdapter.getItemCount()));

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
            changeTagsBackgroundColor(view, R.drawable.tag_back, Color.WHITE, false);
            //view.setBackgroundResource(R.drawable.tag_pressed);

            tagText.setTextColor(normalFontColor);

            if (selectedTags.size() == 0) {
                noSkillSelectedText.setVisibility(View.VISIBLE);
            }

            if (container != tagLayoutContainer) {
                int newPos = tagGgses.indexOf(text);
                if (newPos != -1) {
                    View newView = tagLayoutContainer.getChildAt(newPos);
                    newView.setBackgroundResource(R.drawable.tag_back);
                    ((TextView) newView.findViewById(R.id.tag_text)).setTextColor(normalFontColor);
                }

            }


        } else {
            Log.d("itemcount", selectedTagViewAdapter.getItemCount() + "," + maxAllowedSelectionCount);
            if (selectedTagViewAdapter.getItemCount() < maxAllowedSelectionCount) {
                selectedTagViewAdapter.addData(tags.get(position));
                selectedTagsList.scrollToPosition(selectedTags.size() - 1);
                changeTagsBackgroundColor(view, R.drawable.tag_back, theme.getColorCodeLight(), true);
                tagText.setTextColor(Color.WHITE);
                noSkillSelectedText.setVisibility(View.GONE);
//                Log.d("selectedtags", new Gson().toJson(UtilsGGS.getGgService(getActivity()).getSkills()));
            } else {
                Toast.makeText(context, countOverflowValidationMessage, Toast.LENGTH_SHORT).show();
            }


        }
        currentCount.setText(String.valueOf(selectedTagViewAdapter.getItemCount()));
    }

    private void changeTagsBackgroundColor(View view, int tag_pressed, int color, boolean setColorFilter) {
        Drawable drawable = ContextCompat.getDrawable(getActivity(), tag_pressed);

        if (setColorFilter) drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);

        } else {
            view.setBackground(drawable);

        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.done) {

            if (getCurrentAsyncTask() != null && getCurrentAsyncTask().getStatus() == AsyncTask.Status.RUNNING) {
                getCurrentAsyncTask().cancel(true);
            }
            getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);
            dismiss();


        } else if (viewId == R.id.add_dummy_text) {

            if (queryvalue.length() <= 100) {
                if (selectedTagViewAdapter.getItemCount() < maxAllowedSelectionCount) {
                    TagGgs tagGgs = new TagGgs();
                    tagGgs.setName(queryvalue.trim());
                    if (!selectedTags.contains(tagGgs)) {
                        selectedTagViewAdapter.addData(tagGgs);
                        selectedTagsList.scrollToPosition(selectedTags.size() - 1);

                        noSkillSelectedText.setVisibility(View.GONE);

                        add_dummy_text.setVisibility(View.GONE);
                        searchView.getSearchEditText().setText("");
                        SearchViewSystemHelperGGS.hideKeyboard(getContext(), searchView.getSearchEditText());
                        currentCount.setText(String.valueOf(selectedTagViewAdapter.getItemCount()));
                    } else {
                        Toast.makeText(context, getString(R.string.data_already_exist_ggs), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, countOverflowValidationMessage, Toast.LENGTH_SHORT).show();

                }
            } else {

                Toast.makeText(context, textLengthValidationMessage, Toast.LENGTH_SHORT).show();
            }


            /*
            searchView.getSearchEditText().clearFocus();
            */
        } else if (viewId == R.id.parent_view) {
        }
    }

    @Override
    public void onTagQueryResponse(ArrayList<TagGgs> tagList) {
        if (tagList != null) {
            showSearchResult(tagList);
            Log.d("saskljfd", tagList.size() + "");


            changeVisibilityOfAddDummyLayout(tagList, queryvalue);
            if (tagList.size() == 0) {

                /*tv_dummy.setText(Html.fromHtml("&ldquo;" + queryvalue + "&rdquo;"));
                add_dummy_text.setVisibility(View.VISIBLE);*/
                if (getActivity() != null)
                    Toast.makeText(context, getString(R.string.no_search_result_ggs), Toast.LENGTH_SHORT).show();

            } else {
                //  add_dummy_text.setVisibility(View.GONE);

            }
        } else {
            if (getActivity() != null)
                Toast.makeText(context, getString(R.string.no_search_result_ggs), Toast.LENGTH_SHORT).show();

        }

        //  already_queried = false;
        // h.removeCallbacks(input_finish_checker);

    }

    private void showSearchResult(final ArrayList<TagGgs> tagList) {

        searchResultTagGgses = tagList;
        searchResultTagContainer.setVisibility(View.VISIBLE);
        tagLayout.setVisibility(View.GONE);

        searchResultTagContainer.removeAllViews();
        if (getActivity() != null) {
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

            already_queried = false;
            h.removeCallbacks(input_finish_checker);
        }

    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

        if (v == tagNestedScrollView) {

            SearchViewSystemHelperGGS.hideKeyboard(getContext(), searchView.getSearchEditText());

            int diff = (tagLayout.getHeight() - (v.getHeight() + scrollY));
            Log.d("scroll", "scroll x: " + scrollX + " scrolly: " + scrollY + " oldscroll X: " + oldScrollX + " oldscrollY: " + oldScrollY + " diff " + diff + " bottom: " +
                    v.getBottom() + " height " + v.getHeight());


            if (diff == 0) {
                Log.v("bottom", "reached");
            }

        }

    }
}
