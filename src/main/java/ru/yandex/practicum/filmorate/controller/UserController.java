package ru.yandex.practicum.filmorate.controller;


import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import java.util.Collection;


@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    //получение всех фильмов
    @GetMapping
    public Collection<User> findAll() {

        return userService.findAll();
    }

    //добавление пользовател
    @PostMapping
    public User create(@RequestBody User user) {
        return userService.create(user);
    }


    //обновл пользователя
    @PutMapping
    public User update(@RequestBody User user) {
        return userService.update(user);
    }


    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

}
