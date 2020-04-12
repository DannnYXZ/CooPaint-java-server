package com.epam.coopaint.domain;

import com.epam.coopaint.util.LangPack;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
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
    private Set<String> groups;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (isAuth != user.isAuth) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (!Arrays.equals(hash, user.hash)) return false;
        if (!Arrays.equals(salt, user.salt)) return false;
        if (avatar != null ? !avatar.equals(user.avatar) : user.avatar != null) return false;
        if (uuid != null ? !uuid.equals(user.uuid) : user.uuid != null) return false;
        if (lang != user.lang) return false;
        return groups != null ? groups.equals(user.groups) : user.groups == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(hash);
        result = 31 * result + Arrays.hashCode(salt);
        result = 31 * result + (avatar != null ? avatar.hashCode() : 0);
        result = 31 * result + (isAuth ? 1 : 0);
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        result = 31 * result + (lang != null ? lang.hashCode() : 0);
        result = 31 * result + (groups != null ? groups.hashCode() : 0);
        return result;
    }
}
