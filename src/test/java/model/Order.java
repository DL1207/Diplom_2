package model;

import lombok.Data;
import java.util.List;

@Data
public class Order {
    private List<String> ingredients;
}