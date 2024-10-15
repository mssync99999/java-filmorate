package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    //получение всех фильмов
    Collection<User> findAll();

    //добавление пользовател
    User create(User user);

    //обновл пользователя
    User update(User user);

    //вспомогательный метод для генерации идентификатора нового id
    long getNextId();

    User getUserById(long id);

    //+ PUT /users/{id}/friends/{friendId} — добавление в друзья.
    void addFriend(long id, long friendId);

    //+ DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    void deleteFriend(long id, long friendId);

    //+ GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    Collection<User> getUserFriends(long id);

    //+ GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    Collection<User> getCommonFriends(long idA, long idB);

    boolean deleteUser(User user);
}
