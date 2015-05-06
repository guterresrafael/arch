package rs.pelotas.arch.resource;

import rs.pelotas.arch.helper.QueryString;
import rs.pelotas.arch.helper.ResponseBuilder;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
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
 * @param <EntityType>
 * @param <IdType>
 */
public abstract class BaseResourceImpl<EntityType extends BaseEntity, IdType extends Serializable>
           implements BaseResource<EntityType, IdType> {

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
    public EntityType getEntityById(IdType id) {
        EntityType entity = getService().load(id);
        if (entity == null) {
            throw new WebApplicationException(ResponseBuilder.notFound());
        }
        return entity;
    }

    @Override
    public Collection<EntityType> getEntities(HttpServletRequest request) {
        try {
            List entities;
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
                entities = createEntitiesMapListByQueryParams(entities, queryString);
                return entities;
            } else {
                return entities;
            }
        } catch (Exception e) {
            throw new WebApplicationException(ResponseBuilder.badRequest(e));
        }
    }

    @Override
    public Response postEntity(EntityType entity) {
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
    public Response putEntity(IdType id, EntityType entity) {
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
    public Response deleteEntity(IdType id) {
        try {
            getService().delete(id);
        } catch (Exception e) {
            return ResponseBuilder.conflict(e);
        }
        return ResponseBuilder.deleted();
    }

    private List<Map<String, Object>> createEntitiesMapListByQueryParams(List<EntityType> entities, QueryString queryString) throws IllegalArgumentException, IllegalAccessException {
        List<Map<String, Object>> entitiesMap = new ArrayList<>();
        for (EntityType entity : entities) {
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
}