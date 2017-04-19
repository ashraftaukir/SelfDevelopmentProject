package com.example.taukir.selfdevelopmentproject.Fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taukir.selfdevelopmentproject.Adapter.RecyclerTouchListener;
import com.example.taukir.selfdevelopmentproject.Adapter.RecylerViewAdapter;
import com.example.taukir.selfdevelopmentproject.AsyncTask.Getdata;
import com.example.taukir.selfdevelopmentproject.AsyncTask.ParseDataTask;
import com.example.taukir.selfdevelopmentproject.Interface.ClickListener;
import com.example.taukir.selfdevelopmentproject.Interface.GetDataCallBack;
import com.example.taukir.selfdevelopmentproject.Interface.GetDataNextPageCallBack;
import com.example.taukir.selfdevelopmentproject.Model.MyDataModel;
import com.example.taukir.selfdevelopmentproject.R;
import com.example.taukir.selfdevelopmentproject.Utils.InternetConnection;
import com.example.taukir.selfdevelopmentproject.Utils.PaginationScrollListener;

import java.util.ArrayList;


public class InformationFragment extends android.support.v4.app.Fragment implements GetDataCallBack, GetDataNextPageCallBack,
        View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int PAGE_START = 1;
    private static final String TAG = "INFORMATION";
    View view;
    boolean passCheckBox = false;
    LinearLayoutManager linearLayoutManager;
    int pageNumber;
    private RecyclerView informationrecylerview;
    private RecylerViewAdapter adapter;
    private ArrayList<MyDataModel> informationArraylist;
    private ImageView removeitem, additem;
    private EditText username, useremail, userposition;
    private TextView delete, editItem, deleteItem, tvuserposition;
    private int positionvalue;
    private AlertDialog editordeletealertDialog;
    private boolean itemsetupChecker = false;
    private SwipeRefreshLayout swipe_refresh_layout;
    private int TOTAL_PAGES = 20;
    private int currentPage = PAGE_START;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean checkboxchecker = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.information, container, false);
        informationArraylist = new ArrayList<>();
        init(view);
        setupRecylerView(view);
        initListener();
        ApiCall(currentPage);
        LongPressReomveItem();
        return view;
    }


    private void LongPressReomveItem() {

        informationrecylerview.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), informationrecylerview, new ClickListener() {

            @Override
            public void onLongClick(View view, int position) {
                editordeleteItemInformation();
                positionValue(position);
            }

            @Override
            public void onClick(View view, int position) {

                if (!checkboxchecker) {
                    gotoNextfragment(position);
                }

            }
        }));
    }

    private void gotoNextfragment(int position) {
        DetailsView details = new DetailsView();
        Bundle bundle = new Bundle();
        bundle.putString("name", informationArraylist.get(position).getName());
        bundle.putString("email", informationArraylist.get(position).getEmail());
        bundle.putString("imageurl", informationArraylist.get(position).getPoster_path());
        details.setArguments(bundle);
        (getActivity()).getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_ggs, R.anim.slide_out_ggs, R.anim.slide_back_to_screen_ggs, R.anim.slide_back_ggs)
                .replace(R.id.fragment_container, details).addToBackStack("infromationfragment").commit();
    }

    private void positionValue(int position) {
        positionvalue = position;
    }

    private void editordeleteItemInformation() {

        LayoutInflater li = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") View promptsView = li.inflate(R.layout.editordeletedialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(true);
        editItem = (TextView) promptsView.findViewById(R.id.tv_edit);
        deleteItem = (TextView) promptsView.findViewById(R.id.tv_delete);
        editItem.setOnClickListener(this);
        deleteItem.setOnClickListener(this);
        editordeletealertDialog = alertDialogBuilder.create();
        editordeletealertDialog.show();


    }

    private void ApiCall(int pagenumber) {

        swipe_refresh_layout.setRefreshing(true);
        pageNumber = pagenumber;

        if (InternetConnection.checkConnection(getActivity())) {
            if (currentPage <= TOTAL_PAGES) {
                new ParseDataTask(this, pageNumber).execute();
            } else {

                swipe_refresh_layout.setRefreshing(false);
            }
        } else {
            swipe_refresh_layout.setRefreshing(false);
            Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void initListener() {

        removeitem.setOnClickListener(this);
        additem.setOnClickListener(this);
        delete.setOnClickListener(this);
        swipe_refresh_layout.setOnRefreshListener(this);

    }

    private void init(View view) {
        removeitem = (ImageView) getActivity().findViewById(R.id.remove_item);
        removeitem.setVisibility(View.VISIBLE);
        additem = (ImageView) getActivity().findViewById(R.id.add_item);
        additem.setVisibility(View.VISIBLE);
        delete = (TextView) getActivity().findViewById(R.id.delete_item);
        swipe_refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
    }

    private void setupRecylerView(View view) {

        informationrecylerview = (RecyclerView) view.findViewById(R.id.informationrecylerview);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        informationrecylerview.setLayoutManager(linearLayoutManager);
        informationrecylerview.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        adapter = new RecylerViewAdapter(informationArraylist, passCheckBox);
        informationrecylerview.setAdapter(adapter);
        informationrecylerview.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                if (currentPage <= TOTAL_PAGES) {
                    nextPageApiCall(currentPage);
                }
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

    }

    private void nextPageApiCall(int pagenumber) {

        swipe_refresh_layout.setRefreshing(true);
        pageNumber = pagenumber;
        if (InternetConnection.checkConnection(getActivity())) {
            if (currentPage <= TOTAL_PAGES) {
                new Getdata(this, pageNumber).execute();
            } else {
                swipe_refresh_layout.setRefreshing(false);
            }
        } else {
            swipe_refresh_layout.setRefreshing(false);
            Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void callback(ArrayList<MyDataModel> datalist) {

        if (datalist != null) {
            adapter.addAll(datalist);
            itemsetupChecker = true;
            swipe_refresh_layout.setRefreshing(false);
        } else {
            Toast.makeText(getActivity(), "Dataset is empty", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void nextpagecallback(ArrayList<MyDataModel> datalist) {

        if (datalist != null) {
            isLoading = false;
            itemsetupChecker = true;
            adapter.addAll(datalist);
            if (currentPage > TOTAL_PAGES) {
                isLastPage = true;
            }

            swipe_refresh_layout.setRefreshing(false);

        } else {
            Toast.makeText(getActivity(), "Dataset is empty", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onClick(View v) {

        if (v == removeitem) {
            checkboxVisible();
        } else if (v == additem) {
            addingnewItem();
        } else if (v == deleteItem) {
            deleteLongpressitem();

        } else if (v == editItem) {
            updateInformation();
        } else if (v == delete) {
            deleteCheckboxSelectedItem();
        }
    }

    private void updateInformation() {

        LayoutInflater li = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") View promptsView = li.inflate(R.layout.editdialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setView(promptsView);
        username = (EditText) promptsView.findViewById(R.id.etname);
        useremail = (EditText) promptsView.findViewById(R.id.etemail);
        tvuserposition = (TextView) promptsView.findViewById(R.id.tvposition);
        username.setText(informationArraylist.get(positionvalue).getName());
        useremail.setText(informationArraylist.get(positionvalue).getEmail());
        tvuserposition.setText(String.valueOf(positionvalue));

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().length() != 0 &&
                        useremail.getText().toString().length() != 0
                        && tvuserposition.getText().toString().length() != 0) {
                    seteditValue();
                    alertDialog.dismiss();
                    editordeletealertDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please complete the information page ", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void seteditValue() {
        MyDataModel dataModel = new MyDataModel();
        dataModel.setName(username.getText().toString());
        dataModel.setEmail(useremail.getText().toString());
        dataModel.setPoster_path(informationArraylist.get(Integer.valueOf(tvuserposition.getText().toString())).getPoster_path());
        if (Integer.valueOf(tvuserposition.getText().toString()) <= informationArraylist.size()) {
            informationArraylist.remove(positionvalue);
            adapter.notifyItemRemoved(positionvalue);
            informationArraylist.add(Integer.valueOf(tvuserposition.getText().toString()), dataModel);
            adapter.notifyItemInserted(Integer.valueOf(tvuserposition.getText().toString()));
        } else {
            informationArraylist.add(0, dataModel);
            adapter.notifyItemInserted(0);

        }

    }

    private void deleteLongpressitem() {
        informationArraylist.remove(positionvalue);
        adapter.notifyItemRemoved(positionvalue);
        editordeletealertDialog.dismiss();
    }

    private void addingnewItem() {
        if (itemsetupChecker) {
            showPopUp();
        } else {
            Toast.makeText(getActivity(), "Getting data please wait.....", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkboxVisible() {

        if (informationArraylist.size() != 0 && itemsetupChecker) {
            checkboxchecker = true;
            adapter.setPasscheckbox(true);
            adapter.notifyDataSetChanged();
            removeitem.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(getActivity(), "Getting data please wait.....", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteCheckboxSelectedItem() {

        AlertDialog.Builder aleartdialogbuilderobj = new AlertDialog.Builder(getActivity());
        aleartdialogbuilderobj.setCancelable(true)
                .setMessage("Do you want to delete this items??")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedItemdelete();
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog = aleartdialogbuilderobj.create();
        dialog.show();

    }

    private void selectedItemdelete() {

        for (int i = informationArraylist.size() - 1; i >= 0; i--) {

            if (informationArraylist.get(i).isSelected()) {
                informationArraylist.remove(i);
                adapter.notifyItemRemoved(i);
                adapter.notifyDataSetChanged();
            }

        }
        adapter.setPasscheckbox(false);
        delete.setVisibility(View.GONE);
        removeitem.setVisibility(View.VISIBLE);
        checkboxchecker = false;

    }

    private void showPopUp() {

        LayoutInflater li = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") View promptsView = li.inflate(R.layout.dialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setView(promptsView);
        username = (EditText) promptsView.findViewById(R.id.etname);
        useremail = (EditText) promptsView.findViewById(R.id.etemail);
        userposition = (EditText) promptsView.findViewById(R.id.etposition);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                            }
                        });


        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().length() != 0 &&
                        useremail.getText().toString().length() != 0
                        && userposition.getText().toString().length() != 0) {
                    setValue();
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please complete the information page ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setValue() {

        MyDataModel dataModel = new MyDataModel();
        dataModel.setName(username.getText().toString());
        dataModel.setEmail(useremail.getText().toString());
        if (Integer.valueOf(userposition.getText().toString()) <= informationArraylist.size()) {

            informationArraylist.add(Integer.valueOf(userposition.getText().toString()), dataModel);
            adapter.notifyItemInserted(Integer.valueOf(userposition.getText().toString()));
        } else {
            informationArraylist.add(0, dataModel);
            adapter.notifyItemInserted(0);

        }

    }

    @Override
    public void onRefresh() {
        isLoading = true;
        if (itemsetupChecker) {
            currentPage += 1;
            nextPageApiCall(currentPage);
        } else {
            ApiCall(currentPage);
        }
    }

}
