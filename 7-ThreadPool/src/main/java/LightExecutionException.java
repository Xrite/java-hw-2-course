/** This exception indicates that there was exception during delayed execution */
public class LightExecutionException extends Exception {
    /** Makes the exception with the given cause */
    LightExecutionException(Throwable cause) {
        super(cause);
    }
}
