package lazy.tests;

import lazy.Lazy;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.function.Supplier;

/**
 * Abstract class to simplify making tests for different {@link lazy.Lazy} implementations
 */
@Ignore
public abstract class LazyTest {
    static final Supplier<String> SIMPLE_SUPPLIER = String::new;
    static final Supplier<Object> NULL_SUPPLIER = () -> null;
    private static final Supplier<Object> NULL = null;
    private static final Supplier<Void> FAIL_ON_CALCULATION = () -> {
        Assert.fail("Calculation is not lazy! This should not happen!");
        return null;
    };
    static final Supplier<Integer> LONG_COMPUTATION = () -> {
        int res = 0;
        for (int i = 0; i < 100000000; ++i) {
            res += i;
        }
        return res;
    };

    protected abstract <T> Lazy<T> applyLazyFactoryGenerator(Supplier<T> supplier);

    <T> void testSubsequentCall(Lazy<T> lazy, T expectedResult) {
        T actualResult = lazy.get();
        Assert.assertEquals(expectedResult, actualResult);
        Assert.assertSame(actualResult, lazy.get());
    }

    private <T> void testSubsequentCall(Supplier<T> supplier) {
        testSubsequentCall(applyLazyFactoryGenerator(supplier), supplier.get());
    }

    /**
     * Check that {@link Lazy#get()} returns the same object reference on subsequent calls
     * (computes the value once)
     */
    @Test
    public void testSubsequentCalls() {
        testSubsequentCall(SIMPLE_SUPPLIER);
        testSubsequentCall(NULL_SUPPLIER);
        testSubsequentCall(LONG_COMPUTATION);
    }

    /**
     * Check that wrapping calculation into Lazy doesn't start it before {@link Lazy#get()} call
     */
    @Test
    public void testIsNotStartingOnItsOwn() {
        applyLazyFactoryGenerator(FAIL_ON_CALCULATION);
    }

    /**
     * Check that {@link lazy.Lazy} on null computation works as expected -- we should not accept null computation and
     * thus throw NullPointerException
     */
    @Test(expected = NullPointerException.class)
    public void testNullComputation() {
        Assert.assertNull(applyLazyFactoryGenerator(NULL).get());
    }
}

