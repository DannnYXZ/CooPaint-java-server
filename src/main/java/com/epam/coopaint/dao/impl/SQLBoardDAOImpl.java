package com.epam.coopaint.dao.impl;

import com.epam.coopaint.dao.BoardDAO;
import com.epam.coopaint.domain.Board;
import com.epam.coopaint.domain.User;

import java.util.List;

class SQLBoardDAOImpl implements BoardDAO {
    private static String QUERY_ADD_BOARD = "INSERT into board (id) WHERE id=NULL";
    private static String QUERY_DELETE_BOARD = "DELETE ";

    @Override
    public Board saveBoard(Board board) {
        return null;
    }

    @Override
    public Board getBoardOwner(int boardId) {
        return null;
        /*
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_INSERT,
                Statement.RETURN_GENERATED_KEYS);
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getLong(1));
            }
            else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    public List<Board> getBoards(User user) {
        return null;
    }

    @Override
    public void getRights(User user, long accessRights) {

    }

    @Override
    public void setRights(User user, long accessRights) {

    }
}
