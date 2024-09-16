package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;


import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private Map<Long, User> users = new HashMap<>();

    //получение всех фильмов
    @GetMapping
    public Collection<User> findAll() {
        log.debug("Запрашивается коллекция пользователей: {}", users);
        return users.values();
    }

    //добавление пользовател
    @PostMapping
    public User create(@RequestBody User user) {
        log.debug("Создается пользователь: {}", user);

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
    //обновление пользователя
    @PutMapping
    public User update(@RequestBody User updObj) {
        log.debug("Изменяется пользователь: {}", updObj);

        // проверяем необходимые условия
        if (updObj.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        // проверяем необходимые условия
        if (users.containsValue(updObj.getId())) {
            throw new ValidationException("Id не найден");
        }

        User oldObj = users.get(updObj.getId());


        //обновляем содержимое
        if (updObj.getName() != null) {
            oldObj.setName(updObj.getName());
        }

        //обновляем содержимое Email
        if (updObj.getEmail() != null && updObj.getEmail().contains("@")) {
            oldObj.setEmail(updObj.getEmail());
        }

        //обновляем содержимое Login
        if (updObj.getLogin() != null && !updObj.getLogin().contains(" ")) {
            oldObj.setLogin(updObj.getLogin());
        }

        //обновляем содержимое Name
        if (updObj.getName() != null) {
            oldObj.setName(updObj.getName());
        }

        //обновляем содержимое Birthday
        if (updObj.getBirthday() != null && updObj.getBirthday().isBefore(LocalDate.now())) {
            oldObj.setBirthday(updObj.getBirthday());
        }

        return oldObj;
    }

    //вспомогательный метод для генерации идентификатора нового id
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }

}
