/*
 * MIT License
 *
 * Copyright (c) 2022-present Alan Yeh <alan@yeh.cn>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
