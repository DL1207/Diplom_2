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
import static org.hamcrest.Matchers.*;

@Feature("Обновление пользователя")
public class UserUpdateTest extends BaseTest {
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
    @DisplayName("Изменение email с авторизацией")
    @Description("Проверка успешного изменения email авторизованным пользователем")
    @Story("Успешные сценарии с авторизацией")
    public void testUpdateEmailWithAuth() {
        User updatedUser = new User();
        updatedUser.setEmail("new_" + createdUser.getEmail());

        Response response = updateUserWithAuth(accessToken, updatedUser);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(updatedUser.getEmail().toLowerCase()));
    }

    @Test
    @DisplayName("Изменение name с авторизацией")
    @Description("Проверка успешного изменения имени авторизованным пользователем")
    @Story("Успешные сценарии с авторизацией")
    public void testUpdateNameWithAuth() {
        User updatedUser = new User();
        updatedUser.setName("New Name");
        updatedUser.setEmail(null);

        Response response = updateUserWithAuth(accessToken, updatedUser);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.name", equalTo(updatedUser.getName()));
    }

    @Test
    @DisplayName("Изменение email без авторизации")
    @Description("Проверка ошибки при изменении email без авторизации")
    @Story("Негативные сценарии без авторизации")
    public void testUpdateEmailWithoutAuth() {
        User updatedUser = new User();
        updatedUser.setEmail("new@test.com");

        Response response = updateUserWithoutAuth(updatedUser);

        response.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение name без авторизации")
    @Description("Проверка ошибки при изменении имени без авторизации")
    @Story("Негативные сценарии без авторизации")
    public void testUpdateNameWithoutAuth() {
        User updatedUser = new User();
        updatedUser.setName("New Name");

        Response response = updateUserWithoutAuth(updatedUser);

        response.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Step("Обновление данных пользователя с авторизацией")
    private Response updateUserWithAuth(String token, User user) {
        return userClient.updateUser(token, user);
    }

    @Step("Обновление данных пользователя без авторизации")
    private Response updateUserWithoutAuth(User user) {
        return userClient.updateUserWithoutAuth(user);
    }
}