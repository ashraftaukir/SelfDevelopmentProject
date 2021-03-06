package com.gagagugu.ggservice.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.adapter.RecyclerViewCategoryAdapterGGS;
import com.gagagugu.ggservice.config.ServicePreference;
import com.gagagugu.ggservice.interfaces.CategoryCallBackGGS;
import com.gagagugu.ggservice.interfaces.EditTextChangeTextListener;
import com.gagagugu.ggservice.models.SuggestionDataGGS;
import com.gagagugu.ggservice.utils.EditTextTextWatcherGgs;
import com.gagagugu.ggservice.utils.ProfileColorsGGS;
import com.gagagugu.ggservice.utils.RecyclerTouchListenerGGS;
import com.gagagugu.ggservice.utils.SearchTextViewGGS;
import com.gagagugu.ggservice.utils.UtilsGGS;
import com.gagagugu.ggservice.view.activity.CreateServicePostActivityGGS;

import java.util.ArrayList;


//import android.widget.LinearLayout;


public class CreateServiceCategoryFragmentGGS extends Fragment implements CategoryCallBackGGS {

    View view;
    long idle_min = 500; // 4 seconds after user stops typing
    long last_text_edit = 0;
    Handler h = new Handler();
    boolean already_queried = false;
    Runnable input_finish_checker;
    private RecyclerView mRecyclerView;
    private EditText search;
    private RecyclerViewCategoryAdapterGGS mAdapter;
    private SearchTextViewGGS searchTextView;
    private ArrayList<SuggestionDataGGS> categorys;
    private ArrayList<SuggestionDataGGS> categorysForList;
    // private LinearLayout linear_layout;
    private Bundle categoryBundle;
    private String accessToken;
    View noSearchResultView;
    private OnCategoryFragmentIntractionListner mListener;

    private ProfileColorsGGS theme;
    private FrameLayout appBarLayout;


    public CreateServiceCategoryFragmentGGS() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accessToken = ServicePreference.getInstance(getActivity()).getServiceAccessToken();
        if (UtilsGGS.getGgServiceEditFlag(getContext())) {

            com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.useraddress = "";
            com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.userCountry = "";
            com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS createServiceDetailsGGS = new com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS();
            Bundle bundle = new Bundle();
            bundle.putString("category", UtilsGGS.getGgService(getContext()).getCategory_name());
            createServiceDetailsGGS.setArguments(bundle);
            (getActivity()).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.no_animation_ggs, R.anim.no_animation_ggs, R.anim.slide_back_to_screen_ggs, R.anim.slide_back_ggs)
                    .replace(R.id.fragment_container, createServiceDetailsGGS).addToBackStack("create").commit();

        }

    }



    @Override
    public void onResume() {
        super.onResume();
        mListener.onFragmentInteraction();

    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_service_category_ggs, container, false);
        theme = UtilsGGS.getProfileColor(ServicePreference.getInstance(getContext()).getThemeColor());
        categoryBundle = new Bundle();
        initializeView(view);
        initializeRecycleViewAdapter(view);
        getCategoryList();
        addTextListener();
        getActivity().findViewById(R.id.post_upper).setVisibility(View.GONE);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListenerGGS(getActivity(), new RecyclerTouchListenerGGS.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((getActivity().getWindow().getDecorView().getApplicationWindowToken()), 0);
                com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.useraddress = "";
                com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS.userCountry = "";

                UtilsGGS.getGgService(getContext()).setCategory_id(categorysForList.get(position).getId());
                com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS serviceDetailsGgs = new com.gagagugu.ggservice.fragment.CreateServiceDetailsGGS();

                Bundle bundle = new Bundle();
                bundle.putString("category", categorysForList.get(position).getName());
                serviceDetailsGgs.setArguments(bundle);


                ((AppCompatActivity) getActivity()).getSupportFragmentManager().beginTransaction()

                        .setCustomAnimations(R.anim.slide_in_ggs, R.anim.slide_out_ggs, R.anim.slide_back_to_screen_ggs, R.anim.slide_back_ggs)
                        .replace(R.id.fragment_container, serviceDetailsGgs).addToBackStack("create").commit();


            }

        }));

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                return false;
            }
        });

        return view;

    }




    private void initializeView(View view) {

        searchTextView = (SearchTextViewGGS) view.findViewById(R.id.search_layout);
        search = searchTextView.getSearchEditText();
        noSearchResultView = view.findViewById(R.id.no_search_result_layout);
        appBarLayout= (FrameLayout) getActivity().findViewById(R.id.app_bar_layout_frame_layout);
        appBarLayout.setBackgroundColor(ContextCompat.getColor(getContext(),android.R.color.transparent));
        ViewCompat.setElevation(appBarLayout,0f);

    }


    private void initializeRecycleViewAdapter(View view) {
        categorysForList = getParentActivity().categoriesList;
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new RecyclerViewCategoryAdapterGGS(categorysForList,UtilsGGS.getGgService(getContext()).getCategory_id(),theme);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

    }

    private void changeListItems(ArrayList<SuggestionDataGGS> categorys) {
        categorysForList = categorys;

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new RecyclerViewCategoryAdapterGGS(categorysForList,UtilsGGS.getGgService(getContext()).getCategory_id(),theme);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


        already_queried = false;
        h.removeCallbacks(input_finish_checker);
    }

    public void getCategoryList() {

        categorys = getParentActivity().categoriesList;
        changeListItems(categorys);
    }

    public void addTextListener() {

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    String query = search.getText().toString();
                    if (!query.isEmpty()) {
                        search(query);
                        return true;
                    }
                }

                return false;
            }
        });

        search.addTextChangedListener(new EditTextTextWatcherGgs(new EditTextChangeTextListener() {
            @Override
            public void onChange(final String s) {
                last_text_edit = System.currentTimeMillis();

                if (s.length() != 0) {
                    input_finish_checker = new Runnable() {

                        public void run() {

                            if (System.currentTimeMillis() > (last_text_edit + idle_min - 10)) {

                                if (!already_queried) {
                                    already_queried = true;
                                    search(s);

                                }
                            }
                        }
                    };
                    h.postDelayed(input_finish_checker, idle_min);
                } else {
                    changeListItems(categorys);
                }
            }
        }));

    }

    private void search(String s) {
        ArrayList<SuggestionDataGGS> searchResultTag = searchInLocalList(s);
        if (searchResultTag.size() == 0) {
            // Toast.makeText(getContext(), getString(R.string.no_search_result), Toast.LENGTH_SHORT).show();
            noSearchResultView.setVisibility(View.VISIBLE);
        } else {
            noSearchResultView.setVisibility(View.GONE);

        }
        changeListItems(searchResultTag);

    }

    private ArrayList<SuggestionDataGGS> searchInLocalList(String s) {
        ArrayList<SuggestionDataGGS> list = new ArrayList<>();
        for (SuggestionDataGGS category : categorys) {
            if (category.getName().toLowerCase().contains(s.toLowerCase())) {
                list.add(category);
            }
        }
        return list;
    }


    @Override
    public void onCategoryResponse(ArrayList<SuggestionDataGGS> category) {
        if (category != null) {
            if (category.size() == 0)
                Toast.makeText(getContext(), getString(R.string.no_search_result_ggs), Toast.LENGTH_SHORT).show();
            changeListItems(category);
        } else {
            Toast.makeText(getContext(), getString(R.string.no_search_result_ggs), Toast.LENGTH_SHORT).show();
        }

    }


    private CreateServicePostActivityGGS getParentActivity() {
        return ((CreateServicePostActivityGGS) getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCategoryFragmentIntractionListner) {
            mListener = (OnCategoryFragmentIntractionListner) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCategoryFragmentIntractionListner");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnCategoryFragmentIntractionListner {

        void onFragmentInteraction();
    }
}
