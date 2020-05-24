package com.manimaran.wikiaudio.databases.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


import org.jetbrains.annotations.NotNull;

import java.io.Serializable;


@Entity(tableName = "wiki_language", indices = {@Index(value = {"code"}, unique = true)})
public class WikiLang implements Serializable {

    @PrimaryKey
    @NonNull
    private String code;
    private String name;

    @ColumnInfo(name = "local_name")
    private String localName;

    @ColumnInfo(name = "title_of_words_without_audio")
    private String titleOfWordsWithoutAudio;

    @ColumnInfo(name = "is_left_direction")
    private Boolean isLeftDirection;

    public WikiLang() {
    }

    public WikiLang(@NotNull String code, String name, String localName, String titleOfWordsWithoutAudio, Boolean isLeftDirection) {
        this.code = code;
        this.name = name;
        this.localName = localName;
        this.titleOfWordsWithoutAudio = titleOfWordsWithoutAudio;
        this.isLeftDirection = isLeftDirection;
    }

    @NotNull
    public String getCode() {
        return code;
    }

    public void setCode(@NotNull String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getTitleOfWordsWithoutAudio() {
        return titleOfWordsWithoutAudio;
    }

    public void setTitleOfWordsWithoutAudio(String titleOfWordsWithoutAudio) {
        this.titleOfWordsWithoutAudio = titleOfWordsWithoutAudio;
    }

    public Boolean getIsLeftDirection() {
        return isLeftDirection;
    }

    public void setIsLeftDirection(Boolean leftDirection) {
        isLeftDirection = leftDirection;
    }
}
