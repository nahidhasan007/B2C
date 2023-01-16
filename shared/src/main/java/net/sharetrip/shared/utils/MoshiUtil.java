package net.sharetrip.shared.utils;

import net.sharetrip.shared.model.ErrorResponse;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class MoshiUtil {

    public static List<Integer> convertStringOfIntToArrayList(String jsonResponse) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, Integer.class);
        JsonAdapter<List<Integer>> adapter = moshi.adapter(type);
        List<Integer> infoList = adapter.fromJson(jsonResponse);
        return infoList;
    }

    public static String convertListParamToString(List<String> params) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, String.class);
        JsonAdapter<List<String>> jsonAdapter = moshi.adapter(type);
        String bookingParams = jsonAdapter.toJson(params);
        return bookingParams;
    }

    public static String convertListParamToIntString(List<String> params) {
        if (params.size() == 0) return "";

        String result = '[' + params.get(0);
        for (int i = 1 ; i < params.size(); i++) {
            result =  result + ',' + params.get(i) ;
        }
        return result + ']';
    }

    public static ErrorResponse convertStringToErrorResponse(String errorMsg) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ErrorResponse> jsonAdapter = moshi.adapter(ErrorResponse.class);
        ErrorResponse errorResponse = jsonAdapter.fromJson(errorMsg);
        return errorResponse;
    }


}
