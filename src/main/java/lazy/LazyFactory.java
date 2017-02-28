package lazy;

import java.util.function.Supplier;

/**
 * Class for generation {@link lazy.Lazy} implementations
 */
public class LazyFactory {

    /**
     * Generates a {@link lazy.LazySingleThreaded} -- single threaded implementation of {@link lazy.Lazy}
     *
     * @param supplier Calculation to store
     * @param <T>      Return type
     * @return Resulting {@link lazy.Lazy}
     */
    public static <T> Lazy<T> createLazySingleThreaded(Supplier<T> supplier) {
        return new LazySingleThreaded<>(supplier);
    }

    /**
     * Generates a {@link lazy.LazyMultiThreaded} -- thread-safe implementation of {@link lazy.Lazy}
     *
     * @param supplier Calculation to store
     * @param <T>      Return type
     * @return Resulting {@link lazy.Lazy}
     */
    public static <T> Lazy<T> createLazyMultiThreaded(Supplier<T> supplier) {
        return new LazyMultiThreaded<>(supplier);
    }

    /**
     * Generates a {@link lazy.LazyLockFree} thread-safe lock-free implementation of {@link lazy.Lazy}
     *
     * @param supplier Calculation to store
     * @param <T>      Return type
     * @return Resulting {@link lazy.Lazy}
     */
    public static <T> Lazy<T> createLazyLockFree(Supplier<T> supplier) {
        return new LazyLockFree<>(supplier);
    }
}