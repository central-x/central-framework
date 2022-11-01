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

package central.lang;

import central.bean.OptionalEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 比较结果
 * <p>
 * -1: left < right
 * <p>
 * 0: left = right
 * <p>
 * 1: left > right
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
@Getter
@PublicApi
@AllArgsConstructor
public enum CompareResult implements OptionalEnum<String> {

    LT("小于", "<", List.of(-1)),
    LE("小于等于", "<=", List.of(-1, 0)),
    EQUALS("等于", "=", List.of(0)),
    GT("大于", ">", List.of(1)),
    GE("大于等于", ">=", List.of(0, 1));

    private final String name;
    private final String value;
    private final List<Integer> result;

    @Override
    public boolean isCompatibleWith(Object value) {
        if (value instanceof String s) {
            return this.getValue().equals(s);
        } else if (value instanceof Number n) {
            return this.result.stream().anyMatch(it -> it == n.intValue());
        } else {
            return false;
        }
    }

    /**
     * 是否匹配结果
     * 例：
     * {@code CompareResultEnum.GREATER.matches(first, second) } 的结果为 true 时，表示 first > second
     * {@code CompareResultEnum.LESS.matches(first, second) } 的结果为 true 时，表示 first < second
     *
     * @param first  第一个待比较的对象
     * @param second 第二个待比较的对象
     * @return 匹配结果
     */
    public <T extends Comparable<T>> boolean matches(T first, T second) {
        int compare = first.compareTo(second);
        return this.result.stream().anyMatch(it -> it == compare);
    }
}
