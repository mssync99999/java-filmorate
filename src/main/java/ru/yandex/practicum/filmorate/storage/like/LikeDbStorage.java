package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;


@Component
public class LikeDbStorage implements LikeStorage {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Long> getLikes(Long filmId) {
        String anySql = "SELECT user_id FROM likes WHERE film_id = ?";
        return jdbcTemplate.queryForList(anySql, Long.class, filmId);
    }


    @Override
    public void addLike(long filmId, long userId) {
        String anySql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(anySql, filmId, userId);

    }

    @Override
    public void deleteLike(long filmId, long userId) {
        String anySql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(anySql, filmId, userId);
    }

}
