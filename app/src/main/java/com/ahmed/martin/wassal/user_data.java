package com.ahmed.martin.wassal;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class user_data implements Serializable {
    private String first_name, last_name, address, phoneNumber, ssnNumber;
    private Double address_lat, address_long;

    @Exclude
    private String email;

    public user_data() { }

    public user_data(String first_name, String last_name, String address, String phoneNumber,
                     String ssnNumber, Double address_lat, Double address_long) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.ssnNumber = ssnNumber;
        this.address_lat = address_lat;
        this.address_long = address_long;
    }

    @Exclude
    public String getEmail(){return email;}
    @Exclude
    public void setEmail(String email){ this.email = email;}


    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSsnNumber() {
        return ssnNumber;
    }

    public void setSsnNumber(String ssnNumber) {
        this.ssnNumber = ssnNumber;
    }

    public Double getAddress_lat() {
        return address_lat;
    }

    public void setAddress_lat(Double address_lat) {
        this.address_lat = address_lat;
    }

    public Double getAddress_long() {
        return address_long;
    }

    public void setAddress_long(Double address_long) {
        this.address_long = address_long;
    }
}
