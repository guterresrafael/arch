package rs.pelotas.arch.helper;

import rs.pelotas.arch.enumeration.Method;
import rs.pelotas.arch.enumeration.OrderBy;

/**
 *
 * @author Rafael Guterres
 */
public class Field implements Comparable<Field> {
    
    private String name;
    private Object value;
    private Method method;
    private OrderBy orderBy;
    private Integer orderPriority;

    public Field() {
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

    @Override
    public int compareTo(Field obj) {
        return this.orderPriority.compareTo(obj.orderPriority);
    }
}