package dao;

import dao.RecipeJdbcDao;
import dao.CrudDAO;
import objet.Recipe;

public class DaoFactory {

    private DaoFactory() {
    }

    public static CrudDAO<Recipe> getRecipeDao() {
        return new RecipeJdbcDao();
    }
}