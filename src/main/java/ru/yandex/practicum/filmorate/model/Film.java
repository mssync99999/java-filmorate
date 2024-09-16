package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    Long id;//— идентификатор,
    String name;// —     название — name;
    String description;// — описание,
    LocalDate releaseDate;// — дата релиза LocalDate Instant
    int duration;// продолжительность фильма

}
