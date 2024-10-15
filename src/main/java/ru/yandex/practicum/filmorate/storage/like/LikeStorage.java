package ru.yandex.practicum.filmorate.storage.like;

import java.util.Collection;

public interface LikeStorage {

    Collection<Long> getLikes(Long filmId);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);
}
