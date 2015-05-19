package rs.pelotas.arch.service;

import java.io.Serializable;
import java.util.List;
import javax.validation.ConstraintViolationException;
import rs.pelotas.arch.entity.BaseEntity;
import rs.pelotas.arch.repository.Filter;
import rs.pelotas.arch.helper.Field;
import rs.pelotas.arch.repository.Repository;

/**
 *
 * @author Rafael Guterres
 * @param <T>
 * @param <I>
 */
public interface Service<T extends BaseEntity, I extends Serializable> extends Serializable {
    
    Repository<T, I> getRepository();

    void validate(T entity) throws ConstraintViolationException;
    
    T load(I id);

    T save(T entity);
    
    void delete(I id);
    
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
}