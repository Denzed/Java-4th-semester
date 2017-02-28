package lazy;

import java.util.function.Supplier;

/**
 * Single threaded {@link lazy.Lazy} implementation
 *
 * @param <T> Return type
 */
class LazySingleThreaded<T> implements Lazy<T> {
    private Supplier<T> supplier;
    private T result = null;

    LazySingleThreaded(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Perform single threaded calculations once and return the result.
     * Subsequent calls are guaranteed to return the same object.
     * @return Calculation result
     */
    public T get() {
        if (supplier != null) {
            result = supplier.get();
            supplier = null;
        }
        return result;
    }
}