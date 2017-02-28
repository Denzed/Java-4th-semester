package lazy;


import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Lock-free thread-safe {@link lazy.Lazy} implementation
 *
 * @param <T> Return type
 */
class LazyLockFree<T> implements Lazy<T> {
    private Supplier<T> supplier;
    private AtomicReference<T> result = new AtomicReference<>();

    LazyLockFree(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Perform calculations in a thread-safe way once (and without locks) and return the result.
     * Subsequent calls are guaranteed to return the same object.
     *
     * @return Calculation result
     */
    public T get() {
        if (supplier != null) {
            result.compareAndSet(null, supplier.get());
            supplier = null;
        }
        return result.get();
    }
}
