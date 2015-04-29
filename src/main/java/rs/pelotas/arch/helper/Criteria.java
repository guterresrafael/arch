package rs.pelotas.arch.helper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import rs.pelotas.arch.annotation.CriteriaFilter;
import rs.pelotas.arch.filter.BaseFilter;

/**
 *
 * @author Rafael Guterres
 */
public class Criteria {
    
    private static final String LIKE_PARAM_VALUE = "*";
    private static final String LIKE_CRITERIA_VALUE = "%";
    
    public static void addWhere(CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery,
                                Root root, BaseFilter filter) {
        if (filter != null) {
            List<Field> fields = new ArrayList<>();
            Reflection.getAllFields(fields, filter.getClass());
            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldValue = null;
                try {
                    fieldValue = field.get(filter);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                }
                if (fieldValue != null && field.isAnnotationPresent(CriteriaFilter.class)) {
                    CriteriaFilter criteriaFilter = field.getAnnotation(CriteriaFilter.class);
                    rs.pelotas.arch.helper.Field fieldFilter = new rs.pelotas.arch.helper.Field();
                    fieldFilter.setName(field.getName());
                    fieldFilter.setValue(fieldValue);
                    fieldFilter.setClazz(filter.getClass());
                    fieldFilter.setMethod(criteriaFilter.method());
                    Criteria.addWhere(criteriaBuilder, criteriaQuery, root, fieldFilter);
                }
            }
        }
    }

    public static void addWhere(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> criteriaQuery,
                                Root<?> root, List<rs.pelotas.arch.helper.Field> filterList) {
        if (filterList != null && !filterList.isEmpty()) {
            for (rs.pelotas.arch.helper.Field field : filterList) {
                Criteria.addWhere(criteriaBuilder, criteriaQuery, root, field);
            }
        }
    }
    
    private static void addWhere(CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery,
                                 Root root, rs.pelotas.arch.helper.Field field) {
        addWhereWithValueBased(criteriaBuilder, criteriaQuery, root, field);
        addWhereWithoutValueBased(criteriaBuilder, criteriaQuery, root, field);
        addWhereWithComparableClassBased(criteriaBuilder, criteriaQuery, root, field);
    }

    private static void addWhereWithValueBased(CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery,
                                               Root root, rs.pelotas.arch.helper.Field field) {
        switch (field.getMethod()) {
            case EQUAL:
                criteriaQuery.where(criteriaBuilder.equal(root.get(field.getName()), field.getValue()));
                break;
            case NOT_EQUAL:
                criteriaQuery.where(criteriaBuilder.notEqual(root.get(field.getName()), field.getValue()));
                break;
            case LIKE:
                criteriaQuery.where(criteriaBuilder.like(root.get(field.getName()), addLikeChar(field.getValue())));
                break;
            case NOT_LIKE:
                criteriaQuery.where(criteriaBuilder.notLike(root.get(field.getName()), addLikeChar(field.getValue())));
                break;
        }
    }    
    
    private static void addWhereWithoutValueBased(CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery,
                                                  Root root, rs.pelotas.arch.helper.Field field) {
        switch (field.getMethod()) {
                case IS_NULL:
                criteriaQuery.where(criteriaBuilder.isNull(root.get(field.getName())));
                break;
            case IS_NOT_NULL:
                criteriaQuery.where(criteriaBuilder.isNotNull(root.get(field.getName())));
                break;
            case IS_FALSE:
                criteriaQuery.where(criteriaBuilder.isFalse(root.get(field.getName())));
                break;
            case IS_TRUE:
                criteriaQuery.where(criteriaBuilder.isTrue(root.get(field.getName())));
                break;
            case IS_EMPTY:
                criteriaQuery.where(criteriaBuilder.isEmpty(root.get(field.getName())));
                break;
        }
    }
    
    private static void addWhereWithComparableClassBased(CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery,
                                                         Root root, rs.pelotas.arch.helper.Field field) {
        //TODO: Refactor para utilizar "field.getClazz.cast(field.getValue)"
        boolean isDate = field.getClazz().getName().equals(Date.class.getName());
        switch (field.getMethod()) {
            case GREATER:
                if (isDate) {
                    criteriaQuery.where(criteriaBuilder.greaterThan(root.get(field.getName()), (Date) field.getValue()));
                } else {
                    criteriaQuery.where(criteriaBuilder.greaterThan(root.get(field.getName()), (Long) field.getValue()));
                }
                break;
            case GREATER_OR_EQUAL:
                if (isDate) {
                    criteriaQuery.where(criteriaBuilder.greaterThanOrEqualTo(root.get(field.getName()), (Date) field.getValue()));
                } else {
                    criteriaQuery.where(criteriaBuilder.greaterThanOrEqualTo(root.get(field.getName()), (Long) field.getValue()));
                }                
                break;
            case LESS:
                if (isDate) {
                    criteriaQuery.where(criteriaBuilder.lessThan(root.get(field.getName()), (Date) field.getValue()));
                } else {
                    criteriaQuery.where(criteriaBuilder.lessThan(root.get(field.getName()), (Long) field.getValue()));
                }
                break;
            case LESS_OR_EQUAL:
                if (isDate) {
                    criteriaQuery.where(criteriaBuilder.lessThanOrEqualTo(root.get(field.getName()), (Date) field.getValue()));
                } else {
                    criteriaQuery.where(criteriaBuilder.lessThanOrEqualTo(root.get(field.getName()), (Long) field.getValue()));
                }
                break;
        }
    }

    public static void addOrderBy (CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery,
                                     Root root, List<rs.pelotas.arch.helper.Field> sortList) {
        //TODO: implementar suporte a ordenacao
    }
    
    private static String addLikeChar(Object value) {
        return value.toString().replace(LIKE_PARAM_VALUE, LIKE_CRITERIA_VALUE);
    }
}