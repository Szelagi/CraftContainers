package pl.szelagi.marker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class Metadata implements IMutableMetadata {
    private final Map<String, String> map;

    public Metadata() {
        this.map = new HashMap<>();
    }

    public Metadata(IMetadata metadata) {
        this.map = new HashMap<>();
        metadata.entrySet().forEach(
                entry -> this.map.put(entry.getKey(), entry.getValue())
        );
    }

    protected Metadata(Map<String, String> map) {
        this.map = new HashMap<>(map);
    }

    protected Map<String, String> toMap() {
        return new HashMap<>(map);
    }

    @Override
    public @Nullable String getString(@NotNull String key) {
        return map.get(key);
    }

    @Override
    public @NotNull String put(@NotNull String key, @NotNull String value) {
        map.put(key, value);
        return value;
    }

    @Override
    public @Nullable String remove(@NotNull String key) {
        return map.remove(key);
    }

    @Override
    public @NotNull Set<String> keys() {
        return map.keySet();
    }

    @Override
    public @NotNull Set<String> values() {
        return new HashSet<>(map.values());
    }

    @Override
    public @NotNull Set<Map.Entry<String, String>> entrySet() {
        return map.entrySet();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public String toString() {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

}
