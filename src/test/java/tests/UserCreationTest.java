package tests;

import client.UserClient;
import model.User;
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

@Feature("Создание пользователя")
public class UserCreationTest extends BaseTest {
    private UserClient userClient;
    private User createdUser;
    private String accessToken;

    @BeforeEach
    public void setUp() {
        userClient = new UserClient();
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка успешного создания нового пользователя")
    @Story("Успешное создание")
    public void testCreateUniqueUser() {
        createdUser = TestDataGenerator.generateRandomUser();

        Response response = createUser(createdUser);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(createdUser.getEmail().toLowerCase()))
                .body("user.name", equalTo(createdUser.getName()));

        accessToken = response.path("accessToken");
        assertNotNull(accessToken, "Access token should not be null");
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Проверка ошибки при создании дубликата пользователя")
    @Story("Негативные сценарии")
    public void testCreateDuplicateUser() {
        createdUser = TestDataGenerator.generateRandomUser();
        Response firstResponse = createUser(createdUser);
        accessToken = firstResponse.path("accessToken");

        Response duplicateResponse = createUser(createdUser);

        duplicateResponse.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Проверка ошибки при создании пользователя без email")
    @Story("Негативные сценарии")
    public void testCreateUserWithoutEmail() {
        User userWithoutEmail = TestDataGenerator.getUserWithMissingField("email");

        Response response = createUser(userWithoutEmail);

        response.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Проверка ошибки при создании пользователя без пароля")
    @Story("Негативные сценарии")
    public void testCreateUserWithoutPassword() {
        User userWithoutPassword = TestDataGenerator.getUserWithMissingField("password");

        Response response = createUser(userWithoutPassword);

        response.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Проверка ошибки при создании пользователя без имени")
    @Story("Негативные сценарии")
    public void testCreateUserWithoutName() {
        User userWithoutName = TestDataGenerator.getUserWithMissingField("name");

        Response response = createUser(userWithoutName);

        response.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Step("Создание пользователя")
    private Response createUser(User user) {
        return userClient.createUser(user);
    }
}