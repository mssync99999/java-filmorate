package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;


import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    @PositiveOrZero(message = "Id должен быть указан")
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Length(min = 1, max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;

    private Collection<Long> likes;
    private Mpa mpa;
    private Collection<Genre> genres;

    public void addLike(Long id) {
        if (likes == null) {
            likes = new HashSet<>();
        }
        likes.add(id);
    }

    public void deleteLike(Long id) {
        likes.remove(id);
    }
}
