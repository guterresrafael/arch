package rs.pelotas.arch.entity;

import java.io.Serializable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import org.jboss.resteasy.links.RESTServiceDiscovery;

/**
 *
 * @author Rafael Guterres
 * @param <IdType>
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class BaseEntity<IdType> implements Serializable {

    private static final long serialVersionUID = -4629614059785944341L;

    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    @Transient
    RESTServiceDiscovery rest;

    public abstract IdType getId();

    public abstract void setId(IdType id);
}