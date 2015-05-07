package rs.pelotas.arch.entity;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElementRef;
import org.jboss.resteasy.links.RESTServiceDiscovery;

/**
 *
 * @author Rafael Guterres
 * @param <IdType>
 */
public abstract class BaseEntity<IdType> implements Serializable {
    
    @XmlElementRef
    private RESTServiceDiscovery rest;

    public abstract IdType getId();

    public abstract void setId(IdType id);
}