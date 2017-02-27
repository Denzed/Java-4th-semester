package lazy;

import java.util.function.Supplier;

class LazySingleThreaded<T> implements Lazy {
    Supplier<T> supplier;
    T result;

    LazySingleThreaded(Supplier<T> supplier) {
        this.supplier = () -> {
            result = supplier.get();
            supplier = () -> result;
        };
    }

    T get() {
        return supplier.get();
    }
}