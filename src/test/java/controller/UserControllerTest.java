package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.controller.UserController;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {
    private UserService userService;
    private UserController userController;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenRequestRegisterPageSuccessfully() {
        var view = userController.getRegistationPage();
        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenPostRegisterUserAndGetPageVacancies() {
        var userExpected = new User(1, "user1@gmail.com", "user1", "password");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.of(userExpected));

        var model = new ConcurrentModel();
        var view = userController.register(model, userExpected);
        var actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualUser).isEqualTo(userExpected);
    }

    @Test
    public void whenPostRegisterUserAndGetErrorMessage() {
        var expectedMessage = "Пользователь уже существует";
        when(userService.save(any(User.class))).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.register(model, new User());
        var actualMessage = model.getAttribute("error");

        assertThat(view).isEqualTo("users/register");
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void whenRequestLoginPageSuccessfully() {
        var view = userController.getLoginPage();
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenPostLoginUserThenRedirectVacancies() {

        var user = new User(1, "user1@gmail.com", "user1", "password");

        var emailArgumentCaptor = ArgumentCaptor.forClass(String.class);
        var passwordArgumentCaptor = ArgumentCaptor.forClass(String.class);
        when(userService.findByEmailAndPassword(emailArgumentCaptor.capture(),
                passwordArgumentCaptor.capture())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, new MockHttpServletRequest());
        var actualEmail = emailArgumentCaptor.getValue();
        var actualPassword = passwordArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualEmail).isEqualTo("user1@gmail.com");
        assertThat(actualPassword).isEqualTo("password");
    }

    @Test
    public void whenPostLoginUserThenGetErrorMessage() {
        var expectedMessage = "Почта или пароль введены неверно";

        when(userService.findByEmailAndPassword(any(String.class), any(String.class)))
                .thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.loginUser(new User(), model, new MockHttpServletRequest());
        var actualMessage = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(actualMessage).isEqualTo("Почта или пароль введены неверно");
    }

    @Test
    public void whenRequestLogoutThenRedirectLoginPage() {
        var view = userController.logout(new MockHttpSession());
        assertThat(view).isEqualTo("redirect:/users/login");
    }
}