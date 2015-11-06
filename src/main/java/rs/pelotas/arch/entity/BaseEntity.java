package rs.pelotas.arch.entity;

import java.io.Serializable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import org.jboss.resteasy.links.RESTServiceDiscovery;

/**
 *
 * @author Rafael Guterres
 * @param <I>
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class BaseEntity<I> implements Serializable {

    private static final long serialVersionUID = -4629614059785944341L;

    @XmlElement(name = "action")
    @Transient
    private RESTServiceDiscovery rest;

    public abstract I getId();

    public abstract void setId(I id);
}
