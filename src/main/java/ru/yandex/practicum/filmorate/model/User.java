package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Long> friends;


    public void addFriend(long id) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        friends.add(id);
    }


    public Set<Long> getFriendsId() {
        if (friends == null) {
            friends = new HashSet<>();
        }
        return friends;
    }


}
