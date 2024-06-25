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

package central.lang.reflect;

import central.bean.InitializeException;
import central.bean.Page;
import central.lang.Arrayx;
import central.lang.Assertx;
import central.lang.Stringx;
import central.util.*;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.SneakyThrows;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 类型引用
 * <p>
 * 由于泛型在使用的过程中，会被类型擦除，因此可能需要比较麻烦才能拿到类型信息
 * 根据 Java 的特性，可以通过继承的方式将泛型固化下来
 * <p>
 * 本类通过声明匿名类的方式，可以将泛型正确地固化下来。
 * <p>
 * {@code var reference = new TypeReference<List<String>>(){} }
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public abstract class TypeRef<T> {
    /**
     * 类型
     */
    @Getter
    private final Type type;

    /**
     * 获取类型名称
     */
    public String getName() {
        return this.type.getTypeName();
    }

    private final LazyValue<Class<T>> rawClass = new LazyValue<>(() -> {
        if (isParameterized()) {
            if (getParameterizedType().getRawType() instanceof Class<?> c) {
                return (Class<T>) c;
            }
        }
        if (getType() instanceof Class<?> c) {
            return (Class<T>) c;
        }
        return null;
    });

    /**
     * 将类型转换为 Class
     */
    public Class<T> getRawClass() {
        return this.rawClass.get();
    }

    private final LazyValue<Type> rawType = new LazyValue<>(() -> isParameterized() ? getParameterizedType().getRawType() : getType());

    /**
     * 获取原始类型
     */
    public Type getRawType() {
        return this.rawType.get();
    }

    private final LazyValue<ParameterizedType> parameterizedType = new LazyValue<>(() -> getType() instanceof ParameterizedType p ? p : null);

    /**
     * 获取泛型类型
     * 如果该类型不是泛型，将会返回 null
     * 可以通过 {@link #isParameterized()} 方法检查
     */
    public ParameterizedType getParameterizedType() {
        return this.parameterizedType.get();
    }

    private final LazyValue<List<? extends TypeRef<?>>> actualTypeArguments = new LazyValue<>(() -> getParameterizedType() == null ? Collections.emptyList() : Arrayx.asStream(getParameterizedType().getActualTypeArguments()).map(TypeRef::of).toList());

    /**
     * 获取泛型参 类型
     */
    public List<? extends TypeRef<?>> getActualTypeArguments() {
        return this.actualTypeArguments.get();
    }

    /**
     * 获取泛型参数类型
     *
     * @param index 指定下标
     */
    public TypeRef<?> getActualTypeArgument(int index) {
        return this.getActualTypeArguments().get(index);
    }

    /**
     * 判断当前类型是否泛型
     */
    public boolean isParameterized() {
        return this.type instanceof ParameterizedType;
    }

    private final LazyValue<TypeRef<?>> superType = new LazyValue<>(() -> getRawClass() == null ? null : TypeRef.of(getRawClass().getGenericSuperclass()));

    /**
     * 获取父类型
     */
    public TypeRef<?> getSuperType() {
        return this.superType.get();
    }

    private final LazyValue<List<? extends TypeRef<?>>> interfaceTypes = new LazyValue<>(() -> getRawClass() == null ? Collections.emptyList() : Arrayx.asStream(getRawClass().getAnnotatedInterfaces()).map(AnnotatedType::getType).map(TypeRef::of).toList());

    /**
     * 获取继承的接口
     */
    public List<? extends TypeRef<?>> getInterfaceTypes() {
        return this.interfaceTypes.get();
    }

    /**
     * 获取继承的接口
     *
     * @param index 下标
     */
    public TypeRef<?> getInterfaceType(int index) {
        return this.getInterfaceTypes().get(index);
    }

    /**
     * 类型是否枚举类型
     */
    public boolean isEnum() {
        return getRawClass().isEnum();
    }

    private final LazyValue<List<FieldRef>> fields = new LazyValue<>(() -> getRawClass() == null ? Collections.emptyList() : Arrayx.asStream(getRawClass().getDeclaredFields()).map(FieldRef::of).toList());

    /**
     * 获取字段信息
     */
    public List<FieldRef> getFields() {
        return fields.get();
    }

    private final LazyValue<List<PropertyDescriptor>> properties = new LazyValue<>(new Supplier<List<PropertyDescriptor>>() {
        @Override
        @SneakyThrows
        public List<PropertyDescriptor> get() {
            return getRawClass() == null ? Collections.emptyList() : Arrayx.asStream(Introspector.getBeanInfo(getRawClass()).getPropertyDescriptors()).toList();
        }
    });

    public List<PropertyDescriptor> getProperties() {
        return properties.get();
    }

    public PropertyDescriptor getProperty(String property) {
        return Listx.asStream(this.getProperties()).filter(it -> property.equals(it.getName())).findFirst().orElse(null);
    }

    /**
     * 创建实例
     * 根据指定参数查找构造函数，并通过该构造函数创建实例
     *
     * @param args 构造参数
     */
    @SneakyThrows
    public InstanceRef<T> newInstance(Object... args) {
        Class<T> clazz = this.getRawClass();
        // 处理一些集合类型
        if (List.class == this.getRawType()) {
            clazz = (Class<T>) ArrayList.class;
        } else if (Map.class == this.getRawType()) {
            clazz = (Class<T>) HashMap.class;
        } else if (Set.class == this.getRawType()) {
            clazz = (Class<T>) HashSet.class;
        }

        if (clazz == null) {
            // 无法实例化
            return null;
        } else {
            if (Arrayx.isNullOrEmpty(args)) {
                return InstanceRef.of(this, clazz.getDeclaredConstructor().newInstance());
            } else {
                // 查找匹配的构造器
                List<Constructor<?>> constructors = Arrayx.asStream(clazz.getDeclaredConstructors())
                        .filter(it -> Modifier.isPublic(it.getModifiers()))
                        .filter(it -> it.getParameterCount() == args.length)
                        .toList();

                if (constructors.isEmpty()) {
                    throw new InitializeException(clazz, Stringx.format("Cannot find constructor with specify argument types ({})", Arrayx.asStream(args).map(Objectx::getClass).map(it -> Objectx.isNull(it) ? "?" : it.getName()).collect(Collectors.joining(", "))));
                }

                for (int i = 0; i < args.length; i++) {
                    Class<?> type = Objectx.getClass(args[i]);
                    if (type != null) {
                        final var index = i;
                        constructors = constructors.stream().filter(it -> it.getParameterTypes()[index].isAssignableFrom(type)).toList();
                        if (constructors.isEmpty()) {
                            throw new InitializeException(clazz, Stringx.format("Cannot find constructor with specify argument types ({})", Arrayx.asStream(args).map(Objectx::getClass).map(it -> Objectx.isNull(it) ? "?" : it.getName()).collect(Collectors.joining(", "))));
                        }
                    }
                }

                if (constructors.size() > 1) {
                    // 有多个构造器与给定的参数是匹配的
                    throw new InitializeException(clazz, Stringx.format("Cannot determine constructor with specify argument types ({})", Arrayx.asStream(args).map(Objectx::getClass).map(it -> Objectx.isNull(it) ? "?" : it.getName()).collect(Collectors.joining(", "))));
                }

                Optional<Constructor<?>> constructor = Listx.getFirst(constructors);
                if (constructor.isEmpty()) {
                    throw new InitializeException(clazz, Stringx.format("Cannot find constructor with specify argument types ({})", Arrayx.asStream(args).map(Objectx::getClass).map(it -> Objectx.isNull(it) ? "?" : it.getName()).collect(Collectors.joining(", "))));
                }

                // 实例化
                return InstanceRef.of(this, (T) constructor.get().newInstance(args));
            }
        }
    }

    public boolean isInstance(Object object) {
        if (object == null) {
            return false;
        }

        return this.getRawClass().isInstance(object);
    }

    protected TypeRef() {
        this(null);
    }

    private TypeRef(@Nullable Type type) {
        if (type == null) {
            var superClass = (ParameterizedType) this.getClass().getGenericSuperclass();
            this.type = superClass.getActualTypeArguments()[0];
        } else {
            this.type = type;
        }
    }

    /**
     * 根据 Type 创建 TypeReference
     * <p>
     * 也可以使用 {@link Method#getGenericReturnType()} 的值作为参数
     */
    public static <T> TypeRef<T> of(Type type) {
        return new TypeRef<>(type) {
        };
    }

    /**
     * 根据 Class 创建 TypeReference
     *
     * @param type 类
     * @param <T>  类型
     */
    public static <T> TypeRef<T> of(Class<T> type) {
        return new TypeRef<>(type) {
        };
    }

    @SneakyThrows
    public static TypeRef<?> of(String typeName) {
        Assertx.mustNotNull("typeName", typeName);
        return new TypeRef<>(Class.forName(typeName.trim())) {
        };
    }

    /**
     * 构建 List 类型
     *
     * @param elementType 元素类型
     */
    public static <T extends List<V>, V> TypeRef<T> ofList(TypeRef<V> elementType) {
        return new TypeRef<>(new ParameterizedTypeImpl(List.class, new Type[]{elementType.getType()})) {
        };
    }

    /**
     * 构建 List 类型
     *
     * @param elementType 元素类型
     */
    public static <T extends List<E>, E> TypeRef<T> ofList(Class<? extends E> elementType) {
        return new TypeRef<>(new ParameterizedTypeImpl(List.class, new Type[]{elementType})) {
        };
    }

    /**
     * 构建 Set 类型
     *
     * @param elementType 元素类型
     */
    public static <T extends Set<V>, V> TypeRef<T> ofSet(TypeRef<V> elementType) {
        return new TypeRef<>(new ParameterizedTypeImpl(Set.class, new Type[]{elementType.getType()})) {
        };
    }

    /**
     * 构建 Set 类型
     *
     * @param elementType 元素类型
     */
    public static <T extends Set<E>, E> TypeRef<T> ofSet(Class<? extends E> elementType) {
        return new TypeRef<>(new ParameterizedTypeImpl(Set.class, new Type[]{elementType})) {
        };
    }

    /**
     * 构建 Map 类型
     *
     * @param keyType   Key 类型
     * @param valueType Value 类型
     */
    public static <T extends Map<K, V>, K, V> TypeRef<T> ofMap(TypeRef<K> keyType, TypeRef<V> valueType) {
        return new TypeRef<>(new ParameterizedTypeImpl(Map.class, new Type[]{keyType.getType(), valueType.getType()})) {
        };
    }

    /**
     * 构建 Map 类型
     *
     * @param keyType   Key 类型
     * @param valueType Value 类型
     */
    public static <T extends Map<K, V>, K, V> TypeRef<T> ofMap(Class<? extends K> keyType, TypeRef<V> valueType) {
        return new TypeRef<>(new ParameterizedTypeImpl(Map.class, new Type[]{keyType, valueType.getType()})) {
        };
    }

    /**
     * 构建 Map 类型
     *
     * @param keyType   Key 类型
     * @param valueType Value 类型
     */
    public static <T extends Map<K, V>, K, V> TypeRef<T> ofMap(Class<? extends K> keyType, Class<? extends V> valueType) {
        return new TypeRef<>(new ParameterizedTypeImpl(Map.class, new Type[]{keyType, valueType})) {
        };
    }

    /**
     * 构建 Page 类型
     *
     * @param valueType Value 类型
     */
    public static <T extends Serializable> TypeRef<Page<T>> ofPage(Class<T> valueType) {
        return new TypeRef<>(new ParameterizedTypeImpl(Page.class, new Type[]{valueType})) {
        };
    }

    /**
     * 手动构建类型引用
     *
     * @param rawType             原生类型
     * @param actualTypeArguments 泛型参数
     */
    public static <T> TypeRef<T> ofParameterized(Type rawType, Type... actualTypeArguments) {
        return new TypeRef<>(new ParameterizedTypeImpl(rawType, actualTypeArguments)) {
        };
    }

    private static class ParameterizedTypeImpl implements ParameterizedType {

        private final Type ownerType;

        private final Type rawType;

        private final Type[] actualTypeArguments;

        public ParameterizedTypeImpl(Type rawType, Type[] actualTypeArguments) {
            this(null, rawType, actualTypeArguments);
        }

        public ParameterizedTypeImpl(Type ownerType, Type rawType, Type[] actualTypeArguments) {
            this.ownerType = ownerType;
            this.rawType = rawType;
            this.actualTypeArguments = actualTypeArguments;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments;
        }
    }
}
