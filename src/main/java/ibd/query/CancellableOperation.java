package ibd.query;

/**
 * Interface for operations that can be cancelled and need to clean up resources
 * when cancellation occurs. This provides a generic way to handle cancellation
 * across different types of operations.
 */
public interface CancellableOperation {
    
    /**
     * Cleans up resources and memory when operation is cancelled.
     * This should release any allocated memory, clear collections,
     * and update memory statistics appropriately.
     */
    void cleanupOnCancellation();
    
    /**
     * Checks if the operation should be cancelled.
     * This combines both explicit cancellation requests and thread interruption.
     * 
     * @return true if the operation should be cancelled, false otherwise
     */
    boolean isCancellationRequested();
    
    /**
     * Sets the cancellation flag for this operation.
     * This is used to request cancellation from external sources.
     */
    void requestCancellation();
}
