package net.sharetrip.hotel.utils;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import net.sharetrip.hotel.booking.model.HotelBookingParams;
import net.sharetrip.hotel.booking.model.RoomBookingSummary;
import net.sharetrip.hotel.booking.model.SearchSummary;
import net.sharetrip.hotel.booking.model.TravellerRoomInfo;
import net.sharetrip.hotel.history.model.HotelPolicy;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class MoshiUtil {

    public static List<TravellerRoomInfo> convertStringToTravellerRoomInfo(String jsonResponse) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, TravellerRoomInfo.class);
        JsonAdapter<List<TravellerRoomInfo>> adapter = moshi.adapter(type);
        List<TravellerRoomInfo> infoList = adapter.fromJson(jsonResponse);
        return infoList;
    }

    public static List<Integer> convertStringOfIntToArrayList(String jsonResponse) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, Integer.class);
        JsonAdapter<List<Integer>> adapter = moshi.adapter(type);
        List<Integer> infoList = adapter.fromJson(jsonResponse);
        return infoList;
    }

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

    public static String convertBookingParamToString(HotelBookingParams params) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<HotelBookingParams> jsonAdapter = moshi.adapter(HotelBookingParams.class);
        String bookingParams = jsonAdapter.toJson(params);
        return bookingParams;
    }

    public static HotelBookingParams convertStringToBookingParam(String params) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<HotelBookingParams> jsonAdapter = moshi.adapter(HotelBookingParams.class);
        HotelBookingParams bookingParams = jsonAdapter.fromJson(params);
        return bookingParams;
    }

    public static String convertBookingSummaryToString(RoomBookingSummary params) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<RoomBookingSummary> jsonAdapter = moshi.adapter(RoomBookingSummary.class);
        String bookingParams = jsonAdapter.toJson(params);
        return bookingParams;
    }

    public static RoomBookingSummary convertStringToBookingSummary(String params) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<RoomBookingSummary> jsonAdapter = moshi.adapter(RoomBookingSummary.class);
        RoomBookingSummary bookingParams = jsonAdapter.fromJson(params);
        return bookingParams;
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
        for (int i = 1; i < params.size(); i++) {
            result = result + ',' + params.get(i);
        }
        return result + ']';
    }

    public static String convertHotelSummaryToString(SearchSummary summary) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<SearchSummary> jsonAdapter = moshi.adapter(SearchSummary.class);
        String bookingSummary = jsonAdapter.toJson(summary);
        return bookingSummary;
    }

    public static SearchSummary convertStringToHotelSummary(String summary) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<SearchSummary> jsonAdapter = moshi.adapter(SearchSummary.class);
        SearchSummary bookingSummary = jsonAdapter.fromJson(summary);
        return bookingSummary;
    }

    public static String convertStringToHotelPolicy(String policy) {
        try {
            Moshi moshi = new Moshi.Builder().build();
            Type type = Types.newParameterizedType(List.class, HotelPolicy.class);
            JsonAdapter<List<HotelPolicy>> adapter = moshi.adapter(type);
            List<HotelPolicy> hotelPolicies = adapter.fromJson(policy);
            StringBuilder finalPolicy = new StringBuilder();
            if (hotelPolicies != null && !hotelPolicies.isEmpty() && hotelPolicies.get(0).getParagraphs() != null) {
                for (String value : hotelPolicies.get(0).getParagraphs()) {
                    finalPolicy.append(value).append("\n");
                }
            }
            return finalPolicy.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /*public static DiscountModel convertStringToDiscountModel(String discountString) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<DiscountModel> jsonAdapter = moshi.adapter(DiscountModel.class);
        DiscountModel discountModel = jsonAdapter.fromJson(discountString);
        return discountModel;
    }*/
    /*
           public static ErrorResponse convertStringToErrorResponse(String errorMsg) throws IOException {
               Moshi moshi = new Moshi.Builder().build();
               JsonAdapter<ErrorResponse> jsonAdapter = moshi.adapter(ErrorResponse.class);
               ErrorResponse errorResponse = jsonAdapter.fromJson(errorMsg);
               return errorResponse;
           }
       */
}
