package lazy;

import java.util.function.Supplier;

/**
 * Class for generation of various {@link lazy.Lazy} implementations
 */
public final class LazyFactory {
    private LazyFactory() {
    }

    /**
     * Generates an instance of {@link lazy.LazySingleThreaded} -- single threaded implementation of {@link lazy.Lazy}
     *
     * @param supplier Calculation to store
     * @param <T>      Return type
     * @return the instance of single threaded implementation of {@link lazy.Lazy}
     */
    public static <T> Lazy<T> createLazySingleThreaded(Supplier<T> supplier) {
        return new LazySingleThreaded<>(supplier);
    }

    /**
     * Generates an instance of  {@link lazy.LazyMultiThreaded} -- thread-safe implementation of {@link lazy.Lazy}
     *
     * @param supplier Calculation to store
     * @param <T>      Return type
     * @return the instance of thread-safe implementation of {@link lazy.Lazy}
     */
    public static <T> Lazy<T> createLazyMultiThreaded(Supplier<T> supplier) {
        return new LazyMultiThreaded<>(supplier);
    }

    /**
     * Generates an instance of  {@link lazy.LazyLockFree} -- thread-safe lock-free implementation of {@link lazy.Lazy}
     *
     * @param supplier Calculation to store
     * @param <T>      Return type
     * @return the instance of thread-safe lock-free implementation of  {@link lazy.Lazy}
     */
    public static <T> Lazy<T> createLazyLockFree(Supplier<T> supplier) {
        return new LazyLockFree<>(supplier);
    }
}