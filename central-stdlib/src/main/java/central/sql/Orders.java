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

import java.util.*;
import java.util.stream.Collectors;

/**
 * 排序
 *
 * @author Alan Yeh
 * @since 2022/07/20
 */
public class Orders implements Collection<Orders.Order>, Validatable {
    private List<Order> orders = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Orders orders1 = (Orders) o;
        return orders.equals(orders1.orders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orders);
    }

    /**
     * 快速构造器
     */
    public static Orders order() {
        return new Orders();
    }

    public static Orders order(Orders orders) {
        if (orders == null) {
            orders = Orders.order();
        }
        return orders;
    }

    /**
     * 从 Json 反序列化
     */
    public static Orders from(List<Map<String, Object>> orders) {
        var result = new Orders();
        result.orders = orders.stream().map(Order::new).collect(Collectors.toList());
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
    public <T extends Entity> Orders and(PropertyReference<T, ?> property, boolean desc) {
        this.orders.add(new Order(property, desc));
        return this;
    }

    /**
     * 添加新的排序条件
     *
     * @param property 实体属性名
     * @param desc     是否倒序
     */
    public Orders and(String property, boolean desc) {
        this.orders.add(new Order(property, desc));
        return this;
    }

    /**
     * 添加新的正序排序条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     */
    public <T extends Entity> Orders asc(PropertyReference<T, ?> property) {
        this.orders.add(new Order(property, false));
        return this;
    }

    /**
     * 添加新的正序排序条件
     *
     * @param property 实体属性名
     */
    public Orders asc(String property) {
        this.orders.add(new Order(property, false));
        return this;
    }

    /**
     * 添加新的倒序排序条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     */
    public <T extends Entity> Orders desc(PropertyReference<T, ?> property) {
        this.orders.add(new Order(property, true));
        return this;
    }

    /**
     * 添加新的倒序排序条件
     *
     * @param property 实体属性名
     */
    public Orders desc(String property) {
        this.orders.add(new Order(property, true));
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 内部排序条件存放类
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @NoArgsConstructor
    public static class Order implements Validatable {

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

        Order(PropertyReference<?, ?> property, boolean desc) {
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
            Order order = (Order) o;
            return desc == order.desc &&
                    property.equals(order.property);
        }

        @Override
        public int hashCode() {
            return Objects.hash(property, desc);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Collection 方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int size() {
        return this.orders.size();
    }

    @Override
    public boolean isEmpty() {
        return this.orders.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.orders.contains(o);
    }

    @Override
    public Iterator<Order> iterator() {
        return this.orders.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.orders.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.orders.toArray(a);
    }

    @Override
    public boolean add(Order order) {
        return this.orders.add(order);
    }

    @Override
    public boolean remove(Object o) {
        return this.orders.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.orders.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Order> c) {
        return this.orders.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.orders.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.orders.retainAll(c);
    }

    @Override
    public void clear() {
        this.orders.clear();
    }
}
