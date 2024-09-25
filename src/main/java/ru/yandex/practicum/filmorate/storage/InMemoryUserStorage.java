package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {

        //электронная почта не может быть пустой и должна содержать символ @
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        //логин не может быть пустым и содержать пробелы
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        //дата рождения не может быть в будущем.
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        // формируем дополнительные данные
        user.setId(getNextId());
        //имя для отображения может быть пустым — в таком случае будет использован логин
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        // сохраняем новую публикацию в памяти приложения
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {

        // проверяем необходимые условия
        if (user.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        // проверяем необходимые условия
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Id не найден");

        }

        User oldObj = users.get(user.getId());
        //if (oldObj == null) return null;


        //обновляем содержимое
        if (user.getName() != null) {
            oldObj.setName(user.getName());
        }

        //обновляем содержимое Email
        if (user.getEmail() != null && user.getEmail().contains("@")) {
            oldObj.setEmail(user.getEmail());
        }

        //обновляем содержимое Login
        if (user.getLogin() != null && !user.getLogin().contains(" ")) {
            oldObj.setLogin(user.getLogin());
        }

        //обновляем содержимое Name
        if (user.getName() != null) {
            oldObj.setName(user.getName());
        }

        //обновляем содержимое Birthday
        if (user.getBirthday() != null && user.getBirthday().isBefore(LocalDate.now())) {
            oldObj.setBirthday(user.getBirthday());
        }

        return oldObj;
    }

    @Override
    public long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }

    @Override
    public User getUserById(long id) {
        if (!users.containsKey(id)) {

            throw new NotFoundException("Id не найден");
        }
        return users.get(id);
    }

    //+ PUT /users/{id}/friends/{friendId} — добавление в друзья.
    @Override
    public void addFriend(long id, long friendId) {

        User user = getUserById(id);
        User friend = getUserById(friendId);

        user.addFriend(friendId);
        friend.addFriend(id);
    }


    //+ DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    @Override
    public void deleteFriend(long id, long friendId) {

        User user = getUserById(id);
        User friend = getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);

    }


    //+ GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    @Override
    public Collection<User> getUserFriends(long id) {
        if (!users.containsKey(id)) {

            throw new NotFoundException("Id не найден");
        }
        return users.get(id).getFriends()
                .stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    //+ GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    @Override
    public Set<User> getCommonFriends(long idA, long idB) {
        return getUserById(idA).getFriendsId()
                .stream()
                .filter(getUserById(idB).getFriendsId()::contains)
                .map(this::getUserById)
                .collect(Collectors.toSet());
    }

}
