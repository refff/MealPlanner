package mealplanner;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class PlannerDb {
    private final Connection connection;
    private int numOfRows = 0;
    private Scanner scanner = new Scanner(System.in);

    public PlannerDb () {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql:meals_db", "postgres", "1111)");
            connection.setAutoCommit(true);
            createTables();
            numOfRows();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void add (Meal meal) {
        numOfRows++;
        String mealValues = String.format("('%s', '%s', '%d')",
                meal.getName(), meal.getCategory(), numOfRows);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("insert into meals (" +
                    "meal, " +
                    "category, " +
                    "meal_id)" +
                    "values " + mealValues);

            for (String ingredient : meal.getIngredients()) {
                String ingredientValues = String.format("('%s', '%d')", ingredient, numOfRows);

                statement.executeUpdate("insert into ingredients (" +
                        "ingredient, " +
                        "meal_id)" +
                        "values " + ingredientValues);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void show (String category) {
        try {
            Statement statementMeal = connection.createStatement();
            Statement statementIngredient = connection.createStatement();

            String query = String.format("select * from meals where category = '%s';", category);
            ResultSet resultSetMeal = statementMeal.executeQuery(query);

            while (resultSetMeal.next()) {
                System.out.println();
                System.out.println("Name: " + resultSetMeal.getString("meal"));
                System.out.println("Ingredients:");

                ResultSet resultSetIngredients = statementIngredient.executeQuery(
                        "select * from ingredients where meal_id = " + resultSetMeal.getInt("meal_id") + ";");

                while (resultSetIngredients.next()) {
                    System.out.println(resultSetIngredients.getString("ingredient").strip());
                }
            }
            System.out.println();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTables () {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS meals (" +
                    "meal varchar, " +
                    "category varchar, " +
                    "meal_id integer PRIMARY KEY);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ingredients (" +
                    "ingredient varchar, " +
                    "ingredient_id integer generated always as identity PRIMARY KEY, " +
                    "meal_id integer);");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isEmpty (String category) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format(
                    "select count(*) from meals where category = '%s';", category));
            resultSet.next();
            int rowsCount = resultSet.getInt(1);
            return rowsCount == 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void numOfRows() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select count(*) from meals;");
            resultSet.next();
            numOfRows = resultSet.getInt(1);
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
