package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {
    private UserController userController;

    private UserService userService;

    @BeforeEach
    public void init() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenGetRegistration() {
        var user = new User();
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.of(user));
        var model = new ConcurrentModel();
        var view = userController.register(user, model);
        var rsl = userArgumentCaptor.getValue();
        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(rsl).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenLoginUser() {
        var user = new User(1, "email", "name", "password");
        MockHttpServletRequest request = new MockHttpServletRequest();
        var emailArgumentCaptor = ArgumentCaptor.forClass(String.class);
        var passwordArgumentCaptor = ArgumentCaptor.forClass(String.class);
        when(userService.findByEmailAndPassword(emailArgumentCaptor.capture(),
                                                passwordArgumentCaptor.capture()))
                                                            .thenReturn(Optional.of(user));
        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, request);
        var rslEmail = emailArgumentCaptor.getValue();
        var rslPassword = passwordArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(rslEmail).isEqualTo(user.getEmail());
        assertThat(rslPassword).isEqualTo(user.getPassword());
    }
}