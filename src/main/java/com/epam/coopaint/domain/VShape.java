package com.epam.coopaint.domain;

import java.util.List;

public class VShape {
    public enum ShapeType {LINE}

    private ShapeType type;
    private List<Float> params;

    public ShapeType getType() {
        return type;
    }

    public VShape setType(ShapeType type) {
        this.type = type;
        return this;
    }

    public List<Float> getParams() {
        return params;
    }

    public VShape setParams(List<Float> params) {
        this.params = params;
        return this;
    }
}
