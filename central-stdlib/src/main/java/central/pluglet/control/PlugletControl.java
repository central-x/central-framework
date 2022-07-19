package central.pluglet.control;

import central.data.NameValue;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Pluglet Param
 *
 * @author Alan Yeh
 * @since 2022/07/11
 */
@Data
@Builder
public class PlugletControl {
    /**
     * 控件标题
     */
    private String label;
    /**
     * 控件属性名
     * 前端提交时使用此值作为 key
     */
    private String name;
    /**
     * 控件类型
     */
    private String type;
    /**
     * 控件备注
     */
    private String comment;
    /**
     * 控件的默认值
     */
    private Object defaultValue;
    /**
     * 控件是否必填
     */
    private Boolean required;
    /**
     * 控件选项列表
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<NameValue<String>> options = new ArrayList<>();
}
