package ru.yandex.practicum.filmorate.service;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import java.util.Collection;

@Service
@AllArgsConstructor
public class FriendService {

    private final FriendDbStorage friendDbStorage;
    private final UserDbStorage userDbStorage;
    private final UserService userService;

    //+ PUT /users/{id}/friends/{friendId} — добавление в друзья.
    public void addFriend(long id, long friendId) {
                // вынести в сервис
        // проверяем необходимые условия
        if (userService.getUserById(id) == null) {
            throw new NotFoundException("Id не найден");
        }
        // проверяем необходимые условия
        if (userService.getUserById(friendId) == null) {
            throw new NotFoundException("Id не найден");
        }

        friendDbStorage.addFriend(id, friendId);
    }

    //+ DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    public void deleteFriend(long id, long friendId) {
                // вынести в сервис
        // проверяем необходимые условия
        if (userService.getUserById(id) == null) {
            throw new NotFoundException("Id не найден");
        }
        // проверяем необходимые условия
        if (userService.getUserById(friendId) == null) {
            throw new NotFoundException("Id не найден");
        }

        friendDbStorage.deleteFriend(id, friendId);
    }

    //+ GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    public Collection<User> getUserFriends(long id) {
        // проверяем необходимые условия
        // вынести в сервис
        if (userService.getUserById(id) == null) {
            throw new NotFoundException("Id не найден");
        }

        return friendDbStorage.getUserFriends(id);
    }

    //+ GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    public Collection<User> getCommonFriends(long idA,long idB) {
        return friendDbStorage.getCommonFriends(idA, idB);
    }

}
