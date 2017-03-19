package com.example.android.cokestudio.utilities;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jayabrata Dhakai on 3/18/2017.
 */

public class NetworkUtils {

    public static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    public static final String COKE_STUDIO_API = "http://starlord.hackerearth.com/edfora/cokestudio";

    /**
     * Helper Method to fetch movie data using OKHttpClient
     * @return the un-parsed JSON SongResponse in String format
     */
    public static String makeHTTPRequest(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(COKE_STUDIO_API)
                .build();

        String jsonData = null;
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful())
                jsonData = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(jsonData != null)
            return jsonData;
        else
            return "No Song Fetched";
    }
}
