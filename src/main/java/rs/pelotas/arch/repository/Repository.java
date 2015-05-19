package rs.pelotas.arch.repository;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import rs.pelotas.arch.entity.BaseEntity;
import rs.pelotas.arch.helper.Field;

/**
 *
 * @author Rafael Guterres
 * @param <T>
 * @param <I>
 */
public interface Repository<T extends BaseEntity, I extends Serializable> extends Serializable {

    EntityManager getEntityManager();

    T load(I id);

    void persist(T entity);

    T merge(T entity);

    void remove(I id);

    List<T> findAll();

    Long countAll();

    List<T> findByFilter(Filter filter);

    Long countByFilter(Filter filter);

    List<T> findAllWithPagination(Integer offset, Integer limit);

    Long countAllWithPagination(Integer offset, Integer limit);

    List<T> findByFilterWithPagination(Filter filter, Integer offset, Integer limit);

    Long countByFilterWithPagination(Filter filter, Integer offset, Integer limit);

    List<T> findByFieldListWithPagination(List<Field> filterList, List<Field> sortList, Integer offset, Integer limit);

    Long countByFieldListWithPagination(List<Field> fieldList, Integer offset, Integer limit);

    Path getPath(Root root, String strPath);
}