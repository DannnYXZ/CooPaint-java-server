package com.epam.coopaint.service.impl;

import com.epam.coopaint.dao.DAOFactory;
import com.epam.coopaint.dao.SecurityDAO;
import com.epam.coopaint.domain.ACL;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.domain.UserAction;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.SecurityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.epam.coopaint.domain.ACLData.GROUP_ALL;
import static com.epam.coopaint.domain.ACLData.RESOURCE_ALL;

public class SecurityServiceImpl implements SecurityService {
    // TODO: synchronize
    private static Logger logger = LogManager.getLogger();
    private Map<String, ACL> cacheACL = new ConcurrentHashMap<>(); // <resource, ACL> - offloading DB

    private ACL getACL(String resource) throws DAOException {
        SecurityDAO securityDAO = DAOFactory.getInstance().getSecurityDAO();
        ACL acl = this.cacheACL.get(resource);
        if (acl == null) {
            acl = securityDAO.getACL(resource);
            cacheACL.put(resource, acl);
        }
        return acl;
    }

    private boolean canAccess(ACL resourceACL, Set<String> actorGroups, UserAction action) {
        boolean canAccess = false;
        for (var group : actorGroups) {
            if (resourceACL.getActions(group).contains(action)) {
                canAccess = true;
                break;
            }
        }
        return canAccess;
    }

    @Override
    public boolean canAccess(String resource, UserAction action, User actor) throws ServiceException {
        try {
            ACL resourceACL = getACL(resource);
            ACL allACL = getACL(RESOURCE_ALL);
            // determine common groups
            Set<String> groupsIntersection = new HashSet<>(actor.getGroups());
            groupsIntersection.retainAll(resourceACL.getGroups());
            groupsIntersection.add(GROUP_ALL);

            boolean canAccess = canAccess(resourceACL, groupsIntersection, action);
            canAccess |= canAccess(allACL, groupsIntersection, action);
            return canAccess;
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
    }
}
