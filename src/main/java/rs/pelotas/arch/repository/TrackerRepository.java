package rs.pelotas.arch.repository;

import java.io.Serializable;
import rs.pelotas.arch.entity.BaseEntity;

/**
 *
 * @author Rafael Guterres
 * @param <EntityType>
 * @param <IdType>
 */
public interface TrackerRepository<EntityType extends BaseEntity, IdType extends Serializable> 
         extends BaseRepository<EntityType, IdType> {

}
