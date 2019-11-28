package com.epam.coopaint.service.impl;

import com.epam.coopaint.dao.UserDAO;
import com.epam.coopaint.dao.impl.DAOFactory;
import com.epam.coopaint.dao.GenericDAO;
import com.epam.coopaint.dao.impl.TransactionManager;
import com.epam.coopaint.domain.SignInUpBundle;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.exception.ServiceException;
import com.epam.coopaint.service.FileSystemService;
import com.epam.coopaint.service.UserService;
import com.epam.coopaint.util.LangPack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static com.epam.coopaint.domain.ACLData.*;
import static com.epam.coopaint.domain.LocationData.STORAGE_PATH_AVATAR;

class UserServiceImpl implements UserService {
    private static Logger logger = LogManager.getLogger();

    // validate all input data
    @Override
    public User singIn(SignInUpBundle bundle) throws ServiceException {
        DAOFactory daoObjectFactory = DAOFactory.INSTANCE;
        UserDAO userDAO = daoObjectFactory.createUserDAO();
        var transaction = new TransactionManager();
        try {
            transaction.begin((GenericDAO) userDAO);
            // TODO: validate email n password
            User user = userDAO.signIn(bundle);
            transaction.commit();
            user.getGroups().add(GROUP_USER); // TODO: load groups from db
            return user;
        } catch (DAOException e) {
            transaction.rollback();
            throw new ServiceException("Failed to sign in.", e);
        } finally {
            transaction.end();
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
        guest.getGroups().add(GROUP_GUEST); // TODO: load groups from db
        return guest;
    }

    @Override
    public void singOut(String login) throws ServiceException {

    }

    @Override
    public User signUp(SignInUpBundle signUpBundle) throws ServiceException {
        // validation
        //if (UserValidator.INSTANCE.isValid(signUpBundle)) {
        if (true) { //FIXME
            UserDAO userDAO = DAOFactory.INSTANCE.createUserDAO();
            var transaction = new TransactionManager();
            try {
                userDAO.signUp(signUpBundle);
                List<User> users = userDAO.getUsers(signUpBundle.getEmail());
                transaction.commit();
                if (users.size() != 0) {
                    User user = users.get(0);
                    user.getGroups().add(GROUP_USER);
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
            throw new ServiceException("Invalid user data."); // TODO: reason - aka validator.getReason()
        }
    }

    @Override
    public void updateAvatar(long userId, String newAvatarFileName) throws ServiceException {
        // validate length
        UserDAO userDAO = DAOFactory.INSTANCE.createUserDAO();
        var transaction = new TransactionManager();
        // 1 - get user by id
        try {
            transaction.begin((GenericDAO) userDAO);
            User user = userDAO.getUser(userId);
            String previousAvatarName = user.getAvatar();
            userDAO.updateAvatar(user.getId(), newAvatarFileName);
            transaction.commit();
            // FIXME: service access -> no move to DAO level
            if (!previousAvatarName.isEmpty()) {
                FileSystemService fsService = ServiceFactory.getInstance().getFileSystemService(); // FIXME: DAO class
                fsService.remove(Paths.get(STORAGE_PATH_AVATAR, previousAvatarName).toString());
            }
        } catch (DAOException e) {
            transaction.rollback();
            throw new ServiceException("Failed to update avatar.", e);
        }finally {
            transaction.end();
        }
    }
}
