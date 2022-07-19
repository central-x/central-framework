package central.pluglet;

import central.lang.reflect.FieldReference;
import central.lang.reflect.InstanceReference;

import java.util.Map;

/**
 * Pluglet Field Binder
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
public interface FieldBinder {
    /**
     * 判断是否支持绑定字段
     *
     * @param field 字段
     */
    boolean support(FieldReference field);

    /**
     * 绑定字段
     *
     * @param target 待绑定对象
     * @param field  待绑定对象字段
     * @param params 入参
     */
    void bind(InstanceReference<?> target, FieldReference field, Map<String, Object> params);
}
