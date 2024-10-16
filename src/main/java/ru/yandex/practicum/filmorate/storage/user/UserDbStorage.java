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
    public boolean deleteUser(User user) {
        String anySql = "delete from USERS where user_id = ?";
        return jdbcTemplate.update(anySql, user.getId()) > 0;
    }
}
