package com.epam.coopaint.domain;

public class Snapshot {
    private String link;
    Chat chat = new Chat();
    Board board = new Board();

    public String getLink() {
        return link;
    }

    public Snapshot setLink(String link) {
        this.link = link;
        return this;
    }

    public Chat getChat() {
        return chat;
    }

    public Snapshot setChat(Chat chat) {
        this.chat = chat;
        return this;
    }

    public Board getBoard() {
        return board;
    }

    public Snapshot setBoard(Board board) {
        this.board = board;
        return this;
    }
}
