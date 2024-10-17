package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;


@Component
public class GenreDbStorage implements GenreStorage {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void setGenresByFilmId(Film film) {
        if (film.getGenres() != null) {
            jdbcTemplate.batchUpdate("insert into film_to_genres (film_id, genre_id) values (?, ?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    preparedStatement.setLong(1, film.getId());
                    preparedStatement.setInt(2, (new ArrayList<>(new HashSet<>(film.getGenres()))).get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return (new ArrayList<>(new HashSet<>(film.getGenres()))).size();
                }
            });
        }
    }

    @Override
    public void updateGenresByFilmId(Film film) {
        String sqlD = "delete from film_to_genres where film_id = ?";  //(!)
        jdbcTemplate.update(sqlD, film.getId());
        setGenresByFilmId(film);
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
