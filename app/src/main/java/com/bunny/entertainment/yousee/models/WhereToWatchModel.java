package com.bunny.entertainment.yousee.models;

public class WhereToWatchModel {

    public String ottName, ottImageUrl, ottSubscription, link;

    public WhereToWatchModel() {
    }

    public WhereToWatchModel(String ottName, String ottImageUrl, String ottSubscription) {
        this.ottName = ottName;
        this.ottImageUrl = ottImageUrl;
        this.ottSubscription = ottSubscription;
    }

    public String getOttName() {
        return ottName;
    }

    public void setOttName(String ottName) {
        this.ottName = ottName;
    }

    public String getOttImageUrl() {
        return ottImageUrl;
    }

    public void setOttImageUrl(String ottImageUrl) {
        this.ottImageUrl = ottImageUrl;
    }

    public String getOttSubscription() {
        return ottSubscription;
    }

    public void setOttSubscription(String ottSubscription) {
        this.ottSubscription = ottSubscription;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
