package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDto addDirector(@Valid @RequestBody DirectorDto directorDto) {
        return directorService.addDirector(directorDto);
    }

    @GetMapping
    public Collection<DirectorDto> getAllDirectors() {
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    public DirectorDto getDirectorById(@PathVariable long id) {
        return directorService.getDirectorById(id);
    }

    @PutMapping
    public DirectorDto updateDirector(@Valid @RequestBody DirectorDto updatedDirectorDto) {
        return directorService.updateDirector(updatedDirectorDto);
    }

    @DeleteMapping("/{id}")
    public void removeDirector(@PathVariable long id) {
        directorService.removeDirector(id);
    }
}
