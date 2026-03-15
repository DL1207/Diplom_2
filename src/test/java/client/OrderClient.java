package client;

import io.restassured.response.Response;
import model.Order;
import java.util.List;
import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String ORDERS_PATH = "/api/orders";

    public Response createOrder(String token, List<String> ingredients) {
        Order order = new Order();
        order.setIngredients(ingredients);

        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(order)
                .post(ORDERS_PATH);
    }

    public Response createOrderWithoutAuth(List<String> ingredients) {
        Order order = new Order();
        order.setIngredients(ingredients);

        return given()
                .header("Content-type", "application/json")
                .body(order)
                .post(ORDERS_PATH);
    }
}