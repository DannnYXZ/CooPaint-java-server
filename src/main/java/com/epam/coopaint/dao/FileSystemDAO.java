package com.epam.coopaint.dao;

import com.epam.coopaint.exception.ServiceException;

import java.io.InputStream;

public interface FileSystemDAO {
    String save(InputStream in, String targetDirectoryPath) throws ServiceException;
    void remove(String targetFile) throws ServiceException;
}
