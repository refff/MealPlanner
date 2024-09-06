package mealplanner;

import java.sql.*;

public class Main {
    public static void main(String[] args){
        Planner planner = new Planner();

        try {
            planner.menu();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}