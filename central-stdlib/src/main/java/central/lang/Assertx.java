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

import central.lang.reflect.TypeRef;
import central.util.Collectionx;
import central.util.Mapx;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 断言工具
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
@PublicApi
@UtilityClass
public class Assertx {

    /**
     * <pre>{@code
     * Assertx.must(b instance Integer, ClassCastException::new, "Cannot cast 'b' to {}", Integer.class.getName());
     * }</pre>
     */
    private static <E extends Exception> void must(boolean expression, Function<String, E> throwable, String message, Object... args) throws E {
        if (!expression) {
            throw throwable.apply(Stringx.format(message, args));
        }
    }

    /**
     * <pre>{@code
     * Assertx.must(b instance Integer, () -> new ClassCastException("Cannot cast 'b' to java.lang.Integer"));
     * }</pre>
     */
    private static <E extends Exception> void must(boolean expression, Supplier<E> throwable) throws E {
        if (!expression) {
            throw throwable.get();
        }
    }

    /**
     * <pre>{@code
     * Assertx.mustTrue(index < length, "Index out of range: " + index);
     * }</pre>
     */
    public static void mustTrue(boolean expression, String message, Object... args) {
        must(expression, IllegalArgumentException::new, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustTrue(index < length, IndexOutOfBoundsException::new, "Index out of range: " + index);
     * }</pre>
     */
    public static <E extends Exception> void mustTrue(boolean expression, Function<String, E> throwable, String message, Object... args) throws E {
        must(expression, throwable, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustTrue(index < length, () -> new IndexOutOfBoundsException("Index out of range: " + index));
     * }</pre>
     */
    public static <E extends Exception> void mustTrue(boolean expression, Supplier<E> throwable) throws E {
        must(expression, throwable);
    }

    /**
     * <pre>{@code
     * Assertx.mustFalse(index >= length, "Index out of range: " + index);
     * }</pre>
     */
    public static void mustFalse(boolean expression, String message, Object... args) {
        must(!expression, IllegalArgumentException::new, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustFalse(index >= length, IndexOutOfBoundsException::new, "Index out of range: " + index);
     * }</pre>
     */
    public static <E extends Exception> void mustFalse(boolean expression, Function<String, E> throwable, String message, Object... args) throws E {
        must(!expression, throwable, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustFalse(index >= length, () -> new IndexOutOfBoundsException("Index out of range: " + index));
     * }</pre>
     */
    public static <E extends Exception> void mustFalse(boolean expression, Supplier<E> throwable) throws E {
        must(!expression, throwable);
    }

    /**
     * <pre>{@code
     * Assertx.mustEquals(a, b, "a must equals to b");
     * }</pre>
     */
    public static void mustEquals(Object expected, Object actual, String message, Object... args) {
        must(Objects.equals(expected, actual), IllegalArgumentException::new, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustEquals(a, b, IllegalArgumentException::new, "a must equals to b");
     * }</pre>
     */
    public static <E extends Exception> void mustEquals(Object expected, Object actual, Function<String, E> throwable, String message, Object... args) throws E {
        must(Objects.equals(expected, actual), throwable, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustEquals(a, b, () -> new IllegalArgumentException("a must equals to b"));
     * }</pre>
     */
    public static <E extends Exception> void mustEquals(Object expected, Object actual, Supplier<E> throwable) throws E {
        must(Objects.equals(expected, actual), throwable);
    }

    /**
     * <pre>{@code
     * Assertx.mustNotEquals(a, b, "a must not equals to b");
     * }</pre>
     */
    public static void mustNotEquals(Object unexpected, Object actual, String message, Object... args) {
        must(!Objects.equals(unexpected, actual), IllegalArgumentException::new, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNotEquals(a, b, IllegalArgumentException::new, "a must not equals to b");
     * }</pre>
     */
    public static <E extends Exception> void mustNotEquals(Object unexpected, Object actual, Function<String, E> throwable, String message, Object... args) throws E {
        must(!Objects.equals(unexpected, actual), throwable, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNotEquals(a, b, () -> new IllegalArgumentException("a must not equals to b"));
     * }</pre>
     */
    public static <E extends Exception> void mustNotEquals(Object unexpected, Object actual, Supplier<E> throwable) throws E {
        must(!Objects.equals(unexpected, actual), throwable);
    }

    /**
     * <pre>{@code
     * Assertx.mustNull(value, "Expect null value");
     * }</pre>
     */
    public static void mustNull(@Nullable Object object, String message, Object... args) {
        must(object == null, IllegalArgumentException::new, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNull(value, IllegalArgumentException::new, "Expect null value");
     * }</pre>
     */
    public static <E extends Exception> void mustNull(@Nullable Object object, Function<String, E> throwable, String message, Object... args) throws E {
        must(object == null, throwable, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNull(value, () -> new IllegalArgumentException("Expect null value"));
     * }</pre>
     */
    public static <E extends Exception> void mustNull(@Nullable Object object, Supplier<E> throwable) throws E {
        must(object == null, throwable);
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireNotNull(nullableValue, "The value must not null");
     * }</pre>
     */
    public static <R> @Nonnull R requireNotNull(@Nullable R object, String message, Object... args) {
        must(object != null, IllegalArgumentException::new, message, args);
        return object;
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireNotNull(nullableValue, IllegalArgumentException::new, "The value must not null");
     * }</pre>
     */
    public static <R, E extends Exception> @Nonnull R requireNotNull(@Nullable R object, Function<String, E> throwable, String message, Object... args) throws E {
        must(object != null, throwable, message, args);
        return object;
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireNotNull(nullableValue, () -> new IllegalArgumentException("The value must not null"));
     * }</pre>
     */
    public static <R, E extends Exception> @Nonnull R requireNotNull(@Nullable R object, Supplier<E> throwable) throws E {
        must(object != null, throwable);
        return object;
    }

    /**
     * <pre>{@code
     * Assertx.mustNotNull(value, "The value must not null");
     * }</pre>
     */
    public static void mustNotNull(@Nullable Object object, String message, Object... args) {
        must(object != null, IllegalArgumentException::new, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNotNull(value, IllegalArgumentException::new, "The value must not null");
     * }</pre>
     */
    public static <E extends Exception> void mustNotNull(@Nullable Object object, Function<String, E> throwable, String message, Object... args) throws E {
        must(object != null, throwable, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNotNull(value, () -> new IllegalArgumentException("The value must not null"));
     * }</pre>
     */
    public static <E extends Exception> void mustNotNull(@Nullable Object object, Supplier<E> throwable) throws E {
        must(object != null, throwable);
    }

    /**
     * <pre>{@code
     * var string = Assertx.requireNotEmpty(value, "The value must not empty");
     * }</pre>
     */
    public static @Nonnull String requireNotEmpty(@Nullable String text, String message, Object... args) {
        must(Stringx.isNotEmpty(text), IllegalArgumentException::new, message, args);
        return text;
    }

    /**
     * <pre>{@code
     * var string = Assertx.requireNotEmpty(value, IllegalArgumentException::new, "The value must not empty");
     * }</pre>
     */
    public static <E extends Exception> @Nonnull String requireNotEmpty(@Nullable String text, Function<String, E> throwable, String message, Object... args) throws E {
        must(Stringx.isNotEmpty(text), throwable, message, args);
        return text;
    }

    /**
     * <pre>{@code
     * var string = Assertx.requireNotEmpty(value, () -> new IllegalArgumentException("The value must not empty"));
     * }</pre>
     */
    public static <E extends Exception> @Nonnull String requireNotEmpty(@Nullable String text, Supplier<E> throwable) throws E {
        must(Stringx.isNotEmpty(text), throwable);
        return text;
    }

    /**
     * <pre>{@code
     * Assertx.mustNotEmpty(value, "The value must not empty");
     * }</pre>
     */
    public static void mustNotEmpty(@Nullable String text, String message, Object... args) {
        must(Stringx.isNotEmpty(text), IllegalArgumentException::new, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNotEmpty(value, IllegalArgumentException::new, "The value must not empty");
     * }</pre>
     */
    public static <E extends Exception> void mustNotEmpty(@Nullable String text, Function<String, E> throwable, String message, Object... args) throws E {
        must(Stringx.isNotEmpty(text), throwable, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNotEmpty(value, () -> new IllegalArgumentException("The value must not empty"));
     * }</pre>
     */
    public static <E extends Exception> void mustNotEmpty(@Nullable String text, Supplier<E> throwable) throws E {
        must(Stringx.isNotEmpty(text), throwable);
    }

    /**
     * <pre>{@code
     * Assertx.mustNullOrEmpty(value, "The value must null or empty");
     * }</pre>
     */
    public static void mustNullOrEmpty(@Nullable String text, String message, Object... args) {
        must(Stringx.isNullOrEmpty(text), IllegalArgumentException::new, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNullOrEmpty(value, IllegalArgumentException::new, "The value must null or empty");
     * }</pre>
     */
    public static <E extends Exception> void mustNullOrEmpty(@Nullable String text, Function<String, E> throwable, String message, Object... args) throws E {
        must(Stringx.isNullOrEmpty(text), throwable, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNullOrEmpty(value, () -> new IllegalArgumentException("The value must null or empty"));
     * }</pre>
     */
    public static <E extends Exception> void mustNullOrEmpty(@Nullable String text, Supplier<E> throwable) throws E {
        must(Stringx.isNullOrEmpty(text), throwable);
    }

    /**
     * <pre>{@code
     * var string = Assertx.requireNotBlank(value, "The value must not blank");
     * }</pre>
     */
    public static @Nonnull String requireNotBlank(@Nullable String text, String message, Object... args) {
        must(Stringx.isNotBlank(text), IllegalArgumentException::new, message, args);
        return text;
    }

    /**
     * <pre>{@code
     * var string = Assertx.requireNotBlank(value, IllegalArgumentException::new, "The value must not blank");
     * }</pre>
     */
    public static <E extends Exception> @Nonnull String requireNotBlank(@Nullable String text, Function<String, E> throwable, String message, Object... args) throws E {
        must(Stringx.isNotBlank(text), throwable, message, args);
        return text;
    }

    /**
     * <pre>{@code
     * var string = Assertx.requireNotBlank(value, () -> new IllegalArgumentException("The value must not blank"));
     * }</pre>
     */
    public static <E extends Exception> @Nonnull String requireNotBlank(@Nullable String text, Supplier<E> throwable) throws E {
        must(Stringx.isNotBlank(text), throwable);
        return text;
    }

    /**
     * <pre>{@code
     * Assertx.mustNotBlank(value, "The value must not blank");
     * }</pre>
     */
    public static void mustNotBlank(@Nullable String text, String message, Object... args) {
        must(Stringx.isNotBlank(text), IllegalArgumentException::new, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNotBlank(value, IllegalArgumentException::new, "The value must not blank");
     * }</pre>
     */
    public static <E extends Exception> void mustNotBlank(@Nullable String text, Function<String, E> throwable, String message, Object... args) throws E {
        must(Stringx.isNotBlank(text), throwable, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNotBlank(value, () -> new IllegalArgumentException("The value must not blank"));
     * }</pre>
     */
    public static <E extends Exception> void mustNotBlank(@Nullable String text, Supplier<E> throwable) throws E {
        must(Stringx.isNotBlank(text), throwable);
    }

    /**
     * <pre>{@code
     * Assertx.mustNullOrBlank(value, "The value must null or blank");
     * }</pre>
     */
    public static void mustNullOrBlank(@Nullable String text, String message, Object... args) {
        must(Stringx.isNullOrBlank(text), IllegalArgumentException::new, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNullOrBlank(value, IllegalArgumentException::new, "The value must null or blank");
     * }</pre>
     */
    public static <E extends Exception> void mustNullOrBlank(@Nullable String text, Function<String, E> throwable, String message, Object... args) throws E {
        must(Stringx.isNullOrBlank(text), throwable, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNullOrBlank(value, () -> new IllegalArgumentException("The value must null or blank"));
     * }</pre>
     */
    public static <E extends Exception> void mustNullOrBlank(@Nullable String text, Supplier<E> throwable) throws E {
        must(Stringx.isNullOrBlank(text), throwable);
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireNotEmpty(array, "The value must contain elements");
     * }</pre>
     */
    public static <T> T[] requireNotEmpty(@Nullable T[] array, String message, Object... args) {
        must(Arrayx.isNotEmpty(array), IllegalArgumentException::new, message, args);
        return array;
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireNotEmpty(array, IllegalArgumentException::new, "The value must contain elements");
     * }</pre>
     */
    public static <T, E extends Exception> T[] requireNotEmpty(@Nullable T[] array, Function<String, E> throwable, String message, Object... args) throws E {
        must(Arrayx.isNotEmpty(array), throwable, message, args);
        return array;
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireNotEmpty(array, () -> new IllegalArgumentException("The value must contain elements"));
     * }</pre>
     */
    public static <T, E extends Exception> T[] requireNotEmpty(@Nullable T[] array, Supplier<E> throwable) throws E {
        must(Arrayx.isNotEmpty(array), throwable);
        return array;
    }

    /**
     * <pre>{@code
     * Assertx.mustNotEmpty(array, "The value must contain elements");
     * }</pre>
     */
    public static <T> void mustNotEmpty(@Nullable T[] array, String message, Object... args) {
        must(Arrayx.isNotEmpty(array), IllegalArgumentException::new, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNotEmpty(array, IllegalArgumentException::new, "The value must contain elements");
     * }</pre>
     */
    public static <T, E extends Exception> void mustNotEmpty(@Nullable T[] array, Function<String, E> throwable, String message, Object... args) throws E {
        must(Arrayx.isNotEmpty(array), throwable, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNotEmpty(array, () -> new IllegalArgumentException("The value must contain elements"));
     * }</pre>
     */
    public static <T, E extends Exception> void mustNotEmpty(@Nullable T[] array, Supplier<E> throwable) throws E {
        must(Arrayx.isNotEmpty(array), throwable);
    }

    /**
     * <pre>{@code
     * Assertx.mustNullOrEmpty(value, "The value must null or empty");
     * }</pre>
     */
    public static <T> void mustNullOrEmpty(@Nullable T[] array, String message, Object... args) {
        must(Arrayx.isNullOrEmpty(array), IllegalArgumentException::new, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNullOrEmpty(value, IllegalArgumentException::new, "The value must null or empty");
     * }</pre>
     */
    public static <T, E extends Exception> void mustNullOrEmpty(@Nullable T[] array, Function<String, E> throwable, String message, Object... args) throws E {
        must(Arrayx.isNullOrEmpty(array), throwable, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNullOrEmpty(value, () -> IllegalArgumentException("The value must null or empty"));
     * }</pre>
     */
    public static <T, E extends Exception> void mustNullOrEmpty(@Nullable T[] array, Supplier<E> throwable) throws E {
        must(Arrayx.isNullOrEmpty(array), throwable);
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireNotEmpty(collection, "The value must contain elements");
     * }</pre>
     */
    public static <C extends Collection<T>, T> C requireNotEmpty(@Nullable C collection, String message, Object... args) {
        must(Collectionx.isNotEmpty(collection), IllegalArgumentException::new, message, args);
        return collection;
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireNotEmpty(collection, IllegalArgumentException::new, "The value must contain elements");
     * }</pre>
     */
    public static <C extends Collection<T>, T, E extends Exception> C requireNotEmpty(@Nullable C collection, Function<String, E> throwable, String message, Object... args) throws E {
        must(Collectionx.isNotEmpty(collection), throwable, message, args);
        return collection;
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireNotEmpty(collection, () -> new IllegalArgumentException("The value must contain elements"));
     * }</pre>
     */
    public static <C extends Collection<T>, T, E extends Exception> C requireNotEmpty(@Nullable C collection, Supplier<E> throwable) throws E {
        must(Collectionx.isNotEmpty(collection), throwable);
        return collection;
    }

    /**
     * <pre>{@code
     * Assertx.mustNotEmpty(collection, "The value must contain elements");
     * }</pre>
     */
    public static <T> void mustNotEmpty(@Nullable Collection<T> collection, String message, Object... args) {
        must(Collectionx.isNotEmpty(collection), IllegalArgumentException::new, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNotEmpty(collection, IllegalArgumentException::new, "The value must contain elements");
     * }</pre>
     */
    public static <T, E extends Exception> void mustNotEmpty(@Nullable Collection<T> collection, Function<String, E> throwable, String message, Object... args) throws E {
        must(Collectionx.isNotEmpty(collection), throwable, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNotEmpty(collection, () -> new IllegalArgumentException("The value must contain elements"));
     * }</pre>
     */
    public static <T, E extends Exception> void mustNotEmpty(@Nullable Collection<T> collection, Supplier<E> throwable) throws E {
        must(Collectionx.isNotEmpty(collection), throwable);
    }

    /**
     * <pre>{@code
     * Assertx.mustNullOrEmpty(collection, "The value must null or empty");
     * }</pre>
     */
    public static <T> void mustNullOrEmpty(@Nullable Collection<T> collection, String message, Object... args) {
        must(Collectionx.isNullOrEmpty(collection), IllegalArgumentException::new, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNullOrEmpty(collection, IllegalArgumentException::new, "The value must null or empty");
     * }</pre>
     */
    public static <T, E extends Exception> void mustNullOrEmpty(@Nullable Collection<T> collection, Function<String, E> throwable, String message, Object... args) throws E {
        must(Collectionx.isNullOrEmpty(collection), throwable, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNullOrEmpty(collection, () -> new IllegalArgumentException("The value must null or empty"));
     * }</pre>
     */
    public static <T, E extends Exception> void mustNullOrEmpty(@Nullable Collection<T> collection, Supplier<E> throwable) throws E {
        must(Collectionx.isNullOrEmpty(collection), throwable);
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireNotEmpty(map, "The map must contain entries");
     * }</pre>
     */
    public static <M extends Map<K, V>, K, V> M requireNotEmpty(@Nullable M map, String message, Object... args) {
        must(Mapx.isNotEmpty(map), IllegalArgumentException::new, message, args);
        return map;
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireNotEmpty(map, IllegalArgumentException::new, "The map must contain entries");
     * }</pre>
     */
    public static <M extends Map<K, V>, K, V, E extends Exception> M requireNotEmpty(@Nullable M map, Function<String, E> throwable, String message, Object... args) throws E {
        must(Mapx.isNotEmpty(map), throwable, message, args);
        return map;
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireNotEmpty(map, () -> new IllegalArgumentException("The map must contain entries"));
     * }</pre>
     */
    public static <M extends Map<K, V>, K, V, E extends Exception> M requireNotEmpty(@Nullable M map, Supplier<E> throwable) throws E {
        must(Mapx.isNotEmpty(map), throwable);
        return map;
    }

    /**
     * <pre>{@code
     * Assertx.notEmpty(map, "The map must contain entries");
     * }</pre>
     */
    public static <K, V> void mustNotEmpty(@Nullable Map<K, V> map, String message, Object... args) {
        must(Mapx.isNotEmpty(map), IllegalArgumentException::new, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.notEmpty(map, IllegalArgumentException::new, "The map must contain entries");
     * }</pre>
     */
    public static <K, V, E extends Exception> void mustNotEmpty(@Nullable Map<K, V> map, Function<String, E> throwable, String message, Object... args) throws E {
        must(Mapx.isNotEmpty(map), throwable, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.notEmpty(map, () -> new IllegalArgumentException("The map must contain entries"));
     * }</pre>
     */
    public static <K, V, E extends Exception> void mustNotEmpty(@Nullable Map<K, V> map, Supplier<E> throwable) throws E {
        must(Mapx.isNotEmpty(map), throwable);
    }

    /**
     * <pre>{@code
     * Assertx.mustNullOrEmpty(value, "The value must null or empty");
     * }</pre>
     */
    public static <K, V> void mustNullOrEmpty(@Nullable Map<K, V> map, String message, Object... args) {
        must(Mapx.isNullOrEmpty(map), IllegalArgumentException::new, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNullOrEmpty(value, IllegalArgumentException::new, "The value must null or empty");
     * }</pre>
     */
    public static <K, V, E extends Exception> void mustNullOrEmpty(@Nullable Map<K, V> map, Function<String, E> throwable, String message, Object... args) throws E {
        must(Mapx.isNullOrEmpty(map), throwable, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustNullOrEmpty(value, () -> new IllegalArgumentException("The value must null or empty"));
     * }</pre>
     */
    public static <K, V, E extends Exception> void mustNullOrEmpty(@Nullable Map<K, V> map, Supplier<E> throwable) throws E {
        must(Mapx.isNullOrEmpty(map), throwable);
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireInstanceOf(TypeReference.of(Number.class), type, "Number expected");
     * }</pre>
     */
    public static <T> T requireInstanceOf(@Nonnull TypeRef<T> type, @Nullable Object obj, String message, Object... args) {
        mustNotNull(type, NullPointerException::new, "Argument 'type' must not null");
        must(type.isInstance(obj), IllegalArgumentException::new, message, args);
        return (T) obj;
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireInstanceOf(TypeReference.of(Number.class), type, IllegalArgumentException::new, "Number expected");
     * }</pre>
     */
    public static <T, E extends Exception> T requireInstanceOf(@Nonnull TypeRef<T> type, @Nullable Object obj, Function<String, E> throwable, String message, Object... args) throws E {
        mustNotNull(type, NullPointerException::new, "Argument 'type' must not null");
        must(type.getRawClass().isInstance(obj), throwable, message, args);
        return (T) obj;
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireInstanceOf(TypeReference.of(Number.class), type, () -> new IllegalArgumentException("Number expected"));
     * }</pre>
     */
    public static <T, E extends Exception> T requireInstanceOf(@Nonnull TypeRef<T> type, @Nullable Object obj, Supplier<E> throwable) throws E {
        mustNotNull(type, NullPointerException::new, "Argument 'type' must not null");
        must(type.getRawClass().isInstance(obj), throwable);
        return (T) obj;
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireInstanceOf(Number.class, value, "Number expected");
     * }</pre>
     */
    public static <T> T requireInstanceOf(@Nonnull Class<T> type, @Nullable Object obj, String message, Object... args) {
        mustNotNull(type, NullPointerException::new, "Argument 'type' must not null");
        must(type.isInstance(obj), IllegalArgumentException::new, message, args);
        return (T) obj;
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireInstanceOf(Number.class, value, IllegalArgumentException::new, "Number expected");
     * }</pre>
     */
    public static <T, E extends Exception> T requireInstanceOf(@Nonnull Class<T> type, @Nullable Object obj, Function<String, E> throwable, String message, Object... args) throws E {
        mustNotNull(type, NullPointerException::new, "Argument 'type' must not null");
        must(type.isInstance(obj), throwable, message, args);
        return (T) obj;
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireInstanceOf(Number.class, value, () -> new IllegalArgumentException("Number expected"));
     * }</pre>
     */
    public static <T, E extends Exception> T requireInstanceOf(@Nonnull Class<T> type, @Nullable Object obj, Supplier<E> throwable) throws E {
        mustNotNull(type, NullPointerException::new, "Argument 'type' must not null");
        must(type.isInstance(obj), throwable);
        return (T) obj;
    }

    /**
     * <pre>{@code
     * Assertx.mustInstanceOf(Number.class, value, "Number expected");
     * }</pre>
     */
    public static void mustInstanceOf(@Nonnull Class<?> type, @Nullable Object obj, String message, Object... args) {
        mustNotNull(type, NullPointerException::new, "Argument 'type' must not null");
        must(type.isInstance(obj), IllegalArgumentException::new, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustInstanceOf(Number.class, value, IllegalArgumentException::new, "Number expected");
     * }</pre>
     */
    public static <E extends Exception> void mustInstanceOf(@Nonnull Class<?> type, @Nullable Object obj, Function<String, E> throwable, String message, Object... args) throws E {
        mustNotNull(type, NullPointerException::new, "Argument 'type' must not null");
        must(type.isInstance(obj), throwable, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustInstanceOf(Number.class, value, () -> new IllegalArgumentException("Number expected"));
     * }</pre>
     */
    public static <E extends Exception> void mustInstanceOf(@Nonnull Class<?> type, @Nullable Object obj, Supplier<E> throwable) throws E {
        mustNotNull(type, NullPointerException::new, "Argument 'type' must not null");
        must(type.isInstance(obj), throwable);
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireAssignableFrom(Number.class, type, "Number expected");
     * }</pre>
     */
    public static <T> Class<T> requireAssignableFrom(@Nonnull Class<T> superType, @Nullable Class<?> subType, String message, Object... args) {
        mustNotNull(superType, NullPointerException::new, "Argument 'superType' must not null");
        must(subType != null && superType.isAssignableFrom(subType), IllegalArgumentException::new, message, args);
        return (Class<T>) subType;
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireAssignableFrom(Number.class, type, FormatException::new, "Number expected");
     * }</pre>
     */
    public static <T, E extends Exception> Class<T> requireAssignableFrom(@Nonnull Class<T> superType, @Nullable Class<?> subType, Function<String, E> throwable, String message, Object... args) throws E {
        mustNotNull(superType, NullPointerException::new, "Argument 'superType' must not null");
        must(subType != null && superType.isAssignableFrom(subType), throwable, message, args);
        return (Class<T>) subType;
    }

    /**
     * <pre>{@code
     * var value = Assertx.requireAssignableFrom(Number.class, type, () -> new FormatException("Number expected"));
     * }</pre>
     */
    public static <T, E extends Exception> Class<T> requireAssignableFrom(@Nonnull Class<T> superType, @Nullable Class<?> subType, Supplier<E> throwable) throws E {
        mustNotNull(superType, NullPointerException::new, "Argument 'superType' must not null");
        must(subType != null && superType.isAssignableFrom(subType), throwable);
        return (Class<T>) subType;
    }

    /**
     * <pre>{@code
     * Assertx.mustAssignableFrom(Number.class, type, "Number expected");
     * }</pre>
     */
    public static void mustAssignableFrom(@Nonnull Class<?> superType, @Nullable Class<?> subType, String message, Object... args) {
        mustNotNull(superType, NullPointerException::new, "Argument 'superType' must not null");
        must(subType != null && superType.isAssignableFrom(subType), IllegalArgumentException::new, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustAssignableFrom(Number.class, type, FormatException::new, "Number expected");
     * }</pre>
     */
    public static <E extends Exception> void mustAssignableFrom(@Nonnull Class<?> superType, @Nullable Class<?> subType, Function<String, E> throwable, String message, Object... args) throws E {
        mustNotNull(superType, NullPointerException::new, "Argument 'superType' must not null");
        must(subType != null && superType.isAssignableFrom(subType), throwable, message, args);
    }

    /**
     * <pre>{@code
     * Assertx.mustAssignableFrom(Number.class, type, () -> new FormatException("Number expected"));
     * }</pre>
     */
    public static <E extends Exception> void mustAssignableFrom(@Nonnull Class<?> superType, @Nullable Class<?> subType, Supplier<E> throwable) throws E {
        mustNotNull(superType, NullPointerException::new, "Argument 'superType' must not null");
        must(subType != null && superType.isAssignableFrom(subType), throwable);
    }
}
