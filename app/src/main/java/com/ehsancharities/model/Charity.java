package com.ehsancharities.model;

import java.util.List;

public class Charity
{

    private String charityID;
    private String charityName;
    private String charityEmail;
    private String charityAddress;
    private String charityPhoneNumber;
    private String chairtyImage;
    private String accountType;
   private int stateAccount;

    public int getStateAccount() {
        return stateAccount;
    }

    public void setStateAccount(int stateAccount) {
        this.stateAccount = stateAccount;
    }

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

    public Charity() {
    }


    public String getCharityID() {
        return charityID;
    }

    public void setCharityID(String charityID) {
        this.charityID = charityID;
    }

    public String getCharityName() {
        return charityName;
    }

    public void setCharityName(String charityName) {
        this.charityName = charityName;
    }

    public String getCharityEmail() {
        return charityEmail;
    }

    public void setCharityEmail(String charityEmail) {
        this.charityEmail = charityEmail;
    }

    public String getCharityAddress() {
        return charityAddress;
    }

    public void setCharityAddress(String charityAddress) {
        this.charityAddress = charityAddress;
    }

    public String getCharityPhoneNumber() {
        return charityPhoneNumber;
    }

    public void setCharityPhoneNumber(String charityPhoneNumber) {
        this.charityPhoneNumber = charityPhoneNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getChairtyImage() {
        return chairtyImage;
    }

    public void setChairtyImage(String chairtyImage) {
        this.chairtyImage = chairtyImage;
    }


}

