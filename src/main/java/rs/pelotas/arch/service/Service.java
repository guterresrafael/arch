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
}