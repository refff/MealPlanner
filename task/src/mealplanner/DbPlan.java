package mealplanner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DbPlan<T> {
    private final Connection connection;
    private final Scanner scanner = new Scanner(System.in);
    private final String getMealsId = "select * from meals where meal = '%s'";
    private final String getMealById = "select * from meals where meal_id = %d";

    public DbPlan() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql:meals_db", "postgres", "1111)");
            connection.setAutoCommit(true);
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void add() {
        try (Statement statement = connection.createStatement()){

            for (DaysOfWeek day : DaysOfWeek.values()) {

                System.out.println(day.getName());
                ArrayList<Integer> chosenMealsId = new ArrayList<>();

                for (Categories category:Categories.values()) {

                    String query = String.format("Select * from meals where category = '%s' order by meal;", category.getName().toLowerCase());
                    ResultSet resultSet = statement.executeQuery(query);

                    ArrayList<String> availableMeals = new ArrayList<>();

                    while (resultSet.next()) {
                        String meal = resultSet.getString("meal");
                        availableMeals.add(meal);
                        System.out.println(meal);
                    }

                    System.out.printf("Choose the %s for %s from the list above:\n", category.getName().toLowerCase(), day.getName());
                    inputMeal(availableMeals, chosenMealsId);
                }

                insert(day.getName(), chosenMealsId);
                System.out.printf("Yeah! We planned the meals for %s.\n", day.getName());
                System.out.println();
            }

            show();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void inputMeal(ArrayList<String> availableMeals, ArrayList<Integer> chosenMeals) {
        boolean flag = true;

        while (flag){
            String meal = scanner.nextLine();
            if (availableMeals.contains(meal)) {
                chosenMeals.add((Integer)getValue((T)meal, "meal_id", getMealsId));
                flag = false;
            } else {
                System.out.println("This meal doesn’t exist. Choose a meal from the list above.");
                System.out.println();
            }
        }
    }

    private void insert(String day, ArrayList<Integer> meals) {
        try (Statement statement = connection.createStatement()) {

            int i = 0;
            for (Categories category:Categories.values()) {
                String query = String.format("('%s', '%s', %d)", day, category.getName().toLowerCase(), meals.get(i));
                statement.executeUpdate("insert into plan (meal_option, meal_category, meal_id) values " + query);
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }

    public void show() {
        String name;

        try (Statement statement = connection.createStatement()) {
            for (DaysOfWeek day : DaysOfWeek.values()) {
                System.out.println(day.getName());
                for (Categories category : Categories.values()) {

                    String query = String.format(
                            "select * from plan where meal_option = '%s' and meal_category = '%s'",
                            day.getName(), category.getName().toLowerCase());
                    ResultSet resultSet = statement.executeQuery(query);

                    while (resultSet.next()) {
                        Integer id = resultSet.getInt("meal_id");
                        name = (String) getValue((T)id, "meal", getMealById);

                        System.out.printf("%s: %s\n", category.getName(), name);
                    }
                }
                System.out.println();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        System.out.println("Input a filename:");
        String fileName = scanner.nextLine();

        fileCreating(getAllMeals(), fileName);
    }

    private void fileCreating(HashMap map, String name) {
        //String path = "/Users/fedor/IdeaProjects/Meal Planner (Java)/Shoping list/" + name;
        File file = new File(name);

        Map<String, Integer> list = new HashMap<>();
        try (PrintWriter writer = new PrintWriter(file)) {
            map.forEach((key, value) -> {
                if ((int)value > 1){
                    writer.println(String.format("%s x%d", key, value));
                } else {
                    writer.println(key);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Saved!");
    }

    //turn all ingredients list into hashmap where ingredients is keys and amount is value
    private HashMap listFormat(ArrayList<String> meals) {
        HashMap<String, Integer> list = new HashMap<>();

        for (String ingredient:meals) {
            if (!list.containsKey(ingredient)){
                list.put(ingredient, 1);
            } else {
                int current = list.get(ingredient);
                list.replace(ingredient, current + 1);
            }
        }

        return list;
    }

    private T getValue(T t, String var, String query) {
        T value = null;

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(String.format(query, t));

            while (resultSet.next()){
                value = (T) resultSet.getObject(var);
            }

            return value;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private HashMap getAllMeals() {
        ArrayList<String> ingredients = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select meal_id from plan");

            while (resultSet.next()) {
                Integer id = resultSet.getInt("meal_id");
                ingredients.addAll(getAllIngredients(id));
            }

            //System.out.println(ingredients);
            //System.out.println(listFormat(ingredients));

            return listFormat(ingredients);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<String> getAllIngredients(int id) {
        ArrayList<String> result = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select * from ingredients where meal_id = " + id + ";");

            while (resultSet.next()){
                result.add(resultSet.getString("ingredient"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //(String) getValue((T)id, "ingredient", getIngredient)
        //System.out.println(result);
        return result;
    }

    public boolean isEmpty() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("select count(*) from plan;"));
            resultSet.next();
            int rowsCount = resultSet.getInt(1);
            return rowsCount == 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTables () {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS plan (" +
                    "meal_option VARCHAR," +
                    "meal_category varchar," +
                    "meal_id integer)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
