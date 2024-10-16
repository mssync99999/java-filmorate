package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;


public interface FilmStorage {

    //получение всех фильмов
    Collection<Film> findAll();

    //добавление фильма
    Film create(Film film);

    //обновление фильма
    Film update(Film film);

    //вспомогательный метод для генерации идентификатора нового id
    long getNextId();

    //получать каждый фильм по уникальному идентификатору
    Film getFilmById(long filmId);

    //вывод 10 наиболее популярных фильмов по количеству лайков
    Collection<Film> getFilmsPopular(long count);

}
