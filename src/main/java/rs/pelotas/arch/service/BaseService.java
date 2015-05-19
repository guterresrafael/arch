package rs.pelotas.arch.service;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import rs.pelotas.arch.entity.BaseEntity;
import rs.pelotas.arch.repository.Filter;
import rs.pelotas.arch.helper.Field;

/**
 *
 * @author Rafael Guterres
 * @param <T>
 * @param <I>
 */
public abstract class BaseService<T extends BaseEntity, I extends Serializable> implements Service<T, I> {

    private static final long serialVersionUID = -6857150974317994345L;

    @Inject
    Validator validator;
    
    @Override
    public T load(I id) {
        return getRepository().load(id);
    }

    @Override
    public T save(T entity) {
        if (entity != null && ((BaseEntity) entity).getId() != null) {
            getRepository().persist(entity);
        } else {
            entity = getRepository().merge(entity);
        }
        return entity;
    }

    @Override
    public void delete(I id) {
        getRepository().remove(id);
    }

    @Override
    public List<T> findAll() {
        return getRepository().findAll();
    }
    
    @Override
    public Long countAll() {
        return getRepository().countAll();
    }

    @Override
    public List<T> findByFilter(Filter filter) {
        return getRepository().findByFilter(filter);
    }

    @Override
    public Long countByFilter(Filter filter) {
        return getRepository().countByFilter(filter);
    }

    
    @Override
    public List<T> findAllWithPagination(Integer offset, Integer limit) {
        return getRepository().findAllWithPagination(offset, limit);
    }

    @Override
    public Long countAllWithPagination(Integer offset, Integer limit) {
        return getRepository().countAllWithPagination(offset, limit);
    }

    @Override
    public List<T> findByFilterWithPagination(Filter filter, Integer offset, Integer limit) {
        return getRepository().findByFilterWithPagination(filter, offset, limit);
    }
    
    @Override
    public Long countByFilterWithPagination(Filter filter, Integer offset, Integer limit) {
        return getRepository().countByFilterWithPagination(filter, offset, limit);
    }

    @Override
    public List<T> findByFieldListWithPagination(List<Field> filterList, List<Field> sortList, Integer offset, Integer limit) {
        return getRepository().findByFieldListWithPagination(filterList, sortList, offset, limit);
    }

    @Override
    public Long countByFieldListWithPagination(List<Field> fieldList, Integer offset, Integer limit) {
        return getRepository().countByFieldListWithPagination(fieldList, offset, limit);
    }
    
    @Override
    public void validate(T entity) throws ConstraintViolationException {
        Set<ConstraintViolation<T>> violations = validator.validate(entity);
        
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
    }
}