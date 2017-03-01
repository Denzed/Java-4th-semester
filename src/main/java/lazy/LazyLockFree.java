package lazy;

import javax.xml.ws.Holder;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;

/**
 * Lock-free thread-safe {@link lazy.Lazy} implementation
 *
 * @param <T> Return type
 */
class LazyLockFree<T> implements Lazy<T> {
    private final Supplier<T> supplier;
    private volatile Holder<T> result;

    private static final AtomicReferenceFieldUpdater<LazyLockFree, Holder> resultUpdater =
            AtomicReferenceFieldUpdater.newUpdater(LazyLockFree.class, Holder.class, "result");

    LazyLockFree(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Perform calculations in a thread-safe way without locks and return the result.
     * Subsequent calls are guaranteed to return the same object.
     * However, in this case the computation may be run multiple times.
     *
     * @return Calculation result
     */
    public T get() {
        if (result == null) {
            resultUpdater.compareAndSet(this, null, new Holder<>(supplier.get()));
        }
        return result.value;
    }
}
