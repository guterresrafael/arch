package rs.pelotas.arch.helper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import rs.pelotas.arch.annotation.CriteriaFilter;
import rs.pelotas.arch.filter.Filter;

/**
 *
 * @author Rafael Guterres
 */
public class Criteria {
    
    private static final String LIKE_PARAM_VALUE = "*";
    private static final String LIKE_CRITERIA_VALUE = "%";
    
    public static void addWhere(CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery,
                                Root root, Filter filter) {
        if (filter != null) {
            List<Predicate> predicates = new ArrayList<>();
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
                    Criteria.addPredicate(predicates, criteriaBuilder, root, fieldFilter);
                }
            }
            if (!predicates.isEmpty()) {
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));
            }
        }
    }

    public static void addWhere(CriteriaBuilder criteriaBuilder, CriteriaQuery<?> criteriaQuery,
                                Root<?> root, List<rs.pelotas.arch.helper.Field> filterList) {
        if (filterList != null && !filterList.isEmpty()) {
            List<Predicate> predicates = new ArrayList<>();
            for (rs.pelotas.arch.helper.Field field : filterList) {
                Criteria.addPredicate(predicates, criteriaBuilder, root, field);
            }
            if (!predicates.isEmpty()) {
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));
            }
        }
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