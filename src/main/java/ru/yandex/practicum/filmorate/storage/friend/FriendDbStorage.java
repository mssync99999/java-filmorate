package ru.yandex.practicum.filmorate.storage.friend;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.user.UserMapper;



@Slf4j
@Component
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(long id, long friendId) {

        String sqlS = "select count(*) from friendships where user_id = ? and friend_id = ?";
        String sqlT = "insert into friendships (user_id, friend_id, status) values (?, ?, true)";
        String sqlF = "insert into friendships (user_id, friend_id, status) values (?, ?, false)";
        String sqlU = "update friendships set status = true where friend_id = ? and user_id = ?";

        Integer rowsSearch = jdbcTemplate.queryForObject(sqlS, Integer.class, friendId, id);
        if (rowsSearch > 0) {
            jdbcTemplate.update(sqlU, friendId, id);
            jdbcTemplate.update(sqlT, id, friendId);
        } else {
            jdbcTemplate.update(sqlF, id, friendId);
        }
    }

    @Override
    public void deleteFriend(long id, long friendId) {
        String sqlS = "select count(*) from friendships where user_id = ? and friend_id = ?";
        String sqlD = "delete from friendships where user_id = ? and friend_id = ?";
        String sqlU = "update friendships set status = false where friend_id = ? and user_id = ?";

        Integer rowsSearch = jdbcTemplate.queryForObject(sqlS, Integer.class, friendId, id);

        if (rowsSearch > 0) {
            jdbcTemplate.update(sqlU, friendId, id);
        }

        jdbcTemplate.update(sqlD, id, friendId);
    }

    @Override
    public Collection<User> getUserFriends(long id) {
        String anySql = "SELECT * FROM users WHERE user_id in " +
                "(SELECT friend_id FROM friendships WHERE user_id = ?)";
        log.info("!!! проверка !!!");

        return jdbcTemplate.query(anySql, new UserMapper(), id);
    }

    @Override
    public Collection<User> getCommonFriends(long idA, long idB) {
        String anySql = "select * from users where user_id in " +
                "(select friend_id from friendships where user_id in (? , ?) " +
                "group by friend_id having count(distinct user_id) > 1 )";

        return jdbcTemplate.query(anySql, new UserMapper(), idA, idB);
    }
}
