package com.manimaran.wikiaudio.model;

public class ItemsModel {
    private int icon = -1;
    private String name, about, url , licenseUrl;

    public ItemsModel(String name, String about, String url) {
        this.name = name;
        this.about = about;
        this.url = url;
    }

    public ItemsModel(int icon, String name, String about, String url, String licenseUrl) {
        this.icon = icon;
        this.name = name;
        this.about = about;
        this.url = url;
        this.licenseUrl = licenseUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }
}
