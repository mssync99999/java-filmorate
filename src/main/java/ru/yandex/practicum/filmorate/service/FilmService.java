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
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeDbStorage likeDbStorage;
    private final GenreDbStorage genreDbStorage;

    public Collection<Film> findAll() {


        return filmStorage.findAll().stream()
                .map(film -> film.toBuilder()
                        .likes(likeDbStorage.getLikes(film.getId()))
                        .genres(genreDbStorage.getGenresByFilmId(film.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    public Film create(Film film) {
        Film newFilm =  filmStorage.create(film);
        genreDbStorage.setGenresByFilmId(newFilm);
        return newFilm;
    }

    public Film update(Film film) {
        Film updFilm =  filmStorage.update(film);
        genreDbStorage.updateGenresByFilmId(updFilm);
        return updFilm;
    }



    public Film getFilmById(long filmId) {
        Film film = filmStorage.getFilmById(filmId);
        film.setLikes(likeDbStorage.getLikes(filmId));
        film.setGenres(genreDbStorage.getGenresByFilmId(filmId));
        return film;
    }

    public void addLike(long filmId, long userId) {
        userStorage.getUserById(userId);
        likeDbStorage.addLike(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        likeDbStorage.deleteLike(filmId, userId);
    }


    public Collection<Film> getFilmsPopular(long count) {
        return filmStorage.getFilmsPopular(count);
    }



}
