package utils;

import model.User;
import model.Credentials;
import java.util.UUID;

public class TestDataGenerator {

    public static User generateRandomUser() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return new User(
                "user_" + uniqueId + "@test.com",
                "password123",
                "User_" + uniqueId
        );
    }

    public static User getUserWithMissingField(String missingField) {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String email = "user_" + uniqueId + "@test.com";
        String password = "password123";
        String name = "User_" + uniqueId;

        switch (missingField) {
            case "email":
                return new User(null, password, name);
            case "password":
                return new User(email, null, name);
            case "name":
                return new User(email, password, null);
            default:
                return new User(email, password, name);
        }
    }

    public static Credentials getValidCredentials(User user) {
        return new Credentials(user.getEmail(), user.getPassword());
    }

    public static Credentials getInvalidCredentials() {
        return new Credentials("invalid@test.com", "wrongpassword");
    }
}