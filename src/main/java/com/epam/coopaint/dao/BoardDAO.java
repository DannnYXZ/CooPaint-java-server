package com.epam.coopaint.dao;

import com.epam.coopaint.domain.Board;
import com.epam.coopaint.domain.User;

import java.util.List;

public interface BoardDAO {
    Board saveBoard(Board board);
    Board getBoardOwner(int boardId);
    List<Board> getBoards(User user);
    void getRights(User user, long accessRights);
    void setRights(User user, long accessRights);
}
