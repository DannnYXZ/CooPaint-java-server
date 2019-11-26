package com.epam.coopaint.domain;

import java.util.List;

public class VShape {
    public enum ShapeType {LINE}

    private ShapeType type;
    private List<Float> params;

    public ShapeType getType() {
        return type;
    }

    public void setType(ShapeType type) {
        this.type = type;
    }

    public List<Float> getParams() {
        return params;
    }

    public void setParams(List<Float> params) {
        this.params = params;
    }
}
