package central.lang.reflect;

import lombok.Getter;

import java.lang.reflect.Method;

/**
 * 方法引用
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
public abstract class MethodReference {
    @Getter
    private final Method method;

    private MethodReference(Method method){
        this.method = method;
    }

    public MethodReference(){
        this.method = this.getClass().getEnclosingMethod();
    }

    public static MethodReference of(Method method){
        return new MethodReference(method){
        };
    }

    public TypeReference<?> getReturnType(){
        return TypeReference.of(this.method.getGenericReturnType());
    }
}
