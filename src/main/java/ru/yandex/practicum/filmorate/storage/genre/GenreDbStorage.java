package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.Collection;


@Component
public class GenreDbStorage implements GenreStorage {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> getGenresByFilmId(long filmId) {
        String anySql = "select g.genre_id, g.name as genre_name from genres as g " +
                "INNER JOIN film_to_genres gg on g.genre_id = gg.genre_id " +
                "where gg.film_id = ? ORDER BY g.genre_id asc";
        return jdbcTemplate.query(anySql, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("genre_name")
                ), filmId);
    }

    @Override
    public Collection<Genre> findAll() {
        String anySql = "select genre_id, name from genres order by 1";
        return jdbcTemplate.query(anySql, new GenreMapper());

    }

    @Override
    public Genre getGenreById(int id) {
        String anySql = "select genre_id, name from genres where genre_id = ?";
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(anySql, new GenreMapper(), id);
        } catch (RuntimeException e) {
            throw new NotFoundException("Жанр " + id + " не найден!");
        }
        return genre;
    }
}
