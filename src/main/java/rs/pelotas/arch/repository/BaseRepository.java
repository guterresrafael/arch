package rs.pelotas.arch.repository;

import java.io.Serializable;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import rs.pelotas.arch.entity.BaseEntity;
import rs.pelotas.arch.helper.Field;
import rs.pelotas.arch.helper.Reflection;

/**
 *
 * @author Rafael Guterres
 * @param <EntityType>
 * @param <IdType>
 */
public abstract class BaseRepository<EntityType extends BaseEntity, IdType extends Serializable> 
           implements Repository<EntityType, IdType> {
    
    private final Class<EntityType> entityClass = Reflection.getGenericArgumentType(getClass());
    
    @Override
    public EntityType load(IdType id) {
        EntityType entity = getEntityManager().find(entityClass, id);
        return entity;
    }

    @Override
    public void persist(EntityType entity) {
        getEntityManager().persist(entity);
    }

    @Override
    public EntityType merge(EntityType entity) {
        return (EntityType) getEntityManager().merge(entity);
    }

    @Override
    public void remove(IdType entity) {
        getEntityManager().remove(entity);
    }

    @Override
    public List<EntityType> findAll() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EntityType> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<EntityType> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root);
        return getResultList(criteriaQuery);
    }

    @Override
    public Long countAll() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(entityClass)));
        return getCount(criteriaQuery);
    }
   
    @Override
    public List<EntityType> findByFilter(Filter filter) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EntityType> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<EntityType> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root);
        Criteria.addWhere(criteriaBuilder, criteriaQuery, root, filter);
        return getResultList(criteriaQuery);
    }
    
    @Override
    public Long countByFilter(Filter filter) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<EntityType> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(criteriaBuilder.count(root));
        Criteria.addWhere(criteriaBuilder, criteriaQuery, root, filter);
        return getCount(criteriaQuery);
    }
    
    @Override
    public List<EntityType> findAllWithPagination(Integer offset, Integer limit) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EntityType> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<EntityType> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root);
        return getResultList(criteriaQuery, offset, limit);
    }
    
    @Override
    public Long countAllWithPagination(Integer offset, Integer limit) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(entityClass)));
        return getCount(criteriaQuery, offset, limit);
    }

    @Override
    public List<EntityType> findByFilterWithPagination(Filter filter, Integer offset, Integer limit) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EntityType> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<EntityType> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root);
        Criteria.addWhere(criteriaBuilder, criteriaQuery, root, filter);
        return getResultList(criteriaQuery, offset, limit);
    }

    @Override
    public Long countByFilterWithPagination(Filter filter, Integer offset, Integer limit) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<EntityType> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(criteriaBuilder.count(root));
        Criteria.addWhere(criteriaBuilder, criteriaQuery, root, filter);
        return getCount(criteriaQuery, offset, limit);
    }

    @Override
    public List<EntityType> findByFieldListWithPagination(List<Field> filterList, List<Field> sortList,
                                                          Integer offset, Integer limit) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EntityType> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<EntityType> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root);
        Criteria.addWhere(criteriaBuilder, criteriaQuery, root, filterList);
        Criteria.addOrderBy(criteriaBuilder, criteriaQuery, root, sortList);
        return getResultList(criteriaQuery, offset, limit);
    }

    @Override
    public Long countByFieldListWithPagination(List<Field> fieldList, Integer offset, Integer limit) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<EntityType> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(criteriaBuilder.count(root));
        Criteria.addWhere(criteriaBuilder, criteriaQuery, root, fieldList);
        return getCount(criteriaQuery, offset, limit);
    }

    private List<EntityType> getResultList(CriteriaQuery<?> criteriaQuery) {
        TypedQuery typedQuery = getEntityManager().createQuery(criteriaQuery);
        List<EntityType> entities = typedQuery.getResultList();
        return entities;
    }
    
    private List<EntityType> getResultList(CriteriaQuery<?> criteriaQuery, Integer offset, Integer limit) {
        TypedQuery typedQuery = getEntityManager().createQuery(criteriaQuery);
        List<EntityType> entities = typedQuery.setFirstResult(offset)
                                              .setMaxResults(limit)
                                              .getResultList();
        return entities;
    }
    
    private Long getCount(CriteriaQuery<Long> criteriaQuery) {
        TypedQuery<Long> typedQuery = getEntityManager().createQuery(criteriaQuery);
        Long count = typedQuery.getSingleResult();
        return count;
    }
    
    private Long getCount(CriteriaQuery<Long> criteriaQuery, Integer offset, Integer limit) {
        TypedQuery<Long> typedQuery = getEntityManager().createQuery(criteriaQuery);
        Long count = typedQuery.setFirstResult(offset)
                               .setMaxResults(limit)
                               .getSingleResult();
        return count;
    }
    
    @Override
    public Path getPath(Root root, String strPath) {
        Path path = root;
        String[] fields = strPath.split("\\.");
        for (String field : fields) {
            path = path.get(field);
        }
        return path;
    }
}