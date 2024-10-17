package ru.yandex.practicum.filmorate.storage.film;

import lombok.Data;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class FilmMapper  implements RowMapper<Film> {
    private FilmDbStorage filmDbStorage;

    public FilmMapper(FilmDbStorage filmDbStorage) {
        this.filmDbStorage = filmDbStorage;
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
        film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
        return film;
    }
}
