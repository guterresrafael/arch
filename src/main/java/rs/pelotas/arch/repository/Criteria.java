package rs.pelotas.arch.repository;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import rs.pelotas.arch.enumeration.Method;
import rs.pelotas.arch.helper.Reflection;

/**
 *
 * @author Rafael Guterres
 */
public class Criteria implements Serializable {

    private static final long serialVersionUID = -4367708806996213062L;

    private static final String LIKE_PARAM_VALUE = "*";
    private static final String LIKE_CRITERIA_VALUE = "%";
    
    public static void addWhere(CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery,
                                Root root, Filter filter, Map<String, Object> parameters) {
        List<Predicate> predicates = new ArrayList<>();
        List<Field> fields = new ArrayList<>();
        Reflection.getAllFields(fields, filter.getClass());
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(filter);
                rs.pelotas.arch.helper.Field fieldFilter = new rs.pelotas.arch.helper.Field();
                fieldFilter.setName(field.getName());
                fieldFilter.setValue(fieldValue);
                fieldFilter.setClazz(filter.getClass());
                if (field.isAnnotationPresent(CriteriaFilter.class)) {
                    CriteriaFilter criteriaFilter = field.getAnnotation(CriteriaFilter.class);
                    fieldFilter.setMethod(criteriaFilter.method());
                } else {
                    fieldFilter.setMethod(Method.EQUAL);
                }
                parameters.put(fieldFilter.getName(), fieldFilter.getValue());
                Criteria.addPredicate(predicates, criteriaBuilder, root, fieldFilter);
            } catch (IllegalArgumentException | IllegalAccessException | NullPointerException e) {
            }
        }
        addPredicates(criteriaBuilder, criteriaQuery, predicates);
    }

    public static void addWhere(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> criteriaQuery,
                                Root<?> root, List<rs.pelotas.arch.helper.Field> filterList,
                                Map<String, Object> parameters) {
        List<Predicate> predicates = new ArrayList<>();
        for (rs.pelotas.arch.helper.Field field : filterList) {
            parameters.put(field.getName(), field.getValue());
            Criteria.addPredicate(predicates, criteriaBuilder, root, field);
        }
        addPredicates(criteriaBuilder, criteriaQuery, predicates);
    }
    
    private static void addPredicate(List<Predicate> predicates, CriteriaBuilder criteriaBuilder,
                                     Root root, rs.pelotas.arch.helper.Field field) {
        addPredicateWithValueBased(predicates, criteriaBuilder, root, field);
        addPredicateWithoutValueBased(predicates, criteriaBuilder, root, field);
        addPredicateWithComparableBased(predicates, criteriaBuilder, root, field);
    }

    private static void addPredicateWithValueBased(List<Predicate> predicates, CriteriaBuilder criteriaBuilder,
                                                   Root root, rs.pelotas.arch.helper.Field field) {
        switch (field.getMethod()) {
            case EQUAL:
                predicates.add(criteriaBuilder.equal(root.get(field.getName()), field.getValue()));
                break;
            case NOT_EQUAL:
                predicates.add(criteriaBuilder.notEqual(root.get(field.getName()), field.getValue()));
                break;
            case LIKE:
                predicates.add(criteriaBuilder.like(root.get(field.getName()), addLikeChar(field.getValue())));
                break;
            case NOT_LIKE:
                predicates.add(criteriaBuilder.notLike(root.get(field.getName()), addLikeChar(field.getValue())));
                break;
        }
    }
    
    private static void addPredicateWithoutValueBased(List<Predicate> predicates, CriteriaBuilder criteriaBuilder,
                                                      Root root, rs.pelotas.arch.helper.Field field) {
        switch (field.getMethod()) {
            case IS_NULL:
                predicates.add(criteriaBuilder.isNull(root.get(field.getName())));
                break;
            case IS_NOT_NULL:
                predicates.add(criteriaBuilder.isNotNull(root.get(field.getName())));
                break;
            case IS_FALSE:
                predicates.add(criteriaBuilder.isFalse(root.get(field.getName())));
                break;
            case IS_TRUE:
                predicates.add(criteriaBuilder.isTrue(root.get(field.getName())));
                break;
            case IS_EMPTY:
                predicates.add(criteriaBuilder.isEmpty(root.get(field.getName())));
                break;
        }
    }
    
    private static void addPredicateWithComparableBased(List<Predicate> predicates, CriteriaBuilder criteriaBuilder,
                                                             Root root, rs.pelotas.arch.helper.Field field) {
        switch (field.getMethod()) {
            case GREATER:
                predicates.add(criteriaBuilder.greaterThan(root.get(field.getName()), field.getValueComparable()));
                break;
            case GREATER_OR_EQUAL:
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(field.getName()), field.getValueComparable()));
                break;
            case LESS:
                predicates.add(criteriaBuilder.lessThan(root.get(field.getName()), field.getValueComparable()));
                break;
            case LESS_OR_EQUAL:
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(field.getName()), field.getValueComparable()));
                break;
            case BETWEEN:
                predicates.add(criteriaBuilder.between(root.get(field.getName()), field.getValueComparable(), field.getField().getValueComparable()));
                break;
        }
    }

    public static void addOrderBy (CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery,
                                   Root root, List<rs.pelotas.arch.helper.Field> sortList) {
        //TODO: implementar suporte a ordenacao
    }
    
    private static void addPredicates(CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery, List<Predicate> predicates) {
        if (!predicates.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));
        }
    }
    
    private static String addLikeChar(Object value) {
        return value.toString().replace(LIKE_PARAM_VALUE, LIKE_CRITERIA_VALUE);
    }
}