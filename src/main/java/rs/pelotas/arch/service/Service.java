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
 * @param <EntityType>
 * @param <IdType>
 */
public interface Service<EntityType extends BaseEntity, IdType extends Serializable> {
    
    Repository<EntityType, IdType> getRepository();

    void validate(EntityType entity) throws ConstraintViolationException;
    
    EntityType load(IdType id);

    EntityType save(EntityType entity);
    
    void delete(IdType id);
    
    List<EntityType> findAll();
    
    Long countAll();

    List<EntityType> findByFilter(Filter filter);
    
    Long countByFilter(Filter filter);
    
    List<EntityType> findAllWithPagination(Integer offset, Integer limit);
    
    Long countAllWithPagination(Integer offset, Integer limit);
    
    List<EntityType> findByFilterWithPagination(Filter filter, Integer offset, Integer limit);
    
    Long countByFilterWithPagination(Filter filter, Integer offset, Integer limit);
    
    List<EntityType> findByFieldListWithPagination(List<Field> filterList, List<Field> sortList, Integer offset, Integer limit);
    
    Long countByFieldListWithPagination(List<Field> fieldList, Integer offset, Integer limit);
}