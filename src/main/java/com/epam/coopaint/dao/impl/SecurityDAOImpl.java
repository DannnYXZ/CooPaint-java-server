package com.epam.coopaint.dao.impl;

import com.epam.coopaint.dao.GenericDAO;
import com.epam.coopaint.dao.SecurityDAO;
import com.epam.coopaint.domain.ACL;
import com.epam.coopaint.domain.ResourceAction;
import com.epam.coopaint.exception.DAOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import static com.epam.coopaint.dao.impl.SQLColumns.COLUMN_ACL_GROUP;
import static com.epam.coopaint.util.StringUtil.compactUUID;

class SecurityDAOImpl extends GenericDAO implements SecurityDAO {
    private static final String QUERY_RIGHTS_BY_RESOURCE = "SELECT * from acl WHERE resource=?";

    @Override
    public ACL getACL(String resource) throws DAOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_RIGHTS_BY_RESOURCE)) {
            preparedStatement.setString(1, compactUUID(resource));
            try (ResultSet result = preparedStatement.executeQuery()) {
                ACL acl = mapToACL(result);
                return acl;
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void updateACL(String resourceUUID, String actorUUID, Set<ResourceAction> actions) throws DAOException {
        try (Statement statement = connection.createStatement()) {
            StringBuilder query = new StringBuilder("UPDATE acl SET ");
            for(var possibleAction: ResourceAction.values()){
                query.append(possibleAction.name()).append("=").append(actions.contains(possibleAction) ? "1, " : "0, ");
            }
            query.deleteCharAt(query.length()-2);
            query.append("WHERE resource='").append(compactUUID(resourceUUID))
                .append("' AND `group`='").append(compactUUID(actorUUID)).append("'");
            statement.executeUpdate(query.toString());
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private ACL mapToACL(ResultSet resultSet) throws SQLException {
        ACL acl = new ACL();
        while (resultSet.next()) {
            String group = resultSet.getString(COLUMN_ACL_GROUP);
            acl.getAcl().put(group, new HashSet<>()); // even if no rights
            for (ResourceAction action : ResourceAction.values()) {
                if (resultSet.getBoolean(action.name())) {
                    acl.addAction(group, action);
                }
            }
        }
        return acl;
    }
}