package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;
import java.util.Collection;

public interface GenreStorage {

    Collection<Genre> getGenresByFilmId(long filmId);

    Collection<Genre> findAll();

    Genre getGenreById(int genreId);

}
