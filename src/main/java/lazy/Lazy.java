package lazy;


/**
 * Interface for lazy calculations
 * @param <T> Return type
 */
public interface Lazy<T> {
    /**
     * Perform calculation and return its result.
     * @return Calculation result
     */
    T get();
}