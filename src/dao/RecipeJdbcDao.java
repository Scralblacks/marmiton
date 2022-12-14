package dao;

import objet.Recipe;
import utils.Methode;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RecipeJdbcDao implements CrudDAO<Recipe> {

    public Optional<Recipe> findRand() throws SQLException {
        LocalDate dateMin = LocalDate.now().minus(6, ChronoUnit.DAYS);
        List<Long> validId = new ArrayList<>();

        String query = "SELECT idRecipe FROM recipe " +
                "WHERE lastUsed <= DATE ?";
       Connection con = ConnectionManager.getConnectionInstance();
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setDate(1, Date.valueOf(dateMin));
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    validId.add(rs.getLong(1));
                }
            }
        int alea = (int)(validId.size() * Math.random());
        return findById(validId.get(alea));
    }

    public static void createListedId(String table, String name) throws SQLException {
        String query = "INSERT INTO ? " +
                "(name) " +
                "VALUE " +
                "(?)";
        Connection con = ConnectionManager.getConnectionInstance();
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, table);
                preparedStatement.setString(2, name);
                preparedStatement.executeQuery();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw new RuntimeException(e);
            }
    }

    public static Optional<Integer> addListedId(String table, String name) throws SQLException {
        StringBuilder listId = null;
        String query = "SELECT * FROM ? " +
                "WHERE name LIKE ? ";
        Connection con = ConnectionManager.getConnectionInstance();
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, table);
                preparedStatement.setString(2, "%" + name + "%");
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return Optional.of(rs.getInt(1));
                } else {
                    System.out.println("L'ingr??dient n'existait pas, il a ??t?? ajout??.");
                    createListedId(table, name);
                    return Optional.empty();
                }
            }
            catch (SQLException e) {
                con.rollback();
                throw new RuntimeException(e);
            }
    }

    @Override
    public List<Recipe> findAll() throws SQLException {
        List<Recipe> result = new ArrayList<>();
        String query = "SELECT * FROM recipe ";
        Connection con = ConnectionManager.getConnectionInstance();
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                ResultSet rs =preparedStatement.executeQuery();
                while (rs.next()){
                result.add(new Recipe(rs.getLong(1), rs.getString(2), rs.getString(3)
                        , rs.getString(4), rs.getString(5), rs.getDate(6).toLocalDate(), rs.getString(7)));
                }
                con.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        return result;
    }

    @Override
    public Optional<Recipe> findById(Long id) throws SQLException {
        String query = "SELECT * FROM recipe " +
                "WHERE idRecipe = ?";
            try (Connection con = ConnectionManager.getConnectionInstance();
                    PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setLong(1, id);
                ResultSet rs =preparedStatement.executeQuery();
                rs.next();
                con.commit();
                return Optional.of(new Recipe(rs.getLong(1), rs.getString(2), rs.getString(3)
                        , rs.getString(4), rs.getString(5), rs.getDate(6).toLocalDate(), rs.getString(7)));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }

    public Optional<Recipe> findByKeyWord(String KeyWord) throws SQLException {
        String query = "SELECT * FROM recipe " +
                "WHERE name LIKE ? OR instruction LIKE ? OR description LIKE ?";
        try(Connection con = ConnectionManager.getConnectionInstance();){
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, "%" + KeyWord + "%");
                preparedStatement.setString(2, "%" + KeyWord + "%");
                preparedStatement.setString(3, "%" + KeyWord + "%");
                ResultSet rs =preparedStatement.executeQuery();
                rs.next();
                con.commit();
                return Optional.of(new Recipe(rs.getLong(1), rs.getString(2), rs.getString(3)
                    , rs.getString(4), rs.getString(5), rs.getDate(6).toLocalDate(), rs.getString(7)));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        String query = "DELETE FROM recipe " +
                "WHERE idRecipe = ?";
        try (Connection con = ConnectionManager.getConnectionInstance();){
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setLong(1, id);
                preparedStatement.executeQuery();
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Recipe update(Recipe recipe) throws SQLException {
        String newName = updateName(recipe), newInstruction = updateInstruction(recipe), newIdListIngredient = updateListeIngredient(recipe),
                newIdListCookingTool = updateListeCookingTool(recipe), newDescription = updateDescription(recipe),
                query = "UPDATE recipe" +
                        "SET name = ?, description = ?, instruction = ?, listIdCookingTool = ?, listIdIngredient = ?, dateLastCooked = ?" +
                        "WHERE id = ?";
        LocalDate newDate = updateDate(recipe);
        recipe.setName(newName);
        recipe.setDescription(newDescription);
        recipe.setInstruction(newInstruction);
        recipe.setListIdCookingTool(newIdListCookingTool);
        recipe.setListIdIngredient(newIdListIngredient);
        recipe.setWhenLastCooked(newDate);
        try(Connection con = ConnectionManager.getConnectionInstance();){
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, newName);
                preparedStatement.setString(2, newDescription);
                preparedStatement.setString(3, newInstruction);
                preparedStatement.setString(4, newIdListCookingTool);
                preparedStatement.setString(5, newIdListIngredient);
                preparedStatement.setDate(6, Date.valueOf(newDate));
                preparedStatement.setLong(7, recipe.getId());
                preparedStatement.executeQuery();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw new RuntimeException(e);
            }
        return recipe;
        }
    }

    public String updateName(Recipe recipe){
       Boolean choice = Methode.askBoolean("Voulez-vous changer le nom de la recette ?");
       if (choice){
           return Methode.askString("Quel est le nouveau nom de la recette ?");
       } else {
           return recipe.getName();
       }
    }

    public String updateInstruction(Recipe recipe){
        Boolean choice = Methode.askBoolean("Voulez-vous changer les instructions de la recette ?");
        if (choice){
            return Methode.askString("Quels sont les nouvelles instructions de la recette ?");
        } else {
            return recipe.getInstruction();
        }
    }

    public String updateDescription(Recipe recipe){
        Boolean choice = Methode.askBoolean("Voulez-vous changer la description de la recette ?");
        if (choice){
            return Methode.askString("Quelle est la nouvelle description de la recette ?");
        } else {
            return recipe.getDescription();
        }
    }

    public LocalDate updateDate(Recipe recipe){
        Boolean choice = Methode.askBoolean("Voulez-vous changer la date a laquelle vous avez cuisin?? cette recette ?");
        if (choice){
            return Methode.askDate("Quelle est la nouvelle date ?");
        } else {
            return recipe.getWhenLastCooked();
        }
    }

    public String updateListeIngredient(Recipe recipe) throws SQLException {
        Boolean choice = Methode.askBoolean("Voulez-vous changer la liste des ingr??dients de la recette ?");
        String nameIngredient;
        List<Optional<Integer>> listeIdIngredient = new ArrayList<>();
        if (choice){
            do {
                nameIngredient = Methode.askString("Y a-t-il des ingr??dients ?? ajouter ?? la recette ? (sinon taper NON)");
                if (!nameIngredient.equals("NON")){
                    listeIdIngredient.add(addListedId("ingredient",nameIngredient));
                }
            } while (!nameIngredient.equals("NON"));
            return Arrays.toString(new List[]{listeIdIngredient});
        } else {
            return recipe.getListIdIngredient();
        }
    }

    public String updateListeCookingTool(Recipe recipe) throws SQLException {
        Boolean choice = Methode.askBoolean("Voulez-vous changer la liste des ustensiles de la recette ?");
        String nameCookingTool;
        List<Optional<Integer>> listeIdIngredient = new ArrayList<>();
        if (choice){
            do {
                nameCookingTool = Methode.askString("Y a-t-il des ustensiles ?? ajouter ?? la recette ? (sinon taper NON)");
                if (!nameCookingTool.equals("NON")){
                    listeIdIngredient.add(addListedId("cookingToll",nameCookingTool));
                }
            } while (!nameCookingTool.equals("NON"));
            return Arrays.toString(new List[]{listeIdIngredient});
        } else {
            return recipe.getListIdIngredient();
        }
    }

    @Override
    public Recipe create(Recipe recipe) throws SQLException {
        long id = 0;
        String ingredientName, cookingToolName,
                query = "INSERT INTO recipe " +
                        "(name, description, dateLastCooked, instruction, listIdIngredient, listIdCookingTool) " +
                        "VALUE " +
                        "(?, ?, ?, ?, ?, ?)";
        StringBuilder ingredientList = null, cookingToolList = null;
        List<Optional<Integer>> listeIdIngredient = new ArrayList<>(), listeIdCookingTool = new ArrayList<>();
        String name = Methode.askString("Quel est la nom  du plat ?? ajouter ?");
        String instruction = Methode.askString("Avez-vous des instructions ?");
        LocalDate lastUsed = Methode.askDate("Quand avez-vous manger ce plat pour la derni??re fois ?");
        String description = Methode.askString("Avez-vous une description ?");
        try(Connection con = ConnectionManager.getConnectionInstance()){
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                do {
                    ingredientName = Methode.askString("Y a-t-il des ingr??dients ?? ajouter ?? la recette ? (sinon taper NON)");
                    if (!ingredientName.equals("NON")){
                        listeIdIngredient.add(addListedId("ingredient",ingredientName));
                    }
                } while (!ingredientName.equals("NON"));
                do {
                    cookingToolName = Methode.askString("Y a-t-il des ustensiles de cuisine ?? ajouter ?? la recette ? (sinon taper NON)");
                    if (!cookingToolName.equals("NON")){
                        listeIdCookingTool.add(addListedId("cookingTool", cookingToolName));
                    }
                } while (!cookingToolName.equals("NON"));
                for (Optional<Integer> idInLIst : listeIdIngredient){
                    if (ingredientList.isEmpty()){
                    ingredientList.append(idInLIst);
                    } else {
                        ingredientList.append(", ").append(idInLIst);
                    }
                }
                for (Optional<Integer> idInLIst : listeIdCookingTool){
                    if (cookingToolList.isEmpty()){
                        cookingToolList.append(idInLIst);
                    } else {
                        cookingToolList.append(", ").append(idInLIst);
                    }
                }
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, description);
                preparedStatement.setDate(3, Date.valueOf(lastUsed));
                preparedStatement.setString(4, instruction);
                preparedStatement.setString(5, String.valueOf(ingredientList));
                preparedStatement.setString(6, String.valueOf(cookingToolList));
                preparedStatement.executeQuery();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw new RuntimeException(e);
            }
            String query1 = "SELECT idRecipe FROM recipe WHERE name = ? AND instruction = ?";
            try (PreparedStatement preparedStatement = con.prepareStatement(query1)) {
                preparedStatement.setString(1,name);
                preparedStatement.setString(2,instruction);
                ResultSet rs = preparedStatement.executeQuery();
                rs.next();
                id = rs.getLong(1);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw new RuntimeException(e);
            }
            return new Recipe(id, name, instruction, String.valueOf(ingredientList), String.valueOf(cookingToolList), lastUsed, description);
        }
    }

    public static void print(Recipe recipe) throws SQLException {
        StringBuilder listCookingTool = null , listIngredient = null;
        String queryC = "SELECT name FROM cooking_tool WHERE idCookingTool IN (SELECT CONVERT(value, UNSIGNED INTEGER) FROM STRING_SPLIT(?, \", \"))"
                , queryI = "SELECT name FROM ingredient WHERE idIngredient IN (SELECT CONVERT(value, UNSIGNED INTEGER) FROM STRING_SPLIT(?, \", \"))";
        try(Connection con = ConnectionManager.getConnectionInstance()) {
            try (PreparedStatement preparedStatement = con.prepareStatement(queryC)) {
                preparedStatement.setString(1, recipe.getListIdCookingTool());
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    listCookingTool.append(rs.getString(1));
                }
                con.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try (PreparedStatement preparedStatement = con.prepareStatement(queryI)) {
                preparedStatement.setString(1, recipe.getListIdIngredient());
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    listIngredient.append(rs.getString(1));
                }
                con.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        System.out.println(new StringBuilder("Id : ")
                .append(recipe.getId())
                .append("\nNom : ")
                .append(recipe.getName())
                .append("\nListe des ustensiles de cuisines : ")
                .append(listCookingTool)
                .append("\nListe des ingr??dients : ")
                .append("")//listIngredient)
                .append("\nInstruction : ")
                .append("")//recipe.getInstruction())
                .append("\nDescription : ")
                .append(recipe.getDescription())
                .append("\nDate de derni??re utilisation : ")
                .append(recipe.getWhenLastCooked())
        );
        }
    }
}
