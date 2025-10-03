package pl.szelagi.marker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IMutableMetadata extends IMetadata {
    @NotNull String put(@NotNull String  key, @NotNull String value);
    @Nullable String remove(@NotNull String key);
    void clear();
}
