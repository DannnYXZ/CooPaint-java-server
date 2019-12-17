package com.epam.coopaint.dao.impl;

import com.epam.coopaint.dao.FileSystemDAO;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.util.Encryptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class FileSystemDAOImpl implements FileSystemDAO {
    private static Logger logger = LogManager.getLogger();
    private static int FILE_NAME_LENGTH = 12;
    private static int MAX_ATTEMPTS = 3;

    @Override
    public String save(InputStream in, String targetDirectoryPath) throws ServiceException {
        // in case of name collisions (probability = 1 / (61^LENGTH))
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            String newFileName = Encryptor.generateAlphaNumHash(FILE_NAME_LENGTH);
            Path targetPath = Paths.get(targetDirectoryPath, newFileName);
            try {
                Files.copy(in, targetPath);
                return newFileName;
            } catch (IOException e) {
                logger.info("Failed to save file: " + newFileName, e);
            }
        }
        throw new ServiceException("Failed to save file.");
    }

    @Override
    public void remove(String targetFile) throws ServiceException {
        try {
            Files.delete(Paths.get(targetFile));
        } catch (IOException e) {
            throw new ServiceException("Failed to delete file: " + targetFile, e);
        }
    }
}
