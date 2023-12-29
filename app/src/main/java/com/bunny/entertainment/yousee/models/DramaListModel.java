package com.bunny.entertainment.yousee.models;

import java.io.Serializable;

public class DramaListModel{

    public String dramaName, countrySpecifiedDrama, totalEpisodes, dramaRating, dramaThumbnail, totalResults, dramaId, dataNumber;

    public DramaListModel() {
    }


    public DramaListModel(String dramaName, String countrySpecifiedDrama, String totalEpisodes, String dramaRating, String dramaThumbnail, String totalResults, String dramaId, String dataNumber) {
        this.dramaName = dramaName;
        this.countrySpecifiedDrama = countrySpecifiedDrama;
        this.totalEpisodes = totalEpisodes;
        this.dramaRating = dramaRating;
        this.dramaThumbnail = dramaThumbnail;
        this.totalResults = totalResults;
        this.dramaId = dramaId;
        this.dataNumber = dataNumber;
    }

    public String getDramaName() {
        return dramaName;
    }


    public void setDramaName(String dramaName) {
        this.dramaName = dramaName;
    }

    public String getCountrySpecifiedDrama() {
        return countrySpecifiedDrama;
    }

    public void setCountrySpecifiedDrama(String countrySpecifiedDrama) {
        this.countrySpecifiedDrama = countrySpecifiedDrama;
    }

    public String getTotalEpisodes() {
        return totalEpisodes;
    }

    public void setTotalEpisodes(String totalEpisodes) {
        this.totalEpisodes = totalEpisodes;
    }

    public String getDramaRating() {
        return dramaRating;
    }

    public void setDramaRating(String dramaRating) {
        this.dramaRating = dramaRating;
    }

    public String getDramaThumbnail() {
        return dramaThumbnail;
    }

    public void setDramaThumbnail(String dramaThumbnail) {
        this.dramaThumbnail = dramaThumbnail;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public String getDramaId() {
        return dramaId;
    }

    public void setDramaId(String dramaId) {
        this.dramaId = dramaId;
    }

    public String getDataNumber() {
        return dataNumber;
    }

    public void setDataNumber(String dataNumber) {
        this.dataNumber = dataNumber;
    }
}
