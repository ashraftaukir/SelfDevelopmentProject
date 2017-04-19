package com.gagagugu.ggservice.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.adapter.SelectBrandRecycleViewAdapter;
import com.gagagugu.ggservice.asynctasks.CreateModelTask;
import com.gagagugu.ggservice.asynctasks.CreatePostModelSearchTask;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.interfaces.EditTextChangeTextListener;
import com.gagagugu.ggservice.interfaces.GetModelCallback;
import com.gagagugu.ggservice.interfaces.GetModelSearchQueryCllback;
import com.gagagugu.ggservice.models.SuggestionDataGGS;
import com.gagagugu.ggservice.utils.CustomFontTextViewGGS;
import com.gagagugu.ggservice.utils.DialogManager;
import com.gagagugu.ggservice.utils.EditTextTextWatcherGgs;
import com.gagagugu.ggservice.utils.NetworkUtilGGS;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.RecyclerTouchListenerGGS;
import com.gagagugu.ggservice.utils.SearchTextViewGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;

import java.util.ArrayList;
import java.util.Collections;


public class CreateProductModelFragment extends DialogFragment implements GetModelSearchQueryCllback, GetModelCallback, View.OnClickListener {

    Runnable input_finish_checker;
    long idle_min = 1000;
    long last_text_edit = 0;
    boolean already_queried = false;
    Handler h = new Handler();
    boolean checking = false;
    private ArrayList<SuggestionDataGGS> modelsArrayList;
    private ArrayList<SuggestionDataGGS> adapterArrayList = new ArrayList<>();
    private ProfileColorsGGS profileColorsGGS;
    private RecyclerView brandRecycleView;
    private SelectBrandRecycleViewAdapter selectBrandRecycleViewAdapter;
    private SearchTextViewGGS searchTextViewGGS;
    private TextView tv_dummy;
    private CardView add_dummy_text;
    private RelativeLayout parentLayout;
    private LinearLayout childLayout;
    private String queryvalue;
    private DialogManager dialogManager;
    private CreatePostModelSearchTask createPostModelSearchTask;

    public CreateProductModelFragment() {

    }


    public static com.gagagugu.ggservice.fragment.CreateProductModelFragment newInstance() {
        com.gagagugu.ggservice.fragment.CreateProductModelFragment fragment = new com.gagagugu.ggservice.fragment.CreateProductModelFragment();

        return fragment;
    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.fragment_create_product_brand, null);
        ((CustomFontTextViewGGS) contentView.findViewById(R.id.textView)).setText(getString(R.string.model_ggs));
        profileColorsGGS = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        dialog.setContentView(contentView);
        getData();
        dialogManager = new DialogManager(getActivity());
        initialize(contentView);
        initizeListner();
        searchViewFunctionalityIntegration();

        brandRecycleView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                return false;
            }
        });

    }

    private void searchViewFunctionalityIntegration() {

        searchTextViewGGS.getSearchEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String queryText = searchTextViewGGS.getSearchEditText().getText().toString().trim();
                    if (!queryText.isEmpty()) {
                        passperameter(queryText);
                        callSearchApi(queryText);
                    }
                    return true;
                }
                return false;
            }
        });


        searchTextViewGGS.getSearchEditText().addTextChangedListener(new EditTextTextWatcherGgs(new EditTextChangeTextListener() {
            @Override
            public void onChange(final String s) {
                add_dummy_text.setVisibility(View.GONE);
                last_text_edit = System.currentTimeMillis();

                if (s.trim().length() != 0) {
                    input_finish_checker = new Runnable() {

                        public void run() {
                            add_dummy_text.setVisibility(View.GONE);

                            if (System.currentTimeMillis() > (last_text_edit + idle_min - 10)) {
                                Log.d("callingapibottomsheet", "queried" + String.valueOf(already_queried));
                                if (!already_queried) {
                                    passperameter(s.trim());
                                    already_queried = true;
                                    ArrayList<SuggestionDataGGS> searchResultTag = searchInLocalList(s.trim());
                                    if (searchResultTag.size() != 0) {
                                        showsearchmodel(searchResultTag);
                                        changeVisibiltityOfAddModelView(searchResultTag, s);
                                    } else {

                                        callSearchApi(s.trim());


                                         /*else {
                                            showsearchmodel(searchResultTag);

                                        }*/
                                    }

                                }
                            }
                        }

                    };
                    h.postDelayed(input_finish_checker, idle_min);
                } else {

                    //   changeListItem(modelsArrayList);
                    showsearchmodel(modelsArrayList);
                    add_dummy_text.setVisibility(View.GONE);
                    /*already_queried = false;
                    h.removeCallbacks(input_finish_checker);*/
                }

            }
        }));


    }

    private void changeVisibiltityOfAddModelView(ArrayList<SuggestionDataGGS> searchResultTag, String s) {
        SuggestionDataGGS model = new SuggestionDataGGS();
        model.setName(s.trim());
        if (!searchResultTag.contains(model)) {
            tv_dummy.setText(Html.fromHtml("&ldquo;" + s.trim() + "&rdquo;"));
            add_dummy_text.setVisibility(View.VISIBLE);
        } else {
            add_dummy_text.setVisibility(View.GONE);

        }
    }


    public void passperameter(String s) {

        queryvalue = s;
    }

    private void callSearchApi(String queryText) {

        if(getActivity()!=null) {
            createPostModelSearchTask = new CreatePostModelSearchTask(com.gagagugu.ggservice.fragment.CreateProductModelFragment.this, queryText, getActivity());
            createPostModelSearchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }





    @NonNull
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

    private void getData() {
        if (getArguments() != null) {

            modelsArrayList = (ArrayList<SuggestionDataGGS>) getArguments().getSerializable("model_list");

        }

        if (UtilsGGS.getGgProductEditFlag(getContext())) {
            if (!modelsArrayList.contains(UtilsGGS.getGgProduct(getContext()).getModel()) && UtilsGGS.getGgProduct(getContext()).getModel().getId() >= 0) {
                modelsArrayList.add(0, UtilsGGS.getGgProduct(getContext()).getModel());
            }
        }
    }


    private void initialize(View contentView) {


        add_dummy_text = (CardView) contentView.findViewById(R.id.add_dummy_text);
        tv_dummy = (TextView) contentView.findViewById(R.id.tv_dummy);
        TextView tv_add_item = (TextView) contentView.findViewById(R.id.tv_add_item);
        tv_add_item.setTextColor(profileColorsGGS.getColorCodeLight());
        tv_dummy.setTextColor(profileColorsGGS.getColorCodeLight());
        brandRecycleView = (RecyclerView) contentView.findViewById(R.id.select_brand_recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        brandRecycleView.setLayoutManager(layoutManager);
        this.brandRecycleView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        adapterArrayList.addAll(modelsArrayList);
        Collections.sort(adapterArrayList);
        selectBrandRecycleViewAdapter = new SelectBrandRecycleViewAdapter(this.adapterArrayList, UtilsGGS.getGgProduct(getContext()).getModel().getId(),
                profileColorsGGS.getColorCodeLight());

        brandRecycleView.setAdapter(selectBrandRecycleViewAdapter);
        searchTextViewGGS = (SearchTextViewGGS) contentView.findViewById(R.id.search_edit_text);
        parentLayout = (RelativeLayout) contentView.findViewById(R.id.parent_layout);

        childLayout = (LinearLayout) contentView.findViewById(R.id.child_layout);
    }

    private void changeListItem(ArrayList<SuggestionDataGGS> suggestionDataGGSes) {
        adapterArrayList.clear();
        adapterArrayList.addAll(suggestionDataGGSes);
        selectBrandRecycleViewAdapter.notifyDataSetChanged();

    }

    private void initizeListner() {

        parentLayout.setOnClickListener(this);
        childLayout.setOnClickListener(this);
        add_dummy_text.setOnClickListener(this);
        brandRecycleView.addOnItemTouchListener(new RecyclerTouchListenerGGS(getActivity(), new RecyclerTouchListenerGGS.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                UtilsGGS.getGgProduct(getContext()).setModel(adapterArrayList.get(position));
                getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);
                dismiss();
            }

        }));

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(createPostModelSearchTask!=null && createPostModelSearchTask.getStatus()== AsyncTask.Status.RUNNING){
            createPostModelSearchTask.cancel(true);
        }
    }

    private ArrayList<SuggestionDataGGS> searchInLocalList(String s) {
        ArrayList<SuggestionDataGGS> list = new ArrayList<>();
        for (SuggestionDataGGS suggestionDataGGS : modelsArrayList) {
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
        } else if (v == add_dummy_text) {

            // dismiss();
            if (queryvalue.length() > 100) {
                Toast.makeText(getContext(), getString(R.string.max_model_length_ggs), Toast.LENGTH_SHORT).show();
            } else {
                if (NetworkUtilGGS.isInternetAvailable(getActivity())) {
                    callCreateModelApi();

                } else {
                    dialogManager.showDialog(getString(R.string.failed_to_load_data_ggs), getString(R.string.text_no_internet_connection_ggs), false);

                }
            }


        } else if (v == childLayout) {
            //donothing
        }


    }

    private void callCreateModelApi() {

        new CreateModelTask(getActivity(), queryvalue,
                UtilsGGS.getGgProduct(getContext()), this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void onModelSearchQueryResponse(ArrayList<SuggestionDataGGS> searchModelsList) {

        showsearchmodel(searchModelsList);

        changeVisibiltityOfAddModelView(searchModelsList,queryvalue);
        if (searchModelsList.size() == 0) {


            if(getContext()!=null){
                Toast.makeText(getContext(), getString(R.string.no_search_result_ggs), Toast.LENGTH_SHORT).show();
            }

        } else {

        }

    }

    private void showsearchmodel(ArrayList<SuggestionDataGGS> searchModelsList) {
        adapterArrayList.clear();
        adapterArrayList.addAll(searchModelsList);
        Collections.sort(adapterArrayList);
        selectBrandRecycleViewAdapter.notifyDataSetChanged();
        already_queried = false;
        h.removeCallbacks(input_finish_checker);
    }


    @Override
    public void onSuccess(SuggestionDataGGS suggestionDataGGS) {
        UtilsGGS.getGgProduct(getActivity()).setModel(suggestionDataGGS);
        dismiss();
        getTargetFragment().onActivityResult(getTargetRequestCode(), 0, null);
        UtilsGGS.getProductParentActivity(getContext()).models.add(suggestionDataGGS);

    }
}
