package mealplanner;

import java.util.Scanner;

public class Planner {
    private static final Scanner scanner = new Scanner(System.in);
    private static final PlannerDb db = new PlannerDb();
    private static final DbPlan plan = new DbPlan();

    public void menu() throws Exception {

        String option;
        do {
            System.out.println("What would you like to do (add, show, plan, list plan, save, exit)?");
            option = scanner.nextLine();

            switch (option) {
                case "add" -> categoryChoose();
                case "show" -> show();
                case "plan" -> plan.add();
                case "list plan" -> {
                    if (!plan.isEmpty()){
                        plan.show();
                    } else {
                        System.out.println("Database does not contain any meal plan");
                    }
                }
                case "save" -> {
                    if (!plan.isEmpty()){
                        plan.save();
                    } else {
                        System.out.println("Unable to save. Plan your meals first.");
                    }
                }
            }
        } while (!option.equals("exit"));

        System.out.println("Bye!");
    }

    private static void categoryChoose() {
        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");

        boolean flag;
        do {
            String category = scanner.nextLine();
            flag = false;

            if (category.matches("breakfast|lunch|dinner")){
                addMealOperation(category);
            } else {
                wrongCategoryError();
                flag = true;
            }
        } while (flag);
    }

    private static void show() throws Exception{

        System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");

        while (true) {
            String category = scanner.nextLine();
            if (category.matches("breakfast|lunch|dinner")) {
                if (db.isEmpty(category)) {
                    System.out.println("No meals found.");
                    return;
                }
                System.out.println("Category: " + category);
                db.show(category);
                break;
            } else {
                wrongCategoryError();
            }
        }
    }

    private static void addMealOperation(String mealCategory) {
        Meal meal = new Meal.Builder()
                .setCategory(mealCategory)
                .setName(getMealsName())
                .setIngredients(getMealsIngredients())
                .build();

        try {
            db.add(meal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("The meal has been added!");
    }

    private static String getMealsName(){
        System.out.println("Input the meal's name:");

        while (true) {
            String[] name = scanner.nextLine().split(",");
            if (isValuableInput(name)) return name[0];
            wrongFormatError();
        }
    }

    private static String[] getMealsIngredients() {
        System.out.println("Input the ingredients:");

        while (true) {
            String[] ingredients = scanner.nextLine().split(",\\s?");
            if (isValuableInput(ingredients)) return ingredients;
            wrongFormatError();
        }
    }

    private static boolean isValuableInput(String[] list) {
        boolean result = true;

        for(String unit : list) {
            if (!unit.matches("[a-zA-z]+\\s?[a-zA-z]*")) {
                result = false;
                break;
            }
        }
        return result;
    }

    private static void wrongFormatError() {
        System.out.println("Wrong format. Use letters only!");
    }

    private static void wrongCategoryError() {
        System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
    }
}
