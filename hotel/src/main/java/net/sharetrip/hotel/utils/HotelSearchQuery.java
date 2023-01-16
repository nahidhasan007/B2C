package net.sharetrip.hotel.utils;

import android.os.Parcel;
import android.os.Parcelable;

import net.sharetrip.shared.utils.DateUtil;

import net.sharetrip.hotel.booking.model.PriceRange;
import net.sharetrip.hotel.booking.model.TravellerRoomInfo;

import java.util.ArrayList;
import java.util.List;

public class HotelSearchQuery implements Parcelable {
    private String propertyName;
    private String propertyCode;
    private String checkin;
    private String checkout;
    private List<TravellerRoomInfo> rooms = new ArrayList<>();
    private PriceRange priceRange;
    private String hotelName;
    private String distance;
    private String propertyRating;
    private String meals;
    private String propertyType;
    private String sort;
    private String amenities;
    private String neighborhoods;

    public String getNeighborhoods() {
        return neighborhoods;
    }

    public void setNeighborhoods(String neighborhoods) {
        this.neighborhoods = neighborhoods;
    }

    public String getPointofinterest() {
        return pointofinterest;
    }

    public void setPointofinterest(String pointofinterest) {
        this.pointofinterest = pointofinterest;
    }

    private String pointofinterest;

    public HotelSearchQuery() { }

    protected HotelSearchQuery(Parcel in) {
        propertyName = in.readString();
        propertyCode = in.readString();
        checkin = in.readString();
        checkout = in.readString();
        hotelName = in.readString();
        distance = in.readString();
        propertyRating = in.readString();

        meals = in.readString();
        propertyType = in.readString();
        amenities = in.readString();
        sort = in.readString();
        neighborhoods=in.readString();
        pointofinterest=in.readString();
    }

    public static final Creator<HotelSearchQuery> CREATOR = new Creator<HotelSearchQuery>() {
        @Override
        public HotelSearchQuery createFromParcel(Parcel in) {
            return new HotelSearchQuery(in);
        }

        @Override
        public HotelSearchQuery[] newArray(int size) {
            return new HotelSearchQuery[size];
        }
    };

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyCode() {
        return propertyCode;
    }

    public void setPropertyCode(String propertyCode) {
        this.propertyCode = propertyCode;
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }

    public List<TravellerRoomInfo> getRooms() {
        return rooms;
    }

    public void setRooms(List<TravellerRoomInfo> rooms) {
        this.rooms = rooms;
    }

    public PriceRange getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(PriceRange priceRange) {
        this.priceRange = priceRange;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getMeals() {
        return meals;
    }

    public void setMeals(String meals) {
        this.meals = meals;
    }

    public String getPropertyRating() {
        return propertyRating;
    }

    public void setPropertyRating(String propertyRating) {
        this.propertyRating = propertyRating;
    }


    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(propertyName);
        parcel.writeString(propertyCode);
        parcel.writeString(checkin);
        parcel.writeString(checkout);
        parcel.writeString(hotelName);
        parcel.writeString(distance);
        parcel.writeString(propertyRating);
        parcel.writeString(meals);
        parcel.writeString(propertyType);
        parcel.writeString(amenities);
        parcel.writeString(sort);
        parcel.writeString(pointofinterest);
        parcel.writeString(neighborhoods);
    }

    public void initQueryValue() {
        propertyCode = "";
        checkin = DateUtil.getApiDateFormat(1);
        checkout = DateUtil.getApiDateFormat( 3);
        TravellerRoomInfo room = new TravellerRoomInfo();
        room.setNumberOfAdult(2);
        rooms.add(room);
        priceRange = null;
        hotelName = "";
        distance = "25";
        propertyRating = "";
        sort = "";
    }

    public String getRoomsJsonStringData() {
        if (rooms.size() == 0) return "";

        String value = "[" + rooms.get(0).toString() ;
        if (rooms.size() > 1) value = value + "," + rooms.get(1).toString();
        if (rooms.size() > 2) value = value + "," + rooms.get(2).toString();
        if (rooms.size() > 3) value = value + "," + rooms.get(3).toString();

        return value + "]";
    }
}
