package com.example.taukir.selfdevelopmentproject.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taukir.selfdevelopmentproject.R;
import com.squareup.picasso.Picasso;

public class DetailsView extends Fragment {

    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w150";
    View view;
    TextView tittleviewdetails, details;
    ImageView picture, additem, removeitem;
    String tittle, detailsinfo, imgurl;
    public DetailsView() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.detailsview, container, false);
        init(view);
        getdata();
        return view;
    }

    private void init(View view) {
        tittleviewdetails = (TextView) view.findViewById(R.id.tittleviewdetails);
        picture = (ImageView) view.findViewById(R.id.pictureviewdetails);
        details = (TextView) view.findViewById(R.id.details);
        additem = (ImageView) getActivity().findViewById(R.id.add_item);
        removeitem = (ImageView) getActivity().findViewById(R.id.remove_item);

    }

    private void getdata() {

        if (getArguments() != null && getArguments().containsKey("name")) {
            tittle = getArguments().getString("name");
        }
        if (getArguments() != null && getArguments().containsKey("email")) {
            detailsinfo = getArguments().getString("email");
        }
        if (getArguments() != null && getArguments().containsKey("imageurl")) {
            imgurl = getArguments().getString("imageurl");
        }
        tittleviewdetails.setText(tittle);
        details.setText(detailsinfo);
        Picasso.with(getActivity())
                .load(BASE_URL_IMG + imgurl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(picture);


        additem.setVisibility(View.GONE);
        removeitem.setVisibility(View.GONE);

    }


}
