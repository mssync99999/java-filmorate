package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@SpringBootTest
@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan("ru.yandex.practicum.filmorate")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
	private final UserDbStorage userDbStorage;
	private final FilmDbStorage filmDbStorage;
	private final GenreDbStorage genreDbStorage;
	private final MpaDbStorage mpaDbStorage;
	private final LikeDbStorage likeDbStorage;

	private User firstUser;
	private User secondUser;
	private User thirdUser;
	private Film firstFilm;
	private Film secondFilm;
	private Film thirdFilm;

	@BeforeEach
	public void beforeEach() {
		firstUser = User.builder()
				.name("MisterFirst")
				.login("First")
				.email("1@ya.ru")
				.birthday(LocalDate.of(1980, 12, 23))
				.build();

		secondUser = User.builder()
				.name("MisterSecond")
				.login("Second")
				.email("2@ya.ru")
				.birthday(LocalDate.of(1980, 12, 24))
				.build();

		thirdUser = User.builder()
				.name("MisterThird")
				.login("Third")
				.email("3@ya.ru")
				.birthday(LocalDate.of(1980, 12, 25))
				.build();

		firstFilm = Film.builder()
				.name("Breakfast at Tiffany")
				.description("American romantic comedy film directed by Blake Edwards, written by George Axelrod")
				.releaseDate(LocalDate.of(1961, 10, 5))
				.duration(114)
				.build();
		firstFilm.setMpa(new Mpa(1, "G"));
		firstFilm.setLikes(new HashSet<>());
		firstFilm.setGenres(new HashSet<>(Arrays.asList(new Genre(1, "Комедия"),
				new Genre(2, "Драма"))));

		secondFilm = Film.builder()
				.name("Avatar")
				.description("American epic science fiction film directed, written, produced, and co-edited.")
				.releaseDate(LocalDate.of(2009, 12, 10))
				.duration(162)
				.build();
		secondFilm.setMpa(new Mpa(3, "PG-13"));
		secondFilm.setLikes(new HashSet<>());
		secondFilm.setGenres(new HashSet<>(Arrays.asList(new Genre(6, "Боевик"))));

		thirdFilm = Film.builder()
				.name("One Flew Over the Cuckoo Nest")
				.description("American psychological comedy drama film directed by Milos Forman.")
				.releaseDate(LocalDate.of(1975, 11, 19))
				.duration(133)
				.build();
		thirdFilm.setMpa(new Mpa(4, "R"));
		thirdFilm.setLikes(new HashSet<>());
		thirdFilm.setGenres(new HashSet<>(Arrays.asList(new Genre(2, "Драма"))));
	}

	//***UserDbStorageTest***
	//получение всех фильмов Collection<User>
	@Test
	public void findAllTest() {
		firstUser = userDbStorage.create(firstUser);
		secondUser = userDbStorage.create(secondUser);
		Collection<User> listUsers = userDbStorage.findAll();
		assertThat(listUsers).contains(firstUser);
		assertThat(listUsers).contains(secondUser);
	}

	//добавление пользовател User
	@Test
	public void createTest() {
		User testUser = userDbStorage.create(firstUser);
		assertEquals(testUser.getLogin(), firstUser.getLogin());
		assertEquals(testUser.getName(), firstUser.getName());
		assertEquals(testUser.getEmail(), firstUser.getEmail());
		assertEquals(testUser.getBirthday(), firstUser.getBirthday());
		assertEquals(testUser.getFriends(), firstUser.getFriends());
	}

	//обновл пользователя User
	//найти пользователя User
	@Test
	public void updateAndFindTest() {
		User testUser = userDbStorage.create(firstUser);
		secondUser.setId(testUser.getId());
		userDbStorage.update(secondUser);
		User testUserUpd = userDbStorage.getUserById(testUser.getId());

		assertEquals(testUserUpd.getLogin(), secondUser.getLogin());
		assertEquals(testUserUpd.getName(), secondUser.getName());
		assertEquals(testUserUpd.getEmail(), secondUser.getEmail());
		assertEquals(testUserUpd.getBirthday(), secondUser.getBirthday());
		assertEquals(testUserUpd.getFriends(), secondUser.getFriends());
	}

	//добавление в друзья PUT /users/{id}/friends/{friendId}
	@Test
	public void addFriendTestA() {
		firstUser = userDbStorage.create(firstUser);
		secondUser = userDbStorage.create(secondUser);
		userDbStorage.addFriend(firstUser.getId(), secondUser.getId());
		assertThat(userDbStorage.getUserFriends(firstUser.getId())).hasSize(1);
		assertThat(userDbStorage.getUserFriends(firstUser.getId())).contains(secondUser);
	}

	//удаление из друзей DELETE /users/{id}/friends/{friendId}
	@Test
	public void deleteFriendTest() {
		firstUser = userDbStorage.create(firstUser);
		secondUser = userDbStorage.create(secondUser);
		thirdUser = userDbStorage.create(thirdUser);
		userDbStorage.addFriend(firstUser.getId(), secondUser.getId());
		userDbStorage.addFriend(firstUser.getId(), thirdUser.getId());
		userDbStorage.deleteFriend(firstUser.getId(), secondUser.getId());
		assertThat(userDbStorage.getUserFriends(firstUser.getId())).hasSize(1);
		assertThat(userDbStorage.getUserFriends(firstUser.getId())).contains(thirdUser);
	}

	//возвращаем список пользователей, являющихся его друзьями Collection<User> + GET /users/{id}/friends
	@Test
	public void getUserFriendsTest() {
		firstUser = userDbStorage.create(firstUser);
		secondUser = userDbStorage.create(secondUser);
		thirdUser = userDbStorage.create(thirdUser);
		userDbStorage.addFriend(firstUser.getId(), secondUser.getId());
		userDbStorage.addFriend(firstUser.getId(), thirdUser.getId());
		assertThat(userDbStorage.getUserFriends(firstUser.getId())).hasSize(2);
		assertThat(userDbStorage.getUserFriends(firstUser.getId())).contains(secondUser);
		assertThat(userDbStorage.getUserFriends(firstUser.getId())).contains(thirdUser);
	}

	//список друзей, общих с другим пользователем Collection<User> + GET /users/{id}/friends/common/{otherId}
	@Test
	public void getCommonFriendsTest() {
		firstUser = userDbStorage.create(firstUser);
		secondUser = userDbStorage.create(secondUser);
		thirdUser = userDbStorage.create(thirdUser);
		userDbStorage.addFriend(firstUser.getId(), secondUser.getId());
		userDbStorage.addFriend(firstUser.getId(), thirdUser.getId());
		userDbStorage.addFriend(secondUser.getId(), firstUser.getId());
		userDbStorage.addFriend(secondUser.getId(), thirdUser.getId());
		assertThat(userDbStorage.getCommonFriends(firstUser.getId(), secondUser.getId())).hasSize(1);
		assertThat(userDbStorage.getCommonFriends(firstUser.getId(), secondUser.getId())).contains(thirdUser);
	}

	//удаление пользователя boolean DELETE /users/{id}
	@Test
	public void deleteUserTest() {
		firstUser = userDbStorage.create(firstUser);
		userDbStorage.deleteUser(firstUser);
		Collection<User> listUsers = userDbStorage.findAll();
		assertThat(listUsers).hasSize(0);
	}

	//***FilmDbStorageTest***
	//получение всех фильмов Collection<Film>
	@Test
	public void findAllFilmTest() {
		firstFilm = filmDbStorage.create(firstFilm);
		secondFilm = filmDbStorage.create(secondFilm);
		thirdFilm = filmDbStorage.create(thirdFilm);
		Collection<Film> listFilms = filmDbStorage.findAll();

		assertTrue(listFilms.contains(firstFilm));
		assertTrue(listFilms.contains(secondFilm));
		assertTrue(listFilms.contains(thirdFilm));
	}

    //добавление фильма Film
    @Test
    public void createFilmTest() {
        Film testFilm = filmDbStorage.create(firstFilm);
        assertEquals(testFilm.getDescription(), firstFilm.getDescription());
        assertEquals(testFilm.getName(), firstFilm.getName());
        assertEquals(testFilm.getDuration(), firstFilm.getDuration());
        assertEquals(testFilm.getReleaseDate(), firstFilm.getReleaseDate());
        assertEquals(testFilm.getLikes(), firstFilm.getLikes());
        assertEquals(testFilm.getMpa(), firstFilm.getMpa());
        assertEquals(testFilm.getGenres(), firstFilm.getGenres());

    }

	//обновление фильма Film
	//получать каждый фильм по уникальному идентификатору Film
	@Test
	public void updateAndFindFilmTest() {
		Film testFilm = filmDbStorage.create(firstFilm);
		secondFilm.setId(testFilm.getId());
		filmDbStorage.update(secondFilm);
		Film testFilmUpd = filmDbStorage.getFilmById(testFilm.getId());

		assertEquals(testFilmUpd.getDescription(), secondFilm.getDescription());
		assertEquals(testFilmUpd.getName(), secondFilm.getName());
		assertEquals(testFilmUpd.getDuration(), secondFilm.getDuration());
		assertEquals(testFilmUpd.getReleaseDate(), secondFilm.getReleaseDate());
		assertEquals(testFilmUpd.getLikes(), secondFilm.getLikes());
		assertEquals(testFilmUpd.getMpa(), secondFilm.getMpa());
		assertEquals(testFilmUpd.getGenres(), secondFilm.getGenres());
	}

	//добавление лайка void
	@Test
	public void addLikeTest() {
		firstUser = userDbStorage.create(firstUser);
		firstFilm = filmDbStorage.create(firstFilm);
		filmDbStorage.getLikeDbStorage().addLike(firstFilm.getId(), firstUser.getId());
		firstFilm = filmDbStorage.getFilmById(firstFilm.getId());
		assertThat(firstFilm.getLikes()).hasSize(1);
		assertThat(firstFilm.getLikes()).contains(firstUser.getId());
	};

	//удаление лайка void
	@Test
	public void deleteLikeTest() {
		firstUser = userDbStorage.create(firstUser);
		secondUser = userDbStorage.create(secondUser);
		firstFilm = filmDbStorage.create(firstFilm);
		filmDbStorage.getLikeDbStorage().addLike(firstFilm.getId(), firstUser.getId());
		filmDbStorage.getLikeDbStorage().addLike(firstFilm.getId(), secondUser.getId());
		filmDbStorage.getLikeDbStorage().deleteLike(firstFilm.getId(), firstUser.getId());
		firstFilm = filmDbStorage.getFilmById(firstFilm.getId());
		assertThat(firstFilm.getLikes()).hasSize(1);
		assertThat(firstFilm.getLikes()).contains(secondUser.getId());
	};

	//вывод 10 наиболее популярных фильмов по количеству лайков Collection<Film>
	@Test
	public void getFilmsPopularTest() {

		firstUser = userDbStorage.create(firstUser);
		secondUser = userDbStorage.create(secondUser);
		thirdUser = userDbStorage.create(thirdUser);

		firstFilm = filmDbStorage.create(firstFilm);
		filmDbStorage.getLikeDbStorage().addLike(firstFilm.getId(), firstUser.getId());

		secondFilm = filmDbStorage.create(secondFilm);
		filmDbStorage.getLikeDbStorage().addLike(secondFilm.getId(), firstUser.getId());
		filmDbStorage.getLikeDbStorage().addLike(secondFilm.getId(), secondUser.getId());
		filmDbStorage.getLikeDbStorage().addLike(secondFilm.getId(), thirdUser.getId());

		thirdFilm = filmDbStorage.create(thirdFilm);
		filmDbStorage.getLikeDbStorage().addLike(thirdFilm.getId(), firstUser.getId());
		filmDbStorage.getLikeDbStorage().addLike(thirdFilm.getId(), secondUser.getId());

		List<Film> listFilms = (List<Film>) filmDbStorage.getFilmsPopular(5);

		assertThat(listFilms).hasSize(3);

		assertThat(listFilms.get(0)).hasFieldOrPropertyWithValue("name", "Avatar");
		assertThat(listFilms.get(1)).hasFieldOrPropertyWithValue("name", "One Flew Over the Cuckoo Nest");
		assertThat(listFilms.get(2)).hasFieldOrPropertyWithValue("name", "Breakfast at Tiffany");

	}

}
