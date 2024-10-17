package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.Collection;

public interface MpaStorage {

    Collection<Mpa> getMpaByFilmId(long filmId);

    Collection<Mpa> findAll();

    Mpa getMpaById(int genreId);
}
