package com.epam.coopaint.dao;

import com.epam.coopaint.domain.ACL;
import com.epam.coopaint.domain.ResourceAction;
import com.epam.coopaint.exception.DAOException;
import java.util.Set;

public interface SecurityDAO {
    void createACL(String resourceUUID, String actorUUID, Set<ResourceAction> actions) throws DAOException;
    ACL getACL(String resourceUUID) throws DAOException;
    void updateACL(String resourceUUID, String actorUUID, Set<ResourceAction> actions) throws DAOException;
}
