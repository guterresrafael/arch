package rs.pelotas.arch.helper;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
                    //TODO: Logger
                }
                if (fieldValue != null && field.isAnnotationPresent(CriteriaFilter.class)) {
                    CriteriaFilter criteriaFilter = field.getAnnotation(CriteriaFilter.class);
                    rs.pelotas.arch.helper.Field fieldFilter = new rs.pelotas.arch.helper.Field(field.getName(), fieldValue, criteriaFilter.method());
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
        switch (field.getMethod()) {
            case EQUAL:
                criteriaQuery.where(criteriaBuilder.equal(root.get(field.getName()), field.getValue()));
                break;
            case NOT_EQUAL:
                criteriaQuery.where(criteriaBuilder.notEqual(root.get(field.getName()), field.getValue()));
                break;
            case LIKE:
                criteriaQuery.where(criteriaBuilder.like(root.get(field.getName()), getLike(field.getValue())));
                break;
            case NOT_LIKE:
                criteriaQuery.where(criteriaBuilder.notLike(root.get(field.getName()), getLike(field.getValue())));
                break;
            case IS_NULL:
                criteriaQuery.where(criteriaBuilder.isNull(root.get(field.getName())));
                break;
            case IS_NOT_NULL:
                criteriaQuery.where(criteriaBuilder.isNotNull(root.get(field.getName())));
                break;
            case IS_TRUE:
                criteriaQuery.where(criteriaBuilder.isTrue(root.get(field.getName())));
                break;
            case IS_FALSE:
                criteriaQuery.where(criteriaBuilder.isFalse(root.get(field.getName())));
                break;
            case IS_EMPTY:
                criteriaQuery.where(criteriaBuilder.isEmpty(root.get(field.getName())));
                break;
            case BETWEEN:
                //TODO: Implementar comparacao.
                break;
        }
    }
    
    private static void applyOrderBy () {
        //TODO: implementar suporte a ordenacao
    }
    
    private static String getLike(Object value) {
        return value.toString().replace(LIKE_PARAM_VALUE, LIKE_CRITERIA_VALUE);
    }
}