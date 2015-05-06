package rs.pelotas.arch.resource;

import java.io.Serializable;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import rs.pelotas.arch.entity.BaseEntity;
import rs.pelotas.arch.service.BaseService;

/**
 *
 * @author Rafael Guterres
 * @param <EntityType>
 * @param <IdType>
 */
public interface BaseResource<EntityType extends BaseEntity, IdType extends Serializable> extends Serializable {
    
    BaseService<EntityType, IdType> getService();
    
    Integer getOffsetDefaultValue();
    
    Integer getLimitDefaultValue();
    
    EntityType getEntityById(IdType id);
    
    Collection<EntityType> getEntities(HttpServletRequest request);    
    
    Response postEntity(EntityType entity);

    Response putEntity(IdType id, EntityType entity);
    
    Response deleteEntity(IdType id);
}