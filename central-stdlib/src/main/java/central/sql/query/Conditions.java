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

import central.sql.data.Entity;
import central.bean.Orderable;
import central.bean.Treeable;
import central.validation.Validatable;
import central.lang.Assertx;
import central.lang.reflect.PropertyRef;
import central.lang.Arrayx;
import central.util.Collectionx;
import central.util.Listx;
import central.lang.Stringx;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 条件
 *
 * @author Alan Yeh
 * @since 2022/07/20
 */
public class Conditions<T extends Entity> implements Collection<Conditions.Condition<T>>, Cloneable, Validatable {
    @Getter
    @Setter
    private String id;

    // 下一个连接符
    private Connectors nextConnector = Connectors.AND;

    private final AtomicInteger index = new AtomicInteger(0);

    private final AtomicInteger idGenerator;

    private Conditions(String id, AtomicInteger idGenerator) {
        this.id = id;
        this.idGenerator = idGenerator;
    }

    public Conditions() {
        this(null, new AtomicInteger(1));
    }

    private List<Condition<T>> conditions = new ArrayList<>();
    private Set<String> properties = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var that = (Conditions<?>) o;
        return conditions.equals(that.conditions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conditions);
    }

    @Override
    public String toString() {
        return Stringx.format("Conditions@{}[{}]", Integer.toHexString(hashCode()), this.toSql());
    }

    @Override
    @SneakyThrows(CloneNotSupportedException.class)
    public Conditions<T> clone() {
        var conditions = (Conditions<T>) super.clone();

        conditions.setId(this.getId());
        conditions.idGenerator.set(this.idGenerator.get());
        conditions.index.set(this.index.get());
        conditions.conditions = new ArrayList<>(Listx.asStream(this.conditions).map(Condition::clone).toList());
        conditions.properties = new HashSet<>(this.properties);
        return conditions;
    }

    /**
     * 空条件
     */
    public static <T extends Entity> Conditions<T> empty() {
        return new Conditions<>();
    }

    /**
     * 快速构造器
     */
    public static <T extends Entity> Conditions<T> of(Class<T> type) {
        return new Conditions<>();
    }

    public static <T extends Entity> Conditions<T> of(Conditions<T> conditions) {
        if (conditions == null) {
            return new Conditions<>();
        }
        return conditions;
    }

    /**
     * 将条件使用 () 封装起来
     */
    public static <T extends Entity> Conditions<T> group(Conditions<T> conditions) {
        return new Conditions<T>().nested(conditions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 条件构造方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 添加相等条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     * @param value    值
     */
    public Conditions<T> eq(PropertyRef<T, ?> property, Object value) {
        if (value == null) {
            return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.IS_NULL));
        } else {
            return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.EQ, value));
        }
    }

    /**
     * 添加相等条件
     *
     * @param property 实体属性名
     * @param value    值
     */
    public Conditions<T> eq(String property, Object value) {
        if (value == null) {
            return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.IS_NULL));
        } else {
            return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.EQ, value));
        }
    }

    /**
     * 添加不相等条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     * @param value    值
     */
    public Conditions<T> ne(PropertyRef<T, ?> property, Object value) {
        if (value == null) {
            return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.IS_NOT_NULL));
        } else {
            return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.NE, value));
        }
    }

    /**
     * 添加不相等条件
     *
     * @param property 实体属性名
     * @param value    值
     */
    public Conditions<T> ne(String property, Object value) {
        if (value == null) {
            return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.IS_NOT_NULL));
        } else {
            return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.NE, value));
        }
    }

    /**
     * 添加大于条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     * @param value    值
     */
    public Conditions<T> gt(PropertyRef<T, ?> property, Object value) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.GT, value));
    }

    /**
     * 添加大于条件
     *
     * @param property 实体属性名
     * @param value    值
     */
    public Conditions<T> gt(String property, Object value) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.GT, value));
    }

    /**
     * 添加大于等于条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     * @param value    值
     */
    public Conditions<T> ge(PropertyRef<T, ?> property, Object value) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.GE, value));
    }

    /**
     * 添加大于等于条件
     *
     * @param property 实体属性名
     * @param value    值
     */
    public Conditions<T> ge(String property, Object value) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.GE, value));
    }

    /**
     * 添加小于条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     * @param value    值
     */
    public Conditions<T> lt(PropertyRef<T, ?> property, Object value) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.LT, value));
    }

    /**
     * 添加小于条件
     *
     * @param property 实体属性名
     * @param value    值
     */
    public Conditions<T> lt(String property, Object value) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.LT, value));
    }

    /**
     * 添加小于等于条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     * @param value    值
     */
    public Conditions<T> le(PropertyRef<T, ?> property, Object value) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.LE, value));
    }

    /**
     * 添加小于等于条件
     *
     * @param property 实体属性名
     * @param value    值
     */
    public Conditions<T> le(String property, Object value) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.LE, value));
    }

    /**
     * 添加相似条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     * @param value    值
     */
    public Conditions<T> like(PropertyRef<T, ?> property, Object value) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.LIKE, value));
    }

    /**
     * 添加相似条件
     *
     * @param property 实体属性名
     * @param value    值
     */
    public Conditions<T> like(String property, Object value) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.LIKE, value));
    }

    /**
     * 添加不相似条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     * @param value    值
     */
    public Conditions<T> notLike(PropertyRef<T, ?> property, Object value) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.NOT_LIKE, value));
    }

    /**
     * 添加不相似条件
     *
     * @param property 实体属性名
     * @param value    值
     */
    public Conditions<T> notLike(String property, Object value) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.NOT_LIKE, value));
    }

    /**
     * 添加介于条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     * @param start    开始值
     * @param end      结束值
     */
    public Conditions<T> between(PropertyRef<T, ?> property, Object start, Object end) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.BETWEEN, start, end));
    }

    /**
     * 添加介于条件
     *
     * @param property 实体属性名
     * @param start    开始值
     * @param end      结束值
     */
    public Conditions<T> between(String property, Object start, Object end) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.BETWEEN, start, end));
    }

    /**
     * 添加不介于条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     * @param start    开始值
     * @param end      结束值
     */
    public Conditions<T> notBetween(PropertyRef<T, ?> property, Object start, Object end) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.NOT_BETWEEN, start, end));
    }

    /**
     * 添加不介于条件
     *
     * @param property 实体属性名
     * @param start    开始值
     * @param end      结束值
     */
    public Conditions<T> notBetween(String property, Object start, Object end) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.NOT_BETWEEN, start, end));
    }

    /**
     * 添加为空条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     */
    public Conditions<T> isNull(PropertyRef<T, ?> property) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.IS_NULL));
    }

    /**
     * 添加为空条件
     *
     * @param property 实体属性名
     */
    public Conditions<T> isNull(String property) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.IS_NULL));
    }

    /**
     * 添加不为空条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     */
    public Conditions<T> isNotNull(PropertyRef<T, ?> property) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.IS_NOT_NULL));
    }

    /**
     * 添加不为空条件
     *
     * @param property 实体属性名
     */
    public Conditions<T> isNotNull(String property) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.IS_NOT_NULL));
    }

    /**
     * 添加范围条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     * @param args     数据项
     */
    public Conditions<T> in(PropertyRef<T, ?> property, Object... args) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.IN, args));
    }

    /**
     * 添加范围条件
     *
     * @param property 实体属性名
     * @param args     数据项
     */
    public Conditions<T> in(String property, Object... args) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.IN, args));
    }

    /**
     * 添加不在范围条件
     *
     * @param property 实体属性的Getter方法引用，Entity::Getter
     * @param args     数据项
     */
    public Conditions<T> notIn(PropertyRef<T, ?> property, Object... args) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.NOT_IN, args));
    }

    /**
     * 添加不在范围条件
     *
     * @param property 实体属性名
     * @param args     数据项
     */
    public Conditions<T> notIn(String property, Object... args) {
        return this.addCondition(new Condition<>(String.valueOf(this.idGenerator.getAndIncrement()), this.getId(), this.index.getAndIncrement(), this.nextConnector, property, Operators.NOT_IN, args));
    }

    /**
     * 下一个条件使用 OR 连接
     */
    public Conditions<T> or() {
        this.nextConnector = Connectors.OR;
        return this;
    }

    /**
     * Or 嵌套查询
     *
     * @param consumer 嵌套查询条件
     */
    public Conditions<T> or(Consumer<Conditions<T>> consumer) {
        var conditions = new Conditions<T>();
        consumer.accept(conditions);

        this.nextConnector = Connectors.OR;
        return this.nested(conditions);
    }

    /**
     * Or 嵌套查询
     *
     * @param conditions 嵌套查询条件
     */
    public Conditions<T> or(Conditions<T> conditions) {
        this.nextConnector = Connectors.OR;
        return this.nested(conditions);
    }

    /**
     * 下一个条件使用 AND 连接
     */
    public Conditions<T> and() {
        this.nextConnector = Connectors.AND;
        return this;
    }

    /**
     * and 嵌套查询
     *
     * @param consumer 嵌套查询条件
     */
    public Conditions<T> and(Consumer<Conditions<T>> consumer) {
        var conditions = new Conditions<T>();
        consumer.accept(conditions);
        this.nextConnector = Connectors.AND;
        return this.nested(conditions);
    }

    /**
     * and 嵌套查询
     *
     * @param conditions 嵌套查询条件
     */
    public Conditions<T> and(Conditions<T> conditions) {
        this.nextConnector = Connectors.AND;
        return this.nested(conditions);
    }

    /**
     * 嵌套查询
     *
     * @param consumer 嵌套查询条件
     */
    public Conditions<T> nested(Consumer<Conditions<T>> consumer) {
        var conditions = new Conditions<T>();
        consumer.accept(conditions);
        return this.nested(conditions);
    }

    /**
     * 嵌套查询
     *
     * @param conditions 被嵌套的条件
     */
    public Conditions<T> nested(Conditions<T> conditions) {
        if (Collectionx.isNotEmpty(conditions)) {
            // 处理当前集合的关系
            var clone = conditions.clone();

            var ids = clone.stream().map(Condition::getId).mapToInt(Integer::parseInt).sorted().mapToObj(String::valueOf).toList();
            var idMap = new HashMap<String, String>();
            for (var id : ids) {
                idMap.put(id, String.valueOf(this.idGenerator.getAndIncrement()));
            }

            // 替换主键
            clone.forEach(it -> {
                if (Stringx.isNotBlank(it.getId())) {
                    it.setId(idMap.get(it.getId()));
                }
                if (Stringx.isNotBlank(it.getParentId())) {
                    it.setParentId(idMap.get(it.getParentId()));
                }
            });

            if (clone.getId() == null) {
                // 如果当前的 conditions 的 id 为空
                clone.setId(String.valueOf(this.idGenerator.getAndIncrement()));
            }

            // 添加条件
            return this.addCondition(new Condition<>(clone.getId(), this.getId(), this.index.getAndIncrement(), this.nextConnector, clone.conditions));
        } else {
            return this;
        }
    }

    private Conditions<T> addCondition(Condition<T> condition) {
        if (Listx.isNotEmpty(condition.getChildren())) {
            this.conditions.addAll(condition.getChildren());
            condition.setChildren(null);
        }
        this.conditions.add(condition);
        return this.and();
    }

    public String toSql() {
        // 根据表达式构建
        var sql = new StringBuilder();

        var clone = this.clone();
        var expression = Treeable.build(clone.conditions, Condition.defaultComparator());

        for (int i = 0, length = expression.size(); i < length; i++) {
            var condition = expression.get(i);
            if (i != 0) {
                sql.append(" ").append(condition.getConnector()).append(" ");
            }
            condition.toSql(sql);
        }

        return sql.toString();
    }

    @Override
    public void validate(Class<?>... groups) {
        if (Listx.isNotEmpty(this.conditions)) {
            for (var condition : this.conditions) {
                condition.validate(groups);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 原生条件构造方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @NoArgsConstructor
    public static class Condition<T extends Entity> implements Treeable<Condition<T>>, Orderable<Condition<T>>, Cloneable, Validatable {
        public static <T extends Entity> Comparator<Condition<T>> defaultComparator() {
            return Comparator.comparing(Condition::getOrder);
        }

        /**
         * 条件标识
         */
        @Getter
        @Setter
        private String id;

        /**
         * 条件类型
         */
        @Getter
        @Setter
        private Types type;

        /**
         * 父条件标识
         */
        @Getter
        @Setter
        private String parentId;

        /**
         * 子条件列表
         */
        @Setter
        @Getter(onMethod_ = @JsonIgnore)
        private transient List<Condition<T>> children;

        /**
         * 排序号
         */
        @Getter
        @Setter
        private Integer order;

        /**
         * 与上一个条件的连接符（AND/OR）
         */
        @Getter
        @Setter
        private Connectors connector;

        /**
         * 属性
         */
        @Getter
        @Setter
        @NotEmpty(message = "属性[property]必须不为空")
        private String property;

        /**
         * 别名
         */
        private transient String alias;

        @JsonIgnore
        public String getAlias() {
            if (alias != null) {
                return alias;
            }
            if (Stringx.isNullOrBlank(this.property)) {
                return null;
            }

            int index = property.indexOf(".");
            if (index > 0) {
                this.alias = property.substring(0, index);
            }

            return alias;
        }

        /**
         * 操作符
         */
        @Getter
        @Setter
        @NotNull(message = "操作符[operator]必须不为空")
        private Operators operator;

        /**
         * 参数
         */
        @Getter
        @Setter
        private Object[] values;

        /**
         * 条件分组
         */
        public Condition(String id, String parentId, Integer order, Connectors connector, List<Condition<T>> children) {
            this.id = id;
            this.parentId = parentId;
            this.order = order;
            this.connector = connector;
            this.type = Types.GROUP;
            this.children = children;
            this.children.forEach(it -> {
                if (Stringx.isNullOrBlank(it.getParentId())) {
                    it.setParentId(this.id);
                }
            });
            this.validate();
        }

        /**
         * 使用属性构造条件
         */
        public Condition(String id, String parentId, Integer order, Connectors connector, String property, Operators operator, Object... values) {
            this.id = id;
            this.parentId = parentId;
            this.order = order;
            this.connector = connector;
            this.property = property;
            this.operator = operator;
            this.type = Types.CONDITION;
            this.values = values;

            if (values.length == 1 && (Operators.IN.equals(this.operator) || Operators.NOT_IN.equals(this.operator))) {
                if (values[0] instanceof Collection) {
                    this.values = ((Collection<?>) values[0]).toArray(new Object[0]);
                }
            }
            this.validate();
        }

        /**
         * 使用方法引用构造条件
         */
        public Condition(String id, String parentId, Integer order, Connectors connector, PropertyRef<T, ?> property, Operators operator, Object... values) {
            this.id = id;
            this.parentId = parentId;
            this.order = order;
            this.connector = connector;
            this.type = Types.CONDITION;

            if (property != null) {
                this.property = property.getPropertyName();
            }
            this.operator = operator;
            this.values = values;
            if (values.length == 1 && (Operators.IN.equals(this.operator) || Operators.NOT_IN.equals(this.operator))) {
                if (values[0] instanceof Collection) {
                    this.values = ((Collection<?>) values[0]).toArray();
                }
            }
            this.validate();
        }


        public Condition(Map<String, Object> map) throws IllegalArgumentException {
            this.id = (String) map.get("id");
            this.parentId = (String) map.get("parentId");
            this.order = (Integer) map.get("order");
            this.property = (String) map.get("property");

            Object type = map.get("type");
            if (type == null) {
                // 空的时候，默认为 CONDITION
                this.type = Types.CONDITION;
            } else {
                this.type = Types.resolve(type.toString());
                if (this.type == null) {
                    String msg = Arrays.stream(Types.values()).map(Object::toString).collect(Collectors.joining(", "));
                    throw new IllegalArgumentException(Stringx.format("类型[type]仅支持[{}]", msg));
                }
            }

            Object connector = map.get("connector");
            if (connector == null) {
                // 空的时候，默认为 AND
                this.connector = Connectors.AND;
            } else {
                this.connector = Connectors.resolve(connector.toString());
                if (this.connector == null) {
                    String msg = Arrays.stream(Connectors.values()).map(Object::toString).collect(Collectors.joining(", "));
                    throw new IllegalArgumentException(Stringx.format("条件连接符[connector]仅支持[{}]", msg));
                }
            }

            Object values = map.get("values");
            if (values != null) {
                if (values instanceof Collection) {
                    this.values = ((Collection<?>) values).toArray();
                } else {
                    throw new IllegalArgumentException("values 字段必须是数组类型，请改正后再试");
                }
            } else {
                this.values = new Object[0];
            }


            Object operator = map.get("operator");
            if (operator == null) {
                // 空的时候，默认为 EQ
                this.operator = Operators.EQ;
            } else {
                this.operator = Operators.resolve(operator.toString());
                if (this.operator == null) {
                    String msg = Arrays.stream(Operators.values()).map(Object::toString).collect(Collectors.joining(", "));
                    throw new IllegalArgumentException(Stringx.format("查询操作符[operator]仅支持[{}]", msg));
                }
            }

            this.validate();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Condition<T> condition = (Condition<T>) o;
            return Objects.equals(this.property, condition.property) &&
                    operator == condition.operator &&
                    Arrays.equals(values, condition.values);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(property, operator);
            result = 31 * result + Arrays.hashCode(values);
            return result;
        }

        @Override
        public String toString() {
            return "Condition{" +
                    "id='" + id + '\'' +
                    ", type=" + type +
                    ", parentId='" + parentId + '\'' +
                    ", children=" + children +
                    ", order=" + order +
                    ", connector=" + connector +
                    ", property='" + property + '\'' +
                    ", alias='" + alias + '\'' +
                    ", operator=" + operator +
                    ", values=" + Arrays.toString(values) +
                    '}';
        }

        public void toSql(StringBuilder sql) {
            if (this.type == Types.GROUP) {
                // 这个是一个用于嵌套的分组条件
                if (Listx.isNotEmpty(this.getChildren())) {
                    sql.append("(");
                    for (int i = 0, length = this.getChildren().size(); i < length; i++) {
                        var condition = this.getChildren().get(i);
                        if (i != 0) {
                            sql.append(" ").append(condition.getConnector()).append(" ");
                        }
                        condition.toSql(sql);
                    }
                    sql.append(")");
                }
            } else {
                switch (this.operator) {
                    case EQ, NE, GT, GE, LT, LE, LIKE, NOT_LIKE -> {
                        sql.append(Stringx.format(this.operator.getValue(), this.property, this.values[0]));
                    }
                    case BETWEEN, NOT_BETWEEN -> {
                        sql.append(Stringx.format(this.operator.getValue(), this.property, this.values[0], this.values[1]));
                    }
                    case IS_NULL, IS_NOT_NULL -> {
                        sql.append(Stringx.format(this.operator.getValue(), this.property));
                    }
                    case IN, NOT_IN -> {
                        sql.append(Stringx.format(this.operator.getValue(), this.property, Stringx.join(this.values, ", ")));
                    }
                    default -> {
                        throw new IllegalArgumentException(Stringx.format("Not supported operator '{}'", this.operator.getValue()));
                    }
                }
            }
        }

        @Override
        @SneakyThrows(CloneNotSupportedException.class)
        protected Condition<T> clone() {
            var clone = (Condition<T>) super.clone();
            clone.setId(this.getId());
            clone.setParentId(this.getParentId());
            clone.setType(this.getType());
            clone.setOrder(this.getOrder());
            clone.setConnector(this.getConnector());
            clone.setProperty(this.getProperty());
            clone.setOperator(this.getOperator());
            clone.setValues(this.getValues());

            if (Listx.isNotEmpty(this.getChildren())) {
                clone.setChildren(new ArrayList<>(this.getChildren().stream().map(Condition::clone).toList()));
            }
            return clone;
        }

        @Override
        public void validate(Class<?>... groups) {
            if (this.type == null) {
                this.type = Types.CONDITION;
            }
            if (this.connector == null) {
                this.connector = Connectors.AND;
            }
            if (this.operator == null) {
                this.operator = Operators.EQ;
            }
            if (this.order == null) {
                this.order = 0;
            }
            Assertx.mustNotBlank(this.id, "id 不能为空");

            if (Types.CONDITION.equals(this.type)) {
                Assertx.mustNotBlank(this.property, "property 不能为空");
                if (Stringx.isNotBlank(this.property)) {
                    Assertx.mustTrue(this.property.split("[.]").length <= 2, "property 只允许包含一个符号 [.]");
                }

                switch (this.operator) {
                    case EQ, NE, GT, GE, LT, LE, LIKE, NOT_LIKE -> {
                        Assertx.mustTrue(Arrayx.isNotEmpty(this.values) && this.values.length == 1, "操作符[{}]需要 values 参数数量为一个，请改正后再试", operator);
                    }
                    case BETWEEN, NOT_BETWEEN -> {
                        Assertx.mustTrue(Arrayx.isNotEmpty(this.values) && this.values.length == 2, "操作符[{}]需要 values 参数数量为两个，请改正后再试", operator);
                    }
                    case IS_NULL, IS_NOT_NULL -> {
                        Assertx.mustTrue(Arrayx.isNullOrEmpty(this.values), "操作符[{}]需要 values 参数数量为空，请改正后再试", operator);
                    }
                    case IN, NOT_IN -> {
                        Assertx.mustTrue(Arrayx.isNotEmpty(this.values), "操作符[{}]需要 values 参数数量不为空，请改正后再试", operator);
                    }
                }
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Collection 方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Set<String> getProperties() {
        return this.conditions.stream().map(Condition::getProperty).collect(Collectors.toSet());
    }

    @Override
    public int size() {
        return this.conditions.size();
    }

    @Override
    public boolean isEmpty() {
        return this.conditions.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.conditions.contains(o);
    }

    @Override
    public Iterator<Condition<T>> iterator() {
        return this.conditions.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.conditions.toArray();
    }

    @Override
    @SuppressWarnings("SuspiciousToArrayCall")
    public <A> A[] toArray(@Nonnull A[] a) {
        return this.conditions.toArray(a);
    }

    @Override
    public boolean add(Condition<T> condition) {
        if (Stringx.isNotBlank(condition.getId())) {
            int id = Integer.parseInt(condition.getId());
            this.idGenerator.set(Math.max(this.idGenerator.get(), id) + 1);
        }
        if (condition.getOrder() == null) {
            condition.setOrder(this.index.getAndIncrement());
        } else {
            this.index.set(Math.max(this.index.get(), condition.getOrder()) + 1);
        }
        return this.conditions.add(condition);
    }

    @Override
    public boolean remove(Object o) {
        return this.conditions.remove(o);
    }

    @Override
    @SuppressWarnings("SlowListContainsAll")
    public boolean containsAll(@Nonnull Collection<?> collection) {
        return this.conditions.containsAll(collection);
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends Condition<T>> collection) {
        return this.conditions.addAll(collection);
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> collection) {
        return this.conditions.removeAll(collection);
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> collection) {
        return this.conditions.retainAll(collection);
    }

    @Override
    public void clear() {
        this.conditions.clear();
    }
}