package rs.pelotas.arch.repository;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import rs.pelotas.arch.entity.BaseEntity;
import rs.pelotas.arch.filter.BaseFilter;
import rs.pelotas.arch.helper.Field;

/**
 *
 * @author Rafael Guterres
 * @param <EntityType>
 * @param <IdType>
 */
public interface BaseRepository<EntityType extends BaseEntity, IdType extends Serializable> 
         extends Serializable {

    EntityManager getEntityManager();

    EntityType load(IdType id);

    void persist(EntityType entity);

    EntityType merge(EntityType entity);

    void remove(IdType id);

    List<EntityType> findAll();

    Long countAll();

    List<EntityType> findByFilter(BaseFilter filter);

    Long countByFilter(BaseFilter filter);

    List<EntityType> findAllWithPagination(Integer offset, Integer limit);

    Long countAllWithPagination(Integer offset, Integer limit);

    List<EntityType> findByFilterWithPagination(BaseFilter filter, Integer offset, Integer limit);

    Long countByFilterWithPagination(BaseFilter filter, Integer offset, Integer limit);

    List<EntityType> findByFieldListWithPagination(List<Field> filterList, List<Field> sortList, Integer offset, Integer limit);

    Long countByFieldListWithPagination(List<Field> fieldList, Integer offset, Integer limit);

    Path getPath(Root root, String strPath);
}