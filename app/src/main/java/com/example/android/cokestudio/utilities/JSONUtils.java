package com.example.android.cokestudio.utilities;

import com.example.android.cokestudio.models.Song;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jayabrata Dhakai on 3/18/2017.
 */

public class JSONUtils {

    public static final String LOG_TAG = JSONUtils.class.getSimpleName();

    /**
     * Helper Method to parse the JSON response
     * @param response is the JSON response as one complete String
     * @return the parsed JSON as a ArrayList
     */
    public static List<Song> parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        List<Song> songList;
        songList = Arrays.asList(gson.fromJson(response, Song[].class));
        return songList;
    }
}
