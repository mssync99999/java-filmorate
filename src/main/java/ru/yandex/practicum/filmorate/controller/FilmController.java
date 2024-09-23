package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private Map<Long, Film> films = new HashMap<>();

    //получение всех фильмов
    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Запрашивается коллекция фильмов: {}", films);
        return films.values();
    }

    //добавление фильма
    @PostMapping
    public Film create(@RequestBody Film film) {
        log.debug("Создается фильм: {}", film);

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

        //+Описание не может быть пустым
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

    //обновление фильма
    @PutMapping
    public Film update(@RequestBody Film updObj) {
        log.debug("Изменяется фильм: {}", updObj);

        // проверяем необходимые условия
        if (updObj.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        // проверяем необходимые условия
        if (!films.containsValue(updObj.getId())) {
            //throw new ValidationException("Id не найден");
        }

        Film oldObj = films.get(updObj.getId());

        //обновляем содержимое
        if (updObj.getName() != null) {
            oldObj.setName(updObj.getName());
        }

        //обновляем содержимое
        if (updObj.getDescription() != null && updObj.getDescription().length() <= 200) {
            oldObj.setDescription(updObj.getDescription());
        }

        //обновляем содержимое
        if (updObj.getReleaseDate() != null && updObj.getReleaseDate().isAfter(LocalDate.parse("1895-12-28"))) {
            oldObj.setReleaseDate(updObj.getReleaseDate());
        }

        //обновляем содержимое
        if (updObj.getDuration() > 0) {
            oldObj.setDuration(updObj.getDuration());
        }

        return oldObj;
    }

    //вспомогательный метод для генерации идентификатора нового id
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }

}
