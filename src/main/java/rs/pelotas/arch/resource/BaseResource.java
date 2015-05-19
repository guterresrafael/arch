package rs.pelotas.arch.resource;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import rs.pelotas.arch.entity.BaseEntity;
import rs.pelotas.arch.helper.Reflection;

/**
 *
 * @author Rafael Guterres
 * @param <T>
 * @param <I>
 */
public abstract class BaseResource<T extends BaseEntity, I extends Serializable> implements Resource<T, I> {

    private static final long serialVersionUID = 877833877085359482L;

    private final Class<T> entityClass = Reflection.getGenericArgumentType(getClass());

    private static final Integer PARAM_OFFSET_DEFAULT_VALUE = 0;
    private static final Integer PARAM_LIMIT_DEFAULT_VALUE = 20;
    
    @Override
    public Integer getOffsetDefaultValue() {
        return PARAM_OFFSET_DEFAULT_VALUE;
    }

    @Override
    public Integer getLimitDefaultValue() {
        return PARAM_LIMIT_DEFAULT_VALUE;
    }

    @Override
    public List<T> getEntities(HttpServletRequest request) {
        try {
            List<T> entities;
            QueryString queryString = new QueryString(request);
            
            //Pagination
            Integer offset = (queryString.getOffset() != null) ? queryString.getOffset() : getOffsetDefaultValue();
            Integer limit = (queryString.getLimit() != null) ? queryString.getLimit() : getLimitDefaultValue();
            
            //Filters
            if (!queryString.getFilterList().isEmpty()) {
                entities = getService().findByFieldListWithPagination(queryString.getFilterList(),
                                                                      queryString.getSortList(),
                                                                      offset, limit);
            } else {
                entities = getService().findAllWithPagination(offset, limit);
            }
            
            //OrderBY
            if (queryString.getSortList().isEmpty()) {
                //entities = getService().findAllWithPagination(offset, limit);
            } else {
                //TODO: implementar suporte orderBy
                //entities = getService().findAllWithPagination(offset, limit);
            }
            
            //NotFound
            if (entities.isEmpty()) {
                throw new WebApplicationException(ResponseBuilder.notFound());
            }
            
            //Custom Fields
            if (!queryString.getFieldList().isEmpty()) {
                return getEntitiesFromQueryStringCustomFilters(entities, queryString);
            } else {
                return entities;
            }
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(ResponseBuilder.badRequest(e));
        }
    }

    @Override
    public Response postEntity(T entity) {
        try {
            getService().validate(entity);
            entity = getService().save(entity);
            return ResponseBuilder.ok(entity);
        } catch (ConstraintViolationException cve) {
            return ResponseBuilder.badRequest(cve);
        } catch (Exception e) {
            return ResponseBuilder.badRequest(e);
        }
    }

    @Override
    public T getEntityById(I id) {
        T entity = getService().load(id);
        if (entity == null) {
            throw new WebApplicationException(ResponseBuilder.notFound());
        }
        return entity;
    }
    
    @Override
    public Response putEntity(I id, T entity) {
        try {
            getService().validate(entity);
            entity = getService().save(entity);
            return ResponseBuilder.ok(entity);
        } catch (ConstraintViolationException cve) {
            return ResponseBuilder.badRequest(cve);
        } catch (Exception e) {
            return ResponseBuilder.conflict(e);
        }
    }

    @Override
    public Response deleteEntity(I id) {
        try {
            getService().delete(id);
        } catch (Exception e) {
            return ResponseBuilder.conflict(e);
        }
        return ResponseBuilder.deleted();
    }

    private List<T> getEntitiesFromQueryStringCustomFilters(List<T> entities, QueryString queryString) {
        try {
            List<Map<String, Object>> entitiesMap = getEntitiesMapList(entities, queryString);
            List<T> entitiesCustomFields = getEntitiesCustomFields(entitiesMap);
            return entitiesCustomFields;
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            return null;
        }
    }
    
    private List<Map<String, Object>> getEntitiesMapList(List<T> entities, QueryString queryString) throws IllegalArgumentException, IllegalAccessException {
        List<Map<String, Object>> entitiesMap = new ArrayList<>();
        for (T entity : entities) {
            Map<String, Object> entityMap = new HashMap<>();
            List<Field> entityFields = new ArrayList<>();
            Reflection.getAllFields(entityFields, entity.getClass());
            for (String fieldParam : queryString.getFieldList()) {
                for (Field entityField : entityFields) {
                    entityField.setAccessible(true);
                    if (entityField.getName().equals(fieldParam)) {
                        entityMap.put(fieldParam, entityField.get(entity));
                        break;
                    }
                }
            }
            entitiesMap.add(entityMap);
        }
        return entitiesMap;
    }
    
    private List<T> getEntitiesCustomFields(List<Map<String, Object>> entitiesMap) throws IllegalArgumentException, IllegalAccessException {
        List<T> entities = new ArrayList<>();
        for (Map<String, Object> entityMap : entitiesMap) {
            T entity = Reflection.instantiateClass(entityClass);
            List<Field> entityFields = new ArrayList<>();
            Reflection.getAllFields(entityFields, entity.getClass());
            for (Map.Entry<String, Object> entry : entityMap.entrySet()) {
                for (Field entityField : entityFields) {
                    entityField.setAccessible(true);
                    if (entityField.getName().equals(entry.getKey())) {
                        entityField.set(entity, entry.getValue());
                        break;
                    }
                }
            }
            entities.add(entity);
        }
        return entities;
    }
}