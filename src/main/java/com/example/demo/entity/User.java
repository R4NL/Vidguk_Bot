package com.example.demo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Thealeshka on 01.11.2019 inside the package - com.telegram.bot.entity
 */

@Entity(name = "UserTable")
public class User {
    @Id
    @GeneratedValue
    private Long id;

    private Long chatId;
    private Integer stateId;
    private String Uni;
    private String groupInUni;
    private String language;


    public User() {
    }

    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getChatId() {
        return chatId;
    }

    public User setChatId(Long chatId) {
        this.chatId = chatId;
        return this;
    }

    public Integer getStateId() {
        return stateId;
    }

    public User setStateId(Integer stateId) {
        this.stateId = stateId;
        return this;
    }

    public String getGroupInUni() {
        return groupInUni;
    }

    public User setGroupInUni(String groupInUni) {
        this.groupInUni = groupInUni;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public User setLanguage(String language) {
        this.language = language;
        return this;
    }

    public String getUni() {
        return Uni;
    }

    public User setUni(String uni) {
        Uni = uni;
        return this;
    }
}
