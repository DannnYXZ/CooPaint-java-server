package com.epam.coopaint.domain;

public class Board extends Room<VShape> {
    private String name;

    public String getName() {
        return name;
    }

    public Board setName(String name) {
        this.name = name;
        return this;
    }
}
