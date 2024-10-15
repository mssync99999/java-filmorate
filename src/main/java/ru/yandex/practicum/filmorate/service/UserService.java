package ru.yandex.practicum.filmorate.service;

/*
 добавление в друзья,
 удаление из друзей,
 вывод списка общих друзей.
 */


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.Collection;

@Service
@AllArgsConstructor
public class UserService {

    private final UserStorage userStorage;


    public Collection<User> findAll() {
        return userStorage.findAll();
    }


    public User create(User user) {
        return userStorage.create(user);
    }


    public User update(User user) {
        return userStorage.update(user);
    }




    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    //+ PUT /users/{id}/friends/{friendId} — добавление в друзья.
    public void addFriend(long id, long friendId) {
        userStorage.addFriend(id, friendId);
    }

    //+ DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    public void deleteFriend(long id, long friendId) {
        userStorage.deleteFriend(id, friendId);
    }

    //+ GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    public Collection<User> getUserFriends(long id) {
        return userStorage.getUserFriends(id);
    }

    //+ GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    public Collection<User> getCommonFriends(long idA,long idB) {
        return userStorage.getCommonFriends(idA, idB);
    }

}
