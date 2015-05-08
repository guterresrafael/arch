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
 * @param <EntityType>
 * @param <IdType>
 */
public abstract class BaseService<EntityType extends BaseEntity, IdType extends Serializable>
           implements Service<EntityType, IdType> {

    @Inject
    Validator validator;
    
    @Override
    public EntityType load(IdType id) {
        return getRepository().load(id);
    }

    @Override
    public EntityType save(EntityType entity) {
        if (entity != null && ((BaseEntity) entity).getId() != null) {
            getRepository().persist(entity);
        } else {
            entity = getRepository().merge(entity);
        }
        return entity;
    }

    @Override
    public void delete(IdType id) {
        getRepository().remove(id);
    }

    @Override
    public List<EntityType> findAll() {
        return getRepository().findAll();
    }
    
    @Override
    public Long countAll() {
        return getRepository().countAll();
    }

    @Override
    public List<EntityType> findByFilter(Filter filter) {
        return getRepository().findByFilter(filter);
    }

    @Override
    public Long countByFilter(Filter filter) {
        return getRepository().countByFilter(filter);
    }

    
    @Override
    public List<EntityType> findAllWithPagination(Integer offset, Integer limit) {
        return getRepository().findAllWithPagination(offset, limit);
    }

    @Override
    public Long countAllWithPagination(Integer offset, Integer limit) {
        return getRepository().countAllWithPagination(offset, limit);
    }

    @Override
    public List<EntityType> findByFilterWithPagination(Filter filter, Integer offset, Integer limit) {
        return getRepository().findByFilterWithPagination(filter, offset, limit);
    }
    
    @Override
    public Long countByFilterWithPagination(Filter filter, Integer offset, Integer limit) {
        return getRepository().countByFilterWithPagination(filter, offset, limit);
    }

    @Override
    public List<EntityType> findByFieldListWithPagination(List<Field> filterList, List<Field> sortList, Integer offset, Integer limit) {
        return getRepository().findByFieldListWithPagination(filterList, sortList, offset, limit);
    }

    @Override
    public Long countByFieldListWithPagination(List<Field> fieldList, Integer offset, Integer limit) {
        return getRepository().countByFieldListWithPagination(fieldList, offset, limit);
    }
    
    @Override
    public void validate(EntityType entity) throws ConstraintViolationException {
        Set<ConstraintViolation<EntityType>> violations = validator.validate(entity);
        
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
    }
}