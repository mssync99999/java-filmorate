package ru.yandex.practicum.filmorate.storage.film;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Data
@Component
public class FilmDbStorage implements FilmStorage {

    private JdbcTemplate jdbcTemplate;
    private GenreDbStorage genreDbStorage;
    private LikeDbStorage likeDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage, LikeDbStorage likeDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
        this.likeDbStorage = likeDbStorage;
    }

    @Override
    public Collection<Film> findAll() {
        String anySql = "select a.*, b.name as mpa_name from films as a " +
                "INNER JOIN mpas as b on a.mpa_id = b.mpa_id order by a.film_id asc";
        return jdbcTemplate.query(anySql, new FilmMapper(this));
    }

    @Override
    public Film create(Film film) {

        //Дата релиза — не раньше 28 декабря 1895 года
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {

            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        //проверка mpa
        String sqlC = "select count(*) from mpas where mpa_id = ?";
        Integer rowsSearch = jdbcTemplate.queryForObject(sqlC, Integer.class, film.getMpa().getId());
        if (rowsSearch == 0) {
            throw new ValidationException("Рейтинг " + film.getMpa().getId() + " не найден!");
        }

        //проверка genre
        sqlC = "select count(*) from genres where genre_id = ?";
        Collection<Genre> filmGenre = (film.getGenres() != null) ? film.getGenres() : new ArrayList<>();
        for (Genre entry : filmGenre) {
            rowsSearch = jdbcTemplate.queryForObject(sqlC, Integer.class, entry.getId());
            if (rowsSearch == 0) {
                throw new ValidationException("Жанр " + film.getMpa().getId() + " не найден!");
            }
        }


        String anySql = "insert into films (name, description, releaseDate, duration, mpa_id) values (?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(anySql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, Math.toIntExact(film.getMpa().getId()));  //getMpa_id
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        film.setId(id);

        String sqlG = "insert into film_to_genres (film_id, genre_id) values (?, ?)"; // ON CONFLICT DO NOTHING";
        Set<Genre> setGenre = new HashSet<>(filmGenre);
        for (Genre entry: setGenre) {
            jdbcTemplate.update(sqlG, id, entry.getId());
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        //проверка film
        String sqlC = "select count(*) from films where film_id = ?";
        Integer rowsSearch = jdbcTemplate.queryForObject(sqlC, Integer.class, film.getId());
        if (rowsSearch == 0) {
            throw new NotFoundException("Фильм " + film.getId() + " не найден!");
        }

        //проверка mpa
        sqlC = "select count(*) from mpas where mpa_id = ?";
        rowsSearch = jdbcTemplate.queryForObject(sqlC, Integer.class, film.getMpa().getId());
        if (rowsSearch == 0) {
            throw new ValidationException("Рейтинг " + film.getMpa().getId() + " не найден!");
        }

        //проверка genre
        sqlC = "select count(*) from genres where genre_id = ?";

        Collection<Genre> filmGenre = (film.getGenres() != null) ? film.getGenres() : new ArrayList<>();
        for (Genre entry : filmGenre) {
            rowsSearch = jdbcTemplate.queryForObject(sqlC, Integer.class, entry.getId());
            if (rowsSearch == 0) {
                throw new ValidationException("Жанр " + film.getMpa().getId() + " не найден!");
            }
        }

        String anySql = "UPDATE films SET name = ?, description = ?, releaseDate = ?," +
                " duration = ?, mpa_id = ? WHERE film_id = ?";
        int rowsUpdated = jdbcTemplate.update(anySql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),   ////getMpa_id
                film.getId());

        String sqlD = "delete from film_to_genres where film_id = ?";
        jdbcTemplate.update(sqlD, film.getId());

        String sqlG = "insert into film_to_genres (film_id, genre_id) values (?, ?)";

        Set<Genre> setGenre = new HashSet<>(filmGenre);
        for (Genre entry: setGenre) {
            jdbcTemplate.update(sqlG, film.getId(), entry.getId());
        }

        return film;
    }

    @Override
    public long getNextId() {
        return 0;
    }

    @Override
    public Film getFilmById(@Nullable long filmId) {
        String anySql = "select a.*, b.name as mpa_name from films as a " +
                "INNER JOIN mpas as b on a.mpa_id = b.mpa_id WHERE a.film_id = ?";
        return jdbcTemplate.queryForObject(anySql, new FilmMapper(this), filmId);
    }

    @Override
    public Collection<Film> getFilmsPopular(long count) {
        String anySql = "select count(L.user_id) as cnt, a.*, b.name as mpa_name from films as a " +
                "INNER JOIN mpas as b on a.mpa_id = b.mpa_id " +
                "inner join likes L on L.film_id = a.film_id " +
                "group by a.film_id " +
                "having count(L.user_id) " +
                "order by 1 desc " +
                "limit ?";
        return jdbcTemplate.query(anySql, new FilmMapper(this), count);
    }

    @Override
    public void addLike(long filmId, long userId) {
    }

    @Override
    public void deleteLike(long filmId, long userId) {
    }
}
