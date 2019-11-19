package com.epam.coopaint.domain;

public class WSCommandResult extends CommandResult {
    public enum ResponseType {SEND_BACK, BROADCAST}

    private ResponseType responseType = ResponseType.SEND_BACK;

    public ResponseType getResponseType() {
        return responseType;
    }

    public WSCommandResult setResponseType(ResponseType responseType) {
        this.responseType = responseType;
        return this;
    }
}
