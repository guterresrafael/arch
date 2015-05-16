package rs.pelotas.arch.repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
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

    private static final long serialVersionUID = 1946014114445975865L;

    private final Class<EntityType> entityClass = Reflection.getGenericArgumentType(getClass());

    @Inject
    EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public EntityType load(IdType id) {
        EntityType entity = getEntityManager().find(entityClass, id);
        return entity;
    }

    @Transactional
    @Override
    public void persist(EntityType entity) {
        getEntityManager().persist(entity);
    }

    @Transactional
    @Override
    public EntityType merge(EntityType entity) {
        return (EntityType) getEntityManager().merge(entity);
    }

    @Transactional
    @Override
    public void remove(IdType id) {
        EntityType entity = load(id);
        if (entity != null) {
            getEntityManager().remove(entity);
        }
    }

    @Override
    public List<EntityType> findAll() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EntityType> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<EntityType> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root);
        TypedQuery<EntityType> query = getEntityManager().createQuery(criteriaQuery);
        return getResultList(query);
    }

    @Override
    public Long countAll() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(entityClass)));
        TypedQuery<Long> query = getEntityManager().createQuery(criteriaQuery);
        return getCount(query);
    }

    @Override
    public List<EntityType> findByFilter(Filter filter) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EntityType> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<EntityType> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root);
        Map<String, Object> parameters = new HashMap<>();
        Criteria.addWhere(criteriaBuilder, criteriaQuery, root, filter, parameters);
        TypedQuery<EntityType> query = getEntityManager().createQuery(criteriaQuery);
        addQueryParameters(query, parameters);
        return getResultList(query);
    }

    @Override
    public Long countByFilter(Filter filter) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<EntityType> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(criteriaBuilder.count(root));
        Map<String, Object> parameters = new HashMap<>();
        Criteria.addWhere(criteriaBuilder, criteriaQuery, root, filter, parameters);
        TypedQuery<Long> query = getEntityManager().createQuery(criteriaQuery);
        addQueryParameters(query, parameters);
        return getCount(query);
    }

    @Override
    public List<EntityType> findAllWithPagination(Integer offset, Integer limit) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EntityType> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<EntityType> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root);
        TypedQuery<EntityType> query = getEntityManager().createQuery(criteriaQuery);
        return getResultList(query, offset, limit);
    }

    @Override
    public Long countAllWithPagination(Integer offset, Integer limit) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(entityClass)));
        TypedQuery<Long> query = getEntityManager().createQuery(criteriaQuery);
        return getCount(query, offset, limit);
    }

    @Override
    public List<EntityType> findByFilterWithPagination(Filter filter, Integer offset, Integer limit) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EntityType> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<EntityType> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root);
        Map<String, Object> parameters = new HashMap<>();
        Criteria.addWhere(criteriaBuilder, criteriaQuery, root, filter, parameters);
        TypedQuery<EntityType> query = getEntityManager().createQuery(criteriaQuery);
        addQueryParameters(query, parameters);
        return getResultList(query, offset, limit);
    }

    @Override
    public Long countByFilterWithPagination(Filter filter, Integer offset, Integer limit) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<EntityType> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(criteriaBuilder.count(root));
        Map<String, Object> parameters = new HashMap<>();
        Criteria.addWhere(criteriaBuilder, criteriaQuery, root, filter, parameters);
        TypedQuery<Long> query = getEntityManager().createQuery(criteriaQuery);
        return getCount(query, offset, limit);
    }

    @Override
    public List<EntityType> findByFieldListWithPagination(List<Field> filterList, List<Field> sortList,
                                                          Integer offset, Integer limit) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EntityType> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<EntityType> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root);
        Map<String, Object> parameters = new HashMap<>();
        Criteria.addWhere(criteriaBuilder, criteriaQuery, root, filterList, parameters);
        Criteria.addOrderBy(criteriaBuilder, criteriaQuery, root, sortList);
        TypedQuery<EntityType> query = getEntityManager().createQuery(criteriaQuery);
        return getResultList(query, offset, limit);
    }

    @Override
    public Long countByFieldListWithPagination(List<Field> fieldList, Integer offset, Integer limit) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<EntityType> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(criteriaBuilder.count(root));
        Map<String, Object> parameters = new HashMap<>();
        Criteria.addWhere(criteriaBuilder, criteriaQuery, root, fieldList, parameters);
        TypedQuery<Long> query = getEntityManager().createQuery(criteriaQuery);
        return getCount(query, offset, limit);
    }

    private void addQueryParameters(Query query, Map<String, Object> parameters) {
        if (parameters != null && !parameters.isEmpty()) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                //query.setParameter(entry.getKey(), entry.getValue());
            }
        }
    }

    private List<EntityType> getResultList(Query query) {
        List<EntityType> entities = query.getResultList();
        return entities;
    }

    private List<EntityType> getResultList(Query query, Integer offset, Integer limit) {
        List<EntityType> entities = query.setFirstResult(offset)
                                         .setMaxResults(limit)
                                         .getResultList();
        return entities;
    }

    private Long getCount(TypedQuery<Long> query) {
        Long count = query.getSingleResult();
        return count;
    }

    private Long getCount(TypedQuery<Long> query, Integer offset, Integer limit) {
        Long count = query.setFirstResult(offset)
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