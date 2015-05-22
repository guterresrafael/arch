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
            return getRepository().merge(entity);
        }
        return entity;
    }

    @Override
    public void delete(I id) {
        getRepository().remove(id);
    }

    @Override
    public List<T> find() {
        return getRepository().find();
    }
    
    @Override
    public Long count() {
        return getRepository().count();
    }
    
    @Override
    public List<T> find(Integer offset, Integer limit) {
        return getRepository().find(offset, limit);
    }

    @Override
    public Long count(Integer offset, Integer limit) {
        return getRepository().count(offset, limit);
    }

    @Override
    public List<T> find(Filter filter) {
        return getRepository().find(filter);
    }

    @Override
    public Long count(Filter filter) {
        return getRepository().count(filter);
    }

    @Override
    public List<T> find(Filter filter, Integer offset, Integer limit) {
        return getRepository().find(filter, offset, limit);
    }
    
    @Override
    public Long count(Filter filter, Integer offset, Integer limit) {
        return getRepository().count(filter, offset, limit);
    }

    @Override
    public List<T> find(List<Field> filterList, List<Field> sortList, Integer offset, Integer limit) {
        return getRepository().find(filterList, sortList, offset, limit);
    }

    @Override
    public Long count(List<Field> fieldList, List<Field> sortList, Integer offset, Integer limit) {
        return getRepository().count(fieldList, sortList, offset, limit);
    }
    
    @Override
    public void validate(T entity) throws ConstraintViolationException {
        Set<ConstraintViolation<T>> violations = validator.validate(entity);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
    }
}