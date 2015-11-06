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

    List<T> find();

    Long count();

    List<T> find(Integer offset, Integer limit);

    Long count(Integer offset, Integer limit);

    List<T> find(Filter filter);

    Long count(Filter filter);

    List<T> find(Filter filter, Integer offset, Integer limit);

    Long count(Filter filter, Integer offset, Integer limit);

    List<T> find(List<Field> filterList, List<Field> sortList, Integer offset, Integer limit);

    Long count(List<Field> fieldList, List<Field> sortList, Integer offset, Integer limit);

    Path getPath(Root root, String strPath);
}
