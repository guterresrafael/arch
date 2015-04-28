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
    
    public static void applyFilterAnnotations(CriteriaBuilder criteriaBuilder,
                                              CriteriaQuery<?> criteriaQuery,
                                              Root<?> root,
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
                    switch (criteriaFilter.method()) {
                        case EQUAL:
                            criteriaQuery.where(criteriaBuilder.equal(root.get(field.getName()), fieldValue));
                            break;
                        case BETWEEN:
                            //TODO: Implementar consulta
                            break;
                    }
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
}
