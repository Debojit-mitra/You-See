package com.bunny.entertainment.yousee.models.anime;

public class AnimeCharacterInfoModel {
    String name, role, imageUrl, characterId;
    public AnimeCharacterInfoModel() {
    }
    public AnimeCharacterInfoModel(String name, String role, String imageUrl, String characterId) {
        this.name = name;
        this.role = role;
        this.imageUrl = imageUrl;
        this.characterId = characterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }
}
