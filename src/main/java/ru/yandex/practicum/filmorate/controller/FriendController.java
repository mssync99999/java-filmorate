package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendService;
import java.util.Collection;


@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class FriendController {
    private final FriendService friendService;


    //+ PUT /users/{id}/friends/{friendId} — добавление в друзья.
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        friendService.addFriend(id, friendId);
    }


    //+ DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        friendService.deleteFriend(id, friendId);
    }


    //+ GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    @GetMapping("/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable long id) {
        return friendService.getUserFriends(id);
    }

    //+ GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id,
                                             @PathVariable long otherId) {
        return friendService.getCommonFriends(id, otherId);
    }

}
