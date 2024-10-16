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
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.friend.FriendDbStorage;
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
	private final FriendDbStorage friendDbStorage;
	private final FilmDbStorage filmDbStorage;
	private final FilmService filmService;
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
				.name("FirstN")
				.login("FirstL")
				.email("First@yandex.ru")
				.birthday(LocalDate.of(2001, 1, 2))
				.build();

		secondUser = User.builder()
				.name("SecondN")
				.login("SecondL")
				.email("Second@yandex.ru")
				.birthday(LocalDate.of(2002, 11, 12))
				.build();

		thirdUser = User.builder()
				.name("ThirdN")
				.login("ThirdL")
				.email("Third@yandex.ru")
				.birthday(LocalDate.of(2003, 12, 22))
				.build();

		firstFilm = Film.builder()
				.name("First Film")
				.description("First Film Description")
				.releaseDate(LocalDate.of(1961, 10, 5))
				.duration(110)
				.build();
		firstFilm.setMpa(new Mpa(1, "G"));
		firstFilm.setLikes(new ArrayList<>()); //HashSet<>());
		firstFilm.setGenres(new ArrayList<>(Arrays.asList(new Genre(1, "Комедия"),
				new Genre(2, "Драма")))); // new HashSet<>(

		secondFilm = Film.builder()
				.name("Second Film")
				.description("Second Film Description")
				.releaseDate(LocalDate.of(2001, 12, 12))
				.duration(120)
				.build();
		secondFilm.setMpa(new Mpa(3, "PG-13"));
		secondFilm.setLikes(new ArrayList<>()); //new HashSet<>()
		secondFilm.setGenres(new ArrayList<>(Arrays.asList(new Genre(6, "Боевик"))));
		//new HashSet<>(Arrays.asList(new Genre(6, "Боевик")))

		thirdFilm = Film.builder()
				.name("Third Film")
				.description("Third Film Description")
				.releaseDate(LocalDate.of(1995, 11, 11))
				.duration(130)
				.build();
		thirdFilm.setMpa(new Mpa(4, "R"));
		thirdFilm.setLikes(new ArrayList<>()); //HashSet
		thirdFilm.setGenres(new ArrayList<>(Arrays.asList(new Genre(2, "Драма")))); //HashSet
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
		friendDbStorage.addFriend(firstUser.getId(), secondUser.getId());
		assertThat(friendDbStorage.getUserFriends(firstUser.getId())).hasSize(1);
		assertThat(friendDbStorage.getUserFriends(firstUser.getId())).contains(secondUser);
	}

	//удаление из друзей DELETE /users/{id}/friends/{friendId}
	@Test
	public void deleteFriendTest() {
		firstUser = userDbStorage.create(firstUser);
		secondUser = userDbStorage.create(secondUser);
		thirdUser = userDbStorage.create(thirdUser);
		friendDbStorage.addFriend(firstUser.getId(), secondUser.getId());
		friendDbStorage.addFriend(firstUser.getId(), thirdUser.getId());
		friendDbStorage.deleteFriend(firstUser.getId(), secondUser.getId());
		assertThat(friendDbStorage.getUserFriends(firstUser.getId())).hasSize(1);
		assertThat(friendDbStorage.getUserFriends(firstUser.getId())).contains(thirdUser);
	}

	//возвращаем список пользователей, являющихся его друзьями Collection<User> + GET /users/{id}/friends
	@Test
	public void getUserFriendsTest() {
		firstUser = userDbStorage.create(firstUser);
		secondUser = userDbStorage.create(secondUser);
		thirdUser = userDbStorage.create(thirdUser);
		friendDbStorage.addFriend(firstUser.getId(), secondUser.getId());
		friendDbStorage.addFriend(firstUser.getId(), thirdUser.getId());
		assertThat(friendDbStorage.getUserFriends(firstUser.getId())).hasSize(2);
		assertThat(friendDbStorage.getUserFriends(firstUser.getId())).contains(secondUser);
		assertThat(friendDbStorage.getUserFriends(firstUser.getId())).contains(thirdUser);
	}

	//список друзей, общих с другим пользователем Collection<User> + GET /users/{id}/friends/common/{otherId}
	@Test
	public void getCommonFriendsTest() {
		firstUser = userDbStorage.create(firstUser);
		secondUser = userDbStorage.create(secondUser);
		thirdUser = userDbStorage.create(thirdUser);
		friendDbStorage.addFriend(firstUser.getId(), secondUser.getId());
		friendDbStorage.addFriend(firstUser.getId(), thirdUser.getId());
		friendDbStorage.addFriend(secondUser.getId(), firstUser.getId());
		friendDbStorage.addFriend(secondUser.getId(), thirdUser.getId());
		assertThat(friendDbStorage.getCommonFriends(firstUser.getId(), secondUser.getId())).hasSize(1);
		assertThat(friendDbStorage.getCommonFriends(firstUser.getId(), secondUser.getId())).contains(thirdUser);
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
		firstFilm = filmService.create(firstFilm);
		secondFilm = filmService.create(secondFilm);
		thirdFilm = filmService.create(thirdFilm);
		Collection<Film> listFilms = filmService.findAll();

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
		Film testFilm = filmService.create(firstFilm);
		secondFilm.setId(testFilm.getId());
		filmService.update(secondFilm);
		Film testFilmUpd = filmService.getFilmById(testFilm.getId());

		assertEquals(testFilmUpd.getDescription(), secondFilm.getDescription());
		assertEquals(testFilmUpd.getName(), secondFilm.getName());
		assertEquals(testFilmUpd.getDuration(), secondFilm.getDuration());
		assertEquals(testFilmUpd.getReleaseDate(), secondFilm.getReleaseDate());
		assertTrue(testFilmUpd.getLikes().containsAll(secondFilm.getLikes()));
		assertEquals(testFilmUpd.getMpa(), secondFilm.getMpa());
		assertTrue(testFilmUpd.getGenres().containsAll(secondFilm.getGenres()));
	}

	//добавление лайка void
	@Test
	public void addLikeTest() {
		firstUser = userDbStorage.create(firstUser);
		firstFilm = filmService.create(firstFilm);
		//filmDbStorage.getLikeDbStorage().addLike(firstFilm.getId(), firstUser.getId());
		likeDbStorage.addLike(firstFilm.getId(), firstUser.getId());
		firstFilm = filmService.getFilmById(firstFilm.getId());
		assertThat(firstFilm.getLikes()).hasSize(1);
		assertThat(firstFilm.getLikes()).contains(firstUser.getId());
	}

	//удаление лайка void
	@Test
	public void deleteLikeTest() {
		firstUser = userDbStorage.create(firstUser);
		secondUser = userDbStorage.create(secondUser);
		firstFilm = filmDbStorage.create(firstFilm);
		likeDbStorage.addLike(firstFilm.getId(), firstUser.getId());
		likeDbStorage.addLike(firstFilm.getId(), secondUser.getId());
		likeDbStorage.deleteLike(firstFilm.getId(), firstUser.getId());

		firstFilm = filmService.getFilmById(firstFilm.getId());
		assertThat(firstFilm.getLikes()).hasSize(1);
		assertThat(firstFilm.getLikes()).contains(secondUser.getId());
	}

	//вывод 10 наиболее популярных фильмов по количеству лайков Collection<Film>
	@Test
	public void getFilmsPopularTest() {

		firstUser = userDbStorage.create(firstUser);
		secondUser = userDbStorage.create(secondUser);
		thirdUser = userDbStorage.create(thirdUser);

		firstFilm = filmDbStorage.create(firstFilm);
		likeDbStorage.addLike(firstFilm.getId(), firstUser.getId());

		secondFilm = filmDbStorage.create(secondFilm);
		likeDbStorage.addLike(secondFilm.getId(), firstUser.getId());
		likeDbStorage.addLike(secondFilm.getId(), secondUser.getId());
		likeDbStorage.addLike(secondFilm.getId(), thirdUser.getId());

		thirdFilm = filmDbStorage.create(thirdFilm);
		likeDbStorage.addLike(thirdFilm.getId(), firstUser.getId());
		likeDbStorage.addLike(thirdFilm.getId(), secondUser.getId());

		List<Film> listFilms = (List<Film>) filmDbStorage.getFilmsPopular(5);

		assertThat(listFilms).hasSize(3);
		assertThat(listFilms.get(0)).hasFieldOrPropertyWithValue("name", "Second Film");
		assertThat(listFilms.get(1)).hasFieldOrPropertyWithValue("name", "Third Film");
		assertThat(listFilms.get(2)).hasFieldOrPropertyWithValue("name", "First Film");

	}

}
