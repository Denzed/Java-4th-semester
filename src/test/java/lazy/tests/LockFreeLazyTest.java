package lazy.tests;

import lazy.Lazy;
import lazy.LazyFactory;

import java.util.function.Supplier;

/**
 * Test set for {@link lazy.LazyLockFree}
 */
public class LockFreeLazyTest extends MultiThreadedLazyTest {

    @Override
    protected <T> Lazy<T> applyLazyFactoryGenerator(Supplier<T> supplier) {
        return LazyFactory.createLazyLockFree(supplier);
    }
}
