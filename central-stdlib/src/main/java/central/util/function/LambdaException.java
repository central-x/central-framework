package central.util.function;

import java.io.Serial;

/**
 * Lambda Exception
 * 调用异常
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
public class LambdaException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 6123028632326023554L;

    public LambdaException(String message){
        super(message);
    }

    public LambdaException(String message, Throwable cause){
        super(message, cause);
    }
}
