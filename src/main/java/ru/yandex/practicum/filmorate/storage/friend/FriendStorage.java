package ru.yandex.practicum.filmorate.storage.friend;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface FriendStorage {

    //+ PUT /users/{id}/friends/{friendId} — добавление в друзья.
    void addFriend(long id, long friendId);

    //+ DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    void deleteFriend(long id, long friendId);

    //+ GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    Collection<User> getUserFriends(long id);

    //+ GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    Collection<User> getCommonFriends(long idA, long idB);

}
