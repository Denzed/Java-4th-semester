package lazy;


import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Lock-free thread-safe {@link lazy.Lazy} implementation
 *
 * @param <T> Return type
 */
class LazyLockFree<T> implements Lazy<T> {
    private AtomicReference<Supplier<T>> supplierReference;
    private AtomicReference<T> resultReference = new AtomicReference<>();

    LazyLockFree(Supplier<T> supplier) {
        this.supplierReference = new AtomicReference<>(supplier);
    }

    /**
     * Perform calculations in a thread-safe way without locks and return the resultReference.
     * Subsequent calls are guaranteed to return the same object.
     *
     * @return Calculation result
     */
    public T get() {
        Supplier<T> supplier = supplierReference.get();
        if (supplier != null) {
            resultReference.compareAndSet(null, supplier.get());
            supplierReference.set(null);
        }
        return resultReference.get();
    }
}
