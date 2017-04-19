package com.example.taukir.selfdevelopmentproject.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;


import com.example.taukir.selfdevelopmentproject.Interface.GetDataCallBack;
import com.example.taukir.selfdevelopmentproject.Model.MyDataModel;
import com.example.taukir.selfdevelopmentproject.Parser.JSONParser;
import com.example.taukir.selfdevelopmentproject.Utils.Keys;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ParseDataTask extends AsyncTask<Void, Void, ArrayList<MyDataModel>> {

    private GetDataCallBack getDataCallBack;
    private int pagenumber;

    public ParseDataTask(GetDataCallBack getDataCallBack,int pagenumber) {
        this.getDataCallBack = getDataCallBack;
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
//                            String name = innerObject.getString(Keys.KEY_NAME);
//                            String email = innerObject.getString(Keys.KEY_EMAIL);
//                            String image = innerObject.getString(Keys.KEY_PROFILE_PIC);
//
//                            JSONObject phoneObject = innerObject.getJSONObject(Keys.KEY_PHONE);
//                            String phone = phoneObject.getString(Keys.KEY_MOBILE);
//                            model.setName(name);
//                            model.setEmail(email);
//                            model.setPhone(phone);
//                            model.setProfile_pic(image);
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
        getDataCallBack.callback(myDataModels);
        getDataCallBack = null;
    }


}