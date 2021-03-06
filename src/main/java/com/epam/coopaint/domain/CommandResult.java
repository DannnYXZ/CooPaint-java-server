package com.epam.coopaint.domain;

import javax.servlet.http.HttpServletResponse;

public class CommandResult {
    private int statusCode = HttpServletResponse.SC_OK;
    private String body = "";

    public int getStatusCode() {
        return statusCode;
    }

    public CommandResult setCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public String getBody() {
        return body;
    }

    public CommandResult setBody(String body) {
        this.body = body;
        return this;
    }
}
