package com.example.taukir.selfdevelopmentproject.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.taukir.selfdevelopmentproject.Model.MyDataModel;
import com.example.taukir.selfdevelopmentproject.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class RecylerViewAdapter extends RecyclerView.Adapter<RecylerViewAdapter.ViewHolder> {

    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w150";
    boolean isLoadingAdded = false;
    private ArrayList<MyDataModel> informationlist;
    private Context context;
    private boolean passcheckbox;


    public RecylerViewAdapter(ArrayList<MyDataModel> informationlist, boolean passcheckbox) {
        this.informationlist = informationlist;
        this.passcheckbox = passcheckbox;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        Log.d("viewType", "onCreateViewHolder: " + viewType);
        context = parent.getContext();
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diff_info_block, parent, false);
        } else if (viewType == 2) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diff_info_block2, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.information_block, parent, false);
        }
        return new ViewHolder(view);

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 1;
        if (position == informationlist.size()-1) return 2;
        else return 3;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.text.setText(informationlist.get(position).getName());
        holder.textViewotherinfo.setText(informationlist.get(position).getEmail());
        holder.checkbox.setOnCheckedChangeListener(null);
        holder.checkbox.setChecked(informationlist.get(position).isSelected());
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                informationlist.get(holder.getAdapterPosition()).setSelected(isChecked);

            }
        });


        if (passcheckbox) {
            holder.checkbox.setVisibility(View.VISIBLE);
        } else {
            holder.checkbox.setVisibility(View.GONE);

        }
        Picasso.with(context)
                .load(BASE_URL_IMG + informationlist.get(position).getPoster_path())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.profile_image);


        holder.text.setText(informationlist.get(position).getName());
        holder.textViewotherinfo.setText(informationlist.get(position).getEmail());
        holder.checkbox.setOnCheckedChangeListener(null);
        holder.checkbox.setChecked(informationlist.get(position).isSelected());
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                informationlist.get(holder.getAdapterPosition()).setSelected(isChecked);

            }
        });


        if (passcheckbox) {
            holder.checkbox.setVisibility(View.VISIBLE);
        } else {
            holder.checkbox.setVisibility(View.GONE);

        }
        Picasso.with(context)
                .load(BASE_URL_IMG + informationlist.get(position).getPoster_path())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.profile_image);

    }

    @Override
    public int getItemCount() {
        return informationlist.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void setPasscheckbox(boolean passcheckbox) {
        this.passcheckbox = passcheckbox;
    }

//    public void addFooter() {
//
//        isLoadingAdded = true;
//        add(new MyDataModel());
//    }

//    public void removeLoadingFooter() {
//        isLoadingAdded = false;
//
//        int position = informationlist.size() - 1;
//        MyDataModel result = getItem(position);
//
//        if (result != null) {
//            informationlist.remove(position);
//            notifyItemRemoved(position);
//        }
//
//    }
//    private MyDataModel getItem(int position) {
//        return informationlist.get(position);
//    }

    public void add(MyDataModel r) {
        informationlist.add(r);
        notifyItemInserted(informationlist.size());
    }

    public void addAll(ArrayList<MyDataModel> movieResults) {
        int size = informationlist.size();
        informationlist.addAll(movieResults);
        notifyItemInserted(size);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text, textViewotherinfo;
        ImageView profile_image;
        CheckBox checkbox;
        RelativeLayout information_block;

        ViewHolder(View itemView) {
            super(itemView);

            text = (TextView) itemView.findViewById(R.id.text);
            information_block = (RelativeLayout) itemView.findViewById(R.id.information_block);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            textViewotherinfo = (TextView) itemView.findViewById(R.id.textViewotherinfo);
            profile_image = (ImageView) itemView.findViewById(R.id.profile_image);


        }
    }

}