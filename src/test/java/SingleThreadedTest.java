import lazy.Lazy;
import lazy.LazyFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Supplier;

/**
 * Test set for LazySingleThreaded
 */
public class SingleThreadedTest extends Assert {
    private Supplier<String> testSupplier = String::new;

    @Test
    public void sameObjectTest() {
        Lazy<String> stringLazy = LazyFactory.createLazySingleThreaded(testSupplier);
        assertSame(stringLazy.get(), stringLazy.get());
    }
}
