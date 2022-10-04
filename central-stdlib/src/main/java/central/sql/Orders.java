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

package central.sql;

import central.sql.data.Entity;
import central.validation.Validatable;
import central.lang.Assertx;
import central.lang.reflect.PropertyReference;
import central.util.Listx;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Delegate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 排序
 *
 * @author Alan Yeh
 * @since 2022/07/20
 */
public class Orders<T extends Entity> implements Collection<Orders.Order<T>>, Validatable {
    private final List<Order<T>> orders = new ArrayList<>();

    @Delegate
    private Collection<Order<T>> getDelegate() {
        return this.orders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var that = (Orders<?>) o;
        return orders.equals(that.orders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orders);
    }

    /**
     * 快速构造器
     */
    public static <T extends Entity> Orders<T> of(Class<T> type) {
        return new Orders<>();
    }

    public static <T extends Entity> Orders<T> of(Orders<T> orders) {
        if (orders == null) {
            return new Orders<>();
        }
        return orders;
    }

    /**
     * 从 Json 反序列化
     */
    public static <T extends Entity> Orders<T> from(List<Map<String, Object>> orders) {
        var result = new Orders<T>();
        orders.forEach(it -> result.add(new Order<>(it)));
        return result;
    }

    public String toSql() {
        // 根据表达式构建
        return Listx.asStream(this.orders).map(Order::toSql).collect(Collectors.joining(", "));
    }

    @Override
    public void validate(Class<?>... groups) {
        if (Listx.isNotEmpty(this.orders)) {
            for (var order : this.orders) {
                order.validate(groups);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 排序条件构造
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 添加新的排序条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     * @param desc     是否倒序
     */
    public Orders<T> and(PropertyReference<T, ?> property, boolean desc) {
        this.orders.add(new Order<>(property, desc));
        return this;
    }

    /**
     * 添加新的排序条件
     *
     * @param property 实体属性名
     * @param desc     是否倒序
     */
    public Orders<T> and(String property, boolean desc) {
        this.orders.add(new Order<>(property, desc));
        return this;
    }

    /**
     * 添加新的正序排序条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     */
    public Orders<T> asc(PropertyReference<T, ?> property) {
        this.orders.add(new Order<>(property, false));
        return this;
    }

    /**
     * 添加新的正序排序条件
     *
     * @param property 实体属性名
     */
    public Orders<T> asc(String property) {
        this.orders.add(new Order<>(property, false));
        return this;
    }

    /**
     * 添加新的倒序排序条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     */
    public Orders<T> desc(PropertyReference<T, ?> property) {
        this.orders.add(new Order<>(property, true));
        return this;
    }

    /**
     * 添加新的倒序排序条件
     *
     * @param property 实体属性名
     */
    public Orders<T> desc(String property) {
        this.orders.add(new Order<>(property, true));
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 内部排序条件存放类
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @NoArgsConstructor
    public static class Order<T extends Entity> implements Validatable {

        /**
         * 实体属性Getter对应的属性
         */
        @Getter
        @Setter
        @NotEmpty(message = "属性[property]必须不为空")
        private String property;

        /**
         * 是否倒序排序
         */
        @Getter
        @Setter
        private boolean desc;

        Order(PropertyReference<T, ?> property, boolean desc) {
            this.property = property.getPropertyName();
            this.desc = desc;

            this.validate();
        }


        Order(String property, boolean desc) {
            this.property = property;
            this.desc = desc;

            this.validate();
        }

        Order(Map<String, Object> map) {
            this.property = (String) map.get("property");

            this.validate();

            Object desc = map.get("desc");
            if (desc == null) {
                this.desc = false;
            } else {
                Assertx.mustInstanceOf(Boolean.class, desc, "orders 的参数项中，desc 字段必须是 Boolean 类型");
                this.desc = (Boolean) desc;
            }
        }


        @Override
        public void validate(Class<?>... groups) {
            Assertx.mustNotBlank(this.property, "property 不能为空");
            Assertx.mustTrue(!this.property.contains("."), "排序属性 property 不允许带符号[.]（不允许外键排序）");
        }

        public String toSql() {
            if (this.isDesc()) {
                return this.property + " DESC";
            } else {
                return this.property;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            var order = (Order<?>) o;
            return desc == order.desc &&
                    property.equals(order.property);
        }

        @Override
        public int hashCode() {
            return Objects.hash(property, desc);
        }
    }
}
