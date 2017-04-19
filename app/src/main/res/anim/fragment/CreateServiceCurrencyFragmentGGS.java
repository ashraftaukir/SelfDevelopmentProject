package com.gagagugu.ggservice.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.gagagugu.ggservice.R;
import com.gagagugu.ggservice.adapter.CurrencyRecycleViewAdapterGGS;
import com.gagagugu.ggservice.interfaces.GetCallBackFromCurrencyAdapterGGS;
import com.gagagugu.ggservice.models.CurrencyGGS;

import java.util.ArrayList;


public class CreateServiceCurrencyFragmentGGS extends DialogFragment implements View.OnClickListener, GetCallBackFromCurrencyAdapterGGS {
    private RecyclerView currencyRecycleView;
    private CurrencyRecycleViewAdapterGGS currencyRecycleViewAdapter;
    private Button doneButton;
    private ArrayList<CurrencyGGS> currencyGGSes;
    private int selectedPosition;

    // private OnFragmentInteractionListener mListener;

    public CreateServiceCurrencyFragmentGGS() {
        // Required empty public constructor
    }


    public static com.gagagugu.ggservice.fragment.CreateServiceCurrencyFragmentGGS newInstance() {
        com.gagagugu.ggservice.fragment.CreateServiceCurrencyFragmentGGS fragment = new com.gagagugu.ggservice.fragment.CreateServiceCurrencyFragmentGGS();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_set_currency_dialog_fragment_ggs, container, false);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        setCancelable(false);
        if (!getArguments().isEmpty()) {
            currencyGGSes = (ArrayList<CurrencyGGS>) getArguments().getSerializable("currency_list");
        }
        selectedPosition = -1;

        initializeView(root);
        initializeListner();
        initializeRecycleViewAdapter();


        return root;
    }


    /**
     * for initializing the view item
     *
     * @param root
     */
    private void initializeView(View root) {
        currencyRecycleView = (RecyclerView) root.findViewById(R.id.currency_recycle_view);

        doneButton = (Button) root.findViewById(R.id.currency_done_button);
    }


    /**
     * for adding listener to view item
     */

    private void initializeListner() {
        doneButton.setOnClickListener(this);
    }


    private void initializeRecycleViewAdapter() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        currencyRecycleView.setLayoutManager(layoutManager);
        currencyRecycleViewAdapter = new CurrencyRecycleViewAdapterGGS(this.currencyGGSes);
        currencyRecycleView.setAdapter(currencyRecycleViewAdapter);
        currencyRecycleViewAdapter.setOnItemClickAdapter(this);
    }


    @Override
    public void onClick(View v) {
        if (v == doneButton) {
            Intent intent = new Intent();
            intent.putExtra("position", selectedPosition);
            getTargetFragment().onActivityResult(getTargetRequestCode(), 0, intent);
            dismiss();
        }
    }


    @Override
    public void onRadioButtonClick(int position) {
        selectedPosition = position;
        Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onStop() {
        super.onStop();
        currencyRecycleViewAdapter.removeOnItemClickAdapter();
    }
}
