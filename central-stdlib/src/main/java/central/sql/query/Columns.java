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

package central.sql.query;

import central.lang.reflect.PropertyRef;
import central.sql.data.Entity;
import central.validation.Label;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Delegate;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * 列
 *
 * @author Alan Yeh
 * @since 2023/04/25
 */
public class Columns<T extends Entity> implements Collection<Columns.Column<T>> {

    @Delegate
    private final List<Column<T>> columns = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof Columns<?> that) {
            return this.columns.equals(that.columns);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.columns);
    }

    /**
     * 查询所有列
     */
    public static <T extends Entity> Columns<T> all() {
        return new Columns<>();
    }

    /**
     * 创建默认属性列表
     */
    public static <T extends Entity> Columns<T> of(Columns<T> columns) {
        return Objects.requireNonNullElseGet(columns, Columns::new);
    }

    /**
     * 查询指定属性
     */
    public static <T extends Entity> Columns<T> of(Class<T> type, String... properties) {
        Columns<T> columns = new Columns<>();
        Arrays.asList(properties).forEach(columns::add);
        return columns;
    }

    /**
     * 查询指定属性
     */
    @SafeVarargs
    public static <T extends Entity> Columns<T> of(PropertyRef<T, ?>... properties) {
        Columns<T> columns = new Columns<>();
        Arrays.asList(properties).forEach(columns::add);
        return columns;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 列信息存放类
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @NoArgsConstructor
    public static class Column<T extends Entity> implements Serializable {
        @Serial
        private static final long serialVersionUID = 5995346608003447444L;

        @Label("属性")
        @NotEmpty
        @Getter
        @Setter
        private String property;

        Column(PropertyRef<T, ?> property) {
            this.property = property.getPropertyName();
        }

        Column(String property) {
            this.property = property;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }

            if (o instanceof Column<?> column) {
                return Objects.equals(this.property, column.property);
            }

            return false;
        }

        @Override
        public int hashCode() {
            return this.property.hashCode();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 其它快递方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 添加查询属性
     *
     * @param property 属性
     */
    public void add(PropertyRef<T, ?> property) {
        this.columns.add(new Column<>(property));
    }

    /**
     * 添加查询属性
     *
     * @param property 属性
     */
    public void add(String property) {
        this.columns.add(new Column<>(property));
    }
}
