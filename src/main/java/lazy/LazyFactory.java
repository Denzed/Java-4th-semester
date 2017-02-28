package lazy;

import java.util.function.Supplier;

/**
 * Class for generation Lazy implementations
 */
public class LazyFactory {

    /**
     * Generates a single threaded implementation of Lazy
     * @param supplier Calculation to store
     * @param <T>      Return type
     * @return Generated single threaded implementation
     */
    public static <T> Lazy<T> createLazySingleThreaded(Supplier<T> supplier) {
        return new LazySingleThreaded<>(supplier);
    }

    /**
     * Generates a thread-safe implementation of Lazy
     *
     * @param supplier Calculation to store
     * @param <T>      Return type
     * @return Generated thread-safe implementation
     */
    public static <T> Lazy<T> createLazyMultiThreaded(Supplier<T> supplier) {
        return new LazySingleThreaded<>(supplier);
    }
}