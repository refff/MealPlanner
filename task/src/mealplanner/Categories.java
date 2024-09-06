package mealplanner;

enum Categories {
    BREAKFAST ("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner");

    private String name;

    Categories(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
