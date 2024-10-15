package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        String anySql = "select * from users";
        return jdbcTemplate.query(anySql, new UserMapper());
    }

    @Override
    public User create(User user) {

        //электронная почта не может быть пустой и должна содержать символ @
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        //логин не может быть пустым и содержать пробелы
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        //дата рождения не может быть в будущем.
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        String anySql = "insert into users (email, login, name, birthday) values (?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(anySql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
            }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        // проверяем необходимые условия
        if (user.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        // проверяем необходимые условия
        if (getUserById(user.getId()) == null) {
            throw new NotFoundException("Id не найден");
        }

        String anySql = "UPDATE users SET name = ?, email = ?, birthday = ?, login = ? WHERE user_id = ?";
        int rowsUpdated = jdbcTemplate.update(anySql, user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getLogin(),
                user.getId());

        return user;
    }

    @Override
    public long getNextId() {
        return 0;
    }

    @Override
    public User getUserById(long id) {
        String anySql = "SELECT * FROM users WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(anySql, new UserMapper(), id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void addFriend(long id, long friendId) {
        // проверяем необходимые условия
        if (getUserById(id) == null) {
            throw new NotFoundException("Id не найден");
        }
        // проверяем необходимые условия
        if (getUserById(friendId) == null) {
            throw new NotFoundException("Id не найден");
        }

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
        // проверяем необходимые условия
        if (getUserById(id) == null) {
            throw new NotFoundException("Id не найден");
        }
        // проверяем необходимые условия
        if (getUserById(friendId) == null) {
            throw new NotFoundException("Id не найден");
        }

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
        // проверяем необходимые условия
        if (getUserById(id) == null) {
            throw new NotFoundException("Id не найден");
        }

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

    @Override
    public boolean deleteUser(User user) {
        String anySql = "delete from USERS where user_id = ?";
        return jdbcTemplate.update(anySql, user.getId()) > 0;
    }
}
