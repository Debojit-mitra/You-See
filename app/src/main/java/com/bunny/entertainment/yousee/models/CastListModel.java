package com.bunny.entertainment.yousee.models;

public class CastListModel {

    public String castRealName, castCharacterName, castRole, castImage, castId;

    public CastListModel() {
    }

    public CastListModel(String castRealName, String castCharacterName, String castRole, String castImage, String castId) {
        this.castRealName = castRealName;
        this.castCharacterName = castCharacterName;
        this.castRole = castRole;
        this.castImage = castImage;
        this.castId = castId;
    }

    public String getCastRealName() {
        return castRealName;
    }

    public void setCastRealName(String castRealName) {
        this.castRealName = castRealName;
    }

    public String getCastCharacterName() {
        return castCharacterName;
    }

    public void setCastCharacterName(String castCharacterName) {
        this.castCharacterName = castCharacterName;
    }

    public String getCastRole() {
        return castRole;
    }

    public void setCastRole(String castRole) {
        this.castRole = castRole;
    }

    public String getCastImage() {
        return castImage;
    }

    public void setCastImage(String castImage) {
        this.castImage = castImage;
    }

    public String getCastId() {
        return castId;
    }

    public void setCastId(String castId) {
        this.castId = castId;
    }
}
