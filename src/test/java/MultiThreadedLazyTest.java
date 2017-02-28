import lazy.Lazy;
import lazy.LazyFactory;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Test set for {@link lazy.LazyMultiThreaded}
 */
public class MultiThreadedLazyTest extends LazyTest {
    /**
     * Thread count to use in {@link this.testMultiThreadedCalls}
     * Currently set to 5: number of cores + 1 -- pretty standard number
     */
    private static final int THREADS_TO_TEST = 5;

    protected <T> Lazy<T> applyLazyFactoryGenerator(Supplier<T> supplier) {
        return LazyFactory.createLazyMultiThreaded(supplier);
    }

    private <T> void testMultiThreadedSubsequentCall(Supplier<T> supplier) throws InterruptedException {
        Lazy<T> lazy = applyLazyFactoryGenerator(supplier);
        T expectedResult = supplier.get();
        List<Thread> threads = new LinkedList<>();
        for (int i = 0; i < THREADS_TO_TEST; ++i) {
            threads.add(new Thread(() -> testSubsequentCall(lazy, expectedResult)));
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }

    /**
     * Check that {@link lazy.LazyMultiThreaded} is really thread-safe
     */
    @Test
    public void testMultiThreadedCalls() throws InterruptedException {
        testMultiThreadedSubsequentCall(SIMPLE_SUPPLIER);
        testMultiThreadedSubsequentCall(NULL_SUPPLIER);
        testMultiThreadedSubsequentCall(LONG_COMPUTATION);
    }
}