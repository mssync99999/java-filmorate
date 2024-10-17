package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
