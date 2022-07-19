package central.bean;

import javax.annotation.Nonnull;
import java.io.Serial;
import java.util.Objects;

/**
 * Bean Initialize Exception
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class InitializeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2459396213190128835L;

    public InitializeException(@Nonnull Class<?> beanType, String message){
        super("Cannot initialize " + Objects.requireNonNull(beanType).getSimpleName() + ": " + message);
    }

    public InitializeException(@Nonnull Class<?> beanType, String message, Throwable cause) {
        super("Cannot initialize " + Objects.requireNonNull(beanType).getSimpleName() + ": " + message, cause);
    }

    public InitializeException(@Nonnull Class<?> beanType, Throwable cause){
        super("Cannot initialize " + Objects.requireNonNull(beanType).getSimpleName() + ": " + cause.getLocalizedMessage(), cause);
    }

    public InitializeException(String message) {
        super(message);
    }

    public InitializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
