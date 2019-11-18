package com.epam.coopaint.service;

import com.epam.coopaint.exception.ServiceException;

import java.io.InputStream;

public interface FileSystemService {
    String save(InputStream in, String targetDirectoryPath) throws ServiceException;
    void remove(String targetFile) throws ServiceException;
}
