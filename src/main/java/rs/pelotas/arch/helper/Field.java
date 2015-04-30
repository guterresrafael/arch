package rs.pelotas.arch.helper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import rs.pelotas.arch.enumeration.Method;
import rs.pelotas.arch.enumeration.OrderBy;

/**
 *
 * @author Rafael Guterres
 */
public class Field {

    private static final String DATE_SEPARATOR_CHARACTER = "-";
    
    private String name;
    private Object value;
    private Class<?> clazz;
    private Method method;
    private OrderBy orderBy;
    private Field field;

    public Field() {
    }

    public Field(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public Field(String name, Object value, Method method) {
        this.name = name;
        this.value = value;
        this.method = method;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Comparable getValueComparable() {
        if (this.clazz != null && this.value instanceof Comparable) {
            return (Comparable) this.value;
        }
        return getValueComparableFromParse();
    }
    
    private Comparable getValueComparableFromParse() {
        try {
            String[] dateArray = ((String) this.value).split(DATE_SEPARATOR_CHARACTER);
            LocalDate localDate = LocalDate.of(Integer.parseInt(dateArray[0]),
                                               Integer.parseInt(dateArray[1]),
                                               Integer.parseInt(dateArray[2]));
            return (Comparable) Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
        }
        try {
            return (Comparable) Long.parseLong((String) this.value);
        } catch (Exception e) {
        }
        return (Comparable) this.value;
    }
}