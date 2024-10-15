package ru.yandex.practicum.filmorate.service;

/*
 добавление и
 удаление лайка,
 вывод 10 наиболее популярных фильмов по количеству лайков.
 */

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import java.util.Collection;


@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeDbStorage likeDbStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }



    public Film getFilmById(long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public void addLike(long filmId, long userId) {
        userStorage.getUserById(userId);
        likeDbStorage.addLike(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {

        filmStorage.deleteLike(filmId, userId);
    }


    public Collection<Film> getFilmsPopular(long count) {
        return filmStorage.getFilmsPopular(count);
    }



}
