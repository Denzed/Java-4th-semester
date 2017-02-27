package lazy;


/**
 * Interface for lazy calculations
 *
 * @param <T> Return type
 */
public interface Lazy<T> {
    T get();
}