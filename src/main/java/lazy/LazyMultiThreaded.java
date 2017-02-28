package lazy;

import java.util.function.Supplier;

/**
 * Thread-safe {@link lazy.Lazy} implementation
 *
 * @param <T> Return type
 */
class LazyMultiThreaded<T> implements Lazy<T> {
    private Supplier<T> supplier;
    private volatile T result = null;

    LazyMultiThreaded(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Perform calculations in a thread-safe way once and return the result.
     * Subsequent calls are guaranteed to return the same object.
     *
     * @return Calculation result
     */
    public T get() {
        if (supplier != null && result == null) {
            synchronized (this) {
                if (result == null) {
                    result = supplier.get();
                    supplier = null;
                }
            }
        }
        return result;
    }
}
