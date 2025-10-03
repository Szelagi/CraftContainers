package pl.szelagi.marker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public interface IMetadata extends Serializable {
    @Nullable String getString(@NotNull String key);
    @NotNull Set<String> keys();
    @NotNull Set<String> values();
    @NotNull Set<Map.Entry<String, String>> entrySet();
    int size();

    private <T> T parseNumber(String key, Function<String, T> parser) {
        var valueString = getString(key);
        if (valueString == null) return null;
        try {
            return parser.apply(valueString);
        } catch (NumberFormatException e) {
            throw MetadataException.forInvalidValueFormat(key, e);
        }
    }

    default @NotNull String requireString(String key) {
        var value = getString(key);
        if (value == null)
            throw MetadataException.forMissingKey(key);
        return value;
    }

    default @Nullable Integer getParsedInt(String  key) {
        return parseNumber(key, Integer::parseInt);
    }

    default int requireParsedInt(String  key) {
        var value = getParsedInt(key);
        if (value == null)
            throw MetadataException.forMissingKey(key);
        return value;
    }

    default @Nullable Double getParsedDouble(String  key) {
        return parseNumber(key, Double::parseDouble);
    }

    default double requireParsedDouble(String  key) {
        var value = getParsedDouble(key);
        if (value == null)
            throw MetadataException.forMissingKey(key);
        return value;
    }
    default @Nullable Boolean getParsedBoolean(String  key) {
        return parseNumber(key, Boolean::parseBoolean);
    }

    default boolean requireParsedBoolean(String  key) {
        var value = getParsedBoolean(key);
        if (value == null)
            throw MetadataException.forMissingKey(key);
        return value;
    }

}
