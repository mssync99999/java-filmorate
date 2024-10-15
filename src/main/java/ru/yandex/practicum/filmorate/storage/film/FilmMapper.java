package ru.yandex.practicum.filmorate.storage.film;

import lombok.Data;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@Data
public class FilmMapper  implements RowMapper<Film> {
    private FilmDbStorage filmDbStorage;
    private LikeDbStorage likeDbStorage;
    private GenreDbStorage genreDbStorage;

    public FilmMapper(FilmDbStorage filmDbStorage) {
        this.filmDbStorage = filmDbStorage;
        this.likeDbStorage = filmDbStorage.getLikeDbStorage();
        this.genreDbStorage = filmDbStorage.getGenreDbStorage();
    }

    public FilmMapper() {
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setLikes(new HashSet<>(likeDbStorage.getLikes(rs.getLong("film_id"))));
        film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
        film.setGenres(new HashSet<>(genreDbStorage.getGenresByFilmId(rs.getLong("film_id"))));
        return film;
    }
}
