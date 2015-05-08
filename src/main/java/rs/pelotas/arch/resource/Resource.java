package rs.pelotas.arch.resource;

import java.io.Serializable;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import rs.pelotas.arch.entity.BaseEntity;
import rs.pelotas.arch.service.Service;

/**
 *
 * @author Rafael Guterres
 * @param <EntityType>
 * @param <IdType>
 */
public interface Resource<EntityType extends BaseEntity, IdType extends Serializable> extends Serializable {
    
    Service<EntityType, IdType> getService();
    
    Integer getOffsetDefaultValue();
    
    Integer getLimitDefaultValue();
    
    Collection<EntityType> getEntities(HttpServletRequest request);    
    
    Response postEntity(EntityType entity);
    
    EntityType getEntityById(IdType id);

    Response putEntity(IdType id, EntityType entity);
    
    Response deleteEntity(IdType id);
}