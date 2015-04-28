package rs.pelotas.arch.annotation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import rs.pelotas.arch.filter.BaseFilter;
import rs.pelotas.arch.utils.Reflection;

/**
 *
 * @author Rafael Guterres
 */
public class CriteriaFilterImpl {
    
    public static void applyCriteriaFilterAnnotations(CriteriaBuilder criteriaBuilder,
                                                      CriteriaQuery<?> criteriaQuery,
                                                      Root<?> root,
                                                      BaseFilter filter) {
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
