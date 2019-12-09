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

    public User setId(long id) {
        this.id = id;
        return this;
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

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public byte[] getHash() {
        return hash;
    }

    public User setHash(byte[] hash) {
        this.hash = hash;
        return this;
    }

    public byte[] getSalt() {
        return salt;
    }

    public User setSalt(byte[] salt) {
        this.salt = salt;
        return this;
    }

    public LangPack getLang() {
        return lang;
    }

    public String getAvatar() {
        return avatar;
    }

    @JsonProperty(value = "isAuth")
    public boolean isAuth() {
        return isAuth;
    }

    public User setAuth(boolean auth) {
        isAuth = auth;
        return this;
    }

    public User setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public User setLang(LangPack lang) {
        this.lang = lang;
        return this;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public User setGroups(Set<String> groups) {
        this.groups = groups;
        return this;
    }

    public UUID getUuid() {
        return uuid;
    }

    public User setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }
}
