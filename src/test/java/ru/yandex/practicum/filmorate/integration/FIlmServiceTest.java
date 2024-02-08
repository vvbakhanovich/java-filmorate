package ru.yandex.practicum.filmorate.integration;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dto.FilmSearchDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.impl.FilmServiceImpl;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class FIlmServiceTest {

    @Mock
    private FilmStorage filmStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private FilmLikeStorage filmLikeStorage;

    @Mock
    private DirectorStorage directorStorage;

    @Mock
    private EventStorage eventStorage;


    @InjectMocks
    private FilmService filmService;

    private Film film;

    @BeforeAll
    public void beforeAll() {
        filmService = new FilmServiceImpl(filmStorage, userStorage, filmLikeStorage, directorStorage, eventStorage);
    }

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);
        film = Film.builder()
                .id(1)
                .name("James Bond")
                .description("Good film")
                .duration(2)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(new Mpa(1, "G"))
                .build();
    }

    @Test
    @DisplayName("Тест поиск фильма по не валидному полю")
    void testSearchFilmByWrongSearchField() {
        List<String> search = List.of("id");
        FilmSearchDto query = FilmSearchDto.builder()
                .by(search)
                .query(film.getName())
                .build();

        String formattedResponse = String.format("Поле сортировки '%s' не поддерживается.", search);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> filmService.searchFilms(query));
        assertEquals(formattedResponse, e.getMessage());
    }
}