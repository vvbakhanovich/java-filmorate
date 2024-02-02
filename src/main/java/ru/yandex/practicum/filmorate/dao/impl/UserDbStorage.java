package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(final User user) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "INSERT INTO filmorate_user (email, login, nickname, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey(), "Не удалось добавить пользователя").longValue());
        return user;
    }

    @Override
    public void remove(final long id) {
        final String sql = "DELETE FROM filmorate_user WHERE id = ?";
        int result = jdbcTemplate.update(sql, id);
        if (result != 1) {
            throw new NotFoundException("Пользователь с id '" + id + "' не найден.");
        }
    }

    @Override
    public void update(final User user) {
        final String userUpdateSql = "UPDATE filmorate_user SET email = ?, login = ?, nickname = ?, birthday = ? " +
                "WHERE id = ?";
        int result = jdbcTemplate.update(userUpdateSql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        if (result != 1) {
            throw new NotFoundException("Пользователь с id '" + user.getId() + "' не найден.");
        }
    }

    @Override
    public Collection<User> findAll() {
        final String sql = "SELECT " +
                "fu.ID, fu.EMAIL, fu.LOGIN, fu.NICKNAME, fu.BIRTHDAY, f.FRIEND_ID, f.FRIENDSHIP_STATUS_ID, fs.STATUS_NAME " +
                "FROM FILMORATE_USER fu LEFT JOIN FRIENDSHIP f ON fu.ID = f.USER_ID " +
                "LEFT JOIN FRIENDSHIP_STATUS fs ON f.FRIENDSHIP_STATUS_ID = fs.ID";
        return jdbcTemplate.query(sql, this::extractToUserList);
    }

    @Override
    public User findById(final long id) {
        final String sql = "SELECT " +
                "fu.ID, fu.EMAIL, fu.LOGIN, fu.NICKNAME, fu.BIRTHDAY, f.FRIEND_ID,f.FRIENDSHIP_STATUS_ID, fs.STATUS_NAME " +
                "FROM FILMORATE_USER fu LEFT JOIN FRIENDSHIP f ON fu.ID = f.USER_ID " +
                "LEFT JOIN FRIENDSHIP_STATUS fs ON f.FRIENDSHIP_STATUS_ID = fs.ID WHERE fu.ID = ?";
        User user = jdbcTemplate.query(sql, this::extractToUser, id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id '" + id + "' не найден.");
        }
        return user;
    }

    @Override
    public Collection<User> findFriendsByUserId(final long userId) {
        final String friendsIdsSql = "SELECT friend_id FROM friendship WHERE user_id = ?";
        final String sql = String.format(
                "SELECT fu.ID, fu.EMAIL, fu.LOGIN, fu.NICKNAME, fu.BIRTHDAY, f.FRIEND_ID, f.FRIENDSHIP_STATUS_ID, fs.STATUS_NAME " +
                        "FROM FILMORATE_USER fu LEFT JOIN FRIENDSHIP f ON fu.ID = f.USER_ID " +
                        "LEFT JOIN FRIENDSHIP_STATUS fs ON f.FRIENDSHIP_STATUS_ID = fs.ID WHERE fu.ID IN (%s)", friendsIdsSql);
        return jdbcTemplate.query(sql, this::extractToUserList, userId);
    }

    @Override
    public Collection<User> findCommonFriends(final long userId, final long anotherUserId) {
        final String commonFriendIdsSql = "SELECT fu1.friend_id FROM friendship fu1, friendship fu2 " +
                "WHERE fu1.user_id = ? AND fu2.user_id = ? AND fu1.friend_id = fu2.friend_id";
        final String sql = String.format(
                "SELECT fu.ID, fu.EMAIL, fu.LOGIN, fu.NICKNAME, fu.BIRTHDAY, f.FRIEND_ID, f.FRIENDSHIP_STATUS_ID, fs.STATUS_NAME " +
                        "FROM FILMORATE_USER fu LEFT JOIN FRIENDSHIP f ON fu.ID = f.USER_ID " +
                        "LEFT JOIN FRIENDSHIP_STATUS fs ON f.FRIENDSHIP_STATUS_ID = fs.ID WHERE fu.ID IN (%s)", commonFriendIdsSql);
        return jdbcTemplate.query(sql, this::extractToUserList, userId, anotherUserId);
    }

    @Override
    public Collection<Feed> getFeed(long id) {
        User user = findById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id '" + id + "' не найден.");
        }
        final String sql = "SELECT * FROM FEED_EVENTS WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, this::extractToFeedList, id);
    }

    private User extractToUser(ResultSet rs) throws SQLException, DataAccessException {

        User user = null;
        final Map<Long, User> userIdMap = new HashMap<>();

        while (rs.next()) {

            Long userId = rs.getLong(1);
            user = userIdMap.get(userId);
            if (user == null) {
                user = User.builder()
                        .id(userId)
                        .email(rs.getString("email"))
                        .login(rs.getString("login"))
                        .name(rs.getString("nickname"))
                        .birthday(rs.getDate("birthday").toLocalDate())
                        .build();
                userIdMap.put(userId, user);
            }

            final long friendshipId = rs.getLong("friend_id");
            if (friendshipId == 0) {
                user.getFriends().addAll(Collections.emptyList());
                continue;
            }

            final Friendship friendship = new Friendship();
            friendship.setId(friendshipId);
            friendship.setStatus(rs.getString("status_name"));
            user.getFriends().add(friendship);
        }

        return user;
    }

    private List<User> extractToUserList(ResultSet rs) throws SQLException, DataAccessException {

        final List<User> users = new ArrayList<>();
        final Map<Long, User> userIdMap = new HashMap<>();

        while (rs.next()) {

            Long userId = rs.getLong(1);
            User user = userIdMap.get(userId);
            if (user == null) {
                user = User.builder()
                        .id(userId)
                        .email(rs.getString("email"))
                        .login(rs.getString("login"))
                        .name(rs.getString("nickname"))
                        .birthday(rs.getDate("birthday").toLocalDate())
                        .build();
                users.add(user);
                userIdMap.put(userId, user);
            }

            final long friendshipId = rs.getLong("friend_id");
            if (friendshipId == 0) {
                user.getFriends().addAll(Collections.emptyList());
                continue;
            }

            final Friendship friendship = new Friendship();
            friendship.setId(friendshipId);
            friendship.setStatus(rs.getString("status_name"));
            user.getFriends().add(friendship);
        }

        return users;
    }

    private List<Feed> extractToFeedList(ResultSet rs) throws SQLException, DataAccessException {

        final List<Feed> feed = new ArrayList<>();

        while (rs.next()) {
            Feed currentFeed = Feed.builder()
                    .entityId(rs.getLong("entity_id"))
                    .eventType(EventType.valueOf(rs.getString("event_type")))
                    .eventId(rs.getLong("id"))
                    .operation(Operation.valueOf(rs.getString("operation")))
                    .userId(rs.getLong("user_id"))
                    .timestamp(rs.getTimestamp("publication_time").getTime())
                    .build();
            feed.add(currentFeed);
        }

        return feed;
    }
}

