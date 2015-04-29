package rs.pelotas.arch.helper;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    
    public static void applyFilterAnnotations(CriteriaBuilder criteriaBuilder,
                                              CriteriaQuery criteriaQuery,
                                              Root root,
                                              BaseFilter filter) {
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
                    applyMethod(criteriaBuilder, criteriaQuery, root, fieldFilter);
                }
            }
        }
    }
    
    public static void applyMapList(CriteriaBuilder criteriaBuilder,
                                    CriteriaQuery<?> criteriaQuery,
                                    Root<?> root,
                                    List<Map<String, String>> mapList) {
        if (mapList != null && !mapList.isEmpty()) {
            for (Map<String, String> filter : mapList) {
                for (Map.Entry<String, String> entry : filter.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    criteriaQuery.where(criteriaBuilder.equal(root.get(key), value));
                }
            }
        }
    }

    public static void applyFieldList(CriteriaBuilder criteriaBuilder,
                                      CriteriaQuery<?> criteriaQuery,
                                      Root<?> root,
                                      List<rs.pelotas.arch.helper.Field> fieldList) {
        if (fieldList != null && !fieldList.isEmpty()) {
            for (rs.pelotas.arch.helper.Field field : fieldList) {
                applyMethod(criteriaBuilder, criteriaQuery, root, field);
            }
        }
    }
    
    private static void applyMethod(CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery,
                                    Root root, rs.pelotas.arch.helper.Field field) {
        applyMethodWithValueBased(criteriaBuilder, criteriaQuery, root, field);
        applyMethodWithoutValueBased(criteriaBuilder, criteriaQuery, root, field);
        applyMethodWithComparableClassBased(criteriaBuilder, criteriaQuery, root, field);
    }

    private static void applyMethodWithValueBased(CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery,
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
    
    private static void applyMethodWithoutValueBased(CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery,
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
    
    private static void applyMethodWithComparableClassBased(CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery,
                                                            Root root, rs.pelotas.arch.helper.Field field) {
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

    private static void applyOrderBy () {
        //TODO: implementar suporte a ordenacao
    }
    
    private static String addLikeChar(Object value) {
        return value.toString().replace(LIKE_PARAM_VALUE, LIKE_CRITERIA_VALUE);
    }
}