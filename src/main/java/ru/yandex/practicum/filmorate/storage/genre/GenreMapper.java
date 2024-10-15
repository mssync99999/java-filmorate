package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreMapper implements RowMapper<Genre> {

    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("name"));
        return genre;
    }
}
