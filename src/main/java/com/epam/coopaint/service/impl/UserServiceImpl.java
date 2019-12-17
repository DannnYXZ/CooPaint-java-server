package com.epam.coopaint.service.impl;

import com.epam.coopaint.dao.FileSystemDAO;
import com.epam.coopaint.dao.GenericDAO;
import com.epam.coopaint.dao.UserDAO;
import com.epam.coopaint.dao.impl.DAOFactory;
import com.epam.coopaint.dao.impl.TransactionManager;
import com.epam.coopaint.domain.SignInUpBundle;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.UserService;
import com.epam.coopaint.util.Encryptor;
import com.epam.coopaint.util.LangPack;
import com.epam.coopaint.util.MailSender;
import com.epam.coopaint.validator.UserValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static com.epam.coopaint.dao.impl.LocationData.SERVE_PATH_AVATAR;
import static com.epam.coopaint.dao.impl.LocationData.STORAGE_PATH_AVATAR;
import static com.epam.coopaint.domain.ACLData.*;

class UserServiceImpl implements UserService {
    private static Logger logger = LogManager.getLogger();
    private static final String MESSAGE_WELCOME = "Welcome to CooPainT! " +
            "Now you can manage your boards! (create, update, delete)";
    @Override
    public User singIn(SignInUpBundle bundle) throws ServiceException {
        DAOFactory daoObjectFactory = DAOFactory.INSTANCE;
        UserDAO userDAO = daoObjectFactory.createUserDAO();
        var transaction = new TransactionManager();
        try {
            transaction.begin((GenericDAO) userDAO);
            User user = userDAO.signIn(bundle);
            transaction.commit();
            user.getGroups().add(GROUP_USER); // TODO: load groups from db
            wireAvatarServePath(user);
            return user;
        } catch (DAOException e) {
            transaction.rollback();
            throw new ServiceException("Failed to sign in.", e);
        } finally {
            transaction.end();
        }
    }

    private void wireAvatarServePath(User user) {
        if (!user.getAvatar().isEmpty()) {
            user.setAvatar(Paths.get(SERVE_PATH_AVATAR, user.getAvatar()).toString());
        }
    }

    @Override
    public List<User> getUsersByEmail(String email) throws ServiceException {
        UserDAO userDAO = DAOFactory.INSTANCE.createUserDAO();
        var transaction = new TransactionManager();
        try {
            transaction.begin((GenericDAO) userDAO);
            List<User> users = userDAO.getUsers(email);
            transaction.commit();
            return users;
        } catch (DAOException e) {
            transaction.rollback();
            throw new ServiceException(e);
        } finally {
            transaction.end();
        }
    }

    @Override
    public User createGuest() {
        User guest = new User();
        guest.setName(GUEST_NAME_DEFAULT);
        guest.setLang(LangPack.EN);
        guest.setAuth(false);
        guest.setUuid(UUID.randomUUID());
        guest.setGroups(new HashSet<>()).getGroups().add(GROUP_GUEST);
        return guest;
    }

    @Override
    public User update(User updater) throws ServiceException {
        UserDAO userDAO = DAOFactory.INSTANCE.createUserDAO();
        var transaction = new TransactionManager();
        try {
            transaction.begin((GenericDAO) userDAO);
            if (updater.getPassword() != null) {
                Encryptor encryptor = Encryptor.getInstance();
                encryptor.generateDidgest(updater.getPassword());
                updater.setHash(encryptor.getCurrentHash());
                updater.setSalt(encryptor.getCurrentSalt());
            }
            userDAO.update(updater);
            User updatedUser = userDAO.getUser(updater.getUuid());
            updatedUser.getGroups().add(GROUP_USER); // TODO: load groups from db
            transaction.commit();
            wireAvatarServePath(updatedUser);
            return updatedUser;
        } catch (DAOException e) {
            transaction.rollback();
            throw new ServiceException(e);
        } finally {
            transaction.end();
        }
    }

    @Override
    public User signUp(SignInUpBundle signUpBundle) throws ServiceException {
        if (UserValidator.INSTANCE.isValid(signUpBundle)) {
            UserDAO userDAO = DAOFactory.INSTANCE.createUserDAO();
            var transaction = new TransactionManager();
            try {
                transaction.begin((GenericDAO) userDAO);
                userDAO.signUp(signUpBundle);
                List<User> users = userDAO.getUsers(signUpBundle.getEmail());
                transaction.commit();
                if (users.size() != 0) {
                    User user = users.get(0);
                    user.getGroups().add(GROUP_USER);
                    // notify user
                    MailSender sender = MailSender.getInstance();
                    sender.sendMail(MESSAGE_WELCOME, user.getEmail());
                    return user;
                } else {
                    throw new ServiceException("No user with email: " + signUpBundle.getEmail());
                }
            } catch (DAOException e) {
                transaction.rollback();
                throw new ServiceException("Failed to signUp user.", e);
            } finally {
                transaction.end();
            }
        } else {
            throw new ServiceException("Invalid user data.");
        }
    }

    @Override
    public void updateAvatar(UUID uuid, String newAvatarFileName) throws ServiceException {
        // validate length
        UserDAO userDAO = DAOFactory.INSTANCE.createUserDAO();
        var transaction = new TransactionManager();
        // 1 - get user by id
        try {
            transaction.begin((GenericDAO) userDAO);
            User user = userDAO.getUser(uuid);
            String previousAvatarName = user.getAvatar();
            userDAO.updateAvatar(user.getId(), newAvatarFileName);
            transaction.commit();
            if (!previousAvatarName.isEmpty()) {
                FileSystemDAO fileSystemDAO = DAOFactory.INSTANCE.createFileSystemDAO();
                fileSystemDAO.remove(Paths.get(STORAGE_PATH_AVATAR, previousAvatarName).toString());
            }
        } catch (DAOException e) {
            transaction.rollback();
            throw new ServiceException("Failed to update avatar.", e);
        } finally {
            transaction.end();
        }
    }
}
