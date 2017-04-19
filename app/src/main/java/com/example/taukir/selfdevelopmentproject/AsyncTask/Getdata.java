package com.example.taukir.selfdevelopmentproject.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;


import com.example.taukir.selfdevelopmentproject.Interface.GetDataNextPageCallBack;
import com.example.taukir.selfdevelopmentproject.Model.MyDataModel;
import com.example.taukir.selfdevelopmentproject.Parser.JSONParser;
import com.example.taukir.selfdevelopmentproject.Utils.Keys;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Getdata extends AsyncTask<Void, Void, ArrayList<MyDataModel>> {

    private GetDataNextPageCallBack getDataNextPageCallBack;
    private int pagenumber;

    public Getdata(GetDataNextPageCallBack getDataNextPageCallBack,int pagenumber) {
        this.getDataNextPageCallBack = getDataNextPageCallBack;
        this.pagenumber=pagenumber;
    }



    @Override
    protected ArrayList<MyDataModel> doInBackground(Void... params) {
        JSONObject jsonObject = JSONParser.getDataFromServer(String.valueOf(pagenumber));
        ArrayList<MyDataModel> informationarraylist = new ArrayList<>();
        Gson gson = new Gson();
        try {
            if (jsonObject != null) {
                if (jsonObject.length() > 0) {

                    JSONArray array = jsonObject.getJSONArray(Keys.KEY_RESULTS);
                    int lenArray = array.length();
                    if (lenArray > 0) {
                        for (int i = 0; i < lenArray; i++) {

                            JSONObject innerObject = array.getJSONObject(i);
                            MyDataModel model = gson.fromJson(innerObject.toString(), MyDataModel.class);
                            informationarraylist.add(model);
                        }


                        return informationarraylist;

                    }

                }
            }
        } catch (JSONException je) {
            Log.i(JSONParser.TAG, "" + je.getLocalizedMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<MyDataModel> myDataModels) {
        super.onPostExecute(myDataModels);
        getDataNextPageCallBack.nextpagecallback(myDataModels);
        getDataNextPageCallBack = null;
    }


}