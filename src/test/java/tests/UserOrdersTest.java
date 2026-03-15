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
import static org.hamcrest.Matchers.*;

@Feature("Получение заказов пользователя")
public class UserOrdersTest extends BaseTest {
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

        orderClient.createOrder(accessToken, validIngredients);
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    @Description("Проверка получения списка заказов авторизованным пользователем")
    @Story("Успешное получение заказов")
    public void testGetUserOrdersWithAuth() {
        Response response = getUserOrders(accessToken);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("orders", notNullValue())
                .body("total", notNullValue())
                .body("totalToday", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    @Description("Проверка ошибки при получении заказов без авторизации")
    @Story("Негативные сценарии")
    public void testGetUserOrdersWithoutAuth() {
        Response response = getUserOrdersWithoutAuth();

        response.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Step("Получение списка валидных ингредиентов")
    private List<String> getValidIngredients() {
        Response response = ingredientsClient.getIngredients();
        return response.then()
                .statusCode(200)
                .extract()
                .path("data._id");
    }

    @Step("Получение заказов пользователя")
    private Response getUserOrders(String token) {
        return userClient.getUserOrders(token);
    }

    @Step("Получение заказов без авторизации")
    private Response getUserOrdersWithoutAuth() {
        return userClient.getUserOrdersWithoutAuth();
    }
}