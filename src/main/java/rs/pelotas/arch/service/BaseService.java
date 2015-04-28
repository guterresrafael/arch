package rs.pelotas.arch.service;

import java.io.Serializable;
import java.util.List;
import javax.validation.ConstraintViolationException;
import rs.pelotas.arch.entity.BaseEntity;
import rs.pelotas.arch.filter.BaseFilter;
import rs.pelotas.arch.helper.Field;
import rs.pelotas.arch.repository.BaseRepository;

/**
 *
 * @author Rafael Guterres
 * @param <EntityType>
 * @param <IdType>
 */
public interface BaseService<EntityType extends BaseEntity, IdType extends Serializable> {
    
    BaseRepository<EntityType, IdType> getRepository();

    void validate(EntityType entity) throws ConstraintViolationException;
    
    EntityType load(IdType id);

    EntityType save(EntityType entity);
    
    void delete(IdType id);
    
    List<EntityType> findAll();
    
    Long countAll();

    List<EntityType> findByFilter(BaseFilter filter);
    
    Long countByFilter(BaseFilter filter);
    
    List<EntityType> findAllWithPagination(Integer offset, Integer limit);
    
    Long countAllWithPagination(Integer offset, Integer limit);
    
    List<EntityType> findByFilterWithPagination(BaseFilter filter, Integer offset, Integer limit);
    
    Long countByFilterWithPagination(BaseFilter filter, Integer offset, Integer limit);
    
    List<EntityType> findByFieldListWithPagination(List<Field> fieldList, Integer offset, Integer limit);
    
    Long countByFieldListWithPagination(List<Field> fieldList, Integer offset, Integer limit);
}