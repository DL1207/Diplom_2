package model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Ingredients {
    private List<String> ingredients;

    public Ingredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

}