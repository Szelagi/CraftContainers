package pl.szelagi.allocator;

public class AllocatorDestroyedException extends RuntimeException {
    public AllocatorDestroyedException(ISpaceAllocator allocator) {
        super("Allocator has been destroyed: " + allocator.getClass().getSimpleName());
    }
}