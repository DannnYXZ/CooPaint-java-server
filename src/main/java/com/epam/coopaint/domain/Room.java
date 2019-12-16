package com.epam.coopaint.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.UUID;

public class Room<E> {
    @JsonIgnore
    private long id;
    protected UUID uuid;
    private String name;
    private User creator;
    private List<E> elements;

    public long getId() {
        return id;
    }

    public Room<E> setId(long id) {
        this.id = id;
        return this;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Room<E> setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getName() {
        return name;
    }

    public Room<E> setName(String name) {
        this.name = name;
        return this;
    }

    public User getCreator() {
        return creator;
    }

    public Room<E> setCreator(User creator) {
        this.creator = creator;
        return this;
    }

    public List<E> getElements() {
        return elements;
    }

    public Room<E> setElements(List<E> elements) {
        this.elements = elements;
        return this;
    }
}
