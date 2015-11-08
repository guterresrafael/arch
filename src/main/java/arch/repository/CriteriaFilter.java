package arch.repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import arch.enumeration.Method;

/**
 *
 * @author Rafael Guterres
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface CriteriaFilter {

    Method method() default Method.EQUAL;
}
