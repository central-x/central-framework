package central.util;

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
    public static <T extends Exception> void must(boolean expression, Supplier<T> throwable) throws T {
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

    public static <T extends Exception> void mustTrue(boolean expression, Supplier<T> throwable) throws T {
        must(expression, throwable);
    }

    /**
     * Assertx.mustEquals(a, b, "a must equals to b");
     */
    public static void mustEquals(Object a, Object b, String message, Object... args) {
        must(Objects.equals(a, b), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNotEquals(a, b, "a must not equals to b");
     */
    public static void mustNotEquals(Object a, Object b, String message, Object... args) {
        must(!Objects.equals(a, b), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNull(value, "The value must be null");
     */
    public static void mustNull(@Nullable Object object, String message, Object... args) {
        must(object == null, () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    public static <T extends Exception> void mustNull(@Nullable Object object, Supplier<T> supplier) throws T {
        must(object == null, supplier);
    }

    /**
     * Assertx.mustNotNull(value, "The value must not be null");
     */
    public static void mustNotNull(@Nullable Object object, String message, Object... args) {
        must(object != null, () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNotNull("name", value);
     */
    public static void mustNotNull(String parameter, Object object) {
        must(object != null, () -> new IllegalArgumentException(Stringx.format("Argument '{}' must not null", parameter)));
    }

    /**
     * Assertx.mustNullOrEmpty(value, "The value must be null or empty")
     */
    public static void mustNullOrEmpty(@Nullable String text, String message, Object... args) {
        must(Stringx.isNullOrEmpty(text), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNotEmpty(value, "The value must not empty");
     */
    public static void mustNotEmpty(@Nullable String text, String message, Object... args) {
        must(!Stringx.isNullOrEmpty(text), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNullOrBlank(value, "The value must null or blank")
     */
    public static void mustNullOrBlank(@Nullable String text, String message, Object... args) {
        must(Stringx.isNullOrBlank(text), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNotBlank(value, "The value must not blank");
     */
    public static void mustNotBlank(@Nullable String text, String message, Object... args) {
        must(!Stringx.isNullOrBlank(text), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNullOrEmpty(value, "The value must be null or empty")
     */
    public static <T> void mustNullOrEmpty(@Nullable T[] array, String message, Object... args) {
        must(Arrayx.isNullOrEmpty(array), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNotEmpty(array, "The value must contain elements");
     */
    public static <T> void mustNotEmpty(@Nullable T[] array, String message, Object... args) {
        must(Arrayx.isNotEmpty(array), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNullOrEmpty(value, "The value must be null or empty")
     */
    public static <T> void mustNullOrEmpty(@Nullable Collection<T> collection, String message, Object... args) {
        must(Collectionx.isNullOrEmpty(collection), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNotEmpty(array, "The value must contain elements");
     */
    public static void mustNotEmpty(@Nullable Collection<?> collection, String message, Object... args) {
        must(Collectionx.isNotEmpty(collection), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustNullOrEmpty(value, "The value must be null or empty")
     */
    public static <K, V> void mustNullOrEmpty(@Nullable Map<K, V> map, String message, Object... args) {
        must(Mapx.isNullOrEmpty(map), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.notEmpty(map, "The map must contain entries");
     */
    public static <K, V> void mustNotEmpty(@Nullable Map<K, V> map, String message, Object... args) {
        must(Mapx.isNotEmpty(map), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustInstanceOf(Number.class, myClass, "Number expected");
     */
    public static void mustInstanceOf(@Nonnull Class<?> type, @Nullable Object obj, String message, Object... args) {
        mustNotNull(type, "Argument 'type' must not null");
        must(type.isInstance(obj), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    public static <E extends Exception> void mustInstanceOf(@Nonnull Class<?> type, @Nullable Object obj, Supplier<E> throwable) throws E {
        mustNotNull(type, "Argument 'type' must not null");
        must(type.isInstance(obj), throwable);
    }

    /**
     * Assertx.mustAssignableFrom(Number.class, myClass, "Number expected");
     */
    public static void mustAssignableFrom(@Nonnull Class<?> superType, @Nullable Class<?> subType, String message, Object... args) {
        mustNotNull(superType, "Argument 'superType' must not null");
        must(subType != null && superType.isAssignableFrom(subType), () -> new IllegalArgumentException(Stringx.format(message, args)));
    }

    /**
     * Assertx.mustAssignableFrom(Number.class, myClass, () -> new FormatException("Number expected"));
     */
    public static <T extends Exception> void mustAssignableFrom(@Nonnull Class<?> superType, @Nullable Class<?> subType, Supplier<T> throwable) throws T {
        mustNotNull(superType, "Argument 'superType' must not null");
        must(subType != null && superType.isAssignableFrom(subType), throwable);
    }
}
