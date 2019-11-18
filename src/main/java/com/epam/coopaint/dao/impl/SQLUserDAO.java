package com.epam.coopaint.dao.impl;

import com.epam.coopaint.dao.UserDAO;
import com.epam.coopaint.domain.SignInUpBundle;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.ConnectionPoolException;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.pool.ConnectionPoolImpl;
import com.epam.coopaint.util.Encryptor;
import com.epam.coopaint.util.LangPack;
import com.epam.coopaint.util.MailSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.epam.coopaint.dao.impl.SQLData.*;

public class SQLUserDAO implements UserDAO {
    private static Logger logger = LogManager.getLogger();
    private static final int VALIDATION_LINK_LENGTH = 64;
    private static final String QUERY_ADD_USER = "INSERT INTO user (name, email, hash, salt) VALUES (?, ?, ?, ?)";
    private static final String QUERY_FETCH_USER_BY_EMAIL = "SELECT * FROM user WHERE email=?";
    private static final String QUERY_FETCH_USER_BY_ID = "SELECT * FROM user WHERE id=?";
    private static final String QUERY_UPDATE_USER_AVATAR = "UPDATE user SET avatar=? WHERE id=?";

    @Override
    public User signIn(SignInUpBundle bundle) throws DAOException {
        try (Connection connection = ConnectionPoolImpl.getInstance().takeConnection()) {
            try {
                User user = getUser(bundle.getEmail());
                Encryptor encryptor = Encryptor.getInstance();
                encryptor.generateDidgest(bundle.getPassword(), user.getSalt());
                if (Arrays.equals(user.getHash(), encryptor.getCurrentHash())) {
                    return user;
                } else {
                    throw new DAOException("Wrong password.");
                }
            } catch (DAOException e) {
                throw new DAOException("No such user: " + bundle.getEmail(), e);
            }
        } catch (ConnectionPoolException | SQLException e) {
            throw new DAOException("Internal error", e);
        }
    }

    @Override
    public User getUser(long id) throws DAOException {
        try (Connection connection = ConnectionPoolImpl.getInstance().takeConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FETCH_USER_BY_ID)) {
            preparedStatement.setLong(1, id);
            try (ResultSet result = preparedStatement.executeQuery()) {
                List<User> users = mapToUserList(result);
                if (users.size() > 0) {
                    return users.get(0);
                } else {
                    throw new DAOException("No such user with id: " + id + "found");
                }
            }
        } catch (SQLException | ConnectionPoolException e) {
            throw new DAOException("Failed to get user by id: " + id, e);
        }
    }

    @Override
    public User getUser(String email) throws DAOException {
        try (Connection connection = ConnectionPoolImpl.getInstance().takeConnection();
             PreparedStatement selectStatement = connection.prepareStatement(QUERY_FETCH_USER_BY_EMAIL)) {
            selectStatement.setString(1, email);
            try (ResultSet result = selectStatement.executeQuery()) {
                List<User> users = mapToUserList(result);
                if (users.size() > 0) {
                    return users.get(0);
                } else {
                    throw new DAOException("No such user with email: " + email + " found.");
                }
            }
        } catch (SQLException | ConnectionPoolException e) {
            throw new DAOException("Failed to get user by email: " + email, e);
        }
    }

    @Override
    public void signUp(SignInUpBundle bundle) throws DAOException {
        try (Connection connection = ConnectionPoolImpl.getInstance().takeConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QUERY_ADD_USER)) {
            preparedStatement.setString(1, bundle.getEmail());
            preparedStatement.setString(2, bundle.getEmail());
            Encryptor encryptor = Encryptor.getInstance();
            encryptor.generateDidgest(bundle.getPassword());
            preparedStatement.setBytes(3, encryptor.getCurrentHash());
            preparedStatement.setBytes(4, encryptor.getCurrentSalt());
            int n = preparedStatement.executeUpdate();
            if (n == 1) {
                logger.info("Registered user: " + bundle.getEmail());
                MailSender sender = MailSender.getInstance();
                String validationLink = Encryptor.getInstance().generateRandomHash(VALIDATION_LINK_LENGTH);
                //sender.sendMail(validationLink, bundle.getEmail()); // TODO: long hash link + session status
            } else {
                throw new DAOException("Failed to execute user registration.");
            }
        } catch (ConnectionPoolException e) {
            throw new DAOException("Failed to acquire database connection.", e);
        } catch (SQLException e) {
            throw new DAOException("Failed to register user.", e);
        }
    }

    @Override
    public void updateAvatar(long userId, String newAvatarPath) throws DAOException {
        try (Connection connection = ConnectionPoolImpl.getInstance().takeConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QUERY_UPDATE_USER_AVATAR)) {
            preparedStatement.setString(1, newAvatarPath);
            preparedStatement.setLong(2, userId);
            int n = preparedStatement.executeUpdate();
            if (n == 1) {
                logger.info("Updated user avatar, user_id: " + userId);
            } else {
                throw new DAOException("Failed to update avatar.");
            }
        } catch (SQLException | ConnectionPoolException e) {
            throw new DAOException(e);
        }
    }

    private List<User> mapToUserList(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            var user = new User();
            user.setId(resultSet.getLong(COLUMN_ID));
            user.setName(resultSet.getString(COLUMN_NAME));
            user.setEmail(resultSet.getString(COLUMN_EMAIL));
            user.setHash(resultSet.getBytes(COLUMN_HASH));
            user.setSalt(resultSet.getBytes(COLUMN_SALT));
            user.setAvatar(resultSet.getString(COLUMN_AVATAR));
            user.setLang(LangPack.valueOf(resultSet.getString(COLUMN_LANG)));
            user.setAuth(true);
            users.add(user);
        }
        return users;
    }
}
