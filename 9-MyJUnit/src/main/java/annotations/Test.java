package annotations;

import exceptions.NoException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotation for methods that should be tested */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    /** Expecting exception. Test will be failed if another exception will be thrown */
    Class<? extends Throwable> exception() default NoException.class;

    /** Should be true if method should not be tested. False by default */
    boolean ignored() default false;

    /** Reason for method not be tested. Makes sense only if test is ignored */
    String reason() default "N/A";
}
