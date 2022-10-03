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

package central.sql.impl.standard;

import central.lang.reflect.TypeReference;
import central.sql.SqlTransformer;
import central.sql.SqlExecutor;
import central.util.Setx;
import central.lang.Stringx;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 标准 Bean 转换器
 *
 * @author Alan Yeh
 * @since 2022/09/14
 */
public class StandardTransformer implements SqlTransformer {
    @Override
    public <T> T transform(SqlExecutor executor, Map<String, Object> data, Class<T> type) throws SQLException {
        if (Map.class.isAssignableFrom(type)) {
            return (T) this.toMap(data, type);
        } else if (this.isBaseType(type)) {
            return this.toBaseType(executor, data, type);
        } else {
            return this.toBean(executor, data, type);
        }
    }

    @SneakyThrows({NoSuchMethodException.class, InstantiationException.class, IllegalAccessException.class, InvocationTargetException.class})
    private <T> Map<String, Object> toMap(Map<String, Object> data, Class<T> type) {
        if (type == Map.class) {
            // 如果对 Map 没有要求，则使用 HashMap
            return new HashMap<>(data);
        } else {
            var map = (Map<String, Object>) type.getConstructor().newInstance();
            map.putAll(data);
            return map;
        }
    }

    private <T> T toBean(SqlExecutor executor, Map<String, Object> data, Class<T> type) throws SQLException {
        T bean;
        try {
            bean = type.getConstructor().newInstance();
        } catch (NoSuchMethodException | IllegalAccessException ex) {
            throw new SQLException(Stringx.format("没有在类[{}]中找到公开的无参构造函数", type.getName()), ex);
        } catch (InvocationTargetException | InstantiationException ex) {
            throw new SQLException(Stringx.format("调用类[{}]的无参构造函数创建实例失败", type.getName()), ex);
        }

        var reference = TypeReference.of(type);

        for (var entry : data.entrySet()) {
            if (entry.getValue() == null) {
                // 如果没有值，就不需要设置到 property 了
                continue;
            }

            var property = reference.getProperty(entry.getKey());
            if (property == null) {
                // 如果找不到对应的属性，也忽略这个查询结果
                continue;
            }

            var value = entry.getValue();
            if (executor.getConverter().support(entry.getValue().getClass(), property.getPropertyType())) {
                value = executor.getConverter().convert(value, property.getPropertyType());
            }

            try {
                property.getWriteMethod().invoke(bean, value);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new SQLException(Stringx.format("访问类[{}]的属性[{}]出现异常: " + ex.getLocalizedMessage(), type.getName(), property.getName()), ex);
            }
        }

        return bean;
    }

    public <T> T toBaseType(SqlExecutor executor, Map<String, Object> data, Class<T> type) throws SQLException {
        if (data.isEmpty()) {
            if (type.isPrimitive()) {
                return (T) this.getPrimitiveDefaultValue(type);
            } else {
                return null;
            }
        } else if (data.size() > 1) {
            throw new SQLException(Stringx.format("期望返回 1 列，返回类型为 {}，但查询语句返回了 {} 列", type.getName(), data.size()));
        }

        var value = Setx.getAny(data.entrySet()).getValue();
        if (value == null) {
            if (type.isPrimitive()) {
                return (T) this.getPrimitiveDefaultValue(type);
            } else {
                return null;
            }
        }
        if (!executor.getConverter().support(value.getClass(), type)) {
            throw new SQLException(Stringx.format("不支持将类型[{}]转换为[{}]类型", value.getClass().getName(), type.getName()));
        }
        return executor.getConverter().convert(value, type);
    }


    private boolean isBaseType(Class<?> type) {
        if (type.isPrimitive()) {
            return true;
        }
        if (!type.getName().startsWith("java")) {
            return false;
        }

        return type == String.class || type == Integer.class || type == Long.class || type == Short.class || type == Double.class ||
                type == Float.class || type == Byte.class || type == BigDecimal.class || type == BigInteger.class ||
                type == Boolean.class || type == Date.class || type == java.sql.Date.class || type == Timestamp.class ||
                type == LocalDateTime.class || type == LocalDate.class;
    }

    private Object getPrimitiveDefaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        return switch (type.getName()) {
            case "long" -> 0L;
            case "int", "short" -> 0;
            case "boolean" -> false;
            case "float" -> 0.0f;
            case "double" -> 0.0d;
            case "byte" -> (byte) 0;
            case "char" -> (char) 0;
            default ->
                    throw new IllegalArgumentException(Stringx.format("Type '{}' is not primitive type", type.getName()));
        };
    }
}