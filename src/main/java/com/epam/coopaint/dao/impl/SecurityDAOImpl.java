package com.epam.coopaint.dao.impl;

import com.epam.coopaint.dao.SecurityDAO;
import com.epam.coopaint.domain.ACL;
import com.epam.coopaint.domain.UserAction;
import com.epam.coopaint.exception.ConnectionPoolException;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.pool.ConnectionPoolImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.epam.coopaint.dao.impl.SQLData.COLUMN_ACL_GROUP;

public class SecurityDAOImpl implements SecurityDAO {
    private static final String QUERY_RIGHTS_BY_RESOURCE = "SELECT * from acl WHERE resource=?";

    @Override
    public ACL getACL(String resource) throws DAOException {
        try (Connection connection = ConnectionPoolImpl.getInstance().takeConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QUERY_RIGHTS_BY_RESOURCE)) {
            preparedStatement.setString(1, resource); // attention for resource
            try (ResultSet result = preparedStatement.executeQuery()) {
                ACL acl = mapToACL(result);
                return acl;
            }
        } catch (SQLException | ConnectionPoolException e) {
            throw new DAOException(e);
        }
    }

    private ACL mapToACL(ResultSet resultSet) throws SQLException {
        ACL acl = new ACL();
        while (resultSet.next()) {
            String group = resultSet.getString(COLUMN_ACL_GROUP);
            for (UserAction action : UserAction.values()) {
                if (resultSet.getBoolean(action.name())) {
                    acl.addAction(group, action);
                }
            }
        }
        return acl;
    }
}