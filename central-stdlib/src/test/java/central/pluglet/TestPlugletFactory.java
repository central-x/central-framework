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

package central.pluglet;

import central.bean.LifeCycle;
import central.data.OptionalEnum;
import central.pluglet.annotation.Control;
import central.pluglet.control.ControlType;
import central.util.Listx;
import central.util.Setx;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * PlutletFactory Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/12
 */
public class TestPlugletFactory {

    private PlugletFactory factory;

    @BeforeEach
    public void before() {
        this.factory = new PlugletFactory();
    }

    @Test
    public void case1() {
        var controls = factory.getControls(Plugin.class);
        Assertions.assertEquals(7, controls.size());
        // Label 控件
        Assertions.assertEquals(ControlType.LABEl.name(), controls.get(0).getType());
        Assertions.assertEquals("说明", controls.get(0).getLabel());

        // 数字控件
        Assertions.assertEquals(ControlType.NUMBER.name(), controls.get(1).getType());
        Assertions.assertEquals("数字", controls.get(1).getLabel());
        Assertions.assertEquals("int", controls.get(1).getName());
        Assertions.assertEquals(1000, controls.get(1).getDefaultValue());
        Assertions.assertEquals("这是一个数字控件", controls.get(1).getComment());
        Assertions.assertEquals(true, controls.get(1).getRequired());

        // 文本控件
        Assertions.assertEquals(ControlType.TEXT.name(), controls.get(2).getType());
        Assertions.assertEquals("文本", controls.get(2).getLabel());
        Assertions.assertEquals("text", controls.get(2).getName());
        Assertions.assertEquals("test", controls.get(2).getDefaultValue());
        Assertions.assertEquals("这是一个文本控件", controls.get(2).getComment());
        Assertions.assertEquals(false, controls.get(2).getRequired());

        // 日历控件
        Assertions.assertEquals(ControlType.DATETIME.name(), controls.get(3).getType());
        Assertions.assertEquals("日历", controls.get(3).getLabel());
        Assertions.assertEquals("timestamp", controls.get(3).getName());
        Assertions.assertNull(controls.get(3).getDefaultValue());
        Assertions.assertEquals("这是日历控件", controls.get(3).getComment());
        Assertions.assertEquals(true, controls.get(3).getRequired());

        // 单选框
        Assertions.assertEquals(ControlType.RADIO.name(), controls.get(4).getType());
        Assertions.assertEquals("单选框", controls.get(4).getLabel());
        Assertions.assertEquals("radio", controls.get(4).getName());
        Assertions.assertEquals("false", controls.get(4).getDefaultValue());
        Assertions.assertEquals("这是一个单选框控件", controls.get(4).getComment());
        Assertions.assertEquals(true, controls.get(4).getRequired());
        Assertions.assertEquals(2, controls.get(4).getOptions().size());

        // 多选框
        Assertions.assertEquals(ControlType.CHECKBOX.name(), controls.get(5).getType());
        Assertions.assertEquals("多选框", controls.get(5).getLabel());
        Assertions.assertEquals("checkbox", controls.get(5).getName());
        Assertions.assertEquals(List.of("dog", "cat"), controls.get(5).getDefaultValue());
        Assertions.assertEquals("这是一个多选框控件", controls.get(5).getComment());
        Assertions.assertEquals(true, controls.get(5).getRequired());
        Assertions.assertEquals(4, controls.get(5).getOptions().size());

        // 密码框
        Assertions.assertEquals(ControlType.PASSWORD.name(), controls.get(6).getType());
        Assertions.assertEquals("密码框", controls.get(6).getLabel());
        Assertions.assertEquals("password", controls.get(6).getName());
        Assertions.assertNull(controls.get(6).getDefaultValue());
        Assertions.assertEquals("这是一个密码框", controls.get(6).getComment());
        Assertions.assertEquals(true, controls.get(6).getRequired());
        Assertions.assertTrue(Listx.isNullOrEmpty(controls.get(6).getOptions()));
    }

    @Test
    public void case2(){
        var timestamp = System.currentTimeMillis();
        var params = new HashMap<String, Object>();
        params.put("int", "200");
        params.put("timestamp", timestamp);
        params.put("radio", "true");
        params.put("checkbox", List.of("fish", "bird"));
        params.put("password", "test");

        var plugin = factory.create(Plugin.class, params);

        Assertions.assertEquals(Integer.valueOf(200), plugin.getInteger());
        Assertions.assertEquals("test", plugin.getText());
        Assertions.assertEquals(new Timestamp(timestamp), plugin.getTimestamp());
        Assertions.assertEquals(BooleanEnum.TRUE, plugin.getRadio());
        Assertions.assertTrue(Setx.equals(Setx.newHashSet(AnimalEnum.BIRD, AnimalEnum.FISH), new HashSet<>(plugin.getCheckbox())));
        Assertions.assertEquals("test", plugin.getPassword());
    }

    public static class Plugin implements LifeCycle {
        @Control(label = "说明", type = ControlType.LABEl,
                defaultValue = """
                        　　本插件用于测试插件的运作情况
                        """)
        private String label;

        @Getter
        @Control(label = "数字", type = ControlType.NUMBER, defaultValue = "1000", comment = "这是一个数字控件", name = "int")
        private Integer integer;

        @Getter
        @Control(label = "文本", type = ControlType.TEXT, defaultValue = "test", comment = "这是一个文本控件", required = false)
        private String text;

        @Getter
        @Control(label = "日历", type = ControlType.DATETIME, comment = "这是日历控件")
        private Timestamp timestamp;

        @Getter
        @Control(label = "单选框", type = ControlType.RADIO, defaultValue = "false", comment = "这是一个单选框控件")
        private BooleanEnum radio;

        @Getter
        @Control(label = "多选框", type = ControlType.CHECKBOX, defaultValue = {"dog", "cat"}, comment = "这是一个多选框控件")
        private List<AnimalEnum> checkbox;

        @Getter
        @Control(label = "密码框", type = ControlType.PASSWORD, comment = "这是一个密码框")
        private String password;


    }

    @Getter
    @AllArgsConstructor
    public enum BooleanEnum implements OptionalEnum<String> {
        TRUE("True", "1"),
        FALSE("False", "0");

        private final String name;
        private final String value;

        @Override
        public boolean isCompatibleWith(Object value) {
            if (value == null){
                return false;
            }
            if (value instanceof String s){
                return "true".equalsIgnoreCase(s) || "1".equals(s);
            }
            if (value instanceof BooleanEnum b){
                return this.value.equals(b.value);
            }
            return false;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum AnimalEnum implements OptionalEnum<String> {

        CAT("猫", "cat"),
        DOG("狗", "dog"),
        FISH("鱼", "fish"),
        BIRD("鸟", "bird");

        private final String name;
        private final String value;
    }
}
