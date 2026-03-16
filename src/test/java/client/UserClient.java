package client;

import io.restassured.response.Response;
import model.User;
import model.Credentials;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class UserClient {
    private static final String USER_PATH = "/api/auth/user";
    private static final String REGISTER_PATH = "/api/auth/register";
    private static final String LOGIN_PATH = "/api/auth/login";

    public Response createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .post(REGISTER_PATH);
    }

    public Response loginUser(Credentials credentials) {
        return given()
                .header("Content-type", "application/json")
                .body(credentials)
                .post(LOGIN_PATH);
    }

    public Response updateUser(String token, User user) {
        Map<String, Object> updates = new HashMap<>();
        if (user.getEmail() != null) {
            updates.put("email", user.getEmail());
        }
        if (user.getPassword() != null) {
            updates.put("password", user.getPassword());
        }
        if (user.getName() != null) {
            updates.put("name", user.getName());
        }

        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(updates)
                .patch(USER_PATH);
    }

    public Response updateUserWithoutAuth(User user) {
        Map<String, Object> updates = new HashMap<>();
        if (user.getEmail() != null) {
            updates.put("email", user.getEmail());
        }
        if (user.getPassword() != null) {
            updates.put("password", user.getPassword());
        }
        if (user.getName() != null) {
            updates.put("name", user.getName());
        }

        return given()
                .header("Content-type", "application/json")
                .body(updates)
                .patch(USER_PATH);
    }

    public Response deleteUser(String token) {
        return given()
                .header("Authorization", token)
                .delete(USER_PATH);
    }

    public Response getUserOrders(String token) {
        return given()
                .header("Authorization", token)
                .get("/api/orders");
    }

    public Response getUserOrdersWithoutAuth() {
        return given()
                .get("/api/orders");
    }
}