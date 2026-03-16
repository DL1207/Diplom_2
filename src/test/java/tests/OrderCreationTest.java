package tests;

import client.OrderClient;
import client.UserClient;
import client.IngredientsClient;
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
import java.util.List;
import java.util.ArrayList;
import static org.hamcrest.Matchers.*;

@Feature("Создание заказа")
public class OrderCreationTest extends BaseTest {
    private OrderClient orderClient;
    private UserClient userClient;
    private IngredientsClient ingredientsClient;
    private User createdUser;
    private String accessToken;
    private List<String> validIngredients;

    @BeforeEach
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
        ingredientsClient = new IngredientsClient();

        createdUser = TestDataGenerator.generateRandomUser();
        Response createResponse = userClient.createUser(createdUser);
        accessToken = createResponse.path("accessToken");

        validIngredients = getValidIngredients();
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    @Description("Проверка успешного создания заказа авторизованным пользователем")
    @Story("Успешное создание заказа")
    public void testCreateOrderWithAuthAndIngredients() {
        Response response = createOrderWithAuth(accessToken, validIngredients);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("order", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации с ингредиентами")
    @Description("Проверка создания заказа без авторизации")
    @Story("Создание заказа без авторизации")
    public void testCreateOrderWithoutAuthWithIngredients() {
        Response response = createOrderWithoutAuth(validIngredients);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("order", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с авторизацией без ингредиентов")
    @Description("Проверка ошибки при создании заказа без ингредиентов")
    @Story("Негативные сценарии")
    public void testCreateOrderWithAuthWithoutIngredients() {
        Response response = createOrderWithAuth(accessToken, new ArrayList<>());

        response.then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Проверка ошибки при создании заказа с неверным хешем")
    @Story("Негативные сценарии")
    public void testCreateOrderWithInvalidIngredientHash() {
        List<String> invalidIngredients = List.of("invalid_hash_123", "invalid_hash_456");

        Response response = createOrderWithAuth(accessToken, invalidIngredients);

        response.then()
                .statusCode(500);
    }

    @Step("Получение списка валидных ингредиентов")
    private List<String> getValidIngredients() {
        Response response = ingredientsClient.getIngredients();
        return response.then()
                .statusCode(200)
                .extract()
                .path("data._id");
    }

    @Step("Создание заказа с авторизацией")
    private Response createOrderWithAuth(String token, List<String> ingredients) {
        return orderClient.createOrder(token, ingredients);
    }

    @Step("Создание заказа без авторизации")
    private Response createOrderWithoutAuth(List<String> ingredients) {
        return orderClient.createOrderWithoutAuth(ingredients);
    }
}