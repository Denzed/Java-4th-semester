package lazy;

import java.util.function.Supplier;

/**
 * Class for generation Lazy implementations
 */
public class LazyFactory {

    /**
     * Generates a single threaded implementations of Lazy
     *
     * @param supplier Calculation to store
     * @param <T>      Return type
     * @return Generated single threaded implementation
     */
    public static <T> Lazy<T> createLazySingleThreaded(Supplier<T> supplier) {
        return new LazySingleThreaded<>(supplier);
    }
}