package ru.yandex.practicum.filmorate.dao.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Qualifier("UserDbStorage")
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserDao {

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

    }

    @Override
    public int update(final User user) {
        final String userUpdateSql = "UPDATE filmorate_user SET email = ?, login = ?, nickname = ?, birthday = ? " +
                "WHERE id = ?";
        return jdbcTemplate.update(userUpdateSql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
    }

    @Override
    public Collection<User> findAll() {
        final String sql = "SELECT " +
                "fu.ID, fu.EMAIL, fu.LOGIN, fu.NICKNAME, fu.BIRTHDAY, f.FRIEND_ID,f.FRIENDSHIP_STATUS_ID, fs.STATUS_NAME " +
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
        return jdbcTemplate.query(sql, this::extractToUserList, id).get(0);
    }

    public void addFriend(final long userId, final long friendId, final int status) {
        final String sql = "INSERT INTO friendship VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, status);
    }

    public Collection<User> findFriendsByUserId(long userId) {
//        final String friendsIdsSql = "SELECT friend_id FROM friendship WHERE user_id = ?";
//        final List<Long> friendsIds = jdbcTemplate.queryForList(friendsIdsSql, Long.class, userId);
//        final String inSql;
//        final SqlParameterSource parameters = new MapSqlParameterSource("ids", friendsIds);
//        final String sql = "SELECT " +
//                "fu.ID, fu.EMAIL, fu.LOGIN, fu.NICKNAME, fu.BIRTHDAY, f.FRIEND_ID,f.FRIENDSHIP_STATUS_ID, fs.STATUS_NAME " +
//                "FROM FILMORATE_USER fu LEFT JOIN FRIENDSHIP f ON fu.ID = f.USER_ID " +
//                "LEFT JOIN FRIENDSHIP_STATUS fs ON f.FRIENDSHIP_STATUS_ID = fs.ID WHERE fu.ID IN (:ids)";
//        return jdbcTemplate.query(sql, this::extractToUserList, );

        return null;
    }

    private List<User> extractToUserList(ResultSet rs) throws SQLException, DataAccessException {

        final List<User> users = new ArrayList<>();
        final Map<Long, User> userIdMap = new HashMap<>();
        final Map<Long, Friendship> friendshipIdMap = new HashMap<>();

        while (rs.next()) {

            Long userId = rs.getLong(1);
            User user = userIdMap.get(userId);
            if (user == null) {
                user = new User(
                        userId,
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getString("nickname"),
                        rs.getDate("birthday").toLocalDate()
                );
                users.add(user);
                userIdMap.put(userId, user);
            }

            Long friendshipId = rs.getLong("friend_id");
            if (friendshipId == 0) {
                user.getFriends().addAll(Collections.emptyList());
                break;
            }

            Friendship friendship = friendshipIdMap.get(friendshipId);
            if (friendship == null) {
                friendship = new Friendship();
                friendship.setId(friendshipId);
                friendship.setStatus(rs.getString("status_name"));
                user.getFriends().add(friendship);
                friendshipIdMap.put(friendshipId, friendship);
            }
        }

        return users;
    }
}
