package com.garbagespots.garbagespotsapp;

public class info {

    private String Description;
    private double Latitude;
    private double Longitude;
    private String UserID;
    private long Rating;
    private long Cred;
    private String Date;

    public info() {

    }

    public String getDesc() {
        return Description;
    }

    public void setDesc(String description) {
        Description = description;
    }

    public double getLat() {
        return Latitude;
    }

    public void setLat(double latitude) {
        Latitude = latitude;
    }

    public double getLong() {
        return Longitude;
    }

    public void setLong(double longitude) {
        Longitude = longitude;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public long getRating() {
        return Rating;
    }

    public void setRating(long rating) {
        Rating = rating;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public long getCred() {
        return Cred;
    }

    public void setCred(long cred) {
        Cred = cred;
    }

}
