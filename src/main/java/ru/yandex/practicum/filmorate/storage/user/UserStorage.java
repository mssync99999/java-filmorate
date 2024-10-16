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

    boolean deleteUser(User user);
}
