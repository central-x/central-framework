package central.pluglet.annotation;

import central.pluglet.control.ControlType;

import java.lang.annotation.*;

/**
 * 控件
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Control {
    /**
     * 控件标签
     */
    String label();
    /**
     * 控件字段名
     * 如果为空的话，则取属性名
     */
    String name() default "";
    /**
     * 控件类型
     * 默认为文本输入框
     */
    ControlType type() default ControlType.TEXT;
    /**
     * 控件备注
     */
    String comment() default "";
    /**
     * 默认值
     */
    String[] defaultValue() default {};
    /**
     * 是否必须填写
     */
    boolean required() default true;
}
