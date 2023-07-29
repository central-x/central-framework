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

package central.util.cache;

import central.bean.OptionalEnum;
import central.lang.Arrayx;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 数据类型
 *
 * @author Alan Yeh
 * @since 2023/06/10
 */
@Getter
@AllArgsConstructor
public enum DataType implements OptionalEnum<Integer> {
    NONE("none", "不存在", 0),
    STRING("string", "字符串", 1),
    LIST("list", "列表", 2),
    QUEUE("queue", "队表", 4),
    SET("set", "无序集合", 8),
    ZSET("zset", "有序集合", 16),
    MAP("map", "键值对", 32);

    private final String code;
    private final String name;
    private final Integer value;

    @Override
    public boolean isCompatibleWith(Object value) {
        if (value instanceof Integer integer) {
            return (this.value.intValue() & integer) != 0;
        } else if (value instanceof DataType type) {
            if (type == DataType.NONE) {
                return true;
            } else {
                return Objects.equals(this.value, type.value);
            }
        } else {
            return false;
        }
    }

    @JsonCreator
    public static @Nullable DataType resolve(int value) {
        return Arrayx.asStream(DataType.values()).filter(it -> it.value == value).findFirst().orElse(null);
    }
}
