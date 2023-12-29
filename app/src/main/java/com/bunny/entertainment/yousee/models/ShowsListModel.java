package com.bunny.entertainment.yousee.models;

public class ShowsListModel {

    public String showName, showRating, showThumbnail, totalPages, totalResults, mediaType, showId, dataNumber;

    public ShowsListModel() {
    }

    public ShowsListModel(String showName, String showRating, String showThumbnail, String totalPages, String totalResults, String mediaType, String showId, String dataNumber) {
        this.showName = showName;
        this.showRating = showRating;
        this.showThumbnail = showThumbnail;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
        this.mediaType = mediaType;
        this.showId = showId;
        this.dataNumber = dataNumber;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getShowRating() {
        return showRating;
    }

    public void setShowRating(String showRating) {
        this.showRating = showRating;
    }

    public String getShowThumbnail() {
        return showThumbnail;
    }

    public void setShowThumbnail(String showThumbnail) {
        this.showThumbnail = showThumbnail;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public String getDataNumber() {
        return dataNumber;
    }

    public void setDataNumber(String dataNumber) {
        this.dataNumber = dataNumber;
    }
}
