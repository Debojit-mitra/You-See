package com.bunny.entertainment.yousee.models.anime;

public class CharacterListModel {
    public String CharacterName, characterRole, characterImage, characterId;

    public CharacterListModel() {
    }
    public CharacterListModel(String characterName, String characterRole, String characterImage, String characterId) {
        CharacterName = characterName;
        this.characterRole = characterRole;
        this.characterImage = characterImage;
        this.characterId = characterId;
    }

    public String getCharacterName() {
        return CharacterName;
    }

    public void setCharacterName(String characterName) {
        CharacterName = characterName;
    }

    public String getCharacterRole() {
        return characterRole;
    }

    public void setCharacterRole(String characterRole) {
        this.characterRole = characterRole;
    }

    public String getCharacterImage() {
        return characterImage;
    }

    public void setCharacterImage(String characterImage) {
        this.characterImage = characterImage;
    }

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }
}
