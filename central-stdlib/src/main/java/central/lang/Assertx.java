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

import central.util.Arrayx;
import central.util.Collectionx;
import central.util.Mapx;
import central.util.Stringx;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 断言工具
 *
 * @author Alan Yeh
 * @since 2022/07/05
 */
public class Assertx {

    /**
     * Assertx.must(b instance Integer, () -> new ClassCastException("Cannot case 'b' to java.lang.Integer"));
     */
    public static <E extends Exception> void must(boolean expression, Supplier<E> throwable) throws E {
        if (!expression) {
            throw throwable.get();
        }
    }

    /**
     * Assertx.mustTrue(i > 0, "The value must be greater than zero");
     */
    public static void mustTrue(boolean expression, String message, Object... args) {
        must(expression, () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustTrue(index > length, () -> new IndexOutOfBoundsException(index));
     */
    public static <E extends Exception> void mustTrue(boolean expression, Supplier<E> throwable) throws E {
        must(expression, throwable);
    }

    /**
     * Assertx.mustEquals(a, b, "a must equals to b");
     */
    public static void mustEquals(Object expected, Object actual, String message, Object... args) {
        must(Objects.equals(expected, actual), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustEquals(a, b, () -> new IllegalArgumentException("Expected same object"))
     */
    public static <E extends Exception> void mustEquals(Object expected, Object actual, Supplier<E> throwable) throws E {
        must(Objects.equals(expected, actual), throwable);
    }

    /**
     * Assertx.mustNotEquals(a, b, "a must not equals to b");
     */
    public static void mustNotEquals(Object unexpected, Object actual, String message, Object... args) {
        must(!Objects.equals(unexpected, actual), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNotEquals(a, b, () -> new IllegalArgumentException("Unexpected same object"))
     */
    public static <E extends Exception> void mustNotEquals(Object unexpected, Object actual, Supplier<E> throwable) throws E {
        must(!Objects.equals(unexpected, actual), throwable);
    }

    /**
     * Assertx.mustNull(value, "The value must be null");
     */
    public static void mustNull(@Nullable Object object, String message, Object... args) {
        must(object == null, () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNull(value, () -> new IllegalArgumentException("Unexpected same object"))
     */
    public static <E extends Exception> void mustNull(@Nullable Object object, Supplier<E> throwable) throws E {
        must(object == null, throwable);
    }

    /**
     * Assertx.mustNotNull(value, "The value must not be null");
     */
    public static void mustNotNull(@Nullable Object object, String message, Object... args) {
        must(object != null, () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNotNull(value, () -> new IllegalArgumentException("The value must not be null"))
     */
    public static <E extends Exception> void mustNotNull(@Nullable Object object, Supplier<E> throwable) throws E {
        must(object != null, throwable);
    }

//    /**
//     * Assertx.mustNotNull("name", value);
//     */
//    public static void mustNotNull(String parameter, Object object) {
//        must(object != null, () -> new IllegalArgumentException(Stringx.format("Argument '{}' must not null", parameter)));
//    }

    /**
     * Assertx.mustNotEmpty(value, "The value must not empty");
     */
    public static void mustNotEmpty(@Nullable String text, String message, Object... args) {
        must(Stringx.isNotEmpty(text), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNotEmpty(value, () -> new IllegalArgumentException("The value must not empty"))
     */
    public static <E extends Exception> void mustNotEmpty(@Nullable String text, Supplier<E> throwable) throws E {
        must(Stringx.isNotEmpty(text), throwable);
    }

    /**
     * Assertx.mustNullOrEmpty(value, "The value must be null or empty")
     */
    public static void mustNullOrEmpty(@Nullable String text, String message, Object... args) {
        must(Stringx.isNullOrEmpty(text), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNullOrEmpty(value, () -> new IllegalArgumentException("The value must be null or empty"))
     */
    public static <E extends Exception> void mustNullOrEmpty(@Nullable String text, Supplier<E> throwable) throws E {
        must(Stringx.isNullOrEmpty(text), throwable);
    }

    /**
     * Assertx.mustNotBlank(value, "The value must not blank");
     */
    public static void mustNotBlank(@Nullable String text, String message, Object... args) {
        must(Stringx.isNotBlank(text), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNotBlank(value, new IllegalArgumentException("The value must not blank"));
     */
    public static <E extends Exception> void mustNotBlank(@Nullable String text, Supplier<E> throwable) throws E {
        must(Stringx.isNotBlank(text), throwable);
    }

    /**
     * Assertx.mustNullOrBlank(value, "The value must null or blank")
     */
    public static void mustNullOrBlank(@Nullable String text, String message, Object... args) {
        must(Stringx.isNullOrBlank(text), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNullOrBlank(value, () -> new IllegalArgumentException("The value must null or blank"))
     */
    public static <E extends Exception> void mustNullOrBlank(@Nullable String text, Supplier<E> throwable) throws E {
        must(Stringx.isNullOrBlank(text), throwable);
    }

    /**
     * Assertx.mustNotEmpty(array, "The value must contain elements");
     */
    public static <T> void mustNotEmpty(@Nullable T[] array, String message, Object... args) {
        must(Arrayx.isNotEmpty(array), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNotEmpty(array, () -> new IllegalArgumentException("The value must contain elements"));
     */
    public static <T, E extends Exception> void mustNotEmpty(@Nullable T[] array, Supplier<E> throwable) throws E {
        must(Arrayx.isNotEmpty(array), throwable);
    }

    /**
     * Assertx.mustNullOrEmpty(value, "The value must be null or empty")
     */
    public static <T> void mustNullOrEmpty(@Nullable T[] array, String message, Object... args) {
        must(Arrayx.isNullOrEmpty(array), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNullOrEmpty(value, () -> new IllegalArgumentException("The value must be null or empty")）
     */
    public static <T, E extends Exception> void mustNullOrEmpty(@Nullable T[] array, Supplier<E> throwable) throws E {
        must(Arrayx.isNullOrEmpty(array), throwable);
    }

    /**
     * Assertx.mustNotEmpty(array, "The value must contain elements");
     */
    public static <T> void mustNotEmpty(@Nullable Collection<T> collection, String message, Object... args) {
        must(Collectionx.isNotEmpty(collection), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNotEmpty(array, () -> new IllegalArgumentException("The value must contain elements"))
     */
    public static <T, E extends Exception> void mustNotEmpty(@Nullable Collection<T> collection, Supplier<E> throwable) throws E {
        must(Collectionx.isNotEmpty(collection), throwable);
    }

    /**
     * Assertx.mustNullOrEmpty(value, "The value must be null or empty")
     */
    public static <T> void mustNullOrEmpty(@Nullable Collection<T> collection, String message, Object... args) {
        must(Collectionx.isNullOrEmpty(collection), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNullOrEmpty(value, () -> new IllegalArgumentException("The value must be null or empty"))
     */
    public static <T, E extends Exception> void mustNullOrEmpty(@Nullable Collection<T> collection, Supplier<E> throwable) throws E {
        must(Collectionx.isNullOrEmpty(collection), throwable);
    }

    /**
     * Assertx.notEmpty(map, "The map must contain entries");
     */
    public static <K, V> void mustNotEmpty(@Nullable Map<K, V> map, String message, Object... args) {
        must(Mapx.isNotEmpty(map), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.notEmpty(map, () -> new IllegalArgumentException("The map must contain entries"))
     */
    public static <K, V, E extends Exception> void mustNotEmpty(@Nullable Map<K, V> map, Supplier<E> throwable) throws E {
        must(Mapx.isNotEmpty(map), throwable);
    }

    /**
     * Assertx.mustNullOrEmpty(value, "The value must be null or empty")
     */
    public static <K, V> void mustNullOrEmpty(@Nullable Map<K, V> map, String message, Object... args) {
        must(Mapx.isNullOrEmpty(map), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNullOrEmpty(value, () -> new IllegalArgumentException("The value must be null or empty"))
     */
    public static <K, V, E extends Exception> void mustNullOrEmpty(@Nullable Map<K, V> map, Supplier<E> throwable) throws E {
        must(Mapx.isNullOrEmpty(map), throwable);
    }

    /**
     * Assertx.mustInstanceOf(Number.class, myClass, "Number expected");
     */
    public static void mustInstanceOf(@Nonnull Class<?> type, @Nullable Object obj, String message, Object... args) {
        mustNotNull(type, () -> new NullPointerException("Argument 'type' must not null"));
        must(type.isInstance(obj), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    public static <E extends Exception> void mustInstanceOf(@Nonnull Class<?> type, @Nullable Object obj, Supplier<E> throwable) throws E {
        mustNotNull(type, () -> new NullPointerException("Argument 'type' must not null"));
        must(type.isInstance(obj), throwable);
    }

    /**
     * Assertx.mustAssignableFrom(Number.class, myClass, "Number expected");
     */
    public static void mustAssignableFrom(@Nonnull Class<?> superType, @Nullable Class<?> subType, String message, Object... args) {
        mustNotNull(superType, () -> new NullPointerException("Argument 'superType' must not null"));
        must(subType != null && superType.isAssignableFrom(subType), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustAssignableFrom(Number.class, myClass, () -> new FormatException("Number expected"));
     */
    public static <E extends Exception> void mustAssignableFrom(@Nonnull Class<?> superType, @Nullable Class<?> subType, Supplier<E> throwable) throws E {
        mustNotNull(superType, () -> new NullPointerException("Argument 'superType' must not null"));
        must(subType != null && superType.isAssignableFrom(subType), throwable);
    }
}
