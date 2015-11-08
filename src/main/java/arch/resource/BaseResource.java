package arch.resource;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import arch.entity.BaseEntity;
import arch.helper.Reflection;
import arch.security.UserPrincipal;
import arch.security.role.AdminRole;

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

    @Context
    private SecurityContext securityContext;

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

            Integer offset = null;
            if (queryString.getOffset() != null) {
                queryString.getOffset();
            } else {
                getOffsetDefaultValue();
            }

            Integer limit = null;
            if (queryString.getLimit() != null) {
                queryString.getLimit();
            } else {
                getLimitDefaultValue();
            }

            if (!queryString.getFilterList().isEmpty()) {
                entities = getService().find(queryString.getFilterList(),
                                             queryString.getSortList(),
                                             offset, limit);
                if (entities.isEmpty()) {
                    throw new WebApplicationException(ResponseBuilder.notFound());
                }
                return getEntitiesFromQueryStringCustomFilters(entities, queryString);
            } else {
                entities = getService().find(offset, limit);
                if (entities.isEmpty()) {
                    throw new WebApplicationException(ResponseBuilder.notFound());
                }
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
            return ResponseBuilder.ok(getService().save(entity));
        } catch (ConstraintViolationException cve) {
            return ResponseBuilder.badRequest(cve);
        } catch (Exception e) {
            return ResponseBuilder.badRequest(e);
        }
    }

    @Override
    public T getEntityById(I id) {
        validateSecurityContext(AdminRole.READ, id);
        T entity = getService().load(id);
        if (entity == null) {
            throw new WebApplicationException(ResponseBuilder.notFound());
        }
        return entity;
    }

    @Override
    public Response putEntity(I id, T entity) {
        validateSecurityContext(AdminRole.UPDATE, id);
        try {
            getService().validate(entity);
            return ResponseBuilder.ok(getService().save(entity));
        } catch (ConstraintViolationException cve) {
            return ResponseBuilder.badRequest(cve);
        } catch (Exception e) {
            return ResponseBuilder.conflict(e);
        }
    }

    @Override
    public Response deleteEntity(I id) {
        validateSecurityContext(AdminRole.DELETE, id);
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
            if (!entitiesCustomFields.isEmpty()) {
                return entitiesCustomFields;
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            Logger.getAnonymousLogger().warning(e.getMessage());
        }
        return null;
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

    private void validateSecurityContext(String role, I id) {
        UserPrincipal userPrincipal = (UserPrincipal) securityContext.getUserPrincipal();
        if (!securityContext.isUserInRole(role) && !userPrincipal.getId().equals(id)) {
            throw new WebApplicationException(ResponseBuilder.forbidden());
        }
    }
}
