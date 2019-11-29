package com.epam.coopaint.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Room<E> {
    @JsonIgnore
    protected long id;
    protected UUID uuid;
    protected User creator;
    protected List<E> elements = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public User getCreator() {
        return creator;
    }

    public Room setCreator(User creator) {
        this.creator = creator;
        return this;
    }

    public List<E> getElements() {
        return elements;
    }

    public void setElements(List<E> elements) {
        this.elements = elements;
    }
}
