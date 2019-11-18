package com.epam.coopaint.dao;

import com.epam.coopaint.domain.ACL;
import com.epam.coopaint.domain.Board;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.DAOException;

import java.util.List;

public interface SecurityDAO {
    ACL getACL(String resource) throws DAOException;
}
