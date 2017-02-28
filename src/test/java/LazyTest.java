import lazy.Lazy;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.function.Supplier;

@Ignore
public abstract class LazyTest extends Assert {
    static final Supplier<String> SIMPLE_SUPPLIER = String::new;
    static final Supplier<Object> NULL_SUPPLIER = () -> null;
    private static final Supplier<Object> NULL = null;
    private static final Supplier<Void> FAIL_ON_CALCULATION = () -> {
        fail("Calculation is not lazy! This should not happen!");
        return null;
    };
    static final Supplier<Integer> LONG_COMPUTATION = () -> {
        int res = 0;
        for (int i = 0; i < 1000000; ++i) {
            res += i;
        }
        return res;
    };

    /**
     * Applies predefined LazyFactory method in order to generate desired Lazy implementation
     *
     * @param supplier Calculation to wrap
     * @param <T>      Return type
     * @return Desired Lazy implementation of the given supplier
     */
    protected abstract <T> Lazy<T> applyLazyFactoryGenerator(Supplier<T> supplier);

    <T> void testSubsequentCall(Lazy<T> lazy, T expectedResult) {
        T actualResult = lazy.get();
        assertEquals(expectedResult, actualResult);
        assertSame(actualResult, lazy.get());
    }

    private <T> void testSubsequentCall(Supplier<T> supplier) {
        testSubsequentCall(applyLazyFactoryGenerator(supplier), supplier.get());
    }

    /**
     * Check that Lazy::get returns the same object reference on subsequent calls
     * (computes the value once)
     */
    @Test
    public void testSubsequentCalls() {
        testSubsequentCall(SIMPLE_SUPPLIER);
        testSubsequentCall(NULL_SUPPLIER);
        testSubsequentCall(LONG_COMPUTATION);
    }

    /**
     * Check that wrapping calculation into Lazy doesn't start it before get
     */
    @Test
    public void testIsNotStartingOnItsOwn() {
        applyLazyFactoryGenerator(FAIL_ON_CALCULATION);
    }

    /**
     * Check that Lazy on empty computation works as expected
     */
    @Test
    public void testNullComputation() {
        assertNull(applyLazyFactoryGenerator(NULL).get());
    }
}

