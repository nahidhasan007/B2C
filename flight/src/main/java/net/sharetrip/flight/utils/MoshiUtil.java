package net.sharetrip.flight.utils;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public class MoshiUtil {

    public static <T> T convertStringToModelClass(String params, Class<T> type) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<T> jsonAdapter = moshi.adapter(type);
        return jsonAdapter.fromJson(params);
    }

    public static <T> String convertModelClassToString(T params, Class<T> type) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<T> jsonAdapter = moshi.adapter(type);
        return jsonAdapter.toJson(params);
    }
}