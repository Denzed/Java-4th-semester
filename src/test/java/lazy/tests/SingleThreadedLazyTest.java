package lazy.tests;

import lazy.Lazy;
import lazy.LazyFactory;

import java.util.function.Supplier;

/**
 * Test set for {@link lazy.LazySingleThreaded}
 */
public class SingleThreadedLazyTest extends LazyTest {

    protected <T> Lazy<T> applyLazyFactoryGenerator(Supplier<T> supplier) {
        return LazyFactory.createLazySingleThreaded(supplier);
    }
}