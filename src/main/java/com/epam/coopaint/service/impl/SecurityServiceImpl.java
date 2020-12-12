package com.epam.coopaint.service.impl;

import com.epam.coopaint.dao.GenericDAO;
import com.epam.coopaint.dao.SecurityDAO;
import com.epam.coopaint.dao.UserDAO;
import com.epam.coopaint.dao.impl.DAOFactory;
import com.epam.coopaint.dao.impl.TransactionManager;
import com.epam.coopaint.domain.*;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.SecurityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;
import java.util.*;

import static com.epam.coopaint.dao.impl.LocationData.SERVE_PATH_AVATAR;
import static com.epam.coopaint.domain.ACLData.GROUP_ALL;
import static com.epam.coopaint.domain.ACLData.GROUP_GUEST;
import static com.epam.coopaint.util.StringUtil.standardUUID;

class SecurityServiceImpl implements SecurityService {
    private static Logger logger = LogManager.getLogger();

    public ACL readAcl(String resource) throws ServiceException {
        SecurityDAO securityDAO = DAOFactory.INSTANCE.createSecurityDAO();
        var transaction = new TransactionManager();
        try {
            transaction.begin((GenericDAO) securityDAO);
            ACL acl = securityDAO.getACL(resource);
            return acl;
        } catch (DAOException exception) {
            transaction.rollback();
            throw new ServiceException();
        } finally {
            transaction.end();
        }
    }

    @Override
    public ExtendedAclDTO readExtendedAcl(String resourceUUID) throws ServiceException {
        UserDAO userDAO = DAOFactory.INSTANCE.createUserDAO();
        var transaction = new TransactionManager();
        try {
            transaction.begin((GenericDAO) userDAO);
            ACL usersACL = readAcl(resourceUUID);
            ExtendedAclDTO extendedACL = new ExtendedAclDTO().setUsers(new ArrayList<>()).setGuests(new HashSet<>());
            for (var entry : usersACL.getAcl().entrySet()) {
                String actor = entry.getKey();
                if (GROUP_GUEST.equals(actor) || GROUP_ALL.equals(actor)) {
                    extendedACL.setGuests(entry.getValue());
                } else {
                    User user = userDAO.getUser(UUID.fromString(standardUUID(actor)));
                    user.setAvatar(Paths.get(SERVE_PATH_AVATAR, user.getAvatar()).toString());
                    Set<ResourceAction> availableActions = entry.getValue();
                    extendedACL.getUsers().add(new UserResourceActions(user, availableActions));
                }
            }
            return extendedACL;
        } catch (DAOException e) {
            transaction.rollback();
            e.printStackTrace();
            throw new ServiceException();
        } finally {
            transaction.end();
        }
    }

    @Override
    public void updateAcl(String resourceUUID, ACL acl) {
        SecurityDAO securityDAO = DAOFactory.INSTANCE.createSecurityDAO();
        var transaction = new TransactionManager();
        try {
            transaction.begin((GenericDAO) securityDAO);
            for (var entry : acl.getAcl().entrySet()) {
                securityDAO.updateACL(resourceUUID, entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            transaction.end();
        }
    }

    @Override
    public ExtendedAclDTO updateAllAcl(ACL acl) {
        return null;
    }

    @Override
    public ExtendedAclDTO deleteAcl(String resourceUUID, String actorUUID) {
        return null;
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
        ACL resourceACL = readAcl(resource);
        // determine common groups
        Set<String> groupsIntersection = new HashSet<>(actor.getGroups());
        groupsIntersection.retainAll(resourceACL.getGroups());
        groupsIntersection.add(GROUP_ALL);
        boolean canAccess = canAccess(resourceACL, groupsIntersection, action);
        return canAccess;
    }

    @Override
    public boolean canAccess(List<String> resources, ResourceAction action, User user)
            throws ServiceException {
        boolean canAccess = false;
        for (String resource : resources) {
            if (this.canAccess(resource, action, user)) {
                canAccess = true;
                break;
            }
        }
        return canAccess;
    }

    @Override
    public void createAcl(String userEmail, String resourceUUID, Set<ResourceAction> actions) {
        UserDAO userDAO = DAOFactory.INSTANCE.createUserDAO();
        SecurityDAO securityDAO = DAOFactory.INSTANCE.createSecurityDAO();
        var transaction = new TransactionManager();
        try {
            transaction.begin((GenericDAO) userDAO, (GenericDAO) securityDAO);
            User user = userDAO.getUsers(userEmail).get(0);
            securityDAO.createACL(resourceUUID, user.getUuid().toString(), actions);
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            transaction.end();
        }
    }
}
