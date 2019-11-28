package com.epam.coopaint.service.impl;

import com.epam.coopaint.dao.impl.DAOFactory;
import com.epam.coopaint.dao.SecurityDAO;
import com.epam.coopaint.domain.ACL;
import com.epam.coopaint.domain.ResourceAction;
import com.epam.coopaint.domain.User;
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
import static com.epam.coopaint.service.impl.SecurityData.ACL_USE_CACHING;

class SecurityServiceImpl implements SecurityService {
    // TODO: synchronize
    private static Logger logger = LogManager.getLogger();
    private Map<String, ACL> cacheACL = new ConcurrentHashMap<>(); // <resource, ACL> - offloading DB

    private ACL getACL(String resource) throws DAOException {
        SecurityDAO securityDAO = DAOFactory.INSTANCE.createSecurityDAO();
        if (ACL_USE_CACHING) {
            ACL acl = this.cacheACL.get(resource);
            if (acl == null) {
                acl = securityDAO.getACL(resource);
                cacheACL.put(resource, acl);
            }
            return acl;
        } else {
            ACL acl = securityDAO.getACL(resource);
            return acl;
        }
    }

    private boolean canAccess(ACL resourceACL, Set<String> actorGroups, ResourceAction action) {
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
    public boolean canAccess(String resource, ResourceAction action, User actor) throws ServiceException {
        try {
            ACL resourceACL = getACL(resource);
            // determine common groups
            Set<String> groupsIntersection = new HashSet<>(actor.getGroups());
            groupsIntersection.retainAll(resourceACL.getGroups());
            groupsIntersection.add(GROUP_ALL);
            boolean canAccess = canAccess(resourceACL, groupsIntersection, action);
            return canAccess;
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
    }
}
