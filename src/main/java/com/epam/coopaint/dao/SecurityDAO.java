package com.epam.coopaint.dao;

import com.epam.coopaint.domain.ACL;
import com.epam.coopaint.exception.DAOException;

public interface SecurityDAO {
    ACL getACL(String resource) throws DAOException;
}
