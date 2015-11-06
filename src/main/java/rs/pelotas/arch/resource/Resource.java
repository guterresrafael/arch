package rs.pelotas.arch.resource;

import java.io.Serializable;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import rs.pelotas.arch.entity.BaseEntity;
import rs.pelotas.arch.service.Service;

/**
 *
 * @author Rafael Guterres
 * @param <T>
 * @param <I>
 */
public interface Resource<T extends BaseEntity, I extends Serializable> extends Serializable {

    Service<T, I> getService();

    Integer getOffsetDefaultValue();

    Integer getLimitDefaultValue();

    List<T> getEntities(HttpServletRequest request);

    Response postEntity(T entity);

    T getEntityById(I id);

    Response putEntity(I id, T entity);

    Response deleteEntity(I id);
}
