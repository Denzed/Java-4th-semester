import lazy.Lazy;
import lazy.LazyFactory;

import java.util.function.Supplier;

/**
 * Test set for LazySingleThreaded
 */
public class SingleThreadedTest extends LazyTest {

    @Override
    protected <T> Lazy<T> applyLazyFactoryGenerator(Supplier<T> supplier) {
        return LazyFactory.createLazySingleThreaded(supplier);
    }
}
