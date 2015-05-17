package rs.pelotas.arch.entity;

import java.io.Serializable;

/**
 *
 * @author Rafael Guterres
 */
public abstract class BaseMetaEntity implements Serializable {
    
    public abstract String getKey();
    
    public abstract void setKey(String key);
    
    public abstract String getValue();
    
    public abstract void setValue(String value);
}
