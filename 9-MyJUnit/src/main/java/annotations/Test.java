package annotations;

import exceptions.NoException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for methods that should be tested
 * ignored should be true if method should not be tested
 * reason contains a reason for method not be tested
 * exception is an expecting exception
 * test will be failed if another exception will be thrown
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    Class<? extends Throwable> exception() default NoException.class;

    boolean ignored() default false;

    String reason() default "N/A";
}
