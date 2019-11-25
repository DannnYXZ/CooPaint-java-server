package com.epam.coopaint.domain;

import java.util.UUID;

public class Snapshot {
    private String link;
    private UUID chatID;
    private UUID boardID;

    public String getLink() {
        return link;
    }

    public Snapshot setLink(String link) {
        this.link = link;
        return this;
    }

    public UUID getChatID() {
        return chatID;
    }

    public Snapshot setChatID(UUID chatID) {
        this.chatID = chatID;
        return this;
    }

    public UUID getBoardID() {
        return boardID;
    }

    public Snapshot setBoardID(UUID boardID) {
        this.boardID = boardID;
        return this;
    }
}
