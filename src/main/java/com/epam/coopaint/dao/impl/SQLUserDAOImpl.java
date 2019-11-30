package com.epam.coopaint.dao.impl;

import com.epam.coopaint.dao.GenericDAO;
import com.epam.coopaint.dao.UserDAO;
import com.epam.coopaint.domain.SignInUpBundle;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.ConnectionPoolException;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.util.Encryptor;
import com.epam.coopaint.util.LangPack;
import com.epam.coopaint.util.MailSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.epam.coopaint.dao.impl.SQLData.*;

class SQLUserDAOImpl extends GenericDAO implements UserDAO {
    private static Logger logger = LogManager.getLogger();
    private static final int VALIDATION_LINK_LENGTH = 64;
    private static final String QUERY_USER_ADD = "INSERT INTO user (user_name, user_email, user_hash, user_salt) VALUES (?, ?, ?, ?)";
    private static final String QUERY_USER_FETCH_BY_EMAIL = "SELECT * FROM user WHERE user_email=?";
    private static final String QUERY_USER_FETCH_BY_ID = "SELECT * FROM user WHERE user_id=?";
    private static final String QUERY_USER_UPDATE_AVATAR = "UPDATE user SET user_avatar=? WHERE user_id=?";

    @Override
    public User signIn(SignInUpBundle bundle) throws DAOException {
        List<User> users = getUsers(bundle.getEmail());
        if (users.isEmpty()) {
            throw new DAOException("No such user: " + bundle.getEmail());
        }
        User user = users.get(0);
        Encryptor encryptor = Encryptor.getInstance();
        encryptor.generateDidgest(bundle.getPassword(), user.getSalt());
        if (Arrays.equals(user.getHash(), encryptor.getCurrentHash())) {
            return user;
        } else {
            throw new DAOException("Wrong password.");
        }
    }

    @Override
    public User getUser(long id) throws DAOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_USER_FETCH_BY_ID)) {
            preparedStatement.setLong(1, id);
            try (ResultSet result = preparedStatement.executeQuery()) {
                List<User> users = mapToUserList(result);
                if (users.size() > 0) {
                    return users.get(0);
                } else {
                    throw new DAOException("No such user with id: " + id + "found");
                }
            }
        } catch (SQLException  e) {
            throw new DAOException("Failed to get user by id: " + id, e);
        }
    }

    @Override
    public List<User> getUsers(String email) throws DAOException {
        try (PreparedStatement selectStatement = connection.prepareStatement(QUERY_USER_FETCH_BY_EMAIL)) {
            selectStatement.setString(1, email);
            try (ResultSet result = selectStatement.executeQuery()) {
                List<User> users = mapToUserList(result);
                return users;
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to get user by email: " + email, e);
        }
    }

    @Override
    public void signUp(SignInUpBundle bundle) throws DAOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_USER_ADD)) {
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
                throw new DAOException("Database error.");
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to register user.", e);
        }
    }

    @Override
    public void updateAvatar(long userId, String newAvatarPath) throws DAOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_USER_UPDATE_AVATAR)) {
            preparedStatement.setString(1, newAvatarPath);
            preparedStatement.setLong(2, userId);
            int n = preparedStatement.executeUpdate();
            if (n == 1) {
                logger.info("Updated user avatar, user_id: " + userId);
            } else {
                throw new DAOException("Failed to update avatar.");
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private List<User> mapToUserList(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            var user = new User();
            user.setId(resultSet.getLong(COLUMN_USER_ID));
            user.setUuid(Encryptor.bytesToUuid(resultSet.getBytes(COLUMN_USER_UUID)));
            user.setName(resultSet.getString(COLUMN_USER_NAME));
            user.setEmail(resultSet.getString(COLUMN_USER_EMAIL));
            user.setHash(resultSet.getBytes(COLUMN_USER_HASH));
            user.setSalt(resultSet.getBytes(COLUMN_USER_SALT));
            user.setAvatar(resultSet.getString(COLUMN_USER_AVATAR));
            user.setLang(LangPack.valueOf(resultSet.getString(COLUMN_USER_LANG)));
            user.setAuth(true);
            users.add(user);
        }
        return users;
    }
}
