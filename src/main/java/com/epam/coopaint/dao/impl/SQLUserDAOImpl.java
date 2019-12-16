package com.epam.coopaint.dao.impl;

import com.epam.coopaint.dao.GenericDAO;
import com.epam.coopaint.dao.RsToObject;
import com.epam.coopaint.dao.UserDAO;
import com.epam.coopaint.domain.SignInUpBundle;
import com.epam.coopaint.domain.User;
import com.epam.coopaint.exception.DAOException;
import com.epam.coopaint.util.Encryptor;
import com.epam.coopaint.util.LangPack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static com.epam.coopaint.dao.impl.SQLColumns.*;

class SQLUserDAOImpl extends GenericDAO implements UserDAO {
    private static Logger logger = LogManager.getLogger();
    private static final int VALIDATION_LINK_LENGTH = 64;
    private static final String QUERY_USER_ADD = "INSERT INTO user (user_name, user_email, user_hash, user_salt) VALUES (?, ?, ?, ?)";
    private static final String QUERY_USER_FETCH_BY_EMAIL = "SELECT * FROM user WHERE user_email=?";
    private static final String QUERY_USER_FETCH_BY_UUID = "SELECT * FROM user WHERE user_uuid=?";
    private static final String QUERY_USER_UPDATE_AVATAR = "UPDATE user SET user_avatar=? WHERE user_id=?";
    private static final String QUERY_USER_UPDATE = "UPDATE user SET " +
            "user_name=COALESCE(?, user_name)," +
            "user_email=COALESCE(?, user_email)," +
            "user_hash=COALESCE(?, user_hash)," +
            "user_salt=COALESCE(?, user_salt)," +
            "user_lang=COALESCE(?, user_lang)" +
            " WHERE user_uuid=?";

    static RsToObject<User> MAPPER_USER_ID = (s, u) -> u.setId(s.getLong(COLUMN_USER_ID));
    static RsToObject<User> MAPPER_USER_UUID = (s, u) -> u.setUuid(Encryptor.bytesToUuid(s.getBytes(COLUMN_USER_UUID)));
    static RsToObject<User> MAPPER_USER_GROUPS = (s, u) -> u.setGroups(new HashSet<>());
    static RsToObject<User> MAPPER_USER_NAME = (s, u) -> u.setName(s.getString(COLUMN_USER_NAME));
    static RsToObject<User> MAPPER_USER_EMAIL = (s, u) -> u.setEmail(s.getString(COLUMN_USER_EMAIL));
    static RsToObject<User> MAPPER_USER_HASH = (s, u) -> u.setHash(s.getBytes(COLUMN_USER_HASH));
    static RsToObject<User> MAPPER_USER_SALT = (s, u) -> u.setSalt(s.getBytes(COLUMN_USER_SALT));
    static RsToObject<User> MAPPER_USER_AVATAR = (s, u) -> u.setAvatar(s.getString(COLUMN_USER_AVATAR));
    static RsToObject<User> MAPPER_USER_LANG = (s, u) -> u.setLang(LangPack.valueOf(s.getString(COLUMN_USER_LANG)));
    static RsToObject<User> MAPPER_USER_AUTH = (s, u) -> u.setAuth(true);

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
    public User getUser(UUID uuid) throws DAOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_USER_FETCH_BY_UUID)) {
            preparedStatement.setBytes(1, Encryptor.uuidToBytes(uuid));
            try (ResultSet result = preparedStatement.executeQuery()) {
                var mapper = new RsToObjectListMapper<User>(List.of(
                        MAPPER_USER_ID,
                        MAPPER_USER_UUID,
                        MAPPER_USER_GROUPS,
                        MAPPER_USER_NAME,
                        MAPPER_USER_EMAIL,
                        MAPPER_USER_HASH,
                        MAPPER_USER_SALT,
                        MAPPER_USER_AVATAR,
                        MAPPER_USER_LANG,
                        MAPPER_USER_AUTH
                ));
                List<User> users = mapper.mapToList(result, User::new);
                if (users.size() > 0) {
                    return users.get(0);
                } else {
                    throw new DAOException("No such user with id: " + uuid + "found");
                }
            } catch (Exception e) {
                throw new DAOException("Failed to map user from id: " + uuid + "found");
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to get user by id: " + uuid, e);
        }
    }

    // updater must contain valid uuid
    @Override
    public void update(User updater) throws DAOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_USER_UPDATE)) {
            preparedStatement.setString(1, updater.getName());
            preparedStatement.setString(2, updater.getEmail());
            preparedStatement.setBytes(3, updater.getHash());
            preparedStatement.setBytes(4, updater.getSalt());
            preparedStatement.setString(5, updater.getLang() != null ? updater.getLang().name() : null);
            preparedStatement.setBytes(6, Encryptor.uuidToBytes(updater.getUuid()));
            int n = preparedStatement.executeUpdate();
            if (n == 1) {
                logger.info("Updated user avatar, user_id: " + updater.getUuid());
            } else {
                throw new DAOException("Failed to update avatar.");
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public List<User> getUsers(String email) throws DAOException {
        try (PreparedStatement selectStatement = connection.prepareStatement(QUERY_USER_FETCH_BY_EMAIL)) {
            selectStatement.setString(1, email);
            try (ResultSet result = selectStatement.executeQuery()) {
                var mapper = new RsToObjectListMapper<User>(List.of(
                        MAPPER_USER_ID,
                        MAPPER_USER_UUID,
                        MAPPER_USER_GROUPS,
                        MAPPER_USER_NAME,
                        MAPPER_USER_EMAIL,
                        MAPPER_USER_HASH,
                        MAPPER_USER_SALT,
                        MAPPER_USER_AVATAR,
                        MAPPER_USER_LANG,
                        MAPPER_USER_AUTH
                ));
                List<User> users = mapper.mapToList(result, User::new);
                return users;
            } catch (Exception e) {
                throw new DAOException("Failed to map user from email: " + email, e);
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
}
