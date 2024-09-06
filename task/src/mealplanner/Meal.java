package mealplanner;

public class Meal {
    private final String category;
    private final String name;
    private final String[] ingredients;
    private static int meal_id = 0;

    public Meal(String category, String name, String[] ingredients) {
        this.category = category;
        this.name = name;
        this.ingredients = ingredients;
        meal_id += 1;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public static int getMeal_id() {
        return meal_id;
    }

    @Override
    public String toString() {
        return String.format("""
                Category: %s
                Name: %s
                Ingredients:
                %s
                """, category, name, String.join("\n", ingredients));
    }

    static class Builder {
        private String category;
        private String name;
        private String [] ingredients;

        Builder() {
        }

        Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        Builder setName(String name) {
            this.name = name;
            return this;
        }

        Builder setIngredients(String[] ingredients) {
            this.ingredients = ingredients;
            return this;
        }

        Meal build () {
            return new Meal(category, name, ingredients);
        }
    }

}
