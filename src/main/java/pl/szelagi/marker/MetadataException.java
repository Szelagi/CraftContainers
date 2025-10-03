package pl.szelagi.marker;

public class MetadataException extends RuntimeException {
    public MetadataException(String message) {
        super(message);
    }

    public MetadataException(String message, Throwable cause) {
        super(message, cause);
    }

    public static MetadataException forMissingKey(String key) {
        return new MetadataException("Key not found: " + key);
    }

    public static MetadataException forInvalidValueFormat(String key, NumberFormatException e) {
        return new MetadataException("Invalid value format for key: " + key, e);
    }
}