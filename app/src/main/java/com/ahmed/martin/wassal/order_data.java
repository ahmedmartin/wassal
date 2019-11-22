package com.ahmed.martin.wassal;

import android.net.Uri;

import java.io.Serializable;

public class order_data implements Serializable {

    private String s_uid,r_name,r_phone,r_address,description,weight,provide_pay,delivery_estimate,date ;
    private Double r_long,r_lat;



    public String getR_name() {
        return r_name;
    }

    public String getR_phone() {
        return r_phone;
    }

    public String getR_address() {
        return r_address;
    }

    public String getDescription() {
        return description;
    }

    public String getWeight() {
        return weight;
    }

    public String getProvide_pay() {
        return provide_pay;
    }

    public String getDelivery_estimate() {
        return delivery_estimate;
    }

    public String getDate() {
        return date;
    }





    public void setR_name(String r_name) {
        this.r_name = r_name;
    }

    public void setR_phone(String r_phone) {
        this.r_phone = r_phone;
    }

    public void setR_address(String r_address) {
        this.r_address = r_address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setProvide_pay(String provide_pay) {
        this.provide_pay = provide_pay;
    }

    public void setDelivery_estimate(String delivery_estimate) {
        this.delivery_estimate = delivery_estimate;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public void setR_long(Double r_long) {
        this.r_long = r_long;
    }

    public void setR_lat(Double r_lat) {
        this.r_lat = r_lat;
    }


    public void setS_uid(String s_uid) {
        this.s_uid = s_uid;
    }

    public Double getR_long() {
        return r_long;
    }

    public Double getR_lat() {
        return r_lat;
    }
}
