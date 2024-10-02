# java-filmorate
Template repository for Filmorate project.
![filmorate](https://github.com/user-attachments/assets/954274ab-9f9a-4724-bc54-b99d70192cf5)




Код из dbdiagram.io ( https://dbdiagram.io/d/66ec7a68a0828f8aa66d7911 )

table users {
user_id int [pk, increment]
email varchar(255) [not null]
login varchar(255) [not null]
name varchar(255) [not null]
birthday date [not null]
friendship_user_id int [note: 'fk', ref: < friendships.friendship_user_id]

}

table films {
film_id int [pk, increment]
name varchar(255) [not null]
description varchar(200) [not null]
releaseDate timestamp [not null]
duration int [not null]
like_film_id int [note: 'fk', ref: < likes.like_film_id]
genre_film_id int [not null, note: 'fk', ref: < film_to_genres.genre_film_id ]
mpa_id int [not null, note: 'fk', ref: - mpa.mpa_id]
}

table film_to_genres {
genre_film_id int [pk, increment]
genre_id int [not null, note: 'fk', ref: > genres.genre_id]


}


table genres {
genre_id int [pk, increment]
name varchar(100) [not null]
}

table mpa {
mpa_id int [pk, increment]
name varchar(100) [not null]
}

table likes {
like_film_id int [pk, increment]
user_id int [not null, note: 'fk', ref: > users.user_id]

}

table friendships {
  friendship_user_id int [pk, increment]
  friend_id int [not null, note: 'fk', ref: > users.user_id]
  status boolean
}
