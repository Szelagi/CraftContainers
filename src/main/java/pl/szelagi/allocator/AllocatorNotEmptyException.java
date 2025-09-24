package pl.szelagi.allocator;

public class AllocatorNotEmptyException extends RuntimeException {
    public AllocatorNotEmptyException(ISpaceAllocator allocator) {
        super("Cannot destroy allocator " + allocator.getClass().getSimpleName() +
                " because it still has " + allocator.allocatedSpaces().size() + " active allocations.");
    }
}