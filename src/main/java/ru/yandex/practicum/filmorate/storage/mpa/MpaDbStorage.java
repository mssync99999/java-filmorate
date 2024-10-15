package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.Collection;

@Component
public class MpaDbStorage implements MpaStorage {

    private JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Mpa> getMpaByFilmId(long filmId) {
        String anySql = "select g.genre_id, g.name as genre_name from genres as g " +
                "INNER JOIN film_to_genres gg on g.genre_id = gg.genre_id " +
                "where gg.film_id = ?";
        return jdbcTemplate.query(anySql, (rs, rowNum) -> new Mpa(
                rs.getInt("genre_id"),
                rs.getString("genre_name")
        ), filmId);
    }

    @Override
    public Collection<Mpa> findAll() {
        String anySql = "select mpa_id, name from mpas order by 1";
        return jdbcTemplate.query(anySql, new MpaMapper());
    }

    @Override
    public Mpa getMpaById(int id) {
        String anySql = "select mpa_id, name from mpas where mpa_id = ?";
        Mpa mpa;
        try {
            mpa = jdbcTemplate.queryForObject(anySql, new MpaMapper(), id);
        } catch (RuntimeException e) {
            throw new NotFoundException("Рейтинг " + id + " не найден!");
        }
        return mpa;
    }
}
