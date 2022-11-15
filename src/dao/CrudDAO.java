package dao;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CrudDAO<E> {

        List<E> findAll();

        Optional<E> findById(Long id);

        boolean delete(Long id) throws SQLException;

        E update(E element) throws SQLException;

        E create(E element) throws SQLException;
    }

