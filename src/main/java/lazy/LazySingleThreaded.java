package lazy;

import java.util.function.Supplier;

class LazySingleThreaded<T> implements Lazy<T> {
    private Supplier<T> supplier;
    private T result;

    LazySingleThreaded(final Supplier<T> supplier) {
        this.supplier = () -> {
            result = supplier.get();
            this.supplier = () -> result;
            return result;
        };
    }

    /**
     * Perform calculations once and return the result.
     * Subsequent calls are guaranteed to return the same object.
     * @return Calculation result
     */
    public T get() {
        return supplier.get();
    }
}