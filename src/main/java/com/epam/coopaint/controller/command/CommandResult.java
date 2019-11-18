package com.epam.coopaint.controller.command;

import javax.servlet.http.HttpServletResponse;

public class CommandResult {
    public enum ResponseType {BROADCAST, OK, ERROR}

    int statusCode = HttpServletResponse.SC_OK;
    String body = "";

    public CommandResult() {
    }

    public CommandResult(String body) {
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
