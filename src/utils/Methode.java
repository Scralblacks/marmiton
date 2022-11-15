package utils;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Methode {

    public static String askString(String ask){
        try(Scanner scan = new Scanner(System.in)){
            System.out.println(ask);
            return scan.nextLine();
        }
    }

    public static int askInt(String ask){
        try(Scanner scan = new Scanner(System.in)){
            System.out.println(ask);
            return scan.nextInt();
        }
    }

    public static LocalDate askDate(String ask){
        try(Scanner scan = new Scanner(System.in)){
            System.out.println(ask + "(format yyyy-mm-dd)");
            return LocalDate.parse(scan.nextLine(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        }
    }

    public static void loadDriver() {
        try{
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        } catch (SQLException e){
            e.printStackTrace();
            System.err.println("Driver SQL introuvable.");
        }
    }

    public static Connection connectDB(){
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/tp_marmiton", "root", "");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createRecipe() {

        String name, description,
                query = "INSERT INTO recipe" +
                        "(name, description, lastUsed)" +
                        "VALUE" +
                        "(?, ?, ?)";
        name = askString("Quel est la nom  du plat à ajouter ?");
        description = askString("Avez-vous une description ou des instructions?");
        LocalDate lastUsed = askDate("Quand avez-vous manger ce plat pour la dernière fois ?");
        try (Connection con = connectDB()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, description);
                preparedStatement.setDate(3, Date.valueOf(lastUsed));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
        public static void selectRecipeById(long id){
            String query = "SELECT * FROM recipe" +
                    "WHERE idRecipe = ?";
            try (Connection con = connectDB()) {
                try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                    preparedStatement.setLong(1, id);
                    ResultSet rs =preparedStatement.executeQuery();
                    System.out.println(new StringBuilder("Name : ")
                            .append(rs.getString("name"))
                            .append("\nDescription : ")
                            .append(rs.getString("description"))
                            .append("\nLast time eaten : ")
                            .append(rs.getDate("lastUsed").toLocalDate()));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    public static void selectRecipeByKeyWord(String keyWord){
        String query = "SELECT * FROM recipe" +
                "WHERE name LIKE ? OR description LIKE ?";
        try (Connection con = connectDB()) {
            con.setAutoCommit(false);
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, "%" + keyWord + "%");
                preparedStatement.setString(2, "%" + keyWord + "%");
                ResultSet rs =preparedStatement.executeQuery();
                System.out.println(new StringBuilder("Name : ")
                        .append(rs.getString("name"))
                        .append("\nDescription : ")
                        .append(rs.getString("description"))
                        .append("\nLast time eaten : ")
                        .append(rs.getDate("lastUsed").toLocalDate()));
                con.commit();
            } catch (SQLException e) {
                connectDB().rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void selectRecipeRand() {

        LocalDate dateMin = LocalDate.now().plus(-6, ChronoUnit.DAYS);
        List<Integer> validId = new ArrayList<>();

        String query = "SELECT idRecipe FROM recipe" +
                "WHERE lastUsed <= DATE ?";
        try (Connection con = connectDB()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setDate(1, Date.valueOf(dateMin));
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    validId.add(rs.getInt("idRecipe"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        int alea = (int)(validId.size() * Math.random());
        selectRecipeById(validId.get(alea));
    }

    public static boolean deleteRecipe(long id) throws SQLException {
        String query = "DELETE FROM recipe" +
                "WHERE idRecipe = ?";
        try (Connection con = connectDB()) {
            con.setAutoCommit(false);
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setLong(1, id);
                preparedStatement.executeQuery();
                con.commit();
                return true;
            }
        } catch (SQLException e) {
            connectDB().rollback();
            throw new RuntimeException(e);
        }
    }
}