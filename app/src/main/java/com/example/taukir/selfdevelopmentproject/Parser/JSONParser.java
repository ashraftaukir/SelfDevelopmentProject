package com.example.taukir.selfdevelopmentproject.Parser;

import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class JSONParser {

    public static final String TAG = "TAG";
   // private static final String MAIN_URL = "http://pratikbutani.x10.mx/json_data.json";
    private static final String MAIN_URL = "https://api.themoviedb.org/3/movie/top_rated?api_key=ec01f8c2eb6ac402f2ca026dc2d9b8fd&language=en_US&page=";

    private static Response response;

    public static JSONObject getDataFromServer(String pagenumber) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(MAIN_URL+pagenumber)
                    .build();
            response = client.newCall(request).execute();
            Log.d(TAG, "getDataFromServer: "+MAIN_URL+pagenumber);
            return new JSONObject(response.body().string());
        } catch (@NonNull IOException | JSONException e) {
            Log.e(TAG, "" + e.getLocalizedMessage());
        }
        return null;
    }


}
