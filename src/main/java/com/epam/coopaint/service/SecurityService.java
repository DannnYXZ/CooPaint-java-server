package com.epam.coopaint.service;

import com.epam.coopaint.domain.ACL;
import com.epam.coopaint.domain.ExtendedAclDTO;
import com.epam.coopaint.domain.ResourceAction;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.ServiceException;

public interface SecurityService {
    boolean canAccess(String resource, ResourceAction action, User actor) throws ServiceException;

    ExtendedAclDTO createAcl(String userEmail, ACL acl);

    ACL readAcl(String resourceUUID) throws ServiceException;

    ExtendedAclDTO readExtendedAcl(String resourceUUID) throws ServiceException;

    void updateAcl( String resourceUUID, ACL acl);

    ExtendedAclDTO updateAllAcl(ACL acl);

    ExtendedAclDTO deleteAcl(String resourceUUID, String actorUUID);
}
