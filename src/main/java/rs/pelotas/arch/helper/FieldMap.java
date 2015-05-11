package rs.pelotas.arch.helper;

import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rguterres
 * @param <KeyType>
 * @param <ValueType>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FieldMap<KeyType, ValueType> extends HashMap<KeyType, ValueType> {

    private static final long serialVersionUID = 477417262391130123L;
}