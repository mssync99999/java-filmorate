package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;


@Slf4j
@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {
    private final FilmService filmService;


    //получение всех фильмов+
    @GetMapping
    public Collection<Film> findAll() {

        return filmService.findAll();
    }

    //добавление фильма+
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Создается фильм: {}", film);
        return filmService.create(film);
    }

    //обновление фильма
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Изменяется фильм: {}", film);
        return filmService.update(film);

    }


    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable long filmId, @PathVariable long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLike(@PathVariable long filmId, @PathVariable long userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getFilmsPopular(
            @RequestParam(value = "count", defaultValue = "10") long count) {
        return filmService.getFilmsPopular(count);
    }

}
