package com.epam.coopaint.service;

import com.epam.coopaint.domain.ACL;
import com.epam.coopaint.domain.ExtendedAclDTO;
import com.epam.coopaint.domain.ResourceAction;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.ServiceException;
import java.util.List;
import java.util.Set;

public interface SecurityService {
    boolean canAccess(String resource, ResourceAction action, User actor) throws ServiceException;

    boolean canAccess(List<String> resources, ResourceAction action, User user) throws ServiceException;

    void createAcl(String userEmail, String resourceUUID, Set<ResourceAction> actions);

    ACL readAcl(String resourceUUID) throws ServiceException;

    ExtendedAclDTO readExtendedAcl(String resourceUUID) throws ServiceException;

    void updateAcl( String resourceUUID, ACL acl);

    ExtendedAclDTO updateAllAcl(ACL acl);

    ExtendedAclDTO deleteAcl(String resourceUUID, String actorUUID);
}
