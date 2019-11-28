package com.epam.coopaint.domain;

import com.epam.coopaint.util.LangPack;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User {
    private long id;
    private String name;
    private String email;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private byte[] hash;
    @JsonIgnore
    private byte[] salt;
    private String avatar;
    private boolean isAuth;
    private UUID uuid;
    private LangPack lang;
    @JsonIgnore
    private Set<String> groups = new HashSet<>();

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public LangPack getLang() {
        return lang;
    }

    public String getAvatar() {
        return avatar;
    }

    @JsonProperty(value="isAuth")
    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        isAuth = auth;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setLang(LangPack lang) {
        this.lang = lang;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
