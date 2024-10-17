package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/mpa")
@AllArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> findAll() {
        log.info("Запрос GET к эндпоинту: /mpa");
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public Mpa getGenreById(@PathVariable int id) {
        log.info("Запрос GET к эндпоинту: /mpa/{}", id);
        return mpaService.getMpaById(id);
    }
}
