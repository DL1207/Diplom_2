package tests;

import client.UserClient;
import model.User;
import model.Credentials;
import utils.BaseTest;
import utils.TestDataGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;

@Feature("Авторизация пользователя")
public class UserLoginTest extends BaseTest {
    private UserClient userClient;
    private User createdUser;
    private String accessToken;

    @BeforeEach
    public void setUp() {
        userClient = new UserClient();

        createdUser = TestDataGenerator.generateRandomUser();
        Response createResponse = userClient.createUser(createdUser);
        accessToken = createResponse.path("accessToken");
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Проверка успешной авторизации с валидными данными")
    @Story("Успешная авторизация")
    public void testLoginWithValidCredentials() {
        Credentials validCredentials = TestDataGenerator.getValidCredentials(createdUser);

        Response response = loginUser(validCredentials);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(createdUser.getEmail().toLowerCase()))
                .body("user.name", equalTo(createdUser.getName()));

        String newToken = response.path("accessToken");
        assertNotNull(newToken);
    }

    @Test
    @DisplayName("Логин с неверным логином и паролем")
    @Description("Проверка ошибки при авторизации с неверными данными")
    @Story("Неуспешная авторизация")
    public void testLoginWithInvalidCredentials() {
        Credentials invalidCredentials = TestDataGenerator.getInvalidCredentials();

        Response response = loginUser(invalidCredentials);

        response.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Step("Логин пользователя")
    private Response loginUser(Credentials credentials) {
        return userClient.loginUser(credentials);
    }
}