package com.freshfastfood.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultHome {

    @SerializedName("Banner")
    private List<BannerItem> bannerItems;

    @SerializedName("Catlist")
    private List<CatItem> catItems;

    @SerializedName("Productlist")
    private List<ProductItem> productItems;

    @SerializedName("dynamic_section")
    private List<DynamicData> dynamicData;

    @SerializedName("Remain_notification")
    private int remainNotification;

    @SerializedName("Main_Data")
    @Expose
    private MainDataHome mainData;

    @SerializedName("Wallet")
    @Expose
    private float Wallet;

    public float getWallet() {
        return Wallet;
    }

    public void setWallet(float wallet) {
        Wallet = wallet;
    }

    public MainDataHome getMainData() {
        return mainData;
    }

    public void setMainData(MainDataHome mainData) {
        this.mainData = mainData;
    }


    public List<DynamicData> getDynamicData() {
        return dynamicData;
    }

    public void setDynamicData(List<DynamicData> dynamicData) {
        this.dynamicData = dynamicData;
    }



    public int getRemainNotification() {
        return remainNotification;
    }

    public void setRemainNotification(int remainNotification) {
        this.remainNotification = remainNotification;
    }

    public List<BannerItem> getBannerItems() {
        return bannerItems;
    }

    public void setBannerItems(List<BannerItem> bannerItems) {
        this.bannerItems = bannerItems;
    }

    public List<CatItem> getCatItems() {
        return catItems;
    }

    public void setCatItems(List<CatItem> catItems) {
        this.catItems = catItems;
    }

    public List<ProductItem> getProductItems() {
        return productItems;
    }

    public void setProductItems(List<ProductItem> productItems) {
        this.productItems = productItems;
    }
}
