package com.ehsancharities.model;

import java.util.ArrayList;

public class Donation
{
    private String userID;
    private ArrayList<String> itemsDonations;
    private String descriptions;
    private String donationName;
    private String charityID;
    private String donationID;
    private String donationPhone;
    private String donationImage;
    private String donationEmail;
     private double lat;
     private double lng;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Donation() {
    }

    public String getDonationPhone() {
        return donationPhone;
    }

    public void setDonationPhone(String donationPhone) {
        this.donationPhone = donationPhone;
    }

    public String getDonationImage() {
        return donationImage;
    }

    public void setDonationImage(String donationImage) {
        this.donationImage = donationImage;
    }

    public String getDonationEmail() {
        return donationEmail;
    }

    public void setDonationEmail(String donationEmail) {
        this.donationEmail = donationEmail;
    }

    public String getDonationID() {
        return donationID;
    }

    public void setDonationID(String donationID) {
        this.donationID = donationID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<String> getItemsDonations() {
        return itemsDonations;
    }

    public void setItemsDonations(ArrayList<String> itemsDonations) {
        this.itemsDonations = itemsDonations;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getDonationName() {
        return donationName;
    }

    public void setDonationName(String charityName) {
        this.donationName = charityName;
    }

    public String getCharityID() {
        return charityID;
    }

    public void setCharityID(String charityID) {
        this.charityID = charityID;
    }

    @Override
    public String toString() {
        return "Donations{" +
                "userID='" + userID + '\'' +
                ", itemsDonations=" + itemsDonations +
                ", descriptions='" + descriptions + '\'' +
                ", donationName='" + donationName + '\'' +
                ", charityID='" + charityID + '\'' +
                '}';
    }
}
