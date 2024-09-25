package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> films = new HashMap<>();


    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {

        //название не может быть пустым
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }

        //проверка на дубли
        for (Film entry : films.values()) {
            if (entry.getName().equals(film.getName())) {
                throw new ValidationException("Этот фильм уже внесён");
            }
        }

        //Описание не может быть пустым
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }

        //Максимальная длина описания — 200 символов
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        //Дата релиза — не раньше 28 декабря 1895 года
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        //Продолжительность фильма должна быть положительным числом
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }

        // формируем дополнительные данные
        film.setId(getNextId());
        // сохраняем новую публикацию в памяти приложения
        films.put(film.getId(), film);
        return film;

    }

    @Override
    public Film update(Film film) {
        // проверяем необходимые условия
        if (film.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        // проверяем необходимые условия
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Id не найден");

        }

        Film oldObj = films.get(film.getId());

        //обновляем содержимое
        if (film.getName() != null) {
            oldObj.setName(film.getName());
        }

        //обновляем содержимое
        if (film.getDescription() != null && film.getDescription().length() <= 200) {
            oldObj.setDescription(film.getDescription());
        }

        //обновляем содержимое
        if (film.getReleaseDate() != null && film.getReleaseDate().isAfter(LocalDate.parse("1895-12-28"))) {
            oldObj.setReleaseDate(film.getReleaseDate());
        }

        //обновляем содержимое
        if (film.getDuration() > 0) {
            oldObj.setDuration(film.getDuration());
        }

        return oldObj;
    }

    @Override
    public long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }

    @Override
    public Film getFilmById(long filmId) {
        if (!films.containsKey(filmId)) {

            throw new NotFoundException("Фильм " + filmId + " не найден");
        }
        return films.get(filmId);
    }

    @Override
    public void addLike(long filmId, long userId) {
        if (!films.containsKey(filmId)) {

            throw new NotFoundException("Фильм " + filmId + " не найден");
        }
        films.get(filmId).addLike(userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        Film film = films.get(filmId);

        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм " + filmId + " не найден");
        }

        if (!film.getLikes().contains(userId)) {

            throw new NotFoundException("Пользователь " + userId + " не найден");
        }


        film.deleteLike(userId);
    }

    @Override
    public Collection<Film> getFilmsPopular(long count) {
        return findAll()
                .stream()
                .filter(film -> film.getLikes() != null)
                .sorted((t1, t2) -> t2.getLikes().size() - t1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }


}
