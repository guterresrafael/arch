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
public class Field implements Comparable<Field> {

    private static final String DATE_SEPARATOR_CHARACTER = "-";
    
    private String name;
    private Object value;
    private Class<?> clazz;
    private Method method;
    private OrderBy orderBy;
    private Integer orderPriority;

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
        if (this.clazz == null) {
            this.defineFieldClassFromFieldValue();
        }
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

    public Integer getOrderPriority() {
        return orderPriority;
    }

    public void setOrderPriority(Integer orderPriority) {
        this.orderPriority = orderPriority;
    }

    private void defineFieldClassFromFieldValue() {
        try {
            String valueString = value.toString();
            String[] dateArray = valueString.split(DATE_SEPARATOR_CHARACTER);
            LocalDate localDate = LocalDate.of(Integer.parseInt(dateArray[0]),
                                               Integer.parseInt(dateArray[1]),
                                               Integer.parseInt(dateArray[2]));
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            this.setClazz(Date.class);
            this.setValue(date);
            return;
        } catch (Exception e) {
        }
        try {
            Long longValue = Long.parseLong(value.toString());
            this.setClazz(Long.class);
            this.setValue(longValue);
            return;
        } catch (Exception e) {
        }
        this.setClazz(String.class);
    }
    
    @Override
    public int compareTo(Field obj) {
        return this.orderPriority.compareTo(obj.orderPriority);
    }
}