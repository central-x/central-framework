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

package central.util;

import central.bean.InitializeException;
import central.lang.Assertx;
import central.lang.Stringx;
import central.lang.reflect.TypeReference;
import central.util.converter.ConvertException;
import central.util.converter.Converter;
import central.util.converter.impl.UnsupportedConverter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类型转换器
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public class Convertx {
    private static final Convertx INSTANCE = new Convertx();

    public static Convertx Default() {
        return INSTANCE;
    }

    /**
     * 使用配置文件初始化类型转换器
     */
    public Convertx() {
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("MATA-INF/convertx.properties"));
        } catch (IOException cause) {
            throw new InitializeException(Convertx.class, "Cannot load config in 'MATA-INF/convertx.properties'", cause);
        }

        String classes = properties.getProperty("convertx.converters");

        Arrays.stream(classes.split("[,]"))
                // 将 properties 里面指定的类加载出来
                .map(TypeReference::of)
                // 判断这些类是否都继承 Converter 接口
                .peek(type -> Assertx.mustAssignableFrom(Converter.class, type.getRawClass(), () -> new InitializeException(type.getRawClass(), Stringx.format("'{}' must assignable to {}", type.getName(), Converter.class.getName()))))
                // 实例化这些转换器
                .map(TypeReference::newInstance)
                // 注册转换器
                .forEach(it -> this.register((Converter<?>) it.getInstance()));
    }

    public Convertx(List<Converter<?>> converters) {
        this();
        converters.forEach(this::register);
    }

    /**
     * 已注册的类型转换器
     * target class name -> converter
     */
    private final Map<String, List<Converter<?>>> converters = new ConcurrentHashMap<>();

    private Optional<Type> findTargetType(Converter<?> converter) {
        return Arrays.stream(converter.getClass().getGenericInterfaces())
                .filter(it -> it instanceof ParameterizedType)
                .map(it -> (ParameterizedType) it)
                .filter(it -> Converter.class.getName().equals(it.getRawType().getTypeName()))
                .map(it -> it.getActualTypeArguments()[0])
                .map(it -> it instanceof ParameterizedType p ? p.getRawType() : it)
                .findFirst();
    }

    /**
     * 注册转换器
     *
     * @param converter 数据转换器
     */
    public void register(Converter<?> converter) {
        var targetType = this.findTargetType(converter);

        Assertx.mustTrue(targetType.isPresent(), "Register converter failed: Cannot find interface Converter<?> from {}", converter.getClass());

        this.converters.computeIfAbsent(targetType.get().getTypeName(), key -> new ArrayList<>())
                .add(converter);
        this.cachedConverter.clear();
    }

    /**
     * 取消注册转换器
     *
     * @param converter 数据转换器
     */
    public void deregister(Converter<?> converter) {
        var targetType = this.findTargetType(converter);

        if (targetType.isEmpty()) {
            return;
        }

        this.converters.computeIfAbsent(targetType.get().getTypeName(), key -> new ArrayList<>())
                .remove(converter);
        this.cachedConverter.clear();
    }

    /**
     * 已缓存的转换器
     * 因为如果没找到转换器的话，需要依次调用各个 Converter::support 方法来判断是否支持的数据转换，相对来说比较低效
     * 因此将已知的已匹配的类型保存起来，这样下次就可以直接获取到指定的转换器了
     */
    private final Map<MatchedKey, Converter<?>> cachedConverter = new ConcurrentHashMap<>();

    /**
     * 查找转换器
     */
    private @Nonnull Converter<?> getConverter(Class<?> source, Class<?> target) {
        // 查找之前已匹配的记录
        return cachedConverter.computeIfAbsent(new MatchedKey(toObjectType(source), toObjectType(target)), key -> {
            // 如果没有找到，则需要重新查找合适的转换器
            return this.converters.computeIfAbsent(key.target().getTypeName(), type -> new ArrayList<>())
                    .stream()
                    // 依次判断转换器是否支持转换源数据类型
                    .filter(it -> it.support(key.source()))
                    // 找到第一个支持的转换器即可
                    .findFirst()
                    // 如果没找到，则返回 UnsupportedConverter
                    .or(() -> Optional.of(UnsupportedConverter.getInstance()))
                    .get();
        });
    }

    public Class<?> toObjectType(Class<?> type) {
        if (type.isPrimitive()) {
            return switch (type.getName()) {
                case "long" -> Long.class;
                case "int" -> Integer.class;
                case "short" -> Short.class;
                case "float" -> Float.class;
                case "double" -> Double.class;
                case "char" -> Character.class;
                case "byte" -> Byte.class;
                case "boolean" -> Boolean.class;
                default -> throw new IllegalArgumentException(Stringx.format("未知的原始类型[{}]", type.getName()));
            };
        } else {
            return type;
        }
    }

    /**
     * 判断是否支持转换数据类型
     *
     * @param source 源类型
     * @param target 目标类型
     */
    public boolean support(@Nonnull Class<?> source, @Nonnull Class<?> target) {
        Assertx.mustNotNull(source, "Argument 'source' must not null");
        Assertx.mustNotNull(target, "Argument 'target' must not null");

        if (source == target) {
            return true;
        }

        return UnsupportedConverter.getInstance() != this.getConverter(source, target);
    }

    /**
     * 转换数据类型
     *
     * @param source 源数据
     * @param target 目标类型
     */
    public <T> T convert(@Nullable Object source, @Nonnull Class<T> target) {
        if (source == null) {
            return null;
        }
        Assertx.mustNotNull(target, "Argument 'target' must not null");

        if (source.getClass() == target) {
            // 如果源类型与目标类型一致，则不需要转换
            return (T) source;
        }

        Converter<?> converter = this.getConverter(source.getClass(), target);
        if (UnsupportedConverter.getInstance() == converter) {
            throw new ConvertException(source, target);
        }

        return (T) converter.convert(source);
    }

    private record MatchedKey(Class<?> source, Class<?> target) {
    }
}
