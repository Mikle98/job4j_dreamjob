package ru.job4j.dreamjob.controller;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.*;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.SimpleCandidateService;
import ru.job4j.dreamjob.service.SimpleCityService;

import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CandidateControllerTest {
    private CandidateService candidateService;
    private CandidateController candidateController;
    private CityService cityService;
    private MultipartFile testFile;

    @BeforeEach
    public void init() {
        candidateService = mock(SimpleCandidateService.class);
        cityService = mock(SimpleCityService.class);
        candidateController = new CandidateController(candidateService, cityService);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    public void whenGetCandidate() {
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 2);
        var listCandidates = List.of(candidate);
        var user = new User(1, "email", "name", "password");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);
        when(candidateService.findAll()).thenReturn(listCandidates);

        var model = new ConcurrentModel();
        var view = candidateController.getCandidate(model, session);
        var rslUser = model.getAttribute("user");
        var rslCandidates = model.getAttribute("candidates");

        assertThat(view).isEqualTo("candidates/list");
        assertThat(rslUser).usingRecursiveComparison().isEqualTo(user);
        assertThat(rslCandidates).usingRecursiveComparison().isEqualTo(listCandidates);
    }

    @Test
    public void whenGetCreationPage() {
        var cities = new City(1, "city");
        var listCities = List.of(cities);
        when(cityService.findAll()).thenReturn(listCities);

        var model = new ConcurrentModel();
        var view = candidateController.getCreationPage(model);
        var rsl = model.getAttribute("cities");

        assertThat(view).isEqualTo("candidates/create");
        assertThat(rsl).usingRecursiveComparison().isEqualTo(listCities);
    }

    @Test
    public void whenCreateThrow() {
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 2);
        var expectedException = new RuntimeException();
        when(candidateService.save(any(Candidate.class), any(FileDto.class))).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = candidateController.create(candidate, testFile, model);
        var rsl = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(rsl).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenCreate() {
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 2);
        when(candidateService.save(any(Candidate.class), any(FileDto.class))).thenReturn(candidate);
        var model = new ConcurrentModel();
        var view = candidateController.create(candidate, testFile, model);

        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenGetById() {
        var cities = new City(1, "city");
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 2);
        var listCities = List.of(cities);
        when(candidateService.findById(any(Integer.class))).thenReturn(Optional.of(candidate));
        when(cityService.findAll()).thenReturn(listCities);

        var model = new ConcurrentModel();
        var view = candidateController.getById(model, 1);
        var rslCities = model.getAttribute("cities");
        var rslCandidate = model.getAttribute("candidate");

        assertThat(view).isEqualTo("candidates/one");
        assertThat(rslCities).usingRecursiveComparison().isEqualTo(listCities);
        assertThat(rslCandidate).usingRecursiveComparison().isEqualTo(candidate);
    }

    @Test
    public void whenUpdate() {
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 2);
        when(candidateService.update(any(Candidate.class), any(FileDto.class))).thenReturn(true);

        var model = new ConcurrentModel();
        var view = candidateController.update(candidate, testFile, model);

        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenUpdateThrow() {
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 2);
        var exception = new RuntimeException();
        when(candidateService.update(any(Candidate.class), any(FileDto.class))).thenThrow(exception);

        var model = new ConcurrentModel();
        var view = candidateController.update(candidate, testFile, model);
        var rslException = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(rslException).isEqualTo(exception.getMessage());
    }

    @Test
    public void whenDelete() {
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 2);
        when(candidateService.findById(any(Integer.class))).thenReturn(Optional.of(candidate));

        var model = new ConcurrentModel();
        var view = candidateController.delete(model, 1);

        assertThat(view).isEqualTo("redirect:/candidates");
    }
}