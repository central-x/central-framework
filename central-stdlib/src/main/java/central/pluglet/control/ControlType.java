package central.pluglet.control;

/**
 * 控件类型
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
public enum ControlType {
    /**
     * 文本控件
     */
    LABEl,
    /**
     * 文本输入框
     */
    TEXT,
    /**
     * 密码输入框
     */
    PASSWORD,
    /**
     * 下拉列表
     * 属性类型必须使用 {@code ? extend Enum} 且继承于 OptionalEnum
     */
    RADIO,
    /**
     * 下拉列表
     * 属性类型必须使用 {@code List<? extend Enum>} 且继承于 OptionalEnum
     */
    CHECKBOX,
    /**
     * 整数类型
     */
    NUMBER,
    /**
     * 时间类型（年月日）
     */
    DATE,
    /**
     * 时间类型（年月日时分秒）
     */
    DATETIME,
    /**
     * 时间类型（时分秒）
     */
    TIME
}
